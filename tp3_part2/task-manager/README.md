# Task Manager — TP3 Part 2 : Tests d'intégration avec Testcontainers

Application Spring Boot de gestion de tâches adaptée pour les tests d'intégration avec **Docker** et **Testcontainers**.

---

## 🚀 Fonctionnalités

| Rôle | Capacités |
|------|-----------|
| **Admin (manager)** | Créer/modifier/supprimer des tâches, gérer les utilisateurs, assigner des tâches |
| **Utilisateur (employee)** | Créer ses propres tâches, visualiser, marquer complété/non-complété |
| **Tout utilisateur connecté** | Consulter son profil |

---

## 🛠️ Stack technique

- **Spring Boot 2.1.5** — Framework web & IoC
- **Spring Data JPA / Hibernate** — ORM
- **Spring Security** — Authentification & autorisation
- **Thymeleaf** — Moteur de templates
- **H2** — Base de données embarquée (production/dev)
- **MySQL 8.0** — Base de données réelle injectée par Testcontainers lors des tests
- **Testcontainers 1.19.7** — Gestion de conteneurs Docker pour les tests
- **Maven** — Outil de build
- **Bootstrap 4 / jQuery** — Interface utilisateur

---

## 👤 Utilisateurs de test

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| `manager@mail.com` | `112233` | Admin |
| `ann@mail.com`     | `112233` | User  |
| `mark@mail.com`    | `112233` | User  |

---

## 🧪 Exercice 2 — Analyse et réécriture des tests avec Testcontainers

### 1. Analyse des tests existants

#### Approches utilisées dans le projet original

Le projet original (GitHub: rengreen/task-manager) utilisait **deux approches** pour les tests :

| Fichier | Approche | Niveau |
|---------|----------|--------|
| `TaskControllerTest.java` | **MockMvc + Mockito** (mock du service) | Unitaire |
| `TaskServiceImplTest.java` | **Mockito** (mock du repository) | Unitaire |
| `UserServiceImplTest.java` | **Mockito** (mock du repository) | Unitaire |
| `IndexControllerTest.java` | **MockMvc standalone** | Unitaire |
| `LoginControllerTest.java` | **MockMvc standalone** | Unitaire |
| `RegisterControllerTest.java` | **MockMvc standalone** | Unitaire |
| `StaticPageControllerTest.java` | **MockMvc standalone** | Unitaire |
| `TaskManagerApplicationTests.java` | **Spring Boot Test H2** | Intégration légère |

#### Limitations observées

