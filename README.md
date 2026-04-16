# TP1 — Couverture du Code par les Tests Unitaires

## Structure du projet

```
src/
├── main/java/org/example/tp1/
│   ├── Palindrome.java
│   ├── Anagram.java
│   ├── BinarySearch.java
│   ├── QuadraticEquation.java
│   ├── RomanNumeral.java
│   └── FizzBuzz.java
└── test/java/tp1/
    ├── LineCoverageTest/      Exo1Test → Exo6Test
    ├── BranchCoverageTest/    Exo1Test → Exo6Test
    └── ConditionCoverageTest/ Exo1Test → Exo6Test
```

---

## Bugs identifiés dans le code fourni par l'énoncé

### Exo 1 — `Palindrome.java`

**Bug :** La boucle `while` incrémente et décrémente les mauvaises variables :
```java
// CODE ORIGINAL (BUGGY)
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
// CODE ORIGINAL (BUGGY)
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

| Exercice | Branch ≡ Condition ? | Raison |
|----------|---------------------|--------|
| Exo 1 — Palindrome    | ✅ Oui | Toutes les conditions (`s==null`, `i<j`, `charAt(i)!=charAt(j)`) sont simples |
| Exo 2 — Anagram       | ❌ Non | `s1==null \|\| s2==null` est une condition **composée** → nécessite tests séparés pour C1 et C2 |
| Exo 3 — BinarySearch  | ✅ Oui | Toutes les conditions sont simples |
| Exo 4 — QuadraticEq.  | ✅ Oui | `a==0`, `delta<0`, `delta==0` : toutes simples |
| Exo 5 — RomanNumeral  | ❌ Non | `n<1 \|\| n>3999` est composée → tester C1=T seul, C1=F+C2=T |
| Exo 6 — FizzBuzz      | ✅ Oui | Toutes les conditions sont simples |

---

## Lancer les tests

```bash
mvn clean test
```

Résultat attendu : **37 tests, 0 failures, 0 errors** (TP1 + TP3)
