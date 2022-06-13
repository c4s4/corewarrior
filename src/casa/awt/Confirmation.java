package casa.awt;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

public class Confirmation extends Dialog
        implements WindowListener, ActionListener {
    // éléments de l'interface utilisateur
    private Button boutonOui = new Button("Oui");
    private Button boutonNon = new Button("Non");
    // frame ayant appelé le Dialog
    public Frame frame;
    // méthodes à appeler
    private Method methodeOui;
    private Method methodeNon;

    // constructeur : crée et ajoute les composants de l'interface
    public Confirmation(Frame frame, String titre, String[] lignes,
            String methodeOui, String methodeNon, String image) {
        super(frame, titre, true);
        this.frame = frame;
        // on recherche les méthodes Oui et Non
        if (methodeOui != null) {
            try {
                this.methodeOui = frame.getClass().getMethod(methodeOui, new Class[0]);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (methodeNon != null) {
            try {
                this.methodeNon = frame.getClass().getMethod(methodeNon, new Class[0]);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        // on ajoute le message dans le Dialog
        setLayout(new BorderLayout(10, 10));
        Panel panelMessage = new Panel();
        panelMessage.setLayout(new GridLayout(lignes.length, 1));
        for (int i = 0; i < lignes.length; i++)
            panelMessage.add(new Label(lignes[i]));
        add("Center", panelMessage);
        // on ajoute les boutons dans le Dialog
        Panel panelBoutons = new Panel();
        panelBoutons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBoutons.add(boutonOui);
        panelBoutons.add(boutonNon);
        add("South", panelBoutons);
        // on ajoute le dessin à gauche
        Dessin dessin = new Dessin(image);
        add("West", dessin);
        // on pack le Dialog et on le centre sur la frame
        pack();
        setResizable(false);
        Rectangle a = frame.getBounds();
        Rectangle b = getBounds();
        setLocation(a.x + (a.width - b.width) / 2, a.y + (a.height - b.height) / 2);
        // on enregistre les listeners
        boutonOui.addActionListener(this);
        boutonNon.addActionListener(this);
        addWindowListener(this);
    }

    // gestion des évènements
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        if (cmd.equals("Oui")) {
            if (methodeOui != null) {
                try {
                    methodeOui.invoke(frame, new Object[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dispose();
        } else if (cmd.equals("Non")) {
            if (methodeNon != null) {
                try {
                    methodeNon.invoke(frame, new Object[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dispose();
        }
    }

    // méthodes pour implémenter l'interface WindowListener
    public void windowClosing(WindowEvent evt) {
        if (methodeNon != null) {
            try {
                methodeNon.invoke(frame, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
