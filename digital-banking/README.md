# Digital Banking — Application de gestion de comptes bancaires

Application full-stack de gestion de comptes bancaires avec authentification JWT, tableau de bord
statistique et assistant conversationnel (chatbot RAG). Projet composé de **deux applications séparées** :

| Dossier | Rôle | Techno |
|---|---|---|
| [`digital-banking`](digital-banking) | API REST | Spring Boot 3.5 / Java 21 |
| [`digital-banking-frontend`](digital-banking-frontend) | Client web | Angular 21 |

## Modèle métier
- Un **client** (`Customer`) possède plusieurs **comptes** (`BankAccount`).
- Un compte est soit **courant** (`CurrentAccount`, avec découvert) soit **épargne** (`SavingAccount`, avec taux d'intérêt).
- Un compte subit des **opérations** (`AccountOperation`) de type `CREDIT` ou `DEBIT` ; un **virement** combine un débit et un crédit.
- Enums : `OperationType {CREDIT, DEBIT}`, `AccountStatus {CREATED, ACTIVATED, SUSPENDED}`.

## Fonctionnalités
- **Gestion des clients** : liste, recherche, ajout, édition, suppression
- **Gestion des comptes** : création (courant/épargne), consultation, comptes par client
- **Opérations** : crédit, débit, virement + **historique paginé**
- **Sécurité JWT** : login, rôles **USER** (lecture seule) et **ADMIN** (accès complet)
- **Audit** : chaque enregistrement conserve l'utilisateur authentifié (`createdBy`)
- **Gestion des utilisateurs** (ADMIN) : liste, création, réinitialisation de mot de passe ; chaque
  utilisateur peut **changer son mot de passe**
- **Dashboard** : KPIs + graphiques Chart.js (répartition des comptes, opérations, soldes, top comptes)
- **Chatbot RAG** (Spring AI + OpenAI) intégré dans l'app via un widget flottant, + bot **Telegram** (opt-in)
- **Documentation API** : Swagger UI

## Comptes de démonstration
| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| `admin` | `admin` | ADMIN, USER |
| `user` | `user` | USER |

---

## Démarrage — Option A : Docker Compose (recommandé)

Prérequis : **Docker** + **Docker Compose**.

```bash
# (optionnel) clé OpenAI pour le chatbot
cp .env.example .env      # puis renseigner OPENAI_API_KEY

docker compose up --build
```

| Service | URL |
|---|---|
| Frontend (Angular + nginx) | http://localhost:8033 |
| Backend (API) | http://localhost:8085 |
| Swagger UI | http://localhost:8085/swagger-ui.html |
| PostgreSQL | localhost:5433 (dans le conteneur : 5432) |

Le frontend nginx relaie `/api` et `/auth` vers le backend (même origine, pas de CORS).
Arrêt : `docker compose down` (ajouter `-v` pour supprimer aussi les données PostgreSQL).

## Démarrage — Option B : en local (sans Docker)

Prérequis : **Java 21**, **Maven**, **Node ≥ 20.19 / 22.12**, **PostgreSQL**.

1. **PostgreSQL** : créer une base `tp_spring_angular_jwt` (user `postgres` / mot de passe `root`),
   ou adapter `digital-banking/src/main/resources/application.properties`.

2. **Backend** :
   ```bash
   cd digital-banking
   # (optionnel) chatbot : créer secret.properties avec OPENAI_API_KEY=... (voir README backend)
   ./mvnw spring-boot:run
   ```
   API sur http://localhost:8085.

3. **Frontend** :
   ```bash
   cd digital-banking-frontend
   npm install
   npm start           # ng serve --proxy-config proxy.conf.json
   ```
   App sur http://localhost:4200 (le proxy relaie `/api` et `/auth` vers le backend).

---

## Chatbot (Spring AI + OpenAI)
Le chatbot répond à partir d'une base de connaissances (RAG). Il nécessite une **clé OpenAI valide** :
- **Local** : fichier `digital-banking/secret.properties` → `OPENAI_API_KEY=sk-...`
- **Docker** : variable `OPENAI_API_KEY` dans le fichier `.env` à la racine

Sans clé valide, l'API `/api/chat` répond mais renvoie un message indiquant que la clé est absente/refusée.
Le bot Telegram est **désactivé par défaut** (voir README backend pour l'activer).

## Sécurité — matrice des rôles
| Action | USER | ADMIN |
|---|:--:|:--:|
| Consulter clients / comptes / opérations / dashboard | ✅ | ✅ |
| Chatbot | ✅ | ✅ |
| Changer **son** mot de passe | ✅ | ✅ |
| Créer / modifier / supprimer clients & comptes, opérations | ❌ | ✅ |
| Gérer les utilisateurs | ❌ | ✅ |

## Documentation détaillée
- Backend : [digital-banking/README.md](digital-banking/README.md)
- Frontend : [digital-banking-frontend/README.md](digital-banking-frontend/README.md)

## Structure du dépôt
```
tp-spring-angular-jwt/
├── docker-compose.yml            Orchestration des 3 services
├── .env.example                  Variables pour docker-compose (copier en .env)
├── digital-banking/              Backend Spring Boot (+ Dockerfile)
└── digital-banking-frontend/     Frontend Angular (+ Dockerfile, nginx.conf)
```

> ⚠️ Ne jamais committer `secret.properties` ni `.env` (déjà gitignorés) : ils contiennent des clés secrètes.
