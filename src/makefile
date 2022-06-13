# makefile pour CoreWarrior 0.2
HOME=/home/casa/pgm/corewarrior-0.2
#COMMANDE = jikes +E -depend -d $(HOME)/bin -nowarn
COMMANDE = javac -O -depend -d $(HOME)/bin -nowarn

all: compilateur editeur moteur awt jar

# construit le compilateur
compilateur: casa/corewarrior/compilateur/Compilateur.java casa/corewarrior/compilateur/SyntaxErrorException.java
	$(COMMANDE) casa/corewarrior/compilateur/Compilateur.java

# construit l'éditeur
editeur: awt casa/corewarrior/editeur/Editeur.java casa/corewarrior/editeur/DialogRecherche.java
	$(COMMANDE) casa/corewarrior/editeur/Editeur.java casa/corewarrior/editeur/DialogRecherche.java

# construit le moteur
moteur: awt casa/corewarrior/moteur/Console.java casa/corewarrior/moteur/DialogInstaller.java casa/corewarrior/moteur/Fenetre.java casa/corewarrior/moteur/Moteur.java casa/corewarrior/moteur/Plateau.java casa/corewarrior/moteur/RuntimeErrorException.java
	$(COMMANDE) casa/corewarrior/moteur/Console.java casa/corewarrior/moteur/DialogInstaller.java casa/corewarrior/moteur/Fenetre.java casa/corewarrior/moteur/Moteur.java casa/corewarrior/moteur/Plateau.java casa/corewarrior/moteur/RuntimeErrorException.java

# construit les composants awt
awt: casa/awt/Confirmation.java casa/awt/Dessin.java casa/awt/DialogApropos.java casa/awt/GraphButton.java casa/awt/Help.java casa/awt/InfoDialog.java casa/awt/PanelImage.java casa/awt/BoiteConfigurationTextArea.java casa/awt/ConfigurationTextArea.java
	$(COMMANDE) casa/awt/Confirmation.java casa/awt/Dessin.java casa/awt/DialogApropos.java casa/awt/GraphButton.java casa/awt/Help.java casa/awt/InfoDialog.java casa/awt/PanelImage.java casa/awt/BoiteConfigurationTextArea.java casa/awt/ConfigurationTextArea.java

# construit le fichier JAR constenant les classes
jar: compilateur editeur moteur
	cd ../bin;jar cf ../bin.jar *
