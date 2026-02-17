Feature: tester le site swaglab
  Background: tester test global
Given je suis dans le site swaglab
When je saisie username "standard_user"
    And je saisie password "secret_sauce"
And je clique sur le bouton login
Then redirection vers la page Home et verifier alert

    When je clique Add to cart
    Then ajout succes
@suppression @smoke
    Scenario: tester supp produit
      When je clique remove
      Then produit supp

@checkout @smoke
  Scenario: tester  checkout
    When je clique chekout
    And je saisie firstName "aziz"
    And je saisie lastName "gheribi"
    And je saisie code poste "1200"
    And je clique continue
    And je clique Finish
    Then checkout succes


@logout
  Scenario: tester test logout
    When je clique menu
    And je clique logout
    Then logout succes


