/********************************************************************
* GraphButton (C) Michel CASABIANCA 1998                            *
********************************************************************/

package casa.awt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

public final class GraphButton extends Canvas
{
	// image du bouton à afficher
	private Image image;
  // image du bouton désélectionné
	private Image image2;
	// action à lancer
	private String action="";
	// ActionListeners enregistrés pour envoyer les ActionEvents
	private Vector actionListeners=new Vector();

	// constructeur avec arguments
	public GraphButton(String action,String fichier)
	{
		this.action=action;
		// on charge l'image de fond
		URL url=ClassLoader.getSystemResource(fichier);
		MediaTracker tracker=new MediaTracker(this);
		image=getToolkit().getImage(url);
		tracker.addImage(image,0);
		try {tracker.waitForID(0);}
		catch(InterruptedException e) {e.printStackTrace();}
		// on permet les évènements
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

  // constructeur avec arguments
	public GraphButton(String action,String fichier1,String fichier2)
	{
		this.action=action;
		// on charge l'image de fond
		URL url1=ClassLoader.getSystemResource(fichier1);
		URL url2=ClassLoader.getSystemResource(fichier2);
		MediaTracker tracker=new MediaTracker(this);
		image=getToolkit().getImage(url1);
		tracker.addImage(image,0);
    image2=getToolkit().getImage(url2);
		tracker.addImage(image2,0);
		try {tracker.waitForID(0);}
		catch(InterruptedException e) {e.printStackTrace();}
		// on permet les évènements
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	// retourne les dimensions souhaitées
	public Dimension getPreferredSize()
	{
		return new Dimension(image.getWidth(this),image.getWidth(this));
	}

	// retourne les dimensions minimales
	public Dimension getMinimumSize()
	{
		return new Dimension(image.getWidth(this),image.getWidth(this));
	}

	// surcharge de setEnabled() pour redessiner le bouton
	public void setEnabled(boolean state)
	{
		super.setEnabled(state);
		repaint();
	}

	// dessin de l'image et du cadre
	public void paint(Graphics g)
	{
		if(isEnabled()) g.drawImage(image,0,0,this);
    else if(image2!=null) g.drawImage(image2,0,0,this);
	}

	// ajoute d'un ActionListener au bouton
	public synchronized void addActionListener(ActionListener listener)
	{
		actionListeners.addElement(listener);
	}

	// gère les clics sur le composant
	protected void processMouseEvent(MouseEvent evt)
	{
  	if(!isEnabled()) return;
		if(evt.getID()==MouseEvent.MOUSE_ENTERED)
		{
			Graphics g=getGraphics();
			g.setColor(getBackground());
			Rectangle bounds=getBounds();
			g.draw3DRect(0,0,bounds.width-1,bounds.height-1,true);
		}
		else if(evt.getID()==MouseEvent.MOUSE_EXITED)
		{
			Graphics g=getGraphics();
			g.setColor(getBackground());
			Rectangle bounds=getBounds();
			g.drawRect(0,0,bounds.width-1,bounds.height-1);
		}
		else if(evt.getID()==MouseEvent.MOUSE_PRESSED)
		{
			Graphics g=getGraphics();
			g.setColor(getBackground());
			Rectangle bounds=getBounds();
			g.draw3DRect(0,0,bounds.width-1,bounds.height-1,false);
		}
		else if(evt.getID()==MouseEvent.MOUSE_RELEASED)
		{
			Graphics g=getGraphics();
			g.setColor(getBackground());
			Rectangle bounds=getBounds();
			if(contains(evt.getX(),evt.getY()))
				g.draw3DRect(0,0,bounds.width-1,bounds.height-1,true);
		}
		else if(evt.getID()==MouseEvent.MOUSE_CLICKED)
		{
			ActionEvent event=new ActionEvent(this,ActionEvent.ACTION_PERFORMED,
				action);
			for(int i=0;i<actionListeners.size();i++)
			{
				((ActionListener)actionListeners.elementAt(i)).
					actionPerformed(event);
			}
		}
	}
}
