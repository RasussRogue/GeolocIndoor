## Création d'un bâtiment

1) Aller sur la page web d'administration

2) Télécharger un fichier de template vide de bâtiment en cliquant sur le bouton de droite

![sc-admin-import-building](https://perso-etudiant.u-pem.fr/~gallaman/geolocindoor/img/sc-admin-import-building.png)

3) Lancer QGIS et créer une connexion au fichier sqlite

*  Créer une nouvelle datasource en cliquant sur l'icone 'Datasource' ![sc-datasource-icon](https://perso-etudiant.u-pem.fr/~gallaman/geolocindoor/img/sc-datasource-icon.png)

*  Créer une nouvelle connexion '**Spatialite**', en sélectionnant le fichier téléchargé à l'étape précédente

![sc-qgis-spatialite-datasource](https://perso-etudiant.u-pem.fr/~gallaman/geolocindoor/img/sc-qgis-spatialite-datasource.png)

En cliquant sur le bouton '**Connecter**' (avec la case '**Lister les tables sans géométries**' cochées), les couches disponibles devraient être affichées comme sur l'image ci-dessus

4) Ajouter/créer les données du bâtiment dans QGIS

*  Se connecter au datasource Spatialite précédemment créé

*  Cocher la case 'Lister les tables sans géométries'

*  Ajouter au projet les 6 couches présentes dans le fichier spatialite (beacon, floor, floorbound, floorwalls, graphedge et poi)

Les données peuvent ensuite être ajoutées aux couches, de préférence dans l'ordre qui suit (les étages/floors doivent être renseignés en premier). Bien penser à sauvegarder les couches une fois modifiées.

   1. **Couche floor** : cette couche représente les différents étages du bâtiment. Elle ne contient pas de géométries, par conséquent les données doivent être ajoutées dans la table attributaire (clic droit sur la couche + 'Ouvrir la table d'attributs'). Voici les différents attributs d'une entité 'floor' et leur signification :

| nom de l'attribut | type | signification |
| ------ | ------ | ------ |
| id | nombre entier | identifiant unique de l'étage<br>Garder en mémoire cet identifiant, qui sera nécessaire plus tard pour renseigner des éléments relatifs à l'étage. Il est ainsi recommandé de commencer par 0 puis d'incrémenter de 1 pour des raisons de facilité |
| level | nombre entier positif | nombre indiquant le niveau relatif de l'étage<br>**Attention**: ce nombre **doit** commencer à 0 pour l'étage le plus bas et s'incrémenter de 1 pour chaque étage<br>Exemple: un bâtiment créé contient 3 étages (R-1, RDC et R+2) ; les 3 entités floors doivent avoir comme level 0 (pour le R-1 soit le plus bas), 1 (pour le RDC soit l'intermédiaire) et 2 (pour le R+1 soit le plus haut)|
| name | chaîne de caractères | nom usuel de l'étage (RDC pour l'étage 0 par exemple) |

   2. **Couche floorbound** : cette couche représente les emprises des étages du bâtiment. Il s'agit d'entités de type MultiPolygon, qui peuvent être dessinées dans la carte de QGIS ou ajoutées via d'autres procédures de géotraitement de QGIS. Voici les différents attributs d'une entité 'floorbound' et leur signification :

| nom de l'attribut | type | signification |
| ------ | ------ | ------ |
| id | nombre entier | identifiant unique de l'entité<br>Cette valeur n'est pas importante, et la génération automatique d'identifiants de QGIS (par défaut) peut être utilisée |
| floor_id | nombre entier | identifiant de l'étage qui possède l'emprise<br>**Attention**: le nombre renseigné dans le champ floor_id ne peut pas être vide et doit référencer l'id de l'entité dans la couche floor (même valeur pour 'id' de l'entité 'floor' que 'floor_id' de l'entité 'floorbound') |


   3. **Couche floorwalls** : cette couche représente les emprises des étages du bâtiment. Il s'agit d'entités de type MultiPolygon, qui peuvent être dessinées dans la carte de QGIS ou ajoutées via d'autres procédures de géotraitement de QGIS. Voici les différents attributs d'une entité 'floorwalls' et leur signification :

| nom de l'attribut | type | signification |
| ------ | ------ | ------ |
| id | nombre entier | identifiant unique de l'entité<br>Cette valeur n'est pas importante, et la génération automatique d'identifiants de QGIS (par défaut) peut être utilisée |
| floor_id | nombre entier | identifiant de l'étage qui possède l'emprise<br>**Attention**: le nombre renseigné dans le champ floor_id ne peut pas être vide et doit référencer l'id de l'entité dans la couche floor (même valeur pour 'id' de l'entité 'floor' que 'floor_id' de l'entité 'floorwalls') |

   4. **Couche poi** : cette couche représente les Points Of Interest présents dans le bâtiment. Il s'agit d'entités avec des géométries de type Point. Il existe deux différents types de POI :

