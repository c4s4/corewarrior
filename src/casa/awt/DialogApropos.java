package casa.awt;

import java.awt.*;
import java.awt.event.*;

public final class DialogApropos extends Dialog
        implements WindowListener, ActionListener {
    // éléments de l'interface graphique
    private Dessin dessin;
    private Button boutonOK = new Button(" OK ");
    // référence sur la fenètre affichée
    private static DialogApropos fenetre = null;

    // méthode statique d'invocation
    public static void afficher(Frame frame, String image) {
        // si une fenètre est déja affichée, on la sélectionne
        if (fenetre != null)
            fenetre.requestFocus();
        // sinon on en crée une nouvelle
        else {
            fenetre = new DialogApropos(frame, image);
            fenetre.show();
        }
    }

    // constructeur
    DialogApropos(Frame frame, String image) {
        super(frame, "A propos ...", false);
        // initialisation de l'interface graphique
        dessin = new Dessin(image);
        add("Center", dessin);
        Panel barreBoutons = new Panel();
        barreBoutons.setLayout(new FlowLayout());
        barreBoutons.add(boutonOK);
        add("South", barreBoutons);
        // on pack la fenètre et on la recentre
        pack();
        setResizable(false);
        Rectangle a = frame.getBounds();
        Rectangle b = getBounds();
        setLocation(a.x + (a.width - b.width) / 2, a.y + (a.height - b.height) / 2);
        addWindowListener(this);
        boutonOK.addActionListener(this);
    }

    // méthode pour implémenter l'Actionlistener
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        if (cmd.equals(" OK ")) {
            fenetre = null;
            dispose();
        }
    }

    // méthodes pour implémenter l'interface WindowListener
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
