/********************************************************************
* DialogInstaller (C) Michel CASABIANCA 1998                        *
********************************************************************/

package casa.corewarrior.moteur;

import java.awt.*;
import java.awt.event.*;
import casa.awt.*;

class DialogInstaller extends Dialog
implements WindowListener,ActionListener
{
	// éléments de l'interface graphique
  CheckboxGroup groupAdresse=new CheckboxGroup();
  Checkbox aleatoire=new Checkbox("Aléatoire",groupAdresse,true);
  Checkbox adresses=new Checkbox("Aux adresses :",groupAdresse,false);
  TextField[] adresse={new TextField("0",4),new TextField("0",4)};
  CheckboxGroup groupTrait=new CheckboxGroup();
  Checkbox traitAleatoire=new Checkbox("Aléatoire",groupTrait,true);
  Checkbox[] traits=new Checkbox[2];
  TextField distance=new TextField("100",4);
  TextField nul=new TextField("1000000",7);
  Button boutonOK=new Button(" OK ");
  Button boutonAnnuler=new Button("Annuler");
	// référence sur la fenètre affichée
	private static DialogInstaller dialog;
	// référence à la fenètre du moteur
	private static Fenetre fenetre;

	// méthode statique d'invocation
	public static void afficher(Frame frame)
	{
		fenetre=(Fenetre) frame;
		dialog=new DialogInstaller(fenetre);
		dialog.show();
	}

	// constructeur
	DialogInstaller(Fenetre fenetre)
	{
		super(fenetre,"Installation",true);
    // calcul du nombre de programmes chargés
    int nombre=0;
    for(int i=0;i<2;i++)
    	if(fenetre.charges[i]) nombre++;
		// initialisation de l'interface graphique
		setLayout(new GridBagLayout());
		ajouter(this,aleatoire,0,0,2,1,
			GridBagConstraints.NONE,GridBagConstraints.WEST,0,0);
		ajouter(this,adresses,0,1,2,1,
			GridBagConstraints.NONE,GridBagConstraints.WEST,0,0);
    Panel panelAdresses=new Panel();
    panelAdresses.setLayout(new GridLayout(nombre,2,5,5));
    for(int i=0;i<2;i++)
    {
    	if(fenetre.charges[i])
      {
      	panelAdresses.add(new Label(fenetre.moteur.noms[i]));
        panelAdresses.add(adresse[i]);
      }
    }
    ajouter(this,panelAdresses,0,2,2,1,
			GridBagConstraints.NONE,GridBagConstraints.EAST,0,0);
    // on construit les Checkbox pour le trait
    Panel panelTrait=new Panel();
    panelTrait.setLayout(new GridLayout(4,1));
    if(fenetre.charges[0] && fenetre.charges[1])
    {
    	panelTrait.add(new Label("Trait :"));
      traits[0]=new Checkbox(fenetre.moteur.noms[0],groupTrait,false);
			traits[1]=new Checkbox(fenetre.moteur.noms[1],groupTrait,false);
			panelTrait.add(traitAleatoire);      
      panelTrait.add(traits[0]);
      panelTrait.add(traits[1]);
    }
    ajouter(this,panelTrait,0,3,2,1,
			GridBagConstraints.NONE,GridBagConstraints.WEST,0,0);
    // 
    ajouter(this,new Label("Distance minimale"),0,4,1,1,
			GridBagConstraints.NONE,GridBagConstraints.WEST,0,0);
    ajouter(this,distance,1,4,1,1,
			GridBagConstraints.NONE,GridBagConstraints.EAST,0,0);
    ajouter(this,new Label("Cycles avant nul"),0,5,1,1,
			GridBagConstraints.NONE,GridBagConstraints.WEST,0,0);
    ajouter(this,nul,1,5,1,1,
			GridBagConstraints.NONE,GridBagConstraints.EAST,0,0);
    Panel barreBoutons=new Panel();
    barreBoutons.setLayout(new FlowLayout(FlowLayout.RIGHT,5,10));
    barreBoutons.add(boutonOK);
    barreBoutons.add(boutonAnnuler);
    ajouter(this,barreBoutons,0,6,2,1,
			GridBagConstraints.NONE,GridBagConstraints.EAST,0,0);
		// on pack la fenètre et on la recentre
		pack();
		setResizable(false);
		Rectangle a=fenetre.getBounds();
		Rectangle b=getBounds();
		setLocation(a.x+(a.width-b.width)/2,a.y+(a.height-b.height)/2);
    // on enregistre les Listeners
		addWindowListener(this);
		boutonOK.addActionListener(this);
    boutonAnnuler.addActionListener(this);
    for(int i=0;i<2;i++) adresse[i].addActionListener(this);
	}

  public Insets insets() {return new Insets(30,10,0,10);}

  public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource()==boutonOK)
		{
    	try
      {
    		// on fait la liste des adresses (null si aléatoire)
      	int[] listeAdresses;
      	if(aleatoire.getState()) listeAdresses=null;
      	else
      	{
					listeAdresses=new int[2];
          for(int i=0;i<2;i++)
          {
          	if(fenetre.charges[i])
            	listeAdresses[i]=Integer.parseInt(adresse[i].getText());
            else listeAdresses[i]=-1;
          }
      	}
        // on détermine le trait
        int trait;
        // si deux programmes sont chargés
        if(fenetre.charges[0] && fenetre.charges[1])
        {
        	// si trait aléatoire
        	if(traitAleatoire.getState())
          	trait=(new java.util.Random()).nextInt() & 1;
          // sinon, on lit le choix
          else
        		trait=(traits[0].getState()?0:1);
        }
        // un seul programme chargé
        else trait=fenetre.charges[0]?0:1;
        // on récupère la distance et le nul
        int dist=Integer.parseInt(distance.getText());
        int nulle=Integer.parseInt(nul.getText());
        // on appelle la fonction d'installation des programmes
				fenetre.installer(listeAdresses,dist,nulle,trait);
      }
      catch(Exception e)
      {
      	e.printStackTrace();
      	String[] message={"Erreur lors de l'installation des programmes: ",
        	e.getMessage()};
      	InfoDialog.afficher(fenetre,"Erreur",message);
      }
      fenetre=null;
      dispose();
		}
    else if(evt.getSource()==boutonAnnuler)
    {
    	fenetre=null;
    	dispose();
    }
    else if(evt.getSource()==adresse[0] || evt.getSource()==adresse[1])
    {
    	adresses.setState(true);
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
		gbc.insets=new Insets(2,2,2,2);
		gbl.setConstraints(component,gbc);
		container.add(component);
	}
}