*  Les POIs dits 'visible', qui correspondent à des lieux précis. Ces POIs sont affichés sur la carte dans l'application mobile, et des événements peuvent potentiellement se dérouler dans ces POIs
* Les POIs dit 'basic', qui sont des simples points de passage possibles au sein du bâtiment. Ces POIs ne sont pas affichés sur la carte, et servent uniquement à représenter un noeud du graphe des déplacements possibles, utilisé pour le pathfinding.

Voici les différents attributs d'une entité 'poi' et leur signification :

| nom de l'attribut | type | signification |
| ------ | ------ | ------ |
| id | nombre entier | identifiant unique du POI<br>Garder en mémoire cet identifiant, qui sera nécessaire plus tard pour renseigner les arêtes (graphedge) du graphe de pathfinding. Il est ainsi recommandé de commencer par 0 puis d'incrémenter de 1 pour des raisons de facilité     |
| type_poi          | chaîne de caractères | indique le type du poi<br>**Attention**: cette valeur ne doit pas être vide et ne peut être différente de 'visible' ou 'basic' |
| title | chaîne de caractères | Uniquement pour les POIs du type 'visible' : indique le libellé du POI |
| type | chaîne de caractères | Uniquement pour les POIs du type 'visible' : indique le type du POI, parmi les valeurs suivantes : 'stairs', 'lift', 'food', 'teaching', 'admin', 'wc' |
| floor_id | nombre entier | identifiant de l'étage qui possède le POI<br>**Attention**: le nombre renseigné dans le champ floor_id ne peut pas être vide et doit référencer l'id de l'entité dans la couche floor (même valeur pour 'id' de l'entité 'floor' que 'floor_id' de l'entité 'poi') |

   5. **Couche graphedge** : cette couche représente les arrêtes du graphe des déplacements possibles dans le bâtiment, utilisé par l'application mobile pour le pathfinding. Les entités de cette table ne possèdent pas de géométries, par conséquent les données doivent être ajoutées dans la table attributaire (clic droit sur la couche + 'Ouvrir la table d'attributs'). Voici les différents attributs d'une entité 'graphedge' et leur signification :

| nom de l'attribut | type | signification |
| ------ | ------ | ------ |
| id | nombre entier | identifiant unique de l'entité<br>Cette valeur n'est pas importante, et la génération automatique d'identifiants de QGIS (par défaut) peut être utilisée |
| start_poi | nombre entier | identifiant du poi représentant la première extrémité de l'arrête du graphe<br>**Attention** : la valeur ne peut pas être vide et doit référencer le champ 'id' de l'entité correspondante de la couche 'poi' |
| end_poi | nombre entier | identifiant du poi représentant la deuxième extrémité de l'arrête du graphe<br>**Attention** : la valeur ne peut pas être vide et doit référencer le champ 'id' de l'entité correspondante de la couche 'poi' |
| value | nombre décimal | valeur représentant la distance en mètre entre les deux POIs que relie l'arrête. Il est possible d'utiliser l'outil distances de QGIS pour récupérer la valeur |

*Attention* : si le bâtiment contient plusieurs étages, ne pas oublier de créer les graphedges reliant les POIs liant les différents étages (escaliers ou ascenseurs)

   6. **Couche beacon** : cette couche contient les informations relatives aux beacons installés dans le bâtiment. Les emplacements sont représentés par des géométries de type Point. Les beacons doivent provenir du fabricant Kontakt.io, avec un accès à la [plateforme cloud de Kontakt.io](https://panel.kontakt.io/app/dashboard). Voici les différents attributs d'une entité 'beaocn' et leur signification :


| nom de l'attribut | type | signification |
| ------ | ------ | ------ |
| id | nombre entier | identifiant unique de l'entité<br>Cette valeur n'est pas importante, et la génération automatique d'identifiants de QGIS (par défaut) peut être utilisée |
| uuid | chaîne de caractères de longueur 4 | identifiant Kontakt.IO du beacon, qui peut être lue sur le sticker présent sur le beacon, ou sur l'application mobile de Kontakt.io |
| height | nombre décimal | hauteur en mètres à laquelle est installé le beacon, par rapport à l'étage. Si le beacon est accroché au plafond, la hauteur renseignée doit être celle du plafond, soit la distance entre le sol et le plafond |
| floor_id | nombre entier | identifiant de l'étage qui possède le beacon<br>**Attention**: le nombre renseigné dans le champ floor_id ne peut pas être vide et doit référencer l'id de l'entité dans la couche floor (même valeur pour 'id' de l'entité 'floor' que 'floor_id' de l'entité 'beacon') |



## Import du bâtiment dans la base

1) Aller sur la page web d'administration

2) Dans la partie haute de l'écran, saisir le nom du bâtiment à importer (champ 'New building name')

3) Choisir le fichier .sqlite précédemment modifié qui contient les données du bâtiment à importer (champ 'Upload file')
 
4) Cliquer sur le bouton 'Create' pour importer le bâtiment dans la base de données