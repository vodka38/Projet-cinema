# Projet Cinema
Ce projet est un mini-moteur de tarification pour un cinéma, implémenté en Java. Il calcule le prix total d'une commande de billets en fonction des types de billets, de la séance 3D, et du jour de la semaine, avec des règles de remises et suppléments.

## Prérequis
JDK 17 ou version supérieure
(Optionnel) Jenkins pour l'intégration continue

## Structure du projet
src/main/java/edu/cinema/pricing/: Code source principal
TicketType.java: Enum des types de billets
PriceBreakdown.java: Classe immuable pour le récapitulatif des prix
PricingEngine.java: Logique de calcul des prix
src/test/java/edu/cinema/pricing/: Tests unitaires JUnit 5

## Exécution
Compiler et exécuter les tests
Utilise les commandes appropriées pour ton environnement (par exemple, via un IDE comme IntelliJ ou une ligne de commande si configuré manuellement).

Vérifie le rapport JaCoCo à target/site/jacoco/index.html si généré.

Exécuter manuellement (optionnel)
Pour tester la classe PricingEngine :
Compile avec un outil de build ou IDE.
Utilise une classe principale (non fournie ici, mais tu peux en ajouter une).

## Fonctionnalités
Calcul du prix de base par type de billet :
ADULT: 10.00 €
CHILD: 6.00 €
SENIOR: 7.50 €
STUDENT: 8.00 €
Règles appliquées dans cet ordre :
-20% le mercredi
+2.00 € par billet en 3D
-10% pour un groupe de 4 billets ou plus
Arrondi au centime près.
Tests

Tests JUnit couvrant les cas nominaux, de bord, et d'erreurs.
Objectif : Couverture JaCoCo ≥ 75-85 %.
Rapport généré dans target/site/jacoco/.