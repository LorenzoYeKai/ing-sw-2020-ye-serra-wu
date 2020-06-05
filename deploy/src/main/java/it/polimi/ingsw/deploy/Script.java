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
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private static final String APIBase =
            "https://api.github.com/repos/Kishin98/ing-sw-2020-ye-serra-wu";
    private final ScheduledExecutorService executor;
    private final HttpServer httpServer;
    private final Path directory; // the directory which stores the downloaded jar
    private final String githubToken;
    private final StringWriter log = new StringWriter();
    private final PrintWriter logger = new PrintWriter(this.log);
    // the url for downloading jar
    private String lastRedirectURL = null;
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
        System.out.println("Github Token: ");
        var token = input.nextLine();
        var script = new Script(password, directory, token);
        System.out.println("Starting the server...");
        script.run();
    }

    private Script(String password, Path directory, String token) throws IOException {
        // create an executor
        this.executor = new ScheduledThreadPoolExecutor(2);
        // setup a basic HTTP server to monitor and manage the game server.
        this.httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
        this.httpServer.setExecutor(this.executor);
        this.directory = directory;
        this.githubToken = token;
        // by default it shows the available URLs (i.e. commands):
        this.addHttpHandler("/", exchange -> {
            var response = "Available commands: \r\n" +
                    "isServerOnline\r\n" +
                    "killGameServer[password]\r\n" +
                    "restartGameServer[password]\r\n" +
                    "updateGameServer[github api token]\r\n";
            response += "Logs:\r\n" + this.log.toString();

            sendStringResponse(exchange, 200, response);
        });


        // check if server is online
        this.addHandler("/isServerOnline", exchange -> {
            if (this.gameServer != null && this.gameServer.isAlive()) {
                return "true";
            }
            return "false";
        });

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
        this.addHandler("/updateGameServer", httpExchange -> this.updateGameServer(3));
    }

    private void run() {
        this.httpServer.start();
    }

    // create a handler which executes synchronized code
    private void addHttpHandler(String path, HttpHandler handler) {
        this.httpServer.createContext(path, exchange -> {
            synchronized (this) {
                // shrink the log buffer if its length is greater than 4KB
                if (this.log.getBuffer().length() > 4096) {
                    this.log.getBuffer().replace(0, 2048, "");
                }

                this.logger.println(new Date() + " | " +
                        exchange.getRequestMethod() + " " +
                        path);
                handler.handle(exchange);
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

    // send a plain text response, and close the HttpExchange
    private static void sendStringResponse(HttpExchange exchange, int code, String string) throws IOException {
        exchange.sendResponseHeaders(code, string.length());
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf8");
        var out = exchange.getResponseBody();
        out.write(string.getBytes());
        out.close();
        exchange.close();
    }

    // Try to update game server. If Github Actions isn't completed yet, try again multiple times
    private synchronized String updateGameServer(int tryCount) {
        try {
            if (!this.isLastWorkflowCompleted()) {
                if (tryCount <= 0) {
                    this.logger.println("workflow not completed yet, stop checking.");
                    return "cannot update";
                }

                Runnable next = () -> this.updateGameServer(tryCount - 1);
                // if tryCount is 1, then wait 5000 / 1 milliseconds, = 5 seconds
                var delay = 5000 / tryCount;
                this.executor.schedule(next, delay, TimeUnit.MILLISECONDS);
                this.logger.println("workflow not completed yet, will check again in " + delay / 1000.0 + " seconds");
                return "will check later";
            }

            // get the url of latest artifacts (jar)
            var url = this.getLastVersionURL();
            if (url.equals(this.lastRedirectURL)) {
                this.logger.println("game server is already the latest version");
                return "already up-to-date";
            }

            this.killServer();
            this.downloadFile(new URL(url));
            this.startServer();
            this.lastRedirectURL = url;

            var message = "game server updated";
            this.logger.println(message);
            return message;
        } catch (Exception e) {
            var message = "Error when updating server: " + e;
            this.logger.println(message);
            return message;
        }
    }

    // Connect to Github API with credentials
    private URLConnection connectGithubAPI(URL url) throws IOException {
        var connection = url.openConnection();
        connection.addRequestProperty("Authorization", "token " + this.githubToken);
        connection.connect();
        return connection;
    }

    private JSONObject getGithubAPI(URL url) throws IOException {
        var connection = this.connectGithubAPI(url);
        try (var responseStream = connection.getInputStream()) {
            // read response into string, and parse it to json
            var response = IOUtils.toString(responseStream, StandardCharsets.UTF_8);
            return new JSONObject(response);
        }
    }

    // Check if last Github Actions workflow runs has already completed
    // because if it isn't completed, it means we can't download any artifact yet
    private boolean isLastWorkflowCompleted() throws IOException {
        // start a http request to Github API to check workflow
        var workflowURL = this.getGithubAPI(new URL(APIBase + "/actions/workflows"))
                .getJSONArray("workflows")
                .getJSONObject(0)
                .getString("url");
        var status = this.getGithubAPI(new URL(workflowURL + "/runs"))
                .getJSONArray("workflow_runs")
                .getJSONObject(0)
                .getString("status");
        return status.equals("completed");
    }

    private String getLastVersionURL() throws IOException {
        var artifacts = this.getGithubAPI(new URL(APIBase + "/actions/artifacts"));
        this.logger.println("total number of artifacts: " + artifacts.getInt("total_count"));
        var last = artifacts.getJSONArray("artifacts").getJSONObject(0);
        // get the redirect url
        var redirectURL = last.getString("archive_download_url");
        this.logger.println("redirect url: " + redirectURL);
        return redirectURL;
    }

    private void downloadFile(URL redirectURL) throws IOException {
        // start a http request to Github API to download files
        // using the last redirect url. Java's URLConnection will automatically
        // follow redirect, so we will request the actual file
        var connection = this.connectGithubAPI(redirectURL);
        this.logger.println("Connected to " + connection.getURL());

        // then let's download it to a file
        var archiveFile = this.directory.resolve("downloaded.zip");
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