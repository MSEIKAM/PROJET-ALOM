package alom.retour.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpServer implements Runnable {

	private static final TcpServer INSTANCE = new TcpServer();
	
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>(); 
    private final List<Thread> connectionThreads = new ArrayList<>();
    private boolean running = true;

    private TcpServer() {}

    public static TcpServer getInstance() {
    	return INSTANCE;
    }
    
    public Map<String, String> getTokenStore() {
        return tokenStore;
    }

    
    public void startServer() {
        Thread serverThread = new Thread(this);
        serverThread.start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Serveur TCP started on port 12345.");

            while (running) {
                Socket clientSocket = serverSocket.accept();
                Thread connectionThread = new Thread(new TcpConnectionHandler(clientSocket));
                connectionThread.start();
                
                synchronized (connectionThreads) {
                    connectionThreads.add(connectionThread);
                }
            }
        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            } else {
                System.out.println("Serveur TCP stopped.");
            }
        }
    }

    
    public void stopServer() {
    	
    	synchronized (connectionThreads) {
            for (Thread thread : connectionThreads) {
                try {
                    thread.interrupt(); 
                    thread.join(); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    	
        this.running = false;
        
        
    }
}
