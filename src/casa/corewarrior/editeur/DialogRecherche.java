/********************************************************************
* DialogRecherche (C) Michel CASABIANCA 1998                        *
********************************************************************/

package casa.corewarrior.editeur;

import java.awt.*;
import java.awt.event.*;

class DialogRecherche extends Dialog 
implements WindowListener,ActionListener
{
	// éléments de l'interface graphique
	private Label labelRechercher=new Label("Rechercher : ");
	private TextField textRechercher=new TextField();
	private Checkbox debut=new Checkbox("Du début");
	private Checkbox casse=new Checkbox("Ignorer Casse");
	private Button boutonRechercher=new Button("Rechercher");
	private Button boutonOK=new Button("OK");
	// référence sur la fenètre affichée
	private static DialogRecherche fenetre=null;
	// référence de l'objet CoreWarrior
	private static Editeur editeur=null;
	
	// méthode statique d'invocation
	public static void afficher(Frame frame)
	{
		// si une fenètre est déja affichée, on la sélectionne
		if(fenetre!=null) fenetre.requestFocus();
		// sinon on en crée une nouvelle
		else 
		{
			editeur=(Editeur) frame;
			fenetre=new DialogRecherche(frame);
			fenetre.show();
		}
	}
	
	// constructeur : Frame parent
	DialogRecherche(Frame frame)
	{
		super(frame,"Rechercher",false);
		// initialisation de l'interface graphique
		setLayout(new GridBagLayout());
		Editeur.ajouter(this,labelRechercher,0,0,1,1,
			GridBagConstraints.NONE,GridBagConstraints.WEST,0,0);
		Editeur.ajouter(this,textRechercher,1,0,3,1,
			GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,0,0);
		Editeur.ajouter(this,debut,0,1,1,1,
			GridBagConstraints.NONE,GridBagConstraints.CENTER,0,0);
		Editeur.ajouter(this,casse,1,1,1,1,
			GridBagConstraints.NONE,GridBagConstraints.CENTER,0,0);
		Editeur.ajouter(this,boutonRechercher,2,1,1,1,
			GridBagConstraints.NONE,GridBagConstraints.EAST,0,0);
		Editeur.ajouter(this,boutonOK,3,1,1,1,
			GridBagConstraints.NONE,GridBagConstraints.EAST,0,0);
		// on pack la fenètre et on la recentre
		pack();
		setResizable(false);
		// on centre la fenetre
		Rectangle a=frame.getBounds();
		Rectangle b=getBounds();
		setLocation(a.x+(a.width-b.width)/2,a.y+(a.height-b.height)/2);
		// on enregistre les listeners
		addWindowListener(this);
		boutonRechercher.addActionListener(this);
		boutonOK.addActionListener(this);
	}
	
	// méthode pour implémenter l'Actionlistener
	public void actionPerformed(ActionEvent evt)
	{
		String cmd=evt.getActionCommand();
		// si on a cliqué sur le bouton [Rechercher]
		if(cmd.equals("Rechercher") && !textRechercher.getText().equals(""))
		{
			((Editeur) editeur).next(textRechercher.getText(),
				debut.getState(),casse.getState());
			debut.setState(false);
		}
		// si on a cliqué sur le bouton [OK]
		else if(cmd.equals("OK"))
		{
			fenetre=null;
			dispose();
		}
	}
	// méthodes pour implémenter l'interface WindowListener
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
