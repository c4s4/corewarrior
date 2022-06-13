package casa.awt;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public final class Help extends Frame implements WindowListener {
    // éléments de l'interface
    TextArea texte = new TextArea(30, 55);
    // référence sur la fenètre affichée
    private static Help fenetre = null;

    // méthode statique d'invocation
    public static void afficher(String titre, String fichier,
            Color text, Color fond) {
        // si une fenètre est déja affichée, on la sélectionne
        if (fenetre != null)
            fenetre.requestFocus();
        // sinon on en crée une nouvelle
        else {
            fenetre = new Help(titre, fichier, text, fond);
            fenetre.show();
        }
    }

    // constructeur : on lui passe le titre et le nom du fichier
    public Help(String titre, String fichier,
            Color text, Color fond) {
        super(titre);
        texte.setFont(new Font("monospaced", 10, Font.PLAIN));
        texte.setEditable(false);
        texte.setForeground(text);
        texte.setBackground(fond);
        addWindowListener(this);
        // on charge le texte d'aide
        try {
            URL url = ClassLoader.getSystemResource(fichier);
            BufferedReader entree = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            StringBuffer buffer = new StringBuffer();
            String ligne;
            while ((ligne = entree.readLine()) != null) {
                buffer.append(ligne);
                buffer.append("\n");
            }
            entree.close();
            texte.setText(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // on construit l'interface
        add("Center", texte);
        setFont(Defaults.fonte);
        // on redimensionne et centre la fenêtre
        pack();
        Dimension a = getToolkit().getScreenSize();
        Rectangle b = getBounds();
        setLocation((a.width - b.width) / 2, (a.height - b.height) / 2);
    }

    public void windowClosing(WindowEvent evt) {
        fenetre = null;
        dispose();
    }

    public void windowOpened(WindowEvent evt) {
    }

    public void windowClosed(WindowEvent evt) {
    }

    public void windowIconified(WindowEvent evt) {
    }

    public void windowDeiconified(WindowEvent evt) {
    }

    public void windowActivated(WindowEvent evt) {
    }

    public void windowDeactivated(WindowEvent evt) {
    }
}
