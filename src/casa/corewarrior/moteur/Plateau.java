package casa.corewarrior.moteur;

import java.awt.*;

public class Plateau extends Canvas {
    // références de la fenetre
    private Fenetre fenetre;
    // dimensions du plateau
    private static final int LARGEUR = 64, HAUTEUR = 64;
    // coté d'une case
    private static final int COTE = 7;
    // image du plateau
    public Image image;
    // couleurs du plateau
    private static final Color couleurFond = Color.lightGray;
    // couleurs des programmes
    private static final Color[] couleurs = { Color.red, Color.green };
    // thread de dessin
    private Thread thread;
    // ordre de dessin
    private int ordre;
    // constantes pour ordre
    private static final int NETTOYER = 0, UPDATE = 1;
    // ip des programmes pour mise à jour turbo
    int[] ip;

    // constructeur du plateau
    public Plateau(Fenetre fenetre) {
        this.fenetre = fenetre;
    }

    // méthode update surchargée
    public void update(Graphics g) {
        paint(g);
    }

    // affichage du plateau
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    // création de l'image pour double buffering
    public void creerImage() {
        if (image == null) {
            image = createImage(LARGEUR * COTE, HAUTEUR * COTE);
            // on attend la création de l'image
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // on dessine le fond de la grille
            nettoyer();
        }
    }

    // dessine le fond de la grille
    public void nettoyer() {
        // on dessine le fond
        Graphics g = image.getGraphics();
        g.setColor(couleurFond);
        // on trace le quadrillage
        g.setColor(couleurFond);
        for (int y = 0; y < HAUTEUR; y++) {
            for (int x = 0; x < LARGEUR; x++) {
                g.fill3DRect(x * COTE, y * COTE, COTE, COTE, false);
            }
        }
        repaint();
    }

    // dessine le fond de la grille
    public void miseAjourTurbo(int[] ip) {
        // on dessine le fond
        Graphics g = image.getGraphics();
        g.setColor(couleurFond);
        // on trace le quadrillage
        g.setColor(couleurFond);
        for (int y = 0; y < HAUTEUR; y++) {
            for (int x = 0; x < LARGEUR; x++) {
                if (fenetre.moteur.getMem(x + 64 * y) == 0)
                    g.fill3DRect(x * COTE, y * COTE, COTE, COTE, false);
                else
                    g.fill3DRect(x * COTE, y * COTE, COTE, COTE, true);
            }
        }
        for (int i = 0; i < 2; i++) {
            if (ip[i] > -1)
                dessinerIP(i, ip[i]);
        }
        repaint();
    }

    // mise à jour du plateau
    public void miseAjour(int couleur, int adresse, int longueur, int ip, int oldIP) {
        if (adresse >= 0)
            for (int i = 0; i < longueur; i++)
                dessiner(couleur, adresse + i);
        dessiner(couleur, oldIP);
        if (ip >= 0)
            dessinerIP(couleur, ip);
        repaint();
    }

    // dessine une case d'une couleur
    private void dessiner(int couleur, int adresse) {
        int x = (adresse % LARGEUR) * COTE;
        int y = (adresse / HAUTEUR) * COTE;
        Graphics g = image.getGraphics();
        g.setColor(couleurs[couleur]);
        g.fill3DRect(x, y, COTE, COTE, true);
    }

    // dessine une case d'IP
    private void dessinerIP(int couleur, int ip) {
        int x = (ip % LARGEUR) * COTE;
        int y = (ip / HAUTEUR) * COTE;
        Graphics g = image.getGraphics();
        g.setColor(couleurs[couleur]);
        g.fill3DRect(x, y, COTE, COTE, true);
        g.setColor(Color.black);
        g.fillRect(x + 2, y + 2, COTE - 4, COTE - 4);
    }

    // retourne les dimensions souhaitées
    public Dimension getPreferredSize() {
        return new Dimension(LARGEUR * COTE, HAUTEUR * COTE);
    }

    // retourne les dimensions minimales
    public Dimension getMinimumSize() {
        return new Dimension(LARGEUR * COTE, HAUTEUR * COTE);
    }

    // retourne les dimensions souhaitées
    public Dimension getMaximumSize() {
        return new Dimension(LARGEUR * COTE, HAUTEUR * COTE);
    }
}
