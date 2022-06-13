package casa.awt;

import java.awt.*;
import java.awt.event.*;

public class InfoDialog extends Dialog
        implements WindowListener, ActionListener {
    // éléments de l'interface utilisateur
    private Button boutonOK = new Button(" OK ");
    // frame ayant appelé le Dialog
    public Frame frame;

    // méthode statique d'affichage
    public static void afficher(Frame frame, String titre, String[] lignes) {
        InfoDialog info = new InfoDialog(frame, titre, lignes);
        info.show();
    }

    // constructeur : crée et ajoute les composants de l'interface
    public InfoDialog(Frame frame, String titre, String[] lignes) {
        super(frame, titre, true);
        this.frame = frame;
        // on ajoute le message dans le Dialog
        setLayout(new BorderLayout(10, 10));
        Panel panelMessage = new Panel();
        panelMessage.setLayout(new GridLayout(lignes.length, 1));
        for (int i = 0; i < lignes.length; i++)
            panelMessage.add(new Label(lignes[i]));
        add("Center", panelMessage);
        // on ajoute le boutons dans le Dialog
        Panel panelBoutons = new Panel();
        panelBoutons.add(boutonOK);
        add("South", panelBoutons);
        setFont(Defaults.fonte);
        // on pack le Dialog et on le centre sur la frame
        pack();
        setResizable(false);
        Rectangle a = frame.getBounds();
        Rectangle b = getBounds();
        setLocation(a.x + (a.width - b.width) / 2, a.y + (a.height - b.height) / 2);
        // on enregistre les listeners
        boutonOK.addActionListener(this);
        addWindowListener(this);
    }

    // retourne les marges
    public Insets insets() {
        return new Insets(30, 15, 5, 15);
    }

    // gestion des évènements
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        if (cmd.equals(" OK "))
            dispose();
    }

    // méthodes pour implémenter l'interface WindowListener
    public void windowClosing(WindowEvent evt) {
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
