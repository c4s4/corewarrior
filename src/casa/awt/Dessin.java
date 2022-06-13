/********************************************************************
* Dessin (C) Michel CASABIANCA 1998                                 *
********************************************************************/

package casa.awt;

import java.awt.*;
import java.net.*;

public final class Dessin extends Canvas
{
	// image du bouton à afficher
	private Image[] image=new Image[1];
  // numéro de l'image à afficher
  private int numero=0;

	// constructeur avec une image
	public Dessin(String fichier)
	{
		// on charge l'image de fond
		MediaTracker tracker=new MediaTracker(this);
		URL url=ClassLoader.getSystemResource(fichier);
		image[0]=getToolkit().getImage(url);
		tracker.addImage(image[0],0);
		try {tracker.waitForID(0);}
		catch(InterruptedException e) {e.printStackTrace();}
	}

  // constructeur deux images
	public Dessin(String[] fichiers)
	{
		// on charge l'image de fond
		MediaTracker tracker=new MediaTracker(this);
    image=new Image[fichiers.length];
    for(int i=0;i<fichiers.length;i++)
    {
			URL url=ClassLoader.getSystemResource(fichiers[i]);
			image[i]=getToolkit().getImage(url);
			tracker.addImage(image[i],0);
    }
		try {tracker.waitForID(0);}
		catch(InterruptedException e) {e.printStackTrace();}
	}

  // détermine l'image affichée
  public void setImage(int numero)
  {
  	if(this.numero!=numero)
    {
    	this.numero=numero;
      repaint();
    }
  }
	
	// retourne les dimensions souhaitées
	public Dimension getPreferredSize() 
	{
		return new Dimension(image[0].getWidth(this),image[0].getHeight(this));
	}

	// retourne les dimensions minimales
	public Dimension getMinimumSize()
	{
		return new Dimension(image[0].getWidth(this),image[0].getHeight(this));
	}

	// retourne les dimensions maximales
	public Dimension getMaximumSize()
	{
		return new Dimension(image[0].getWidth(this),image[0].getHeight(this));
	}

	// dessin de l'image et du cadre
	public void paint(Graphics g)
	{
		g.drawImage(image[numero],0,0,this);
	}
}
