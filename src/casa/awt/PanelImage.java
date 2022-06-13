package casa.awt;

import java.awt.*;
import java.net.*;

public class PanelImage extends Panel {
    private Image image;

    // constructeur : lui passer le chemin du fichier image
    public PanelImage(String fichier) {
        // on charge l'image de fond
        URL url = ClassLoader.getSystemResource(fichier);
        MediaTracker tracker = new MediaTracker(this);
        image = getToolkit().getImage(url);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // méthode update surclassée ne repaint pas le fond
    public void update(Graphics g) {
        // on appelle la méthode paint()
        paint(g);
    }

    // dessin du fond de la barre de boutons
    public void paint(Graphics g) {
        // on calcule le nombre d'images horizontalement et verticalement
        Rectangle bounds = getBounds();
        int a = bounds.width / image.getWidth(this);
        int b = bounds.height / image.getHeight(this);
        // on dessine l'image du fond
        for (int y = 0; y <= b; y++) {
            for (int x = 0; x <= a; x++) {
                g.drawImage(image, x * image.getWidth(this),
                        y * image.getHeight(this), this);
            }
        }
    }
}
