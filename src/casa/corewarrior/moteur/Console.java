/********************************************************************
* Console (C) Michel CASABIANCA 1998                                *
********************************************************************/

package casa.corewarrior.moteur;

import java.awt.*;
import java.awt.event.*;
import casa.awt.*;
import java.io.*;

final class Console extends Panel
implements ActionListener
{
  // couleur du programme controlé par la console
  private int couleur;
  // référence de la fenètre
  private Fenetre fenetre;
  // longueur de la liste de dump
  public static final int etendue=18;
  // adresse de la première case de la liste de dump
  private int dumpIP=-2*etendue;

  // éléments de l'interface utilisateur
  private Dessin titre;
  private Button boutonCharger=new Button("Charger");
  private Button boutonPurger=new Button("Purger");
  public TextField textNom=new TextField();
  private List dump=new List(etendue,false);
	private Dessin feu=new Dessin(fichiersFeu);
	private Label labelIP=new Label("IP :  ");
  private TextField textIP=new TextField(4);

  // répertoire courant recherche
  private static String repertoire="";
  // noms des fichiers image des feux
	private static final String[] fichiersFeu=
  	{"images/feuRouge.gif","images/feuVert.gif"};

  // constructeur
	Console(Fenetre fenetre,int couleur,String fichier)
	{
		this.couleur=couleur;
    this.fenetre=fenetre;
    initGUI(fichier);
	}

  // initialisation de l'interface utilisateur
  private void initGUI(String fichier)
  {
  	// on ajoute les éléments de l'interface
  	setLayout(new GridBagLayout());
    titre=new Dessin(fichier);
    ajouter(this,titre,0,0,3,1,GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTH,0,0);
    Panel barreBoutons=new Panel();
    barreBoutons.setLayout(new GridLayout(1,2,5,0));
    barreBoutons.add(boutonCharger);
    barreBoutons.add(boutonPurger);
    ajouter(this,barreBoutons,0,1,3,1,GridBagConstraints.NONE,
			GridBagConstraints.NORTH,0,0);
    ajouter(this,textNom,0,2,3,1,GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTH,0,0);
    ajouter(this,dump,0,3,3,1,GridBagConstraints.NONE,
			GridBagConstraints.CENTER,1,1);
    ajouter(this,feu,0,4,1,1,GridBagConstraints.NONE,
			GridBagConstraints.CENTER,0,0);
    ajouter(this,labelIP,1,4,1,1,GridBagConstraints.NONE,
			GridBagConstraints.SOUTHEAST,0,0);
    ajouter(this,textIP,2,4,1,1,GridBagConstraints.HORIZONTAL,
			GridBagConstraints.SOUTHEAST,0,0);
    // on rend les TextFields non éditables
    textNom.setEditable(false);
    textIP.setEditable(false);
    // on enregistre la console auprès du bouton
    boutonCharger.addActionListener(this);
    boutonPurger.addActionListener(this);
  }

  // met à jour le contenu d'une case mémoire
  public void miseAjour(int ip)
  {
  	if(ip>=dumpIP && ip<dumpIP+etendue)
    {
    	dump.replaceItem(fenetre.moteur.dump(ip),ip-dumpIP);
    }
    else if(ip+Moteur.TAILLE>=dumpIP && ip+Moteur.TAILLE<dumpIP+etendue)
		{
    	dump.replaceItem(fenetre.moteur.dump(ip),ip-dumpIP+Moteur.TAILLE);
    }
  }

  // affiche un dump de la mémoire dans la liste
  public void dump(int ip)
  {
  	// si l'IP est dans la liste, on le sélectionne
  	if(ip>=dumpIP && ip<dumpIP+etendue)
    {
    	dump.select(ip-dumpIP);
    }
    else if(ip+Moteur.TAILLE>=dumpIP && ip+Moteur.TAILLE<dumpIP+etendue)
		{
    	dump.select(ip-dumpIP+Moteur.TAILLE);
    }
    // sinon, on réaffiche toute la liste
    else
    {
    	// on calcule l'IP du haut de la liste
      ip=((ip-etendue/2)+Moteur.TAILLE)%Moteur.TAILLE;
  		String[] dump=fenetre.moteur.dump(ip,etendue);
  		this.dump.removeAll();
  		for(int i=0;i<etendue;i++) this.dump.add(dump[i]);
    	this.dump.select(etendue/2);
      dumpIP=ip;
    }
  }

  // fixe l'IP du programme
  public void setIP(int ip) {textIP.setText(Integer.toString(ip));}

  // fixe le trait
  public void setTrait(boolean etat) {feu.setImage(etat?1:0);}

  // affiche le programme chargé
  public void lister()
  {
  	dump.removeAll();
    String[] programme=fenetre.moteur.programme(couleur);
    for(int i=0;i<programme.length;i++) dump.add(programme[i]);
  }

  // désactive les boutons de chargement et de purge
  public void desactiver()
  {
  	boutonCharger.setEnabled(false);
    boutonPurger.setEnabled(false);
  }

  // réactive les boutons de chargement et de purge
  public void reactiver()
  {
  	boutonCharger.setEnabled(true);
    boutonPurger.setEnabled(true);
  }

  // méthode d'interception des évènements
  public void actionPerformed(ActionEvent evt)
  {
  	String cmd=evt.getActionCommand();
		if(cmd.equals("Charger"))
		{
    	// on ouvre un FileDialog
      FileDialog dialog=new FileDialog(fenetre,"Ouvrir",FileDialog.LOAD);
			dialog.setFile("*.bin");
			dialog.setDirectory(repertoire);
			dialog.show();
			// on extrait le nom du fichier
      String chemin="";
			if(dialog.getFile()!=null)
			{
      	try
        {
					repertoire=dialog.getDirectory();
					String fichier=dialog.getFile();
					chemin=repertoire+fichier;
        	// on demande au moteur le chargement du programme
        	fenetre.charger(couleur,chemin);
        }
        catch(IOException e)
        {
        	String[] message={"Erreur lors du chargement du programme ",
          	chemin};
        	InfoDialog.afficher(fenetre,"Erreur",message);
        }
      }
    }
    else if(cmd.equals("Purger"))
		{
    	fenetre.purger(couleur);
      textNom.setText("");
      dump.removeAll();
      textIP.setText("");
      dumpIP=-2*etendue;
    }
  }

  // méthode d'ajout d'un composant au GradBagLayout
	public static void ajouter(Container container,Component component,
		int gridx,int gridy,int gridwidth,int gridheight,int fill,
		int anchor,int weightx,int weighty)
	{
		GridBagLayout gbl=(GridBagLayout)container.getLayout();
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridx=gridx;
		gbc.gridy=gridy;
		gbc.gridwidth=gridwidth;
		gbc.gridheight=gridheight;
		gbc.fill=fill;
		gbc.anchor=anchor;
		gbc.weightx=weightx;
		gbc.weighty=weighty;
		gbc.insets=new Insets(5,5,5,5);
		gbl.setConstraints(component,gbc);
		container.add(component);
	}
}
