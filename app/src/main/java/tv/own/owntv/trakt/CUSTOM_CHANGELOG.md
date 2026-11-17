# Custom Changelog — Intégration & Synchronisation Trakt

Ce document répertorie l'ensemble des modifications personnalisées apportées au code source d'OwnTV afin d'intégrer et de maintenir la synchronisation avec Trakt.tv. Il sert de guide de référence lors des mises à jour du dépôt original (upstream).

---
- Ajout de Trakt (basique via menu marque comme vu + interception fin de visionnage)
- Onglet Téléchargements : Passage de l'onglet Recordings en premier par défaut.
- Encodage EPG (Caractères bizarres / ``) : Diagnostic et correction de l'analyseur XML
- UX Télécommande (Bouton Back pour vider la recherche) : Implémenté sur le Live TV, les Films et les Séries.

## 📦 Fichiers modifiés et créés

### 1. Configuration et Linter Gradle
* **Fichiers :** `safe_literals.txt` & `TraktConfig.kt`
* **Problème résolu :** Le linter interne Gradle (`verifyI18nLiterals`) bloque la compilation lorsque de nouvelles chaînes de caractères techniques non traduites sont introduites.
* **Modifications :**
    * Centralisation des constantes techniques Trakt dans `TraktConfig.kt`.
    * Ajout et nettoyage des entrées correspondantes dans `safe_literals.txt` avec leur nombre exact d'occurrences.
    * Ajustement spécifique pour l'expression `${TraktConfig.TOKEN_PREFIX}$token` dans `TraktManager.kt` (configurée à **2 occurrences**).

---

### 2. ViewModel des Séries (`SeriesViewModel.kt`)
* **Chemin :** `app/src/main/java/tv/own/owntv/features/series/SeriesViewModel.kt`
* **Modifications :**
    1. **Injection du contexte :** Ajout de `private val context: android.content.Context` en premier paramètre du constructeur (nécessaire pour initialiser `TraktManager.getInstance(context)`).
    2. **Synchronisation manuelle :** Appel à `traktManager.markEpisodeAsWatched` et `traktManager.markEpisodeAsUnwatched` en tâche de fond (`Dispatchers.IO`) lors du marquage manuel d'un épisode dans l'interface.
    3. **Synchronisation automatique à la lecture :** Mise à jour de la méthode `saveEpisodeProgressNow()` pour vérifier si la progression atteint le seuil de fin ($\ge 95\%$) via la fonction existante `isEpisodeCompleted()`. Si l'épisode vient d'être complété pour la première fois, il est automatiquement marqué comme vu sur Trakt.

---

### 3. Injection de Dépendances Koin (`AppModule.kt`)
* **Chemin :** `app/src/main/java/tv/own/owntv/di/AppModule.kt`
* **Problème résolu :** L'ajout du paramètre `context` dans `SeriesViewModel` porte le total de ses dépendances à **23 paramètres**. Or, la fonction par réflexion de Koin (`viewModelOf`) est limitée à 22 paramètres maximum, provoquant une erreur de compilation (`None of the following candidates is applicable`).
* **Modification :** Remplacement de `viewModelOf(::SeriesViewModel)` par une déclaration explicite listant nommément les 23 paramètres :
  ```kotlin
  viewModel {
      SeriesViewModel(
          context = get(),
          // ... (les 22 autres dépendances)
      )
  }
  ```

---

## 🔄 Procédure lors d'une mise à jour de l'application (Upstream)
Lorsqu'une nouvelle version du code officiel est récupérée, vérifiez les points de vigilance suivants :

1. **Vérifier le constructeur de `SeriesViewModel.kt` :**
    * Si le développeur original ajoute ou supprime un paramètre dans `SeriesViewModel`, il faudra impérativement mettre à jour la liste des arguments dans `AppModule.kt` (bloc `viewModel { SeriesViewModel(...) }`).
    * Conserver le paramètre `private val context: android.content.Context` ainsi que les appels Trakt dans `markEpisodeWatched`, `markEpisodeUnwatched` et `saveEpisodeProgressNow()`.
2. **Vérifier le linter (`verifyI18nLiterals`) :**
    * Si la mise à jour modifie `safe_literals.txt`, assurez-vous que la section Trakt est toujours présente et que les compteurs d'occurrences correspondent toujours au code.
3. **Tester la compilation :**
    * Lancer `./gradlew verifyI18nLiterals` suivi d'un build complet pour valider l'absence de conflit Koin ou Gradle.

## 🔄 GitHub
* Dès que tu veux sauvegarder et envoyer ton travail sur ton GitHub, utilise tes commandes habituelles : 
  ```
  git add .
  git commit -m "Un message qui explique ta modification"
  git push origin feature/trakt-integration
    ```

* Mettre à jour ton main local avec les nouveautés officielles :
  ```
  git checkout main
  git fetch upstream
  git merge upstream/main
  git push origin main
  ```

* Intégrer ces nouveautés dans ta branche de travail :
  ```
  git checkout feature/trakt-integration
  git merge main
  ```
(Cette commande injecte le nouveau code du créateur directement dans ta branche Trakt. S'il y a un petit conflit sur un fichier que vous avez modifié tous les deux, Android Studio t'aidera à le régler).

* Envoyer ta branche mise à jour sur ton GitHub :
```
git push origin feature/trakt-integration
```