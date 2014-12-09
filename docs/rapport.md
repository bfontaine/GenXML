# Projet XML

Par David Galichet et Baptiste Fontaine

## Conversion

Notre convertisseur est écrit en Scala et permet de convertir tous les tags
demandés ainsi que tous les évènements liés aux individus et aux familles, les
informations d’un document (copyright, version de GEDCOM), et les objets
multimédias.

## Architecture

Le code du parseur est dans `parser/`, plus précisément dans les deux fichiers
`parser/src/main/scala/parser.scala` qui est chargé des entrées/sorties et
`parser/src/main/scala/model.scala` qui est chargé de la transformation des
entitées Gedcom en XML.
Nous utilisons la bibliothèque [Gedcom4j][g4j] pour parser les fichiers Gedcom
avec un pré-processing pour supprimer les espaces en trop et un post-processing
pour parser le nom des individus et normaliser les valeurs dans certains
fichiers mal formatés. Notre convertisseur peut également lire des fichiers
dans des encodages différents (l’un des exemples fournis pour le projet est en
latin1 alors que les autres sont en utf-8).

## Exécution

Le convertisseur peut être compilé en une archive jar, inclue dans l’archive
rendue pour le projet. À la racine du projet, exécutez `make` pour générer le
jar (il faut avoir `scala` et `scala-sbt`) et `make tests` pour tester avec les
fichiers fournis. Le Makefile génère également un script shell (`gedcom2xml`)
qui exécute l’archive. Le convertisseur peut afficher le XML sur la sortie
standard ou l’écrire dans un fichier :

    ./gedcom2xml toto.ged > toto.xml
    ./gedcom2xml toto.ged toto.xml


[g4j]: http://gedcom4j.org/main/
