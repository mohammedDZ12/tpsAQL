# TP1 — Couverture du Code par les Tests Unitaires

## Bugs identifiés dans le code fourni par l'énoncé

### Exo 1 — `Palindrome.java`

**Bug :** La boucle `while` incrémente et décrémente les mauvaises variables :

```java
// CODE ORIGINAL
j++;  // devrait être j--
i--;  // devrait être i++
```

**Effet :** Boucle infinie ou `StringIndexOutOfBoundsException`.  
**Correction :** Intervertir les opérations → `i++; j--;`  
**Classe de correction :** `PalindromeCorrection.java`

---

### Exo 2 — `Anagram.java`

**Bug :** La boucle `for` utilise `i <= s1.length()` au lieu de `i < s1.length()` :

```java

for (int i = 0; i <= s1.length(); i++) {
```

**Effet :** `StringIndexOutOfBoundsException` à l'index `s1.length()` (hors limites).  
**Correction :** `i < s1.length()`  
**Classe de correction :** `AnagramCorrection.java`

---

### Exo 3 — `BinarySearch.java`

**Bug :** La condition du `while` est `low < high` au lieu de `low <= high` :

```java
// CODE ORIGINAL (BUGGY)
while (low < high) {
```

**Effet :** L'élément n'est pas trouvé quand il reste un seul candidat (`low == high`).  
**Correction :** `while (low <= high)`  
**Classe de correction :** `BinarySearchCorrection.java`

---

### Exo 5 — `RomanNumeral.java`

**Bug 1 :** Boucle `for` avec `i <= symbols.length` → `ArrayIndexOutOfBoundsException` :

```java
// CODE ORIGINAL (BUGGY)
for (int i = 0; i <= symbols.length; i++) {
```

**Correction :** `i < symbols.length`

**Bug 2 :** Condition `n > values[i]` → les valeurs exactes (1, 5, 10...) ne sont jamais soustraites → résultat vide :

```java
// CODE ORIGINAL (BUGGY)
while (n > values[i]) {
```

**Correction :** `while (n >= values[i])`  
**Classe de correction :** `RomanNumeralCorrection.java`

---

### Exo 6 — `FizzBuzz.java`

**Bug :** La garde invalide `n = 1` qui est un nombre FizzBuzz valide (devrait retourner `"1"`) :

```java
// CODE ORIGINAL (BUGGY)
if (n <= 1) {
    throw new IllegalArgumentException("n must be positive");
}
```

**Effet :** `fizzBuzz(1)` lève une exception alors qu'il devrait retourner `"1"`.  
**Correction :** `if (n < 1)` (ou supprimer la garde pour n=1)  
**Classe de correction :** `FizzBuzzCorrection.java`

---

## Observations sur l'équivalence des critères de couverture

Pour certains exercices, les tests de couverture des **branches** et des **conditions** sont **identiques**, car chaque `if` est contrôlé par une seule condition booléenne simple (non composée).

| Exercice             | Branch ≡ Condition ? | Raison                                                                                          |
| -------------------- | -------------------- | ----------------------------------------------------------------------------------------------- |
| Exo 1 — Palindrome   | Oui                  | Toutes les conditions (`s==null`, `i<j`, `charAt(i)!=charAt(j)`) sont simples                   |
| Exo 2 — Anagram      | Non                  | `s1==null \|\| s2==null` est une condition **composée** → nécessite tests séparés pour C1 et C2 |
| Exo 3 — BinarySearch | Oui                  | Toutes les conditions sont simples                                                              |
| Exo 4 — QuadraticEq. | Oui                  | `a==0`, `delta<0`, `delta==0` : toutes simples                                                  |
| Exo 5 — RomanNumeral | Non                  | `n<1 \|\| n>3999` est composée → tester C1=T seul, C1=F+C2=T                                    |
| Exo 6 — FizzBuzz     | Oui                  | Toutes les conditions sont simples                                                              |

---

## TP3 Partie 2 : Docker, Testcontainers et Spring Boot

### Exercice 1 : Testcontainers pour les DAO/Repository
- **UserRepository (ex1)** : Remplacement des tests par des tests sur une vraie base MySQL via `@Testcontainers`.
- **OrderDao (ex2)** : Implémentation JDBC testée en conditions réelles avec `MySQLContainer`.
- Code source dans les packages respectifs (`org.example.tp3.ex1`/`ex2` et dans `src/test/java/tp3`).

### Exercice 2 : Application Task Manager (Testcontainers avec Spring Boot)

**1. Analyse des tests existants :**
- **Tests identifiés :** `TaskManagerApplicationTests` et les tests des Web controllers (`TaskControllerTest`, etc.).
- **Approche utilisée :** L'approche utilisée pour l'intégration est `@SpringBootTest` avec un mock global au niveau des WebMvc (en utilisant `@WebMvcTest` ou `MockMvcBuilders.standaloneSetup()`). Les services sont simulés (`@Mock` ou `@MockBean`), il s'agit donc principalement de tests web unitaires, et non de véritables tests d'intégration du flow complet. De plus, le projet charge `com.h2database:h2` en memory-database pour exécuter le contexte Spring.
- **Limitations :** Utiliser des bases de données en mémoire comme H2 ou des mocks de services masque les vraies erreurs de syntaxe SQL (qui pourraient, par exemple, dépendre du dialecte MySQL/PostgreSQL) ou les problèmes transactionnels, ce qui réduit considérablement la fiabilité; la couverture réelle sur l'intégration entre le code et le SGBD de production est nulle.

**2. Tests d'intégration réécrits avec Testcontainers :**
- Afin de garantir une haute fiabilité, un `TaskControllerIntegrationTest` a été créé dans le projet `task-manager`. Ce test déploie un `MySQLContainer` avec Testcontainers pour avoir la vraie base de données.

**3. Analyse et comparaison :**
- **Comparaison :** L'utilisation de Testcontainers permet de se rapprocher d'un environnement "Production-Ready". La lisibilité reste correcte, même si les tests sont plus longs à s'exécuter à cause du démarrage du conteneur.
- **Avantages de Testcontainers :** Environnement 100% conforme à l'endroit où tournera l'application. Fiabilité accrue.
- **Inconvénients :** Temps d'exécution rallongé. Nécessite l'installation et le démarrage de Docker sur la machine exécutant les tests CI/CD/locaux.
