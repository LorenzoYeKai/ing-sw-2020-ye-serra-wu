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
import java.util.Date;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

// helper functional interface, similar to Supplier
// but can throw errors
interface Supplier {
    String get() throws Exception;
}

public class Script {
    private final HttpServer httpServer;
    private final StringWriter log = new StringWriter();
    private final PrintWriter logger = new PrintWriter(this.log);
    //the Github API access token
    private final String token;
    // the url for downloading jar
    private String lastRedirectUrl = null;
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
        System.out.println("Github Access Token: ");
        var token = input.nextLine();
        var script = new Script(password, directory, token);
        System.out.println("Starting the server...");
        script.run();
    }

    private Script(String password, Path directory, String token) throws IOException {
        // setup a basic HTTP server to monitor and manage the game server.
        this.httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
        // save the github api token
        this.token = token;
        // by default it shows the available URLs (i.e. commands):
        this.addHandler("/", exchange -> {
            var response = "Available commands: \r\n" +
                    "isServerOnline\r\n" +
                    "killGameServer[password]\r\n" +
                    "restartGameServer[password]\r\n" +
                    "updateGameServer\r\n";
            response += "Logs:\r\n" + this.log.toString();

            sendStringResponse(exchange, 200, response);
        });

        // check if server is online
        this.addHandler("/isServerOnline", () -> {
            if (this.gameServer != null && this.gameServer.isAlive()) {
                return "true";
            }
            return "false";
        });

        // killGameServer can only be invoked with correct password
        this.addHandler("/killGameServer" + password, () -> {
            this.killServer();
            return "game server killed";
        });

        // restartGameServer can only be invoked with correct password
        this.addHandler("/restartGameServer" + password, () -> {
            this.killServer();
            this.startServer();
            return "game server restarted";
        });

        // check if if a new version of game server exists.
        // if yes, download it and restart the server.
        this.addHandler("/updateGameServer", () -> {
            var url = this.getLastVersionUrl();
            if (url.equals(this.lastRedirectUrl)) {
                return "game server is already the latest version";
            }
            this.killServer();
            this.downloadFile(new URL(url), directory);
            this.startServer();
            this.lastRedirectUrl = url;
            return "game server updated";
        });
    }

    private void run() {
        // set default executor
        this.httpServer.setExecutor(null);
        this.httpServer.start();
    }

    // create a handler which executes synchronized code
    private void addHandler(String path, HttpHandler handler) {
        this.httpServer.createContext(path, exchange -> {
            synchronized (this) {
                // shrink the log buffer if its length is greater than 4KB
                if (this.log.getBuffer().length() > 4096) {
                    this.log.getBuffer().replace(0, 2048, "");
                }

                this.logger.println(new Date() + " | " +
                        exchange.getRequestMethod() + " " +
                        exchange.getRequestURI());
                handler.handle(exchange);
            }
        });
    }

    // create a synchronized handler which executes the action and then print the result
    private void addHandler(String path, Supplier action) {
        this.addHandler(path, httpExchange -> {
            try {
                var response = action.get();
                this.logger.println(" - reply: " + response);
                sendStringResponse(httpExchange, 200, response);
            } catch (Exception e) {
                this.logger.println(" - error: " + e);
                sendStringResponse(httpExchange, 500, "Error: " + e);
            }
        });
    }

    // send a plain text response, and close the HttpExchange
    private static void sendStringResponse(HttpExchange exchange, int code, String string) throws IOException {
        exchange.sendResponseHeaders(code, string.length());
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf8");
        var out = exchange.getResponseBody();
        out.write(string.getBytes());
        out.close();
        exchange.close();
    }

    private String getLastVersionUrl() throws IOException {
        // start a http request to Github API to check artifacts
        final var apiUrl =
                "https://api.github.com/repos/Kishin98/ing-sw-2020-ye-serra-wu/actions/artifacts";
        var connection = new URL(apiUrl).openConnection();
        connection.addRequestProperty("Authorization", "token " + this.token);
        connection.connect();
        try (var responseStream = connection.getInputStream()) {
            // read response into string, and parse it to json
            var response = IOUtils.toString(responseStream, StandardCharsets.UTF_8);
            var json = new JSONObject(response);

            this.logger.println("total number of artifacts: " + json.getInt("total_count"));
            var last = json.getJSONArray("artifacts").getJSONObject(0);

            // get the redirect url
            var redirectUrl = last.getString("archive_download_url");
            this.logger.println("redirect url: " + redirectUrl);
            return redirectUrl;
        }
    }

    private void downloadFile(URL redirectUrl, Path directory) throws IOException {
        // start a http request to Github API to download files
        // using the last redirect url. Java's URLConnection will automatically
        // follow redirect, so we will request the actual file
        var connection = redirectUrl.openConnection();
        connection.addRequestProperty("Authorization", "token " + this.token);
        connection.connect();
        this.logger.println("Connected to " + connection.getURL());

        // then let's download it to a file
        var archiveFile = directory.resolve("downloaded.zip");
        try (var stream = connection.getInputStream();
             var output = new FileOutputStream(archiveFile.toFile())) {
            stream.transferTo(output);
        }
        this.logger.println("Archive file downloaded to " + archiveFile);
    }

    private void killServer() {

    }

    private void startServer() {

    }
}