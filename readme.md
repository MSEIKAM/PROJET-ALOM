# Projet de Serveur de Tchat à Base de Microservices

## Objectifs

L'objectif de ce projet est de développer un serveur de tchat utilisant trois moyens de communication : les WebServices, les systèmes de messagerie distribuée et les sockets.

## Fonctionnalités

Le serveur de tchat fonctionne de la manière suivante :
- **Microservices** :
    - **Interface "aller"** : Utilise des WebServices pour envoyer des informations au serveur.
    - **Interface "retour"** : Utilise des sockets pour que le serveur envoie des informations aux clients connectés.
    - **Gestion des utilisateurs** : Permet de créer des utilisateurs, de se connecter avec un nickname et un mot de passe, et de recevoir un token d'authentification.
    - **Communication privée** : Permet d'envoyer des messages privés à d'autres utilisateurs.
    - **Gestion des channels** : Permet de rejoindre des channels, d'envoyer des messages à tous les participants d'un channel, et de quitter un channel.

## Structure du Projet

Le projet est divisé en plusieurs microservices, chacun étant un projet Java dédié :

- **aller** : Interface "aller" utilisant des WebServices.
- **retour** : Interface "retour" utilisant des sockets.
- **auth** : Gestion des utilisateurs et authentification.
- **privateMessages** : Gestion des messages privés.

## Prérequis

- Java
- Maven
- Kafka
- Un logiciel de test WebServices (Postman ou SOAP-UI)
- Un logiciel client socket TCP (nc sous Linux ou Putty en mode Raw sous Windows)

## Installation et Utilisation

### Étape 2 : Créer un Compte

Envoyez une requête POST à l'URL `http://localhost:8080/aller/register` avec le payload JSON suivant :

```json
{
        "firstname": "John",
        "lastname": "Doe",
        "pwd": "password123"
}
```

### Se Connecter

Envoyez une requête POST à l'URL `http://localhost:8080/aller/login` avec le payload JSON suivant :

```json
{
        "nickname": "John-Doe",
        "pwd": "password123"
}
```

La réponse contiendra un token d'authentification.

### Envoyer un Message Privé

Envoyez une requête POST à l'URL `http://localhost:8080/aller/send-message` avec le payload JSON suivant :

```json
{
        "token": "<TOKEN>",
        "receiver": "Jane-Doe",
        "message": "Hello Jane!"
}
```

Remplacez `<TOKEN>` par le token d'authentification reçu lors de la connexion.