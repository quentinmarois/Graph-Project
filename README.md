# Graph Scheduling Project

Projet de fin de semestre de L3 pour le cours de **Théorie des graphes**.



## Fonctionnalités

- Lire et créer un graphe à partir d'une table de contraintes (`/src/tests/*.txt`)
- Vérifier des propriétés spécifiques des graphes d'ordonnancement (une seule entrée, une seule sortie, pas de cycle...)
- Appliquer différents algorithmes si le graphe est bel est bien un graphe d'ordonnancement.
- Créer un fichier (`trace.txt` dans la branche [delivery](../../tree/delivery)) permettant au professeur de valider le programme



## Améliorations potentielles

- Migrer les fonctions dédiées aux graphes dirigés dans une classe `DirectedGraph` qui étend `Graph`
- Ajouter des fonctionnalités dédiées aux graphes non dirigés dans une classe `UndirectedGraph`
