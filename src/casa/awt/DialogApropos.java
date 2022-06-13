/********************************************************************
* DialogApropos (C) Michel CASABIANCA 1998                          *
********************************************************************/

package casa.awt;

import java.awt.*;
import java.awt.event.*;
import casa.awt.*;

public final class DialogApropos extends Dialog
implements WindowListener,ActionListener
{
	// �l�ments de l'interface graphique
	private Dessin dessin;
	private Button boutonOK=new Button(" OK ");
	// r�f�rence sur la fen�tre affich�e
	private static DialogApropos fenetre=null;
	
	// m�thode statique d'invocation
	public static void afficher(Frame frame,String image)
	{
		// si une fen�tre est d�ja affich�e, on la s�lectionne
		if(fenetre!=null) fenetre.requestFocus();
		// sinon on en cr�e une nouvelle
		else 
		{
			fenetre=new DialogApropos(frame,image);
			fenetre.show();
		}
	}
	
	// constructeur
	DialogApropos(Frame frame,String image)
	{
		super(frame,"A propos ...",false);
		// initialisation de l'interface graphique
		dessin=new Dessin(image);
		add("Center",dessin);
		Panel barreBoutons=new Panel();
		barreBoutons.setLayout(new FlowLayout());
		barreBoutons.add(boutonOK);
		add("South",barreBoutons);
		// on pack la fen�tre et on la recentre
		pack();
		setResizable(false);
		Rectangle a=frame.getBounds();
		Rectangle b=getBounds();
		setLocation(a.x+(a.width-b.width)/2,a.y+(a.height-b.height)/2);
		addWindowListener(this);
		boutonOK.addActionListener(this);
	}
	
	// m�thode pour impl�menter l'Actionlistener
	public void actionPerformed(ActionEvent evt)
	{
		String cmd=evt.getActionCommand();
		if(cmd.equals(" OK ")) 
		{
			fenetre=null;
			dispose();
		}
	}
	// m�thodes pour impl�menter l'interface WindowListener
	public void windowClosing(WindowEvent evt) 
	{
		fenetre=null;
		dispose();
	}
  public void windowOpened(WindowEvent evt) {}
  public void windowClosed(WindowEvent evt) {}
  public void windowIconified(WindowEvent evt) {}
  public void windowDeiconified(WindowEvent evt) {}
  public void windowActivated(WindowEvent evt) {}
  public void windowDeactivated(WindowEvent evt) {}
}
