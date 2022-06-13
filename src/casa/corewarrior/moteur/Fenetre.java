/********************************************************************
* Fenetre pour le Moteur Core Warrior (C) Michel CASABIANCA 1998    *
********************************************************************/

package casa.corewarrior.moteur;

import java.awt.*;
import java.awt.event.*;
import casa.awt.*;
import java.io.*;
import java.util.*;

public final class Fenetre extends Frame
implements WindowListener,ActionListener,Runnable
{
	// r�f�rence du moteur
  Moteur moteur;
	// constante de d�finition des couleurs
  final static int OUEST=0,EST=1;
	// consoles pour le controle des programmes
	Console[] consoles=new Console[2];
  // Repr�sentation graphique de la m�moire
  public Plateau plateau;
  // boolean indiquant si on doit terminer le programme � la fermeture
	private boolean exit;

  // boutons de d�filement des progarmmes
  private GraphButton boutonStep=
		new GraphButton("Step","images/step.gif","images/step2.gif");
  private GraphButton boutonRun=
		new GraphButton("Run","images/launch.gif","images/launch2.gif");
  private GraphButton boutonPause=
		new GraphButton("Pause","images/pause.gif","images/pause2.gif");
  private GraphButton boutonStop=
		new GraphButton("Stop","images/stop.gif","images/stop2.gif");
  private TextField textCycle=new TextField(7);
  // �l�ments de r�glage
  private Button boutonInstaller=new Button("Installer");
  private CheckboxGroup group=new CheckboxGroup();
  private Checkbox checkDebug=new Checkbox("Debug",group,false);
  private Checkbox checkTrace=new Checkbox("Trace",group,true);
  private Checkbox checkTurbo=new Checkbox("Turbo",group,false);
  private TextField textClic=new TextField("5",4);

  // indicateurs de chargement des programmes
  public boolean[] charges=new boolean[2];
  // programme ayantle trait
  private int trait;
  // indique que l'on doit arr�ter les programmes
  private boolean pause;
  // thread pour ex�cution des programmes (mode Trace)
  private Thread thread;

	// constructeur
	public Fenetre(Moteur moteur,boolean quitter)
	{
  	super("Core Warrior 0.1");
    plateau=new Plateau(this);
    this.moteur=moteur;
		exit=quitter;
		initGUI();
	}

	// construction de l'interface graphique
	private void initGUI()
	{
  	setBackground(Color.lightGray);
    setLayout(new BorderLayout(5,5));
    // on instancie les consoles
    consoles[OUEST]=new Console(this,OUEST,"images/rouge.gif");
    consoles[EST]=new Console(this,EST,"images/vert.gif");
    // cr�ation de la barre
		PanelImage barre=new PanelImage("images/chantier.gif");
		barre.setLayout(new GridBagLayout());
    // cr�ation de la barre de navigation
    Panel barreNavig=new Panel();
    barreNavig.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    barreNavig.add(boutonStep);
    barreNavig.add(boutonRun);
    barreNavig.add(boutonPause);
    barreNavig.add(boutonStop);
    barreNavig.add(textCycle);
    ajouter(barre,barreNavig,0,0,1,1,GridBagConstraints.NONE,
			GridBagConstraints.WEST,1,1);
    // cr�ation de la barre d'installation
		Panel barreInstal=new Panel();
    barreInstal.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    barreInstal.add(boutonInstaller);
    barreInstal.add(new Label("Mode :"));
    barreInstal.add(checkDebug);
    barreInstal.add(checkTrace);
    barreInstal.add(checkTurbo);
    barreInstal.add(new Label("Temps (ms) :"));
    barreInstal.add(textClic);
    ajouter(barre,barreInstal,1,0,1,1,GridBagConstraints.NONE,
			GridBagConstraints.EAST,1,1);
    // on modifie l'�tat de composants
    boutonStep.setEnabled(false);
    boutonRun.setEnabled(false);
    boutonPause.setEnabled(false);
    boutonStop.setEnabled(false);
    textCycle.setEditable(false);
    boutonInstaller.setEnabled(false);
  	// on ajoute le plateau et les consoles
    add("North",barre);
    add("Center",plateau);
    add("West",consoles[OUEST]);
    add("East",consoles[EST]);
    // on packe la fen�tre et on la centre � l'�cran
    pack();
		setResizable(false);
    Dimension a=getToolkit().getScreenSize();
		Rectangle b=getBounds();
		setLocation((a.width-b.width)/2,(a.height-b.height)/2);
    // on enregistre les Listeners
    addWindowListener(this);
    boutonInstaller.addActionListener(this);
    boutonStep.addActionListener(this);
    boutonRun.addActionListener(this);
    boutonPause.addActionListener(this);
    boutonStop.addActionListener(this);
	}

  // chargement d'un programme
  public void charger(int couleur,String chemin) throws IOException
  {
  	try
    {
    	moteur.charger(couleur,chemin);
      charges[couleur]=true;
			boutonInstaller.setEnabled(true);
			// on affiche le nom du programme
			String nom=moteur.noms[couleur];
			consoles[couleur].textNom.setText(nom);
			// on affiche le listing du programme
			consoles[couleur].lister();
    }
    catch(IOException e) {throw e;}
  }

  // purge un programme
  public void purger(int couleur)
  {
  	charges[couleur]=false;
    if(!charges[0] && !charges[1]) boutonInstaller.setEnabled(false);
  }

  // installation des programmes
  public void installer(int[] adresses,int distance,int nul,int trait)
  {
  	// si placement al�atoire, on appelle le placement al�atoire
		if(adresses==null) adresses=moteur.placer();
    // on installe les programmes
    for(int i=0;i<2;i++)
    {
    	if(charges[i])
      {
      	moteur.installer(i,adresses[i]);
        plateau.miseAjour(i,adresses[i],moteur.prog[i].length,
        	adresses[i],adresses[i]);
      }
    }
    // on fixe les caract�ristiques du moteur (distance et nul)
    moteur.setCaracteristiques(distance,nul,trait);
    // on affiche les IP des programmes
    for(int i=0;i<2;i++) if(charges[i]) consoles[i].setIP(adresses[i]);
    // on affiche les dumps des programmes
    for(int i=0;i<2;i++) if(charges[i]) consoles[i].dump(adresses[i]);
    // on enregistre les infos
    this.trait=trait;
    consoles[trait].setTrait(true);
    // on d�sactive les boutons de chargement et installation
    boutonInstaller.setEnabled(false);
    for(int i=0;i<2;i++) consoles[i].desactiver();
    // on active les boutons de navigation
    boutonStep.setEnabled(true);
    boutonRun.setEnabled(true);
    boutonStop.setEnabled(true);
  }

	// avance le d�roulement du programme d'un pas
  private void step()
  {
  	// on intercepte une RuntimeError (crash d'un programme)
  	try
    {
    	// on enregistre l'ancienne IP
    	int oldIP=moteur.getIP(trait);
      // on fait un pas et r�cup�re l'IP (0) et l'adresse � updater (1)
    	int[] rens=moteur.step(trait);
      // on met � jour le num�ro du cycle et le plateau
      textCycle.setText(Integer.toString(moteur.getCycle()));
			// on met � jour le plateau
      plateau.miseAjour(trait,rens[1],1,rens[0],oldIP);
      // on update les consoles
      consoles[trait].setIP(rens[0]);
      if(rens[1]!=-1)
      {
        for(int i=0;i<2;i++) if(charges[i]) consoles[i].miseAjour(rens[1]);
      }
      consoles[trait].dump(moteur.getIP(trait));
      // on passe au programme suivant
      consoles[trait].setTrait(false);
      suivant();
      consoles[trait].setTrait(true);
    }
    // si une erreur � �t� intercept�e on affiche une fen�tre
    catch(RuntimeErrorException e)
    {
      // on inhibe les boutons de navigation
			boutonStep.setEnabled(false);
    	boutonRun.setEnabled(false);
    	boutonPause.setEnabled(false);
      boutonStop.setEnabled(true);
			// on pr�pare le message
    	String[] message=new String[2];
      StringTokenizer tokenizer=new StringTokenizer(e.toString(),":");
      for(int i=0;i<2;i++) message[i]=tokenizer.nextToken();
			// on affiche la fen�tre
    	InfoDialog.afficher(this,"Fin du match :",message);
    }
  }

  // lance le thread d'ex�cution des programmes
  private void start()
  {
  	thread=new Thread(this);
    thread.start();
  }

  // lance le programme
  public void run()
  {
  	// lecture du clic
  	int clic=10;
  	try {clic=Integer.parseInt(textClic.getText());}
    catch(Exception e) {textClic.setText(Integer.toString(clic));}
    // mode Debug :
    if(checkDebug.getState())
    {
			debug(clic);
		}
    // mode trace :
    else if(checkTrace.getState())
    {
			trace(clic);
		}
    // mode turbo :
    else
    {
      turbo();
    }
  }

	// mode debug
	private void debug(int clic)
	{
		// on d�sactive tous les boutons sauf la pause
		boutonStep.setEnabled(false);
		boutonRun.setEnabled(false);
		boutonStop.setEnabled(false);
		boutonPause.setEnabled(true);
		// on intercepte une RuntimeError (crash d'un programme)
  	try
    {
			// on d�sarme l'indicateur de pause
			pause=false;
			// on boucle tant que pas de pause et pas de nul
			while(!pause && moteur.getCycle()<moteur.nul)
			{
				// on enregistre l'ancienne IP
				int oldIP=moteur.getIP(trait);
				// on fait un pas et r�cup�re l'IP (0) et l'adresse � updater (1)
				int[] rens=moteur.step(trait);
				// on met � jour le num�ro du cycle et le plateau
				textCycle.setText(Integer.toString(moteur.getCycle()));
				// on met � jour le plateau
				plateau.miseAjour(trait,rens[1],1,rens[0],oldIP);
				// on update les consoles
				consoles[trait].setIP(rens[0]);
				if(rens[1]!=-1)
				{
					for(int i=0;i<2;i++) if(charges[i]) consoles[i].miseAjour(rens[1]);
				}
				consoles[trait].dump(moteur.getIP(trait));
				// on passe au programme suivant
				consoles[trait].setTrait(false);
				suivant();
				consoles[trait].setTrait(true);
				// on fait une pause
				try {Thread.sleep(clic);}
				catch(InterruptedException e) {e.printStackTrace();}
			}
			// si nul
			if(moteur.getCycle()>=moteur.nul)
			{
				// on actualise l'�tat des boutons
				boutonStep.setEnabled(false);
				boutonRun.setEnabled(false);
				boutonStop.setEnabled(true);
				boutonPause.setEnabled(false);
				// on affiche la fenetre de partie nulle
      	fenetreNul();
			}
			// sinon on r�active les boutons
			else
			{
				boutonStep.setEnabled(true);
				boutonRun.setEnabled(true);
				boutonStop.setEnabled(true);
				boutonPause.setEnabled(false);
			}
    }
    // si une erreur � �t� intercept�e on affiche une fen�tre
    catch(RuntimeErrorException e)
    {
      // on inhibe les boutons de navigation
			boutonStep.setEnabled(false);
    	boutonRun.setEnabled(false);
    	boutonPause.setEnabled(false);
      boutonStop.setEnabled(true);
			// on pr�pare le message
    	String[] message=new String[2];
      StringTokenizer tokenizer=new StringTokenizer(e.toString(),":");
      for(int i=0;i<2;i++) message[i]=tokenizer.nextToken();
			// on affiche la fen�tre
    	InfoDialog.afficher(this,"Fin du match :",message);
    }
	}

  // trace l'ex�cution du programme
  private void trace(int clic)
  {
		// on d�sactive tous les boutons sauf la pause
		boutonStep.setEnabled(false);
		boutonRun.setEnabled(false);
		boutonStop.setEnabled(false);
		boutonPause.setEnabled(true);
		// on intercepte une RuntimeError (crash d'un programme)
		try
		{
			// on d�sarme l'indicateur de pause
			pause=false;
			// on boucle tant que pas de pause et pas de nul
			while(!pause && moteur.getCycle()<moteur.nul)
			{
				// on enregistre l'ancienne IP
				int oldIP=moteur.getIP(trait);
				// on fait un pas et r�cup�re l'IP (0) et l'adresse � updater (1)
				int[] rens=moteur.step(trait);
				// on met � jour le num�ro du cycle et le plateau
				textCycle.setText(Integer.toString(moteur.getCycle()));
				plateau.miseAjour(trait,rens[1],1,rens[0],oldIP);
				consoles[trait].setIP(rens[0]);
				// on passe auprogramme suivant
				suivant();
				// on fait une pause
				try {Thread.sleep(clic);}
				catch(InterruptedException e) {e.printStackTrace();}
			}
			// on actualise les consoles
      for(int i=0;i<2;i++)
      {
      	int ip=moteur.getIP(i);
      	if(charges[i])
        {
        	consoles[i].dump(ip);
      		consoles[i].setIP(ip);
          consoles[i].setTrait(i==trait);
        }
      }
			// si nul on d�sactive les boutons
			if(moteur.getCycle()>=moteur.nul)
			{
				// on actualise l'�tat des boutons
				boutonStep.setEnabled(false);
				boutonRun.setEnabled(false);
				boutonStop.setEnabled(true);
				boutonPause.setEnabled(false);
				// on affiche la fenetre de partie nulle
      	fenetreNul();
			}
			// sinon on r�active les boutons
			else
			{
				boutonStep.setEnabled(true);
				boutonRun.setEnabled(true);
				boutonStop.setEnabled(true);
				boutonPause.setEnabled(false);
			}
		}
		// si une erreur � �t� intercept�e on affiche une fen�tre d'erreur
		catch(RuntimeErrorException e)
		{
			// on inhibe les boutons de navigation
			boutonStep.setEnabled(false);
			boutonRun.setEnabled(false);
			boutonPause.setEnabled(false);
			boutonStop.setEnabled(true);
			// on actualise les consoles
      for(int i=0;i<2;i++)
      {
      	int ip=moteur.getIP(i);
      	if(charges[i])
        {
        	consoles[i].dump(ip);
      		consoles[i].setIP(ip);
          consoles[i].setTrait(i==trait);
        }
      }
			// on actualise les dumps des consoles
			for(int i=0;i<2;i++) if(charges[i]) consoles[i].dump(moteur.getIP(i));
			// on pr�pare le message d'erreur
			String[] message=new String[2];
			StringTokenizer tokenizer=new StringTokenizer(e.toString(),":");
			for(int i=0;i<2;i++) message[i]=tokenizer.nextToken();
			// on affiche la fen�tre
			InfoDialog.afficher(this,"Fin du match :",message);
		}
  }

  // mode turbo
  private void turbo()
  {
		// on d�sactive le marqueur de pause
		pause=false;
  	// on intercepte une RuntimeError (crash d'un programme)
  	try
    {
			// on boucle tant que pas de pause et pas de nul
    	while(!pause && moteur.getCycle()<moteur.nul)
      {
      	// on lance le mode turbo du moteur
    		moteur.turbo(trait);
      	suivant();
      }
      // on actualise les consoles
      for(int i=0;i<2;i++)
      {
      	int ip=moteur.getIP(i);
      	if(charges[i])
        {
        	consoles[i].dump(ip);
      		consoles[i].setIP(ip);
          consoles[i].setTrait(i==trait);
        }
      }
      textCycle.setText(Integer.toString(moteur.getCycle()));
      // on affiche le contenu de la m�moire
      int[] ip={(charges[0]?moteur.getIP(0):-1),
      	(charges[1]?moteur.getIP(1):-1)};
      plateau.miseAjourTurbo(ip);
			// on examine si la partie est nulle
      if(moteur.getCycle()>=moteur.nul)
      {
				// on actualise l'�tat des boutons
				boutonStep.setEnabled(false);
				boutonRun.setEnabled(false);
				boutonStop.setEnabled(true);
				boutonPause.setEnabled(false);
				// on affiche la fenetre de partie nulle
      	fenetreNul();
      }
			// sinon (pause)
			{
				// on actualise l'�tat des boutons
				boutonStep.setEnabled(true);
				boutonRun.setEnabled(true);
				boutonStop.setEnabled(true);
				boutonPause.setEnabled(false);
			}
    }
    // si une erreur � �t� intercept�e on affiche une fen�tre
    catch(RuntimeErrorException e)
    {
			// on actualise l'�tat des boutons
      boutonStep.setEnabled(false);
    	boutonRun.setEnabled(false);
    	boutonStop.setEnabled(true);
    	boutonPause.setEnabled(false);
      // on actualise les consoles
      for(int i=0;i<2;i++)
      {
      	int ip=moteur.getIP(i);
      	if(charges[i])
        {
        	consoles[i].dump(ip);
      		consoles[i].setIP(ip);
          consoles[i].setTrait(i==trait);
        }
      }
      textCycle.setText(Integer.toString(moteur.getCycle()));
      // on affiche le contenu de la m�moire
      int[] ip={(charges[0]?moteur.getIP(0):-1),
      	(charges[1]?moteur.getIP(1):-1)};
      plateau.miseAjourTurbo(ip);
			// on pr�pare le message
    	String[] message=new String[2];
      StringTokenizer tokenizer=new StringTokenizer(e.toString(),":");
      for(int i=0;i<2;i++) message[i]=tokenizer.nextToken();
			// on affiche la fen�tre
    	InfoDialog.afficher(this,"Fin du match :",message);
    }
  }

  // affiche une fen�tre indiquant une partie nulle
  private void fenetreNul()
  {
  	String[] message={"La partie est nulle","pas de crash au cycle",
    	Integer.toString(moteur.nul)};
    InfoDialog.afficher(this,"Partie nulle",message);
  }

  // arr�t des programmes
  private void pause() {pause=true;}

  // r�initialisation du plateau
  private void stop()
  {
    // on remet � z�ro les compteurs
    textCycle.setText("0");
    // on r�active les concoles
    for(int i=0;i<2;i++)
    {
    	consoles[i].reactiver();
    	if(charges[i])
      {
      	consoles[i].lister();
        consoles[i].setTrait(false);
      }
    }
    // on actualise l'�tat des boutons
    boutonStep.setEnabled(false);
   	boutonRun.setEnabled(false);
   	boutonStop.setEnabled(false);
   	boutonPause.setEnabled(false);
  	// on nettoie la grille et le moteur
		moteur.nettoyer();
    plateau.nettoyer();
    boutonInstaller.setEnabled(true);
  }

  // fait changer le trait
  private void suivant() {do {trait=(trait+1)%2;} while(!charges[trait]);}

	// m�thode show surcharg�e pour cr�er l'image du plateau
	public void show()
	{
		plateau.creerImage();
		super.show();
	}

  // appel� lorsqu'on clique sur un bouton
	public void actionPerformed(ActionEvent evt)
  {
		String cmd=evt.getActionCommand();
		if(cmd.equals("Installer"))
		{
    	DialogInstaller.afficher(this);
    }
    else if(cmd.equals("Step"))
		{
    	step();
    }
    else if(cmd.equals("Run"))
		{
    	start();
    }
    else if(cmd.equals("Pause"))
		{
    	pause();
    }
    else if(cmd.equals("Stop"))
		{
    	stop();
    }
  }
	public void windowClosing(WindowEvent e)
  {
  	if(exit) System.exit(0);
    else dispose();
  }
  public void windowOpened(WindowEvent e) {}
  public void windowClosed(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowActivated(WindowEvent e) {}
  public void windowDeactivated(WindowEvent e) {}

  // m�thode d'ajout d'un composant au GradBagLayout
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
