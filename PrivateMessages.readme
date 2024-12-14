Étape 1 : Création d'Utilisateurs
Créez un premier utilisateur sur l'endpoint http://localhost:8080/aller/webapi/register.

Exemple de requête pour le premier utilisateur : { "firstname": "user", "lastname" : "one", "pwd": "pwd1234" } 
Réponse : user-97c75

Créez un deuxième utilisateur sur le même endpoint avec la requête suivante : { "firstname": "user", "lastname" : "two", "pwd": "pwd1234" } 
Réponse : user-711a5

Étape 2 : Connexion des Utilisateurs
Connectez le premier et le deuxième utilisateur sur l'endpoint http://localhost:8080/aller/webapi/login.

Réponse du premier utilisateur : 7cb2560c-f7ef-4795-ad27-331fa0e8f07d

Réponse du deuxième utilisateur : cb122fea-8ebc-4f73-a13a-896f0a977128

Étape 3 : Connexion au Serveur TCP
Connectez les deux utilisateurs dans deux terminaux différents sur le serveur TCP qui écoute sur le port 12345 avec la commande ncat localhost 12345. Au prompt, entrez le token de chaque utilisateur : "Enter your token (or type 'exit' to quit):"

Étape 4 : Lancer Zookeeper et Kafka
Accédez au répertoire Kafka où se trouve le dossier bin, puis lancez le serveur Zookeeper avec la commande suivante : bin\windows\zookeeper-server-start.bat config\zookeeper.properties. Ensuite, lancez le serveur Kafka avec la commande : bin\windows\kafka-server-start.bat config\server.properties.

Étape 5 : Envoi de Messages
Envoyez un message à l'un des utilisateurs connectés sur l'endpoint http://localhost:8080/aller/webapi/send-message.

Exemple de requête pour envoyer un message : { "token": "cb122fea-8ebc-4f73-a13a-896f0a977128", "receiver" : "user-711a5", "message": "hello user-711a5" }

Pour répondre sur le même endpoint : { "token": "7cb2560c-f7ef-4795-ad27-331fa0e8f07d", "receiver" : "user-97c75", "message": "hello user-97c75, j'ai bien reçu ton message" }


