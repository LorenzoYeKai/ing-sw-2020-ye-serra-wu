/*
  This script is used together with the CI of Github Actions to automatically
  deploy our Santorini Game Server application on our AWS EC2 instance.

  It can also be used to stop or restart manually the game server.

  This script will setup a HTTP server on port 8000.

  NOTE: For now, this script cannot update itself. So a new version of this
  script must be manually uploaded to server and be run manually.
*/

package it.polimi.ingsw.deploy;

import java.io.IOException;
import java.lang.Process;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Script {
    private List<String> args;
    private HttpServer httpServer = null;
    private Process gameServer = null;

    public static void main(String[] args) throws Exception {
        //var script = new Script(Arrays.asList(args));
        var script = new Script(Arrays.asList("-password", "123", "-directory", "."));
        script.run();
    }

    private Script(List<String> args) throws IOException {
        this.args = args;
        // setup a basic HTTP server to monitor and manage the game server.
        this.httpServer = HttpServer.create(new InetSocketAddress(8000), 0);

        var password = this.findArgument("-password");
        var directory = this.findArgument("-directory");

        // by default it shows the available URLs (i.e. commands):
        this.addHandler("/", exchange -> {
            var response = "Available commands: \r\n" +
                    "isServerOnline\r\n" +
                    "killGameServer[password]\r\n" +
                    "restartGameServer[password]\r\n" +
                    "updateGameServer";
            sendStringResponse(exchange, 200, response);
        });

        // check if server is online
        this.addHandler("/isServerOnline", exchange -> {
            var response = "false";
            if (this.gameServer != null && this.gameServer.isAlive()) {
                response = "true";
            }
            sendStringResponse(exchange, 200, response);
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
            if (!this.hasNewVersion()) {
                return "game server is already the latest version";
            }
            this.killServer();
            this.downloadFile();
            this.startServer();
            return "game server updated";
        });
    }

    private void run() {
        // set default executor
        this.httpServer.setExecutor(null);
        this.httpServer.start();
    }

    // get value from command line arguments
    private String findArgument(String argumentName) {
        var index = this.args.indexOf(argumentName);
        if (index == -1) {
            throw new IllegalArgumentException("Missing argument " + argumentName);
        }
        return args.get(index + 1);
    }

    // create a handler which executes synchronized code
    private void addHandler(String path, HttpHandler handler) {
        this.httpServer.createContext(path, exchange -> {
            synchronized (this) {
                handler.handle(exchange);
            }
        });
    }

    // create a synchronized handler which executes the action and then print the result
    private void addHandler(String path, Supplier<String> action) {
        this.httpServer.createContext(path, httpExchange -> {
            try {
                sendStringResponse(httpExchange, 200, action.get());
            } catch (Exception e) {
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

    private boolean hasNewVersion() {
        return false;
    }

    private void downloadFile() {

    }

    private void killServer() {

    }

    private void startServer() {

    }
}