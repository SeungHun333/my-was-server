package com.hun;

import com.hun.http.HttpResponse;
import com.hun.servlet.Servlet;
import com.hun.servlet.handler.DispatchServlet;
import com.hun.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        logger.debug("Connection ip: {}, port: {}", connection.getInetAddress(), connection.getPort());
        try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            DataOutputStream dos = new DataOutputStream(out)) {

            HttpRequest req = HttpUtil.parseRequest(in);
            HttpResponse res = new HttpResponse();

            ApplicationContext ac = ApplicationContext.getApplicationContext();
            Servlet servlet = DispatchServlet.getDispatcherServlet(ac);

            byte[] body = res.getbody();
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
