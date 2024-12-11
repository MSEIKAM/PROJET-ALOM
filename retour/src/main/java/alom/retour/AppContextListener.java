package alom.retour;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import alom.retour.tcp.TcpServer;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Application démarrée : initialisation de ressources.");
        TcpServer.getInstance().startServer();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application arrêtée : libération des ressources.");
        TcpServer.getInstance().stopServer();
    }
}
