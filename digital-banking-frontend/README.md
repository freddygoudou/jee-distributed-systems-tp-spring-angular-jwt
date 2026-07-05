# Digital Banking — Frontend Angular (Partie 2)

Client Angular consommant l'API REST du backend `digital-banking`.

## Stack
- Angular 21 (standalone components, signals, control flow `@if/@for`)
- Bootstrap 5 + Bootstrap Icons
- HttpClient + proxy de dev vers le backend

## Prérequis
Le backend doit tourner sur **http://localhost:8085** (voir `../digital-banking`).
Les appels `/api/**` sont redirigés vers le backend via `proxy.conf.json`.

## Lancer
```bash
npm install
npm start        # ng serve --proxy-config proxy.conf.json
```
> Le script `start` sert l'app sur le port par défaut 4200. Si occupé :
> `npx ng serve --port 4300 --proxy-config proxy.conf.json`

Ouvrir http://localhost:4200 (ou 4300).

## Authentification (Partie 3 — JWT)
- **Login** (`/login`) : obtient un JWT stocké en `localStorage`, décodé pour username/rôles
- **Interceptor** : ajoute `Authorization: Bearer <token>` sur les appels `/api` et `/auth/profile`;
  déconnecte et redirige vers `/login` sur `401`
- **Guard** (`authGuard`) : protège toutes les routes métier
- **Navbar** : affiche l'utilisateur + rôle, lien changement de mot de passe, déconnexion
- **Changer mot de passe** (`/change-password`)
- Bouton *Supprimer* client visible uniquement pour le rôle **ADMIN**

Comptes de démo : `admin/admin` (ADMIN) · `user/user` (USER).

## Fonctionnalités
- **Dashboard** (`/dashboard`) : KPIs + graphiques **Chart.js** (répartition des comptes, opérations
  par type, solde par type) + top 5 comptes
- **Chatbot** : widget flottant (bulle en bas à droite) branché sur `/api/chat` (RAG OpenAI côté backend),
  visible une fois connecté — **Partie 5 (intégration du chatbot dans l'app)**
- **Clients** (`/customers`) : liste, recherche par nom, ajout, édition, suppression (ADMIN)
- **Comptes d'un client** (`/customers/:id/accounts`)
- **Comptes** (`/accounts`) : liste de tous les comptes, création de compte courant/épargne
- **Détail compte** (`/accounts/:id`) : infos + solde, historique **paginé** des opérations,
  et opérations **Crédit / Débit / Virement**

## Structure
```
src/app/
  models/banking.model.ts       Interfaces alignées sur les DTOs backend
  services/                     CustomerService, AccountService (HttpClient)
  components/
    customers/                  CRUD clients
    customer-accounts/          Comptes d'un client
    accounts/                   Liste + création de comptes
    account-detail/             Historique paginé + opérations
  app.routes.ts                 Routing
```

> Parties suivantes : sécurité JWT (login, guards, intercepteur), dashboard ChartJS, chatbot RAG.
