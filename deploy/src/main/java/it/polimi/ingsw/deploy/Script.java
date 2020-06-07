/*
  This script is used together with the CI of Github Actions to automatically
  deploy our Santorini Game Server application on our AWS EC2 instance.

  It can also be used to stop or restart manually the game server.

  This script will setup a HTTP server on port 8000.

  NOTE: For now, this script cannot update itself. So a new version of this
  script must be manually uploaded to server and be run manually.
*/

package it.polimi.ingsw.deploy;

import java.io.*;
import java.lang.Process;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

// helper functional interface, similar to HttpHandler
// but it returns a String which will be the response
interface Handler {
    String get(HttpExchange exchange) throws Exception;
}

interface StringHandler {
    String apply(String data) throws Exception;
}

public class Script {
    private static final String apiBase =
            "https://api.github.com/repos/Kishin98/ing-sw-2020-ye-serra-wu";
    private static final String utf8PlainText = "text/plain; charset=utf8";
    private final HttpServer httpServer;
    private final File serverJar; // santorini server jar
    private final File serverOutputLog; // output log of server jar
    private final File serverErrorLog; // error log of server jar
    private final PrintWriter logger;
    // the instant when the jar was updated
    private Instant lastUpdatedAt = null;
    // the game server process
    private Process gameServer = null;

    public static void main(String[] args) throws Exception {
        var input = new Scanner(System.in);
        System.out.println("Type password: ");
        var password = input.nextLine();

        System.out.println("Type directory: ");
        var directoryInput = input.nextLine();
        var directory = FileSystems.getDefault()
                .getPath(directoryInput).normalize().toAbsolutePath();
        System.out.println("Directory is " + directory);
        // check and create the directory
        if (!directory.toFile().isDirectory()) {
            if (!directory.toFile().mkdirs()) {
                throw new IOException("Failed to create directory");
            }
        }

        var script = new Script(password, directory);
        System.out.println("Starting the server...");
        script.run();
    }

    private Script(String password, Path directory) throws IOException {
        // setup a basic HTTP server to monitor and manage the game server.
        this.httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
        // set a default executor
        this.httpServer.setExecutor(null);
        this.serverJar = directory.resolve("server.jar").toFile();
        // the log file of this deploy script
        File httpLog = directory.resolve("httpLog.txt").toFile();
        this.serverOutputLog = directory.resolve("serverOutputLog.txt").toFile();
        this.serverErrorLog = directory.resolve("serverErrorLog.txt").toFile();

        this.logger = new PrintWriter(new FileOutputStream(httpLog), true);

        // by default it shows the available URLs (i.e. commands):
        this.addHttpHandler("/", exchange -> {
            var title = "Santorini Game Server Manager";
            var serverStatus = new StringBuilder();
            if (this.gameServer == null) {
                serverStatus.append("Game server is null<br />");
            } else {
                this.gameServer.info().startInstant().ifPresent(instant ->
                        serverStatus.append("Game server was created at {{ new Date(\"")
                                .append(instant)
                                .append("\") }}<br />")
                );
                serverStatus.append("Game server is ")
                        .append(this.gameServer.isAlive()
                                ? "<span style=\"color: green\">⬤</span> ONLINE"
                                : "<span style=\"color: red\">⬤</span> OFFLINE")
                        .append("<br />");
            }
            var html = ""
                    + "<html>"
                    + "<head>"
                    + "  <title>" + title + "</title>"
                    + "  <script src=\"https://cdn.jsdelivr.net/npm/vue@2/dist/vue.min.js\"></script>"
                    + "</head>"
                    + "<body>"
                    + "  <div id=\"app\">"
                    + "  <h1>" + title + "</h1>"
                    + "  <h3>Santorini Game Server Status: </h3>"
                    + "  <p>" + serverStatus + "</p>"
                    + "  <h3>Available commands: </h3>"
                    + "  <div v-for=\"command in commands\">"
                    + "    <a v-bind:href=\"command\">{{ command }}</a>"
                    + "  </div>"
                    + "  <label>Password: </label>"
                    + "  <input type=\"text\" v-model=\"password\" /><br />"
                    + "  <label>Github Personal Access Token: </label>"
                    + "  <input type=\"text\" v-model=\"token\" /><br />"
                    + "  <script>new Vue({ "
                    + "    el: '#app', "
                    + "    data: { password: '', token: '' }, "
                    + "    computed: { "
                    + "      passwordHint() { "
                    + "        return this.password || '+needPassword'; "
                    + "      }, "
                    + "      tokenHint() { "
                    + "        return this.token || '+needGithubToken'; "
                    + "      }, "
                    + "      commands() { "
                    + "        return [ "
                    + "          'httpLog', "
                    + "          'serverOutputLog', "
                    + "          'serverErrorLog', "
                    + "          'killGameServer' + this.passwordHint, "
                    + "          'restartGameServer' + this.passwordHint, "
                    + "          'updateGameServer' + this.tokenHint, "
                    + "        ]; "
                    + "      } "
                    + "    } "
                    + "  })</script>"
                    + "</body>"
                    + "</html>";

            sendStringResponse(exchange, 200, html, "text/html; charset=utf8");
        });


        // check if server is online
        this.addHandler("/isServerOnline", exchange -> Boolean.toString(this.isServerAlive()));

        // get the http log
        this.addFileSender("/httpLog", httpLog);
        // get the server's stdout
        this.addFileSender("/serverOutputLog", this.serverOutputLog);
        // get the server's stderr
        this.addFileSender("/serverErrorLog", this.serverErrorLog);

        // killGameServer can only be invoked with correct password
        this.addStringHandler("/killGameServer", inputPassword -> {
            if (!inputPassword.equals(password)) {
                throw new IllegalArgumentException("Incorrect password");
            }
            this.killServer();
            return "game server killed";
        });

        // restartGameServer can only be invoked with correct password
        this.addStringHandler("/restartGameServer", inputPassword -> {
            if (!inputPassword.equals(password)) {
                throw new IllegalArgumentException("Incorrect password");
            }
            this.killServer();
            this.startServer();
            return "game server restarted";
        });

        // check if if a new version of game server exists.
        // if yes, download it and restart the server.
        this.addStringHandler("/updateGameServer", token -> {
            if (token.isBlank()) {
                throw new IllegalArgumentException("Need a github token");
            }
            return this.updateGameServer(token);
        });
    }

