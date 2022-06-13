package casa.corewarrior.editeur;

import casa.awt.*;
import casa.corewarrior.compilateur.*;
import casa.corewarrior.moteur.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.datatransfer.*;
import java.util.*;

public final class Editeur extends Frame implements
        WindowListener, ActionListener, TextListener, Runnable, FilenameFilter {
    private static final String version = "0.2";
    private static final String copyright = "Core Warrior " + version + " ";
    // indicateur d'un chargement de fichier
    private boolean chargement = false;

    // composants de l'interface
    private static Editeur editeur;
    private MenuBar menuBar = new MenuBar();
    private Menu menuFichier = new Menu("Fichier");
    private MenuItem itemNouveau = new MenuItem("Nouveau");
    private MenuItem itemOuvrir = new MenuItem("Ouvrir");
    private MenuItem itemEnregistrer = new MenuItem("Enregistrer");
    private MenuItem itemEnregistrerSous = new MenuItem("Enregistrer sous ...");
    private MenuItem itemSeparateur1 = new MenuItem("-");
    private MenuItem itemQuitter = new MenuItem("Quitter");

    private Menu menuEdition = new Menu("Edition");
    private MenuItem itemCouper = new MenuItem("Couper");
    private MenuItem itemCopier = new MenuItem("Copier");
    private MenuItem itemColler = new MenuItem("Coller");
    private MenuItem itemSeparateur2 = new MenuItem("-");
    private MenuItem itemRechercher = new MenuItem("Rechercher ...");

    private Menu menuCompiler = new Menu("Compiler");
    private MenuItem itemCompiler = new MenuItem("Compiler");
    private MenuItem itemSeparateur3 = new MenuItem("-");
    private MenuItem itemExecuter = new MenuItem("Exécuter");

    private Menu menuAide = new Menu("Aide");
    private MenuItem itemAide = new MenuItem("Aide");
    private MenuItem itemFonte = new MenuItem("Fonte");
    private MenuItem itemSeparateur4 = new MenuItem("-");
    private MenuItem itemApropos = new MenuItem("A propos ...");

    private GraphButton boutonNouveau = new GraphButton("Nouveau", "img/newFile.gif");
    private GraphButton boutonOuvrir = new GraphButton("Ouvrir", "img/openFile.gif");
    private GraphButton boutonEnregistrer = new GraphButton("Enregistrer", "img/saveFile.gif");
    private GraphButton boutonCouper = new GraphButton("Couper", "img/cut.gif");
    private GraphButton boutonCopier = new GraphButton("Copier", "img/copy.gif");
    private GraphButton boutonColler = new GraphButton("Coller", "img/paste.gif");
    private GraphButton boutonCompiler = new GraphButton("Compiler", "img/compile.gif");
    private GraphButton boutonExecuter = new GraphButton("Exécuter", "img/run.gif");
    private GraphButton boutonAide = new GraphButton("Aide", "img/help.gif");

    private TextArea texte = new TextArea();
    private Label etat = new Label();

    // répertoire et nom du fichier édité
    private static String repertoire = "";
    private static String fichier = "";
    private boolean modifie = false;
    private Font fonte = new Font("Dialog", Font.PLAIN, 14);
    private static final String imageConfirmation = "img/warning.gif";
    private Thread thread;

    // main instancie Editeur et l'affiche
    public static void main(String[] args) {
        if (args.length == 0)
            editeur = new Editeur();
        else
            editeur = new Editeur(args[0]);
        editeur.setTitle(copyright + "[" + fichier + "]");
        editeur.show();
        DialogApropos.afficher(editeur, "img/aPropos.gif");
    }

    // constructeur sans argument
    Editeur() {
        // initialisation interface utilisateur
        initUI();
        // on charge le fichier de configuration
        chargerConfig();
    }

    // lecture du fichier de configuration et des messages
    public void chargerConfig() {
        // on détermine le fichier de configuration
        String fichier = System.getProperty("cfg");
        if (fichier == null) {
            fichier = System.getProperty("user.home") +
                    System.getProperty("file.separator") +
                    ".corewarrior.cfg";
            System.getProperties().put("cfg", fichier);
        }
        // on crée les propriétés de configuration
        Properties configuration = new Properties();
        // maintenant on charge le fichier
        try {
            FileInputStream entree = new FileInputStream(fichier);
            configuration.load(entree);
            entree.close();
            String chaine;
            if ((chaine = configuration.getProperty("fonte")) != null) {
                String[] tokens = decouperChaine(chaine, ",");
                texte.setFont(new Font(tokens[0],
                        Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
            }
            if ((chaine = configuration.getProperty("couleurs")) != null) {
                String[] tokens = decouperChaine(chaine, ",");
                texte.setBackground(new Color(Integer.parseInt(tokens[0], 16)));
                texte.setForeground(new Color(Integer.parseInt(tokens[1], 16)));
            }
        } catch (IOException e) {
        }
    }

    // constructeur avec nom du fichier à ouvrir
    Editeur(String chemin) {
        this();
        ouvrirSansDialog(chemin);
    }

    // ouverture d'un fichier sans Dialog
    private void ouvrirSansDialog(String chemin) {
        // on charge le fichier sélectionné
        try {
            BufferedReader entree = new BufferedReader(new InputStreamReader(
                    new FileInputStream(chemin)));
            StringBuffer buffer = new StringBuffer();
            String ligne;
            while ((ligne = entree.readLine()) != null) {
                buffer.append(ligne);
                buffer.append("\n");
            }
            entree.close();
            texte.setText(buffer.toString());
            int i = 0;
            if ((i = chemin.lastIndexOf(System.getProperty("file.separator"))) >= 0) {
                fichier = chemin.substring(i + 1, chemin.length());
                repertoire = chemin.substring(0, i + 1);
            } else {
                fichier = chemin;
                repertoire = "";
            }
            modifie = false;
            chargement = true;
            if (!etat.getText().equals(""))
                etat.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // initialisation de l'interface utilisateur
    private void initUI() {
        setBackground(Color.lightGray);
        // construction du menu Fichier
        menuBar.add(menuFichier);
        menuFichier.add(itemNouveau);
        menuFichier.add(itemOuvrir);
        menuFichier.add(itemEnregistrer);
        menuFichier.add(itemEnregistrerSous);
        menuFichier.add(itemSeparateur1);
        menuFichier.add(itemQuitter);
        // construction du menu Edition
        menuBar.add(menuEdition);
        menuEdition.add(itemCouper);
        menuEdition.add(itemCopier);
        menuEdition.add(itemColler);
        menuEdition.add(itemSeparateur2);
        menuEdition.add(itemRechercher);
        // construction du menu Compiler
        menuBar.add(menuCompiler);
        menuCompiler.add(itemCompiler);
        menuCompiler.add(itemSeparateur3);
        menuCompiler.add(itemExecuter);
        // construction du menu Aide
        menuBar.add(menuAide);
        menuAide.add(itemAide);
        menuAide.add(itemFonte);
        menuAide.add(itemSeparateur4);
        menuAide.add(itemApropos);
        setMenuBar(menuBar);
        // création de la barre de boutons
        PanelImage barreBoutons = new PanelImage("img/chantier.gif");
        barreBoutons.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        Panel groupeFichier = new Panel();
        groupeFichier.setLayout(new GridLayout(1, 3));
        groupeFichier.add(boutonNouveau);
        groupeFichier.add(boutonOuvrir);
        groupeFichier.add(boutonEnregistrer);
        barreBoutons.add(groupeFichier);
        Panel groupeEdition = new Panel();
        groupeEdition.setLayout(new GridLayout(1, 3));
        groupeEdition.add(boutonCouper);
        groupeEdition.add(boutonCopier);
        groupeEdition.add(boutonColler);
        barreBoutons.add(groupeEdition);
        Panel groupeMake = new Panel();
        groupeMake.setLayout(new GridLayout(1, 2));
        groupeMake.add(boutonCompiler);
        groupeMake.add(boutonExecuter);
        barreBoutons.add(groupeMake);
        barreBoutons.add(boutonAide);
        add("North", barreBoutons);
        // on ajoute la zone d'édition
        add("Center", texte);
        texte.setBackground(Color.black);
        texte.setForeground(Color.orange);
        // on ajoute la ligne d'état
        add("South", etat);
        etat.setBackground(Color.lightGray);
        // redimensionne la fenètre
        setSize(640, 480);
        Dimension a = getToolkit().getScreenSize();
        Rectangle b = getBounds();
        setLocation((a.width - b.width) / 2, (a.height - b.height) / 2);

        // on ajoute les listeners
        itemNouveau.addActionListener(this);
        itemOuvrir.addActionListener(this);
        itemEnregistrer.addActionListener(this);
        itemEnregistrerSous.addActionListener(this);
        itemQuitter.addActionListener(this);

        itemCouper.addActionListener(this);
        itemCopier.addActionListener(this);
        itemColler.addActionListener(this);
        itemRechercher.addActionListener(this);

        itemCompiler.addActionListener(this);
        itemExecuter.addActionListener(this);

        itemAide.addActionListener(this);
        itemFonte.addActionListener(this);
        itemApropos.addActionListener(this);

        boutonNouveau.addActionListener(this);
        boutonOuvrir.addActionListener(this);
        boutonEnregistrer.addActionListener(this);
        boutonCouper.addActionListener(this);
        boutonCopier.addActionListener(this);
        boutonColler.addActionListener(this);
        boutonCompiler.addActionListener(this);
        boutonExecuter.addActionListener(this);
        boutonAide.addActionListener(this);

        addWindowListener(this);
        texte.addTextListener(this);
    }

    // création d'un nouveau fichier
    public void nouveau() {
        chargement = true;
        texte.setText("");
        modifie = false;
        fichier = "";
        editeur.setTitle(copyright + "[" + fichier + "]");
        if (!etat.getText().equals(""))
            etat.setText("");
    }

    // ouverture d'un fichier
    public void ouvrir() {
        // on ouvre la fenètre de sélection de fichier
        FileDialog dialog = new FileDialog(this, "Ouvrir", FileDialog.LOAD);
        dialog.setFilenameFilter(this);
        dialog.setFile("*.src");
        dialog.setDirectory(repertoire);
        dialog.show();
        // on extrait le nom du fichier
        if (dialog.getFile() != null) {
            chargement = true;
            repertoire = dialog.getDirectory();
            fichier = dialog.getFile();
            String chemin = repertoire + fichier;
            // on charge le fichier sélectionné
            try {
                BufferedReader entree = new BufferedReader(new InputStreamReader(
                        new FileInputStream(chemin)));
                StringBuffer buffer = new StringBuffer();
                String ligne;
                while ((ligne = entree.readLine()) != null) {
                    buffer.append(ligne);
                    buffer.append("\n");
                }
                entree.close();
                texte.setText(buffer.toString());
                modifie = false;
                editeur.setTitle(copyright + "[" + fichier + "]");
                if (!etat.getText().equals(""))
                    etat.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // accept() pour implémenter FilenameFilter
    public boolean accept(File dir, String name) {
        if (name.endsWith(".src"))
            return true;
        else
            return false;
    }

    // enregistrement du fichier en cours d'édition
    private void enregistrer() {
        if (fichier.equals("")) {
            enregistrerSous();
        } else {
            String chemin = repertoire + fichier;
            // on enregistre le fichier sélectionné
            try {
                BufferedWriter sortie = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(chemin)));
                sortie.write(texte.getText(), 0, texte.getText().length());
                sortie.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        modifie = false;
        editeur.setTitle(copyright + "[" + fichier + "]");
        if (!etat.getText().equals(""))
            etat.setText("");
    }

    // enregistrement sous un autre nom
    private void enregistrerSous() {
        // on ouvre la fenètre de sélection de fichier
        FileDialog dialog = new FileDialog(this, "Enregistrer sous ...",
                FileDialog.SAVE);
        dialog.setFile("*.src");
        dialog.setDirectory(repertoire);
        dialog.show();
        // on extrait le nom du fichier
        if (dialog.getFile() != null) {
            repertoire = dialog.getDirectory();
            fichier = dialog.getFile();
            String chemin = repertoire + fichier;
            // on enregistre le fichier sélectionné
            try {
                BufferedWriter sortie = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(chemin)));
                sortie.write(texte.getText(), 0, texte.getText().length());
                sortie.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        modifie = false;
        editeur.setTitle(copyright + "[" + fichier + "]");
        if (!etat.getText().equals(""))
            etat.setText("");
    }

    // quitter le programme
    public void quitter() {
        enregistrerConfig();
        System.exit(0);
    }

    // enregistrement de la configuration
    private void enregistrerConfig() {
        // on crée les propriétés à enregistrer
        Properties configuration = new Properties();
        configuration.put("fonte", getChaineFonte());
        configuration.put("couleurs", getChaineCouleurs());
        // on enregistre la configuration
        try {
            FileOutputStream out = new FileOutputStream(System.getProperty("cfg"));
            configuration.save(out, copyright);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // renvoie une chaine decrivant la fonte
    private String getChaineFonte() {
        String chaine = texte.getFont().getName() + "," + texte.getFont().getStyle() +
                "," + texte.getFont().getSize();
        return chaine;
    }

    // renvoie une chaine decrivant les couleurs du forum
    private String getChaineCouleurs() {
        String chaine = completer(Integer.toHexString(texte.getBackground().getRed())) +
                completer(Integer.toHexString(texte.getBackground().getGreen())) +
                completer(Integer.toHexString(texte.getBackground().getBlue())) + "," +
                completer(Integer.toHexString(texte.getForeground().getRed())) +
                completer(Integer.toHexString(texte.getForeground().getGreen())) +
                completer(Integer.toHexString(texte.getForeground().getBlue()));
        return chaine;
    }

    // complete une chaine hexa pour avoir 2 caracteres
    private String completer(String s) {
        if (s.length() < 2)
            return "0" + s;
        else
            return s;
    }

    // couper le texte sélectionné
    private void couper() {
        String selection = texte.getSelectedText();
        if (selection != null) {
            // on récupère la chaine sélectionée
            StringSelection stringSelection = new StringSelection(selection);
            this.getToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
            // on efface la chaine sélectionnée
            String chaine = texte.getText();
            int debut = texte.getSelectionStart();
            int fin = texte.getSelectionEnd();
            texte.setText(chaine.substring(0, debut) +
                    chaine.substring(fin, chaine.length()));
            texte.setCaretPosition(debut);
        }
    }

    // copier le texte sélectionné
    private void copier() {
        String selection = texte.getSelectedText();
        if (selection != null) {
            // on récupère la chaine sélectionée
            StringSelection stringSelection = new StringSelection(selection);
            this.getToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
        }
    }

    // coller le texte du presse papier
    private void coller() {
        // on récupère la chaine du clipboard
        Clipboard clipboard = getToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(this);
        String chaine = null;
        try {
            chaine = (String) transferable.getTransferData(
                    DataFlavor.stringFlavor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // si elle est non vide, on la colle dans le texte
        if (chaine != null) {
            int position = texte.getCaretPosition();
            texte.setText(texte.getText().substring(0, position) + chaine +
                    texte.getText().substring(position, texte.getText().length()));
            texte.setCaretPosition(position + chaine.length());
        }
    }

    // rechercher un texte
    private void rechercher() {
        DialogRecherche.afficher(this);
    }

    // recherche de l'occurence suivante du texte
    void next(String recherche, boolean debut, boolean ignoreCase) {
        int index = (debut ? 0 : texte.getCaretPosition() + 1);
        String chaine = texte.getText();
        if (ignoreCase) {
            chaine = chaine.toLowerCase();
            recherche = recherche.toLowerCase();
        }
        int next = chaine.indexOf(recherche, index);
        // si la chaine a été trouvée
        if (next >= 0) {
            // on place le caret à la fin
            texte.setCaretPosition(next + recherche.length());
            // on sélectionne la chaine
            texte.select(next, next + recherche.length());
            texte.requestFocus();
        }
        // si elle n'a pas été trouvée, on beep
        else {
            getToolkit().beep();
            // on place le caret à la fin
            int position = chaine.length();
            texte.setCaretPosition(position);
            texte.select(position, position);
            texte.requestFocus();
        }
    }

    // remplacement de texte
    private void remplacer() {
        System.out.println("Remplacer");
    }

    // compiler le programme édité
    private void compiler() {
        try {
            InputStream entree = new StringBufferInputStream(texte.getText());
            String fichierSortie = repertoire + fichier.substring(
                    0, fichier.lastIndexOf(".")) + ".bin";
            OutputStream sortie = new FileOutputStream(fichierSortie);
            Compilateur.compiler(entree, sortie);
            sortie.close();
            etat.setText("Compilation OK");
        } catch (SyntaxErrorException e) {
            etat.setText(e.toString());
            int ligne = e.getLigne();
            // on recherche la position du caret pour
            // le début et la fin de la ligne
            int debut = 0;
            for (int i = 0; i < ligne; i++) {
                debut = texte.getText().indexOf('\n', debut) + 1;
            }
            int fin = texte.getText().indexOf('\n', debut);
            if (fin == -1)
                fin = texte.getText().length();
            // on place le caret à la fin
            texte.setCaretPosition(fin);
            // on sélectionne la ligne
            texte.select(debut, fin);
            texte.requestFocus();
        } catch (IOException e) {
            etat.setText(e.toString());
        }
    }

    // exécuter le programme édité
    private void executer() {
        thread = new Thread(this);
        thread.start();
    }

    // méthode run pour le thread d'attente de l'ouverture du moteur
    public void run() {
        try {
            // on crée une instance graphique du moteur
            Moteur moteur = new Moteur();
            // on crée une instance de la fenètre
            Fenetre fenetre = new Fenetre(moteur, false);
            // on affiche la fenètre de visualisation
            fenetre.show();
            // on attent l'affichage de la fenetre
            while (!fenetre.isShowing()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // maintenant on charge le fichier binaire et on l'installe
            String chemin = repertoire + fichier.substring(
                    0, fichier.lastIndexOf(".")) + ".bin";
            fenetre.charger(0, chemin);
            int[] adresses = { 0, 0 };
            fenetre.installer(adresses, 100, 1000000, 0);
        } catch (Exception e) {
            etat.setText("Erreur lors de l'ouverture du moteur");
            e.printStackTrace();
        }
    }

    // affichage de l'aide
    private void aide() {
        Help.afficher("Aide Editeur", "aide/aide.txt",
                Color.orange, Color.black);
    }

    // changement de la fonte et des couleurs de l'éditeur
    private void fonte() {
        BoiteConfigurationTextArea.ouvrir(this, texte);
    }

    // affichage de la fenêtre à propos
    private void aPropos() {
        DialogApropos.afficher(editeur, "img/aPropos.gif");
    }

    // gestion des évènements
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        if (cmd.equals("Nouveau")) {
            if (modifie) {
                String titre = "Confirmation";
                String[] lignes = {
                        "Le nouveau fichier va écraser",
                        "le fichier en cours d'édition.",
                        "Confirmez-vous l'opération ?" };
                Confirmation confirmation = new Confirmation(
                        this, titre, lignes, "nouveau", null, imageConfirmation);
                confirmation.show();
            } else
                nouveau();
        } else if (cmd.equals("Ouvrir")) {
            if (modifie) {
                String titre = "Confirmation";
                String[] lignes = {
                        "Le fichier ouvert va écraser",
                        "le fichier en cours d'édition.",
                        "Confirmez-vous l'opération ?" };
                Confirmation confirmation = new Confirmation(
                        this, titre, lignes, "ouvrir", null, imageConfirmation);
                confirmation.show();
            } else
                ouvrir();
        } else if (cmd.equals("Enregistrer")) {
            enregistrer();
        } else if (cmd.equals("Enregistrer sous ...")) {
            enregistrerSous();
        } else if (cmd.equals("Quitter")) {
            if (modifie) {
                String titre = "Confirmation";
                String[] lignes = {
                        "En quittant, vous allez perdre",
                        "le fichier en cours d'édition.",
                        "Confirmez-vous l'opération ?" };
                Confirmation confirmation = new Confirmation(
                        this, titre, lignes, "quitter", null, imageConfirmation);
                confirmation.show();
            } else
                quitter();
        } else if (cmd.equals("Couper")) {
            couper();
        } else if (cmd.equals("Copier")) {
            copier();
        } else if (cmd.equals("Coller")) {
            coller();
        } else if (cmd.equals("Rechercher ...")) {
            rechercher();
        } else if (cmd.equals("Remplacer ...")) {
            remplacer();
        } else if (cmd.equals("Compiler")) {
            compiler();
        } else if (cmd.equals("Exécuter")) {
            executer();
        } else if (cmd.equals("Aide")) {
            aide();
        } else if (cmd.equals("Fonte")) {
            fonte();
        } else if (cmd.equals("A propos ...")) {
            aPropos();
        }
    }

    public void textValueChanged(TextEvent evt) {
        if (chargement)
            chargement = false;
        else {
            if (!modifie) {
                editeur.setTitle(copyright + "[" + fichier + " *]");
            }
            modifie = true;
            if (!etat.getText().equals(""))
                etat.setText("");
        }
    }

    public void windowClosing(WindowEvent e) {
        if (modifie) {
            String titre = "Confirmation";
            String[] lignes = {
                    "En quittant, vous allez perdre",
                    "le fichier en cours d'édition.",
                    "Confirmez-vous l'opération ?" };
            Confirmation confirmation = new Confirmation(
                    this, titre, lignes, "quitter", null, imageConfirmation);
            confirmation.show();
        } else
            quitter();
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    // méthode d'ajout d'un composant au GradBagLayout
    public static void ajouter(Container container, Component component,
            int gridx, int gridy, int gridwidth, int gridheight, int fill,
            int anchor, int weightx, int weighty) {
        GridBagLayout gbl = (GridBagLayout) container.getLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.fill = fill;
        gbc.anchor = anchor;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(component, gbc);
        container.add(component);
    }

    // decoupage d'une chaine en jetons
    public static String[] decouperChaine(String chaine, String separateur) {
        StringTokenizer st = new StringTokenizer(chaine, separateur);
        String[] mots = new String[st.countTokens()];
        for (int i = 0; i < mots.length; i++) {
            mots[i] = st.nextToken();
        }
        return mots;
    }
}
