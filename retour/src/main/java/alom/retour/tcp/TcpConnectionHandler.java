package alom.retour.tcp;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class TcpConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final Map<String, String> tokenStore;

    public TcpConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.tokenStore = TcpServer.getInstance().getTokenStore();
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            out.println("Enter your token (or type 'exit' to quit):");

            String token = in.readLine();

            if (token == null || "exit".equalsIgnoreCase(token.trim())) {
                out.println("Closing connection...");
                return;
            }

            token = token.trim();
            String nickname = checkToken(token);

            if (nickname == null) {
                out.println("Invalid token. Connection closed.");
            } else {
                out.println("Connected as " + nickname);

                // Nettoyer le nickname si nécessaire
                if (nickname.startsWith("Welcome ")) {
                    nickname = nickname.substring(8).trim(); // enlever "Welcome " et les espaces
                }

                // Démarrer la consommation des messages Kafka dans un thread séparé
                final String cleanedNickname = nickname; // Utiliser un alias final pour le thread
                new Thread(() -> consumeKafkaMessages(cleanedNickname, out)).start();

                // Appel pour démarrer la consommation des messages de channel
                new Thread(() -> consumeChannelMessages("channel-name", out)).start();

                // Lire les messages entrants si nécessaire
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Message reçu de " + cleanedNickname + ": " + message);
                }
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
                return response; // Ne pas ajouter de préfixe
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void consumeKafkaMessages(String nickname, PrintWriter out) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "tcp-server-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(nickname));

        try {
            System.out.println("Started Kafka consumer for nickname: " + nickname);
            while (!clientSocket.isClosed()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    synchronized (out) {
                        out.println("New message: " + record.value());
                        out.flush();
                    }
                    System.out.println("Message sent to " + nickname + ": " + record.value());
                }
            }
        } catch (Exception e) {
            System.err.println("Error consuming Kafka messages for " + nickname + ": " + e.getMessage());
        } finally {
            consumer.close();
            System.out.println("Kafka consumer closed for " + nickname);
        }
    }

    private void consumeChannelMessages(String channelName, PrintWriter out) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "channel-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(channelName));

        try {
            while (!clientSocket.isClosed()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    synchronized (out) {
                        out.println("Message dans le channel " + channelName + ": " + record.value());
                        out.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
