# Custom Changelog — Intégration & Synchronisation Trakt

Ce document répertorie l'ensemble des modifications personnalisées apportées au code source d'OwnTV. 
Il sert de guide de référence lors des mises à jour du dépôt original (upstream).

---

### 1. Ajout de Trakt.tv
* **Ajout de Trakt** (basique via menu marque comme vu + interception fin de visionnage)

### 2. Améliorations Ergonomiques (QoL / UX TV)
* **Onglet Téléchargements :** Passage de l'onglet *Recordings* en premier dans `DownloadsScreen.kt`.
* **Encodage EPG :** Correction dans le module Core (`XmltvParser.kt` via `BufferedInputStream`) pour forcer la lecture UTF-8 des guides XMLTV.
* **Bouton Back pour effacer la recherche :** Ajout d'un `BackHandler` sur les chaines Live TV, les titres de Films et de Séries pour vider instantanément la recherche active au premier appui.
* **Fermeture instantanée du Rail de Navigation :** Modification de `selectFromNav` dans `OwnTVShell.kt` pour que cliquer OK sur n'importe quel élément du menu sélectionne la section **et** focalise immédiatement sur le contenu sans exiger un clic supplémentaire vers la droite.
* **Personnalisation des flèches Haut/Bas du lecteur Live :** Ajout d'un réglage dans *Settings › Remote shortcuts › Player buttons* permettant de configurer les flèches Haut et Bas en plein écran live : **Zapper** (défaut), **Contrôles & Historique** (Bas = Ouvre le HUD, Haut = Ouvre l'historique *Recently Watched*), ou **Désactivé**.
* **Ordre des actions du menu Playlist :** Réorganisation du sous-menu d'une playlist dans *Settings › Playlists* pour placer **Re-sync** en premier lien (action la plus fréquente).
* **Menu rapide sur les affiches *Now Trending* (Accueil) :** Ajout d'un menu contextuel rapide avec les actions : *Détails*, *Bande-annonce*, *Favoris* et *Télécharger* sur les affiches du mode *Posters only* de la section *Now Trending* sur l'écran d'accueil (`HomeScreen.kt`).