1. **Absence de tests d'intégration réels** : tous les tests existants utilisent des mocks (Mockito) ou une base H2 en mémoire, ce qui ne valide pas le comportement avec une vraie base relationnelle.
2. **H2 ≠ MySQL** : H2 supporte un mode de compatibilité MySQL, mais il existe des divergences de comportement (clés étrangères, types de données, contraintes d'unicité, dialectes SQL) qui peuvent masquer des bugs.
3. **Isolation insuffisante** : les tests avec H2 partagent parfois un état entre eux, rendant les résultats dépendants de l'ordre d'exécution.
4. **Couverture fonctionnelle limitée** : le test de service ne couvre que `findAll()`, laissant de côté `createTask()`, `deleteTask()`, `updateTask()`, `setTaskCompleted()`, `assignTaskToUser()`, etc.
5. **Pas de test de repository** : aucune vérification directe que Spring Data JPA génère les requêtes correctement en base réelle.

---

### 2. Tests d'intégration réécrits avec Testcontainers

#### Fichiers créés

| Classe | Couche testée | Scénarios |
|--------|--------------|-----------|
| `TaskControllerIntegrationTest.java` | Service (via couche service + MySQL réel) | Create, GetById, Delete, FindAll, SetCompleted, Update, FreeTasks |
| `TaskRepositoryIntegrationTest.java` | Repository (Spring Data JPA) | Save, FindById, FindById unknown, DeleteById, FindAll, Update, Count |
| `UserServiceIntegrationTest.java` | Service utilisateur (MySQL réel) | CreateUser+BCrypt, GetByEmail, IsEmailPresent, GetById, FindAll, Assign/Unassign |

#### Nouveaux scénarios ajoutés (non présents dans les tests originaux)

> Ces scénarios sont marqués **(Nouveau)** dans les fichiers de test.

| Scénario | Classe | Justification |
|----------|--------|---------------|
| `testFindAllTasks` | `TaskControllerIntegrationTest` | Vérifie la persistance multiple en base réelle |
| `testSetTaskCompleted` | `TaskControllerIntegrationTest` | Valide le flag `isCompleted` en base MySQL |
| `testUpdateTask` | `TaskControllerIntegrationTest` | Vérifie la mise à jour effective en base (sans mock) |
| `testFindFreeTasks` | `TaskControllerIntegrationTest` | Vérifie le filtre métier sur les tâches sans owner |
| `TaskRepositoryIntegrationTest` (entier) | Repository | Valide la couche JPA directement sur MySQL |
| `testCreateUser_shouldPersistWithEncodedPassword` | `UserServiceIntegrationTest` | Vérifie que BCrypt encode bien le mot de passe |
| `testIsUserEmailPresent_existingEmail_shouldReturnTrue` | `UserServiceIntegrationTest` | Vérifie la détection d'unicité d'e-mail |
| `testAssignAndUnassignTask` | `UserServiceIntegrationTest` | Valide la relation Task–User en base réelle |

---

### 3. Comparaison Tests originaux vs Tests Testcontainers

| Critère | Tests originaux (Mock/H2) | Tests Testcontainers (MySQL Docker) |
|---------|--------------------------|-------------------------------------|
| **Fidélité** | ⚠️ Moyenne — H2 ≠ MySQL | ✅ Haute — MySQL identique à prod |
| **Vitesse** | ✅ Rapide (pas de démarrage Docker) | ⚠️ Plus lent (pull image + démarrage conteneur ~5-15s) |
| **Isolation** | ⚠️ Partielle — H2 partagée | ✅ Totale — schéma recréé par `create-drop` |
| **Reproductibilité** | ✅ Bonne | ✅ Très bonne — version Docker fixée |
| **Couverture** | ⚠️ Partielle — surtout layer Mockito | ✅ Complète — service + repo + SQL réel |
| **Détection de bugs SQL** | ❌ Non (mock ou H2) | ✅ Oui — dialecte MySQL identique |
| **Facilité de setup** | ✅ Simple — aucune dépendance externe | ⚠️ Requiert Docker installé |
| **Lisibilité** | ✅ Tests courts et simples | ✅ Tests explicites avec commentaires |
| **Maintenabilité** | ⚠️ Mocks peuvent désynchroniser du vrai comportement | ✅ Toujours en phase avec la vraie DB |

---

### 4. Avantages et inconvénients de Testcontainers

#### ✅ Avantages

- **Environnement identique à la production** : même version MySQL, mêmes contraintes SQL.
- **Isolation parfaite** : chaque classe de test dispose de son propre conteneur et schéma.
- **Pas de configuration serveur externe** : le conteneur est démarré automatiquement par l'annotation `@ClassRule`.
- **Détection de vrais bugs d'intégration** : problèmes de contraintes FK, encodage, index uniques.
- **Réutilisable en CI/CD** : fonctionne sur n'importe quelle machine avec Docker.

#### ⚠️ Inconvénients

- **Temps d'exécution plus long** : pull de l'image MySQL (~200 MB la 1ère fois) + démarrage JVM Spring (~15-30s par classe).
- **Dépendance à Docker** : Docker doit être installé et disponible (daemon démarré).
- **Consommation de ressources** : chaque conteneur utilise de la RAM et du CPU pendant les tests.
- **Complexité de setup initial** : nécessite `TestPropertySourceUtils` ou `@DynamicPropertySource` pour injecter l'URL dynamique.

---

## ▶️ Exécution des tests

### Prérequis

- Java 8+
- Maven 3.x
- **Docker Desktop** en cours d'exécution

### Lancer tous les tests

```bash
cd tp3_part2/task-manager
mvn test
```

### Lancer uniquement les tests d'intégration Testcontainers

```bash
mvn test -Dtest="TaskControllerIntegrationTest,TaskRepositoryIntegrationTest,UserServiceIntegrationTest"
```

### Lancer uniquement les tests unitaires

```bash
mvn test -Dtest="TaskServiceImplTest,UserServiceImplTest,RoleServiceImplTest,TaskControllerTest,IndexControllerTest,LoginControllerTest,RegisterControllerTest,StaticPageControllerTest"
```

---

## 📁 Structure des tests

```
src/test/java/pl/rengreen/taskmanager/
├── TaskManagerApplicationTests.java          # Test de chargement du contexte Spring
├── TaskControllerIntegrationTest.java        # Tests d'intégration service (Testcontainers)
├── TaskRepositoryIntegrationTest.java        # Tests d'intégration repository (Testcontainers)
├── UserServiceIntegrationTest.java           # Tests d'intégration utilisateurs (Testcontainers)
├── controller/
│   ├── IndexControllerTest.java              # Test unitaire MockMvc
│   ├── LoginControllerTest.java              # Test unitaire MockMvc
│   ├── RegisterControllerTest.java           # Test unitaire MockMvc
│   ├── StaticPageControllerTest.java         # Test unitaire MockMvc
│   └── TaskControllerTest.java              # Test unitaire Mockito + MockMvc
└── service/
    ├── RoleServiceImplTest.java              # Test unitaire Mockito
    ├── TaskServiceImplTest.java              # Test unitaire Mockito
    └── UserServiceImplTest.java             # Test unitaire Mockito
```

---

## 📚 Références

- [Testcontainers Java Documentation](https://java.testcontainers.org/)
- [Spring Boot Testing Guide](https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/html/boot-features-testing.html)
- [Docker Hub – MySQL](https://hub.docker.com/_/mysql)
- [Original project](https://github.com/rengreen/task-manager)
