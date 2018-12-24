/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sikke.cli;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author selim
 */
public class RootHandler implements HttpHandler {

    public RootHandler() {
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        int port = 9000;
        String response = "<h1>Server start success if you see this message</h1 > " + " < h1 > Port: " + port + "</h1 >";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
