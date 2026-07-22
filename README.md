# Blog CMS — Projet 7 : CMS pour blog personnel avec MongoDB

CMS de blog personnel avec gestion des rôles, upload d'images, recherche full-text et statistiques par agrégation. Projet réalisé dans le cadre du cours **Base de Données nouvelle génération**

**Démo en ligne :** http://15.224.72.12

## Stack technique

| Composant | Technologie |
|---|---|
| Frontend | Angular 20 (standalone components), Bootstrap 5 |
| Backend | Spring Boot 4, Spring Security (JWT) |
| Base de données | MongoDB 7 |
| Stockage fichiers | MinIO (compatible S3) |
| Déploiement | Docker Compose, Nginx, AWS Lightsail |

## Fonctionnalités

- Authentification JWT, 3 rôles (`ADMIN`, `AUTHOR`, `READER`)
- CRUD complet des articles avec upload d'images (MinIO)
- Commentaires (ouverts à tout utilisateur connecté)
- Recherche par tag, par auteur, et recherche full-text pondérée (index texte MongoDB)
- Statistiques par agrégation (articles par mois, tags populaires)
- Tableau de bord et gestion des utilisateurs (admin)
- Page "Mes articles" (brouillons inclus) pour les auteurs
```

## Lancer le projet en local

Prérequis : Docker et Docker Compose.

```bash
git clone https://github.com/sntactic/blog-personnel.git
cd blog-personnel
docker compose up -d --build
```

- Frontend : http://localhost
- Backend (direct) : http://localhost:8080
- Console MinIO : http://localhost:9001

## Accès à la base de données

```bash
mongosh "mongodb://admin:admin123@15.224.72.12:27017/blogdb?authSource=admin"
```

Les requêtes CRUD et pipelines d'agrégation testables sont dans `crud_articles.txt`, `crud_users.txt`, `crud_comments.txt` et `aggregation.txt`.

## Compte de test

| | |
|---|---|
| Email | admin@blog.com |
| Mot de passe | db_newgen |

> Projet académique sans données sensibles : identifiants volontairement laissés en clair pour faciliter l'évaluation.

## Documentation

- **Modèle de données JSON** — structure des collections `users`, `articles`, `comments`
- **Rapport sur la flexibilité et les performances** — choix de modélisation, index, pipelines d'agrégation

## Auteur

Khadim MBAYE — Institut Polytechnique de Saint-Louis