    private void run() {
        this.httpServer.start();
    }

    // create a synchronized handler which executes synchronized code
    private void addHttpHandler(String path, HttpHandler handler) {
        this.httpServer.createContext(path, exchange -> {
            synchronized (this) {
                // automatically close the exchange after use
                try (exchange) {
                    if (!Objects.equals(path, "/")) {
                        this.logger.println(new Date() + " | " +
                                exchange.getRequestMethod() + " " +
                                path);
                    }
                    handler.handle(exchange);
                } catch (Exception e) {
                    this.logger.println("Unhandled exception from handler: " + e);
                }
            }
        });
    }

    // create a handler which executes the action and then print the result
    private void addFileSender(String path, File file) {
        this.addHttpHandler(path, httpExchange -> {
            this.logger.println("Sending file " + file);
            final InputStream input;
            try {
                input = new FileInputStream(file);
            } catch (Exception e) {
                var errorMessage = "Failed to open file " + file + ": " + e;
                this.logger.println(e);
                sendStringResponse(httpExchange, 500, errorMessage);
                return;
            }

            try (input) {
                this.sendStream(httpExchange, input);
            }
        });
    }

    // create a synchronized handler which executes the action and then print the result
    private void addHandler(String path, Handler action) {
        this.addHttpHandler(path, httpExchange -> {
            try {
                var response = action.get(httpExchange);
                this.logger.println(" - reply: " + response);
                sendStringResponse(httpExchange, 200, response);
            } catch (Exception e) {
                this.logger.println(" - error: " + e);
                sendStringResponse(httpExchange, 500, "Error: " + e);
            }
        });
    }

    // create a handler which accepts the extra string appended on the url as argument
    private void addStringHandler(String path, StringHandler handler) {
        this.addHandler(path, httpExchange -> {
            var actualPath = httpExchange.getRequestURI().getPath();
            var startPosition = actualPath.indexOf(path);
            if (startPosition == -1) {
                throw new UnsupportedOperationException("Unexpected path");
            }
            var dataStartPosition = startPosition + path.length();
            var data = actualPath.substring(dataStartPosition);

            return handler.apply(data);
        });
    }

    // send a plain text response
    private static void sendStringResponse(HttpExchange exchange, int code, String string) throws IOException {
        sendStringResponse(exchange, code, string, Script.utf8PlainText);
    }

