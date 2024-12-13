package alom.retour.servlet;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import alom.retour.tcp.TcpServer;

public class TokenServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Map<String, String> tokenStore;

    @Override
    public void init() throws ServletException {
        super.init();
        // Retrieve the shared token store from the singleton TcpServer instance
        tokenStore = TcpServer.getInstance().getTokenStore();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/register".equals(path)) {
            String token = request.getParameter("token");
            String nickname = request.getParameter("nickname");

            if (token == null || nickname == null || token.isEmpty() || nickname.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Missing token or nickname.");
                return;
            }
            token = token.trim();
            nickname = nickname.trim();
            tokenStore.put(token, nickname);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Token et nickname registered successfully.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/check".equals(path)) {
            String token = request.getParameter("token");

            if (token == null || token.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Missing token.");
                return;
            }

            token = token.trim();
            String nickname = tokenStore.get(token);
            if (nickname != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Nickname : " + nickname);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Token unfound.");
            }
        }
    }
}
