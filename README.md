# ![Logo IUT Aix-Marseille](https://raw.githubusercontent.com/IUTInfoAix-M2105/Syllabus/master/assets/logo.png) Bases de données avancées

## IUT d’Aix-Marseille – Département Informatique Aix-en-Provence

* **Cours:** [M3106](http://cache.media.enseignementsup-recherche.gouv.fr/file/25/09/7/PPN_INFORMATIQUE_256097.pdf)
* **Responsable:** [Sébastien NEDJAR](mailto:sebastien.nedjar@univ-amu.fr)
* **Enseignants:** [Sébastien NEDJAR](mailto:sebastien.nedjar@univ-amu.fr)
* **Besoin d'aide ?**
  * Consulter et/ou créér des [issues](https://github.com/IUTInfoAix-M3106/TutoJdbc/issues).
  * [Email](mailto:sebastien.nedjar@univ-amu.fr) pour une question d'ordre privée, ou pour convenir d'un rendez-vous physique.

## TP découverte de JDBC et Création du couche de persistance [![Java CI](https://github.com/IUTInfoAix-M3106/TpJdbc/actions/workflows/mvn_build.yml/badge.svg)](https://github.com/IUTInfoAix-M3106/TpJdbc/actions/workflows/mvn_build.yml)

Tp d'initiation à JDBC donné aux étudiants de deuxième année du DUT Informatique d'Aix-Marseille. En plus d'apprendre à utiliser l'API JDBC, l'objectif de ce TP est de sensibiliser les étudiants à la difficulté de de construction et de maintenance manuelle d'une couche de persistance.

Ce document est diffusé sous licence Creative Common CC-BY-SA.

L’objectif de ce document est de présenter une méthode d’accès à un SGBD à travers le langage de programmation Java. Pour cela, l’API JDBC ([Java DataBase Connectivity](https://en.wikipedia.org/wiki/Java_Database_Connectivity)) sera utilisée. C’est un ensemble de classes permettant d’exécuter des ordres SQL de manière générique. L’API JDBC est construite autour de pilotes (Driver) interchangeables. Un pilote est un module logiciel dédié à une source de données tabulaires (un SGBD Relationnel dans la plupart des cas). Pour utiliser comme source de données une base MySQL au lieu d’une base Oracle, il suffit de de remplacer le pilote Oracle par celui de MySQL. Ce changement de pilote peut se faire directement par paramétrage sans même avoir besoin changer le code ni même le recompiler (Il faut tout de même pondérer ces avantages car dans la pratique il existe de très nombreuses incompatibilités liées à des implémentations du langage SQL non respectueuses des standards).

Comme indiqué dans le cours, l'un des principal défaut que l’on peut reprocher à JDBC est d’être une API de bas niveau qui conduit à une trop forte imbrication entre le code métier et la base de données. Le code produit est donc peu modulaire et trop dépendant du SGBD choisit. Cela implique une moins grande maintenabilité et une plus grande dépendance face à une technologie de manipulation des données. Pour contourner cette difficulté à petite échelle, nous allons construire une couche dédiée à l’accès aux données. La construction d’une telle couche a pour objectif d'isoler les accès aux données du code de notre application. Les techniques présentées constituent une première introduction prototypale aux solutions de persistance Objets/Relationnelle comme [Hibernate][1] ou [EclipseLink][2].

Pour illustrer ce propos, nous utiliserons la base de données « [Gestion Pédagogique][3] » que vous avez utilisée lors de vos TP de PL/SQL.

## Création de votre fork du TP

La première chose que vous allez faire est de créer un fork d'un dépôt. Pour ce faire, rendez-vous sur le lien suivant :

[https://classroom.github.com/a/gVF_Kllt](https://classroom.github.com/a/gVF_Kllt)

Comme pour les TP d'IHM, GitHub va vous créer un dépôt contenant un fork du dépôt 'IUTInfoAix-m3106/TpJdbc' et s'appellant 'IUTInfoAix-m3106-2021/TpJdbc-votreUsername'. Vous apparaîtrez automatiquement comme contributeur de ce projet pour y pousser votre travail.

Une fois votre fork créé, il suffit de l'importer dans un IDE pour faire le TP. La réalisation du TP étant notée, veillez à pousser vos modifications régulièrement.

## Ouvrir votre projet avec Gitpod

Si vous n'êtes pas certain de pouvoir disposer d'un environnement correctement configuré, vous pouvez ouvrir le dépôt dans l'IDE en ligne mis à disposition avec l'outil Gitpod.

Gitpod est un outil permettant de créer des environnements de développement éphémères. Ces environnements permettent aux développeurs de disposer à chaque instant, d'un IDE prêts avec tous les outils et dépendances pré-paramétrés.

Si vous avez installé l'extension navigateur pour Gitpod lors du précédent tutoriel, vous pouvez ouvrir votre environnement directement en cliquant sur le boutons `Gitpod` qui s'est ajouté à la page web principale de votre dépôt Github. Si ce n'est pas le cas, vous pouvez démarrer votre  espace de travail en préfixant l'URL du référentiel git par [https://gitpod.io/#](https://gitpod.io/#).

## Couche de persistance

L’API JDBC permet de récupérer et manipuler un ensemble de tuples récupéré à partir d’une base de données. Chaque tuple n’est pas simplement une concaténation de valeurs sans rapport les unes avec les autres mais un ensemble de valeurs structuré permettant de modéliser une « entité » de l’univers réel. Lorsqu’un tuple est récupéré à partir de JDBC, il faut donc impérativement conserver ce lien sémantique existant entre les attributs. C’est pour cela que chaque tuple de la base de données devra être associé (mappé) à un objet du langage de programmation. Une fois le mapping établi, l’objet commence son existence autonome comme n’importe quel autre objet de l’application. Son état (ensemble des valeurs de ses propriétés) sera très probablement mis à jour. Afin que ces changements soient visibles pour les autres utilisateurs, il faudra périodiquement synchroniser l’état de l’objet et de la base de données. Une fois les modifications sauvegardées, l’objet pourra être détruit car l’utilisateur peut à tout moment reconstruire un objet semblable à partir de la base de données. Le mapping objet/relationnel permet ainsi de rendre les objets de l’application persistants.

Dans la suite de cette section nous allons montrer une méthode pour créer un tel mapping. La solution présentée est principalement pédagogique : elle ne sera en conséquence pas satisfaisante pour une solution à plus grande échelle, mais sera amplement suffisante pour le développement d’une application mono-utilisateur.

### Connexion à la base de données

Recréer une connexion pour chaque requête est inutile et coûteux. Pour éviter cela, il faut partager la connexion entre plusieurs traitements. La problématique est de savoir quelles sont les requêtes à exécuter ensemble. La solution classique est d’exécuter au sein d’une même connexion tous les traitements concourant à la réalisation d’un même objectif. Chacun de ces ensembles de traitements constitue une session. Une même session peut contenir plusieurs transactions [unités de traitement indivisibles][4]. Tout ceci permet de gérer efficacement et intelligemment les problèmes de concurrence et de reprise après erreur. Réaliser une gestion réaliste des connexions, sessions et transactions demande un travail important que nous n’avons pas les moyens fournir. Notre application étant simple et mono-utilisateur nous utiliserons la méthode dite *« session-per-application »*, c’est à dire qu’il n’y aura qu’une seule connexion active à la fois et tous les objets devront se la partager.

Le [pattern singleton][5] est mis en œuvre pour que tous les objets de notre application puissent récupérer l’unique instance de la classe `Connection`.

#### Question 1

Écrire la classe `ConnexionUnique` dont le diagramme UML vous est donné ci-dessous. Copier la classe `ExempleJDBC` dans la nouvelle classe `ExempleConnexion`. Modifier le code de cette nouvelle classe pour qu’elle utilise un objet `ConnexionUnique`.

![Diagramme de la classe ConnexionUnique](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/ConnexionUnique.puml)

Ce diagramme est généré avec l'outil PlantUML. La convention graphique des schémas UML varie en fonction de l'outil utilisé. Vous pouvez retrouver la documentation de PlantUML ainsi que la représentation visuelle adoptée sur cette page : [https://plantuml.com/fr/class-diagram](https://plantuml.com/fr/class-diagram).

### Création des classes d’objets métiers

La création d’un mapping entre le « monde objet » et le « monde relationnel » nécessite au préalable la création de modèles de données semblables mais adaptés aux spécificités de chacun de ses mondes. L’objectif est donc de transformer le modèle relationnel de la base « Gestion Pédagogique » (schéma Entité/Association) en un modèle objet satisfaisant ([diagramme de classes UML][6]).

La figure ci-dessous est une traduction directe du schéma Entité/Association en un diagramme de classe UML. Chacun des concepts du schéma E/A a été transformé en son équivalent UML (Le losange est le symbole matérialisant les associations n-aires (*n* &gt; 2). `Notation` est une classe dite d’association. Elle permet de modéliser les attributs portés par une association). À partir de cette traduction, des modifications seront apportées à ce modèle pour le rendre implémentable.

![Diagramme de classe « Gestion Pédagogique »](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/GestionPedagogique.puml)

### Modélisation des types d’entités

Pour établir une correspondance entre une entité de notre BD et un objet de notre application, il faut commencer par écrire les classes associées à chacun des types d’entités. Pour des raisons qui apparaitront plus tard, chaque classe métier devra suivre les conventions suivantes :

* La classe doit être [« sérialisable »][8] (*i.e.* implémenter l’interface `Serializable`) pour pouvoir sauvegarder et restaurer l’état des instances de cette classe ;

* La classe doit posséder un constructeur sans argument (constructeur par défaut);

* Les propriétés privées de la classe (variables d’instances) doivent être accessibles publiquement via des méthodes accesseurs construites avec `get` ou `set` suivi du nom de la propriété avec la première lettre transformée en majuscule (utiliser les fonctionnalités de génération de l'IDE).

* La classe doit surcharger la méthode `toString()` pour pouvoir afficher l’état des instances de cette classe (voir les capacités de génération de l'IDE).

* La classe doit aussi surcharger les méthodes `equals()` et `hashCode()` héritées de [`Object`][9] (comme pour les autres questions ne surtout pas essayer de les écrire à la main. Demander à votre IDE de les générer directement)

#### Question 2

Implémenter (en respectant les conventions ci-dessus) les classes `Etudiant`, `Module` et `Prof` dont le diagramme UML incomplet vous est donné dans la figure ci-après (un squelette vous est donné dans le paquetage `beans`). Copier la classe `ExempleConnexion` dans la nouvelle classe `ExempleEntite`. Modifier le code de cette classe pour que sa boucle principale remplisse un [`ArrayList`][10] d’objets `Etudiant` et qu’elle affiche le contenu de cette liste en utilisant la méthode `toString()` à travers un simple `System.out.println`.

![Diagramme de classe des entités](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/ClassesMetier.puml)

### Modélisation des types d’association hiérarchiques

Précédement tous les types d’association binaires du MCD ont été représentés par leurs équivalents en UML. Ces associations UML sont symbolisées par un trait liant deux classes. Les multiplicités (les nombres situés aux extrémités de l’association) correspondent aux cardinalités du MCD mis à part qu’elles sont placées à l’inverse. Par exemple, pour indiquer qu’une classe `A` peut participer 0 ou 1 fois à une association avec la classe `B`, on placerait la multiplicité `0..1` du coté de `B`. UML permet d’écrire certaines multiplicités de manière simplifiée : `0..*` devient `*` et `1..1` devient `1`.

Par défaut les associations sont bidirectionnelles, cela signifie qu’une instance à l’une des extrémités peut savoir avec quelles autres instances elle est liée par cette association. Dans la pratique, ce double lien peut être coûteux à maintenir, c’est pourquoi UML permet de privilégier un seul sens en interdisant l’accès dans l’autre. C’est ce que l’on appelle la restriction de la navigabilité d’une association. Elle est symbolisée par une flèche indiquant le sens de navigation permis.

La figure ci-aprés montre le sens de navigation des trois types d’association hiérarchiques de la base « Gestion Pédagogique ».
Les sens de navigation choisis imposent que :

* pour chaque instance de la classe `Prof` on connaitra le `Module` pour lequel il est spécialiste mais pour un `Module` on ne peut pas savoir quels sont les `Prof` spécialistes;

* pour chaque instance de la classe `Module` on connaitra le `Prof` responsable mais pour un `Prof` on ne peut pas savoir quels sont les `Modules` dont il est responsable;

* pour chaque instance de la classe `Module` on connaitra son `Module` père mais pour un `Module` donné on ne peut pas retrouver l’ensemble de ses fils.

![Diagramme de classe avec navigabilité](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/Navigabilite.puml)

#### Question 3

Implémenter en respectant le sens de navigation imposé l’association *« est spécialiste »* entre `Prof` et `Module`. Un objet `Prof` n’étant associé qu’à un seul `Module`, il suffit d’ajouter à la classe `Prof` un attribut `matSpec` (sans oublier les accesseurs associés) qui est une référence vers un `Module`. Il permet de lier un objet `Prof` à sa spécialité.

Faire de même pour les deux autres TA hiérarchiques en respectant à chaque fois les sens de navigation de la figure ci-dessus. Copier la classe `ExempleEntite` dans la nouvelle classe `ExempleAsso1`. Modifier le code de cette classe pour remplir un `ArrayList` d’objets `Prof`. Pour chacun d’eux construire un objet `Module` représentant sa spécialité et conserver une référence vers cet objet dans l’attribut `matSpec`. Afficher chacun des profs et le module dont il est spécialiste.

### Modélisation des types d’association non hiérarchiques

Contrairement aux types d’association hiérarchiques qui peuvent être implémentés simplement par des références (pointeurs en *C++*), les types d’association non hiérarchiques nécessitent une structure supplémentaire . Nous allons présenter trois manières d’implémenter ces types d’association : les collections de pointeurs de chaque coté de l’association, les objets d’association et la promotion d’une association en classe. Chacune de ces méthodes d’implémentation a des avantages et des inconvénients qu’il faudra prendre en compte avant de faire un choix.

#### Collections de pointeurs aux extrémités de l’association

La première méthode est en quelque sorte une extension de la technique d’implémentation du paragraphe précédent. Pour simplifier la présentation, cette approche est appliquée dans un premier temps sur l’association `Notation`, dans laquelle on ne considère pas les données portées par l’association (*cf.* figure ci dessus). L’implémentation complète (en rajoutant la classe d’association) de cette association sera faite dans un second temps.

![Association Notation sans la classe d'association](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/NotationSimple.puml)

Dans le cas de l’association « `est spécialiste` » où un `Prof` n’était lié qu’à un seul `Module`, il a suffi d’ajouter dans `Prof` une référence vers une instance de `Module`. Ici, un `Etudiant` peut être lié à plusieurs `Module`. On ajoute donc non pas une seule référence, mais un ensemble (ou collection) de références, nommé `notations`, vers des objets `Module`. Cette collection doit être d’un type implémentant l’interface [`Set`][11] tel que `HashSet` ou `TreeSet`. Cette contrainte garantit l’unicité des objets contenus dans la collection. Ainsi, un même `Etudiant` ne peut pas être lié plusieurs fois à un même `Module`, ce qui indispensable pour modéliser correctement une association.

Aucun sens de navigation n’étant privilégié, il faut rajouter de manière symétrique une collection appelée `etudiants` dans `Module`. Cet ensemble de références vers des objets `Etudiant` rend possible la navigation dans le sens inverse. Le diagramme de la figure ci-dessous décrit les changements apportés aux classes `Etudiant` et `Module` pour implémenter l’association.

![Classes `Module` et `Etudiant` utilisant deux Set](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/NotationSimpleAvecMembre.puml)

L'implémentation proposée permet de savoir à quel `Module` un `Etudiant` est lié (et inversement) mais elle ne permet pas d'ajouter des informations supplémentaires aux liens. Pour implémenter l'association comme dans la figure ci-dessous, il faut prendre en compte la classe d'association `Notation`.

![Association `Notation` en considérerant les attributs portés](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/NotationAvecAttributs.puml)

Les ensembles de références sont remplacés par des dictionnaires (des conteneurs associatifs) pour atteindre cet objectif. Un dictionnaire peut être globalement perçu, d'un point de vue fonctionnel, comme une sorte de tableau indexable par n'importe quel type d'objet (plus seulement par des entiers). Malgré leur simplicité d'utilisation, ils ont un coût d'accès plus élevé qu'un tableau classique. En Java, les conteneurs associatifs sont des classes implémentant l'interface [`Map`][12] (tel que `HashMap`). Ces classes permettent d'associer un objet clef (l'objet servant d'index) à un objet valeur (n'importe quel autre objet). D'après le diagramme de classe, cet objet valeur sera une référence vers un objet de la classe `Notation`.  Les modifications à apporter aux classes `Module` et `Etudiant` pour prendre en compte ces changements sont décrite dans le diagramme qui suit. Le lien `Notation` entre un `Etudiant` et un `Module` est ainsi représenté sous forme de collections (associatives) de pointeurs de part et d'autre de l'association.

![Classe `Module`, `Etudiant` avec prise en compte des notations](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/NotationAvecMap.puml)

#### Objets d’association

L’approche précédente est relativement simple à mettre en œuvre du point de vue des modifications à apporter aux différentes classes. La principale difficulté provient de l’interdépendance entre objets qu’elle introduit. En effet, chacun des objets participant à une association a la responsabilité de construire et de maintenir à jour sa propre liste de liens. Si l’on souhaite supprimer un objet, il faut avant cela supprimer cet objet dans chacune des listes des objets avec lequel il est lié. La responsabilité de la cohérence (réciprocité) d’un lien est partagée entre plusieurs objets de classes différentes, il y a donc éparpillement du code de gestion l’association ce qui implique un risque plus important d’erreur.

Dans notre cas (application avec objets persistants en BD) une telle approche n’est pas envisageable, car lorsque l’on doit rendre persistant un objet dans une base de données, cela implique de vérifier si les objets avec lesquels il est lié sont déjà stockés dans la base de données. Or, cette tache n’est pas du tout évidente d’un point de vue algorithmique et a un coût important s’il existe un grand nombre de liens.

La seconde solution  consiste à créer un unique objet qui aura la responsabilité de conserver et gérer tous les liens d’une association. Cet objet ayant une vision globale des liens existants, il peut facilement supprimer tous les liens entretenus par un seul et même objet. De plus, un objet n’a plus à connaitre tous les objets qui lui sont liés mais uniquement l’objet association qui pourra retrouver au besoin tous ces liens. En quelque sorte, cette approche est une solution globale qui décharge les différents objets de la responsabilité de gérer chacun des liens localement. Pour rendre persistante une association, il suffit de stocker tous les objets connus par l’association avant de stocker l’objet d’association lui même.

L’implémentation d’un objet d’association se fait en utilisant un ensemble (`Set`) d’objet lien. Chaque objet lien est un *n*-uplet de références vers les différentes classes participant à l’association. La figure ci-après donne le diagramme de classe de l’objet d’association `AssociationNotation`. Un `Lien` est dans notre cas un triplet d’étudiant, module et notation. Pour gérer correctement la contrainte d’unicité de l’association, la classe `Lien` doit surcharger les méthodes `equals()` et `hashCode()` héritées de [`Object`][13]. Deux liens sont considérés comme égaux s’ils référencent le même étudiant et le même module (peu importe la note).

![Diagramme de classe de `AssociationNotation`](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/AssociationNotation.puml)

#### Question 4 (À faire après la question 6 et 7)

Implémenter l’association « `Notation` » entre `Etudiant` et `Module` en utilisant l’objet d’association `AssociationNotation`. Copier la classe `ExempleAsso1` dans la nouvelle classe `ExempleAsso2`. Modifier le code de cette classe pour charger toutes les notes des différents étudiants aux différents modules dans l’objet d’association `AssociationNotation`. Pour simplifier les traitements, penser à charger l’ensemble des étudiants et des modules à l’avance. Afficher les étudiants et leurs notes pour le module ’ACSI’.

#### Promotion d’une association en classe

La dernière approche présentée a pour objectif de simplifier le diagramme de classe pour contourner le problème des associations trop complexes à matérialiser. Comme nous venons de le voir, implémenter une association bidirectionnelle non-hiérarchique demande un travail important. Généralement lorsque l’on rencontre des associations *n*-aires (avec *n* &gt; 2), l’une des techniques employées est de promouvoir cette association en une classe. Celle-ci sera liée par une association hiérarchique à chacune des classes participant à l’ancienne association. Cette modification du diagramme de classe modifie aussi partiellement sa sémantique. En effet, la contrainte d’unicité de l’association n’est plus vérifiée structurellement, la responsabilité de cette contrainte revient au code de l’utilisateur. Il faudra en être conscient avant de faire le choix d’utiliser cette solution.

Dans notre base de données « Gestion Pédagogique » il n’y a qu’une seule association ternaire : `Enseignement`. Elle sera donc notre support pour mettre en pratique cette technique.

#### Question 5 (À faire après la question 6 et 7)

Implémenter l’association « `Enseignement` » entre `Etudiant`, `Module` et `Prof` en transformant l'association en une classe. Modifier chacune des classes participantes pour que les associations *A*<sub>*i*</sub> soient navigables dans les deux sens. Copier la classe `ExempleAsso2` dans la nouvelle classe `ExempleAsso3`. Modifier le code de cette classe pour charger tous les enseignements. Afficher tous les enseignements suivis par les étudiants du groupe 1.

### Construction de la couche d’accès aux données

Les paragraphes précédents ont présenté comment construire le modèle objet miroir du modèle relationnel. L’objectif est maintenant d’écrire le code permettant de faire communiquer ces deux modèles. Les questions ont mis en évidence la difficulté (et l’aspect répétitif) d’écrire un tel code avec JDBC. Utiliser directement JDBC à chaque accès aux données produirait deux effets très négatifs :

* Une pollution importante du code métier par du code JDBC. Cela implique donc une moins grande lisibilité du code et ainsi un risque d’erreur plus important.

* Une moins grande indépendance vis à vis du SGBD. L’intrication forte entre code métier et code d’accès au données rend le changement de SGBD (par exemple le remplacement de Oracle par Postgres) très délicat voir impossible.

Pour éviter ces problèmes, nous allons construire une couche dédiée à l’accès aux données qui utilisera le pattern [DAO][14] (Data Access Object). Cette couche encapsulera tous les accès à la source de données. Les autres parties de l’application utiliseront uniquement les objets de cette couche pour gérer la persistance. Elle sera donc une sorte d’abstraction du modèle de données indépendante de la solution de stockage des données. La couche DAO contiendra au moins autant de classes de DAO que d’entités du MCD (classe d’objet métier). L’écriture et la maintenance d’une telle couche est donc une opération généralement fastidieuse. C’est l’une des raisons pour lesquelles les solutions de persistance actuelles génèrent automatiquement une grande partie du code (Java et/ou SQL).

### Structure d'un DAO

Chacun des DAO devra contenir des méthodes pour effectuer les 4 opérations de base pour la persistance des données : *créer, récupérer, mettre à jour et supprimer* (Généralement désigné par l’acronyme anglais CRUD pour *Create, Retrieve, Update et Delete*).Par convention, chacune des classes de DAO devra être nommée par "`DAO`" suivi du nom de la classe métier associée. La figure ci-dessous décrit la classe `DAOEtudiantJDBC` qui est le DAO associé à la classe d’objet métier `Etudiant`.

![Diagramme de classe de `DAOEtudiantJDBC`](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/DAOEtudiant.puml)

Cette classe est constituée des méthodes suivantes :

* `insert` qui à pour objectif de créer un nouvel étudiant dans la base de données. L’identifiant d’un tuple ne pouvant être connu avant son insertion, cette méthode retourne une copie de l’objet métier passé en paramètre avec un identifiant définitif. L’identité d’un objet dépendant uniquement de l’identifiant, un objet métier créé localement avec le constructeur par défaut (objet temporaire sans identité propre du point de vue de `equals()` et `hashCode()`) ne devra participer à aucune association avant d’être inséré dans la base avec cette méthode (Ces objets sans identité jouent le rôle des objets de transfert de données (*Data Transfer Object*) du pattern DAO original).

* `update` qui prend un objet métier en paramètre et essaie faire la mise à jour dans la base de données. La valeur retournée par cette méthode indique si la mise à jour a pu avoir lieu.

* `delete` qui prend un étudiant en paramètre et essaie de le supprimer de la base de données. La valeur retournée par cette méthode indique si la suppression a pu avoir lieu.

* les `get` qui constituent, avec les `find`, les méthodes de récupération des données. Les paramètres passés à ces méthodes permettent de récupérer uniquement les tuples satisfaisants certains critères. La différence entre ces deux familles de méthodes est que les `get` doivent retourner exactement un seul résultat alors que les `find` peuvent en retourner plusieurs.

* les `compute` qui, comme leur nom l’indique, ont pour objectif d’effectuer des calculs sur les étudiants. La plupart du temps (sauf si le calcul demande de ne rapatrier aucune donnée) on préférera, pour des raisons d’efficacité, le faire directement dans le SGBD. Ces méthodes sont donc soit des requêtes SQL agrégatives soit des appels de procédures stockées.

### Utilisation d'un DAO

En utilisant `DAOEtudiant`, la récupération par l’application de l’étudiant d’identifiant 1 dans la base de données se déroule comme suit :

1. L’application demande un objet `Etudiant` correspondant au tuple d’identifiant 1 dans la base de données à l’unique instance de `DAOEtudiant`.

2. L’objet `DAOEtudiant` récupère cette demande (méthode `getByID(1)` ) et il s’occupe d’exécuter la requête SQL avec JDBC.

3. Le SGBD interprète la requête SQL et retourne le résultat attendu (s’il existe).

4. L’objet `DAOEtudiant` récupère ces informations.

5. L’objet `DAOEtudiant` instancie un objet `Etudiant` avec les données récupérées.

6. Enfin, l’objet `DAOEtudiant` retourne l’instance de l’objet `Etudiant`.

Cette séquence d’opération illustre bien le rôle central de l’objet DAO dans l’accès aux données. Les opérations de mise à jour et de suppression se dérouleront à peu près de la même manière. Pour l’insertion d’un nouveau tuple, il faudra d’abord créer un objet sans identité (avec le constructeur par défaut) puis appeler la méthode `insert()` qui nous retournera notre objet définitif (avec un identifiant valide). Le code ci-dessous illustre l’utilisation typique du DAO pour l’ajout d’un nouvel étudiant et sa modification :

```java
public class Main {
  public static void main(String[] args){
    DAOEtudiant dao = new DAOEtudiantJDBC();
    Etudiant e = new Etudiant();//e est un Etudiant temporaire
    e.setNom("Dupont");
    e.setPrenom("Paul");
    e.setCp("13100");
    e.setVille("Aix-en-Provence");
    e.setAnnee(1);//Modification des attributs de e 
    e.setGroupe(5);
    e = dao.insert(e);//e referencie maintenant un Etudiant definitif
    //...
    e.setAnnee(2);// Modification des attributs de e 
    e.setGroupe(3);
    //...
    boolean updateOk = dao.update(e);//Sauvegarde des modifications
    //...
  }
}
```

#### Question 6

Implémenter la classe `DAOEtudiant`. Copier la classe `ExempleEntite` dans la classe `ExempleDAOEtudiant` et la modifier pour qu'elle utilise un `DAO`.

### Hiérarchie des DAO

Tous les DAO de notre application ont un certain nombre de méthodes communes. Pour améliorer l’indépendance du code client vis à vis de la couche de persistance, nous ajoutons une interface `DAO` que tous les objets DAO devront implémenter. Les objets métiers dépendront ainsi d’une interface et non d’une implémentation particulière. La figure ci-après donne le diagramme de classe de l’ensemble des DAO de l’application gestion pédagogique. Dans sa version complète, le pattern présenté utilise des `AbstractFactory` pour améliorer encore la modularité de la couche de persistance.

![Diagramme de classe des `DAO`](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/IUTInfoAix-M3106/TpJdbc/master/src/main/resources/assets/DAO.puml)

#### Question 7

Implémenter les 2 autres classes `DAO` pour les entités `Prof` et `Module`.  Prenez en compte intelligemment les associations existant entre les différentes classes métiers. Copier la classe `ExempleAsso2` dans la nouvelle classe `ExempleDAO`. Modifier le code de celle-ci pour que sa boucle principale récupère tous les étudiants de deuxième années, les affiche, puis augmente toutes leurs notes pour le module « ACSI » d’un point et enfin sauvegarde les résultats dans la base.

[1]:<http://www.hibernate.org/>

[2]:<http://www.eclipse.org/eclipselink/>

[3]:<https://github.com/IUTInfoAix-M3106/TutoJdbc>

[4]:<http://fr.wikipedia.org/wiki/Transaction_informatique>

[5]:<http://fr.wikipedia.org/wiki/Singleton_%28patron_de_conception%29>

[6]:<http://uml.free.fr/>

[8]:<http://fr.wikipedia.org/wiki/S%C3%A9rialisation>

[9]:<http://download.oracle.com/javase/8/docs/api/java/lang/Object.html>

[10]:<http://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html>

[11]:<http://java.sun.com/developer/onlineTraining/collections/Collection.html#SetInterface>

[12]:<http://java.sun.com/developer/onlineTraining/collections/Collection.html#MapInterface>

[13]:<http://download.oracle.com/javase/8/docs/api/java/lang/Object.html>

[14]:<http://java.sun.com/blueprints/corej2eepatterns/Patterns/DataAccessObject.html>
