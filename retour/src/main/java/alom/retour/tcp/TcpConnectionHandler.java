package alom.retour.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class TcpConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final Map<String, String> tokenStore;

    public TcpConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        // Get the shared token store from the singleton TcpServer instance
        this.tokenStore = TcpServer.getInstance().getTokenStore();
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
          
            out.write("Entre token (or type 'exit' to quit):\n".getBytes());
            
            String token = in.readLine();

            if (token == null || "exit".equalsIgnoreCase(token.trim())) {
                out.write("Closing connection...\n".getBytes());
            } else {

                String response = checkToken(token);
                out.write((response + "\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
               
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String checkToken(String token) {
        try {
            // Construction de l'URL du Auth avec le token
            URL url = new URL("http://localhost:8080/auth/webapi/auth/verify-token?token=" + token);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            
            // Récupération de la réponse de l'authentification
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = in.readLine();
                in.close();
                return response; 
            } else {
                return "User not found.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error connecting to authentication service.";
        }
    }


}