    // send a plain text response with content type
    private static void sendStringResponse(HttpExchange exchange,
                                           int code, String string, String contentType) throws IOException {
        var bytes = string.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        // get the response body, and automatically close it afterwards
        try (var out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    // send response from a stream
    private void sendStream(HttpExchange exchange, InputStream input) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseHeaders().set("Content-Type", Script.utf8PlainText);
        // automatically close fileStream and out afterwards
        try (var out = exchange.getResponseBody()) {
            input.transferTo(out);
        }
    }

    // Try to update game server. If Github Actions isn't completed yet, try again multiple times
    private synchronized String updateGameServer(String token) {
        try {
            var asset = this.getServerJar(token);
            if (!asset.getString("state").equals("uploaded")) {
                return "state is not `uploaded`";
            }

            var updatedAt = Instant.parse(asset.getString("updated_at"));
            this.logger.println("asset was updated at " + updatedAt);
            if (this.lastUpdatedAt != null) {
                this.logger.println("current server jar was updated at " + this.lastUpdatedAt);
                if (!updatedAt.isAfter(this.lastUpdatedAt)) {
                    return "game server was already up-to-date";
                }
            }

            this.killServer();
            this.downloadFile(new URL(asset.getString("url")), token);
            this.startServer();
            this.lastUpdatedAt = updatedAt;

            return "game server updated";
        } catch (Exception e) {
            return "Error when updating server: " + e;
        }
    }

    // connect to github api with credentials
    private JSONObject getGithubAPI(URL url, String token) throws IOException {
        var connection = url.openConnection();
        connection.addRequestProperty("Authorization", "token " + token);
        connection.connect();
        // get the response input stream, and automatically close it afterwards
        try (var responseStream = connection.getInputStream()) {
            // read response into string, and parse it to json
            var response = IOUtils.toString(responseStream, StandardCharsets.UTF_8);
            return new JSONObject(response);
        }
    }

    // Check if the Github Release already contains an updated server jar
    private JSONObject getServerJar(String token) throws IOException {
        // start a http request to Github API to check release
        var assets = this.getGithubAPI(new URL(Script.apiBase + "/releases/tags/tip"), token)
                .getJSONArray("assets");
        for (var i = 0; i < assets.length(); ++i) {
            var asset = assets.getJSONObject(i);
            var name = asset.getString("name").toLowerCase();
            // find the server-***.jar
            if (name.startsWith("server-") && name.endsWith(".jar")) {
                this.logger.println("found server jar asset: " + name);
                return asset;
            }
        }

        throw new FileNotFoundException("server jar not in github release");
    }

    private void downloadFile(URL redirectURL, String token) throws IOException {
        // start a http request to Github API to download files
        // using the last redirect url. Java's URLConnection will automatically
        // follow redirect, so we will request the actual file
        var connection = redirectURL.openConnection();
        connection.addRequestProperty("Authorization", "token " + token);
        connection.addRequestProperty("Accept", "application/octet-stream");
        connection.connect();
        this.logger.println("Connected to " + connection.getURL());

        // get the response input stream, open the destination jar file
        // and automatically close them afterwards
        try (var stream = connection.getInputStream();
             var destination = new FileOutputStream(this.serverJar)) {
            // then download the jar file
            stream.transferTo(destination);
        }
        this.logger.println("jar file saved to " + this.serverJar);
    }

    private void killServer() throws ExecutionException, InterruptedException {
        if (!this.isServerAlive()) {
            this.logger.println("game server is not alive, no need to kill");
            return;
        }
        this.logger.println("killing the server...");
        this.gameServer.destroyForcibly();
        this.gameServer.onExit().get();
    }

    private void startServer() throws IOException {
        if (this.isServerAlive()) {
            throw new UnsupportedOperationException("cannot start server when server is already running");
        }
        var processBuilder = new ProcessBuilder("java", "-jar", this.serverJar.getAbsolutePath());
        this.logger.println("starting the server with command line: ");
        this.logger.println(processBuilder.command());
        processBuilder.redirectOutput(this.serverOutputLog);
        processBuilder.redirectError(this.serverErrorLog);
        this.gameServer = processBuilder.start();
    }

    private boolean isServerAlive() {
        return this.gameServer != null && this.gameServer.isAlive();
    }
}
