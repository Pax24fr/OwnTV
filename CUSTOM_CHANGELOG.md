# Custom Changelog — Intégration & Synchronisation Trakt

Ce document répertorie l'ensemble des modifications personnalisées apportées au code source d'OwnTV. 
Il sert de guide de référence lors des mises à jour du dépôt original (upstream).

---

### 1. Ajout de Trakt.tv
* **Ajout de Trakt** (basique via menu marque comme vu + interception fin de visionnage)

### 2. Améliorations Ergonomiques (QoL / UX TV)
* **Fermeture instantanée du Rail de Navigation :** Modification de `selectFromNav` dans `OwnTVShell.kt` pour que cliquer OK sur n'importe quel élément du menu sélectionne la section **et** focalise immédiatement sur le contenu sans exiger un clic supplémentaire vers la droite.

* **Bouton Back pour effacer la recherche :** Ajout d'un `BackHandler` sur les chaines Live TV, les titres de Films et de Séries pour vider instantanément la recherche active au premier appui sur Back.

* **Personnalisation des flèches Haut/Bas du lecteur Live :** Ajout d'un réglage dans *Settings › Remote shortcuts › Player buttons* permettant de configurer les flèches Haut et Bas en plein écran live : **Zapper** (défaut), **Contrôles & Historique** (Bas = Ouvre le HUD, Haut = Ouvre l'historique *Recently Watched*), ou **Désactivé**.

* **Menu rapide sur les affiches *Now Trending* (Accueil) :** Ajout d'un menu contextuel rapide avec les actions : *Détails*, *Bande-annonce*, *Favoris* et *Télécharger* sur les affiches du mode *Posters only* de la section *Now Trending* sur l'écran d'accueil (`HomeScreen.kt`).

* **Ordre des actions du menu Playlist :** Réorganisation du sous-menu d'une playlist dans *Settings › Playlists* pour placer **Re-sync** en premier lien (action la plus fréquente).

* **Onglet Téléchargements :** Passage de l'onglet *Recordings* en premier dans `DownloadsScreen.kt`.

* **Raccourcis télécommande "Toggle favorite" & "Record or download" (`TOGGLE_FAVORITE` & `INSTANT_RECORD_DOWNLOAD`) :** 
  * Ajout d'actions configurables dans *Settings › Layout › Remote shortcuts*.
  * **Toggle favorite** : Permet d'assigner une touche pour basculer instantanément le statut de favori du contenu actif (chaîne Live, film, série, ou élément *Now Trending*). Une notification apparaît brièvement en bas de l'écran avec le texte de confirmation (Ajouté/Retiré + [Nom]).
  * **Record or download** : Permet d'assigner une touche dédiée (ex: la touche *RECORD*) pour démarrer ou stopper instantanément l'enregistrement d'une chaîne Live (avec vérification du budget de connexions et affichage du motif de refus en cas de dépassement), ou lancer le téléchargement d'un film ou d'une série en VOD.

* **Indicateurs de playlist unifiés (Logos de catégories & Points de grille VOD) :** 
  * Uniformisation de l'affichage des badges de playlist (`StagePlaylistMark`) sur toutes les catégories (Live TV, Movies, Series) en mode feuille (`sheet`) et colonne (`column`).
  * Ajout d'un indicateur visuel (point coloré aux couleurs de la playlist) en tête de titre sous les affiches en mode grille VOD (Films et Séries) pour identifier instantanément la playlist d'origine de chaque titre.

* **Focus Mini-player depuis le Guide TV :** Un premier appui `OK` sur une autre chaîne change la lecture dans le mini-player. Un second appui sur `OK` bascule immédiatement le focus sur les contrôles du mini-player (pour l'agrandir en plein écran ou le fermer). L'appui sur `BACK` depuis le mini-player remet le focus sur la ligne du guide.

* **Encodage EPG :** Correction dans le module Core (`XmltvParser.kt` via `BufferedInputStream`) pour forcer la lecture UTF-8 des guides XMLTV.


