/********************************************************************
* Moteur Core Warrior (C) Michel CASABIANCA 1998                    *
********************************************************************/

package casa.corewarrior.moteur;
import casa.awt.*;

import java.io.*;
import java.util.*;

public final class Moteur implements Runnable
{
  // indique si on doit quitter en cas d'erreur (mode texte)
  private boolean exit;

  // taille de la mémoire pour le jeu
  public static final int TAILLE=4096;
	// valeur du mode d'adressage immédiat
  private static final int DIRECT=0;
  // valeur du mode d'adressage relatif
  private static final int RELATIF=1;
  // valeur du mode d'adressage indirect
  private static final int INDIRECT=2;
  // valeur des différentes mnémonics
  private static final int
  	DAT=0,MOV=1,ADD=2,SUB=3,JMP=4,JMZ=5,JMG=6,DJZ=7,CMP=8;

  // trace des instructions à l'écran (mode texte)
  private boolean tracer=false;
  // dump mémoire autour de l'instruction du crash (mode texte)
  private boolean etat=false;

  // mémoire pour le jeu
  private int[] memoire=new int[TAILLE];
  // nombre de programmes en mémoire (2 en mode graphique)
  private int nombre;
  // IP des programmes en mémoire
  private int[] IP;
  // numéro du cycle actuel
  private int cycle;
  // programmes en mémoire (2 en mode graphique)
  public int[][] prog;
  // noms des programmes en mémoire (2 en mode graphique)
  public String[] noms;
  // indique que l'on doit arrêter l'exécution (en mode texte)
  private boolean stop;
  // thread de l'exécution du programme (en mode texte)
  private Thread thread;
  // temps d'attente entre 2 cycles (en ms) (en mode texte)
  private int temps;
  // trait (numéro du prog exécutant la prochaine instruction (en mode texte)
  private int trait;
  // distance minimale entre deux programmes en mémoire
  private int distance=100;
  // nombre de cycles avant partie nulle
  public int nul=1000000;
  // indique l'adresse d'une case modifiée par un MOV
  private int modif;
  // indique si on doit enregistrer les cases modifiées (mode graphique)
  private boolean log;

  // méthode main pour lancement de la ligne de commande
	public static void main(String[] args)
	{
  	// si le nombre d'arguments est nul : mode graphique
    if(args.length==0)
    {
			// on crée une instance graphique du moteur
  		Moteur moteur=new Moteur();
			// on crée une instance de la fenètre
			Fenetre fenetre=new Fenetre(moteur,true);
			// on affiche la fenètre de visualisation
			fenetre.show();
			// on affiche le splash screen
			DialogApropos.afficher(fenetre,"img/aPropos.gif");
    }
    // sinon, mode texte : les arguments sont les programmes
    else
    {
    	// on crée une instance du moteur
      Moteur moteur=new Moteur(args);
      // on lance l'exécution des programmes
	    moteur.start();
    }
	}

  // constructeur du mode texte
	public Moteur(String[] args)
	{
  	// on quitte en cas d'erreur
  	exit=true;
    // on lit les options de la ligne de commande et on les élimine
    args=options(args);
    // on enregistre le nombre de programmes
    nombre=args.length;
    // création du tableau des IP
    IP=new int[nombre];
   	// création des tableaux d'int pour les programmes
    prog=new int[nombre][];
    // création du tableau du nom des programmes
    noms=new String[nombre];
    // on charge les programmes
    for(int i=0;i<nombre;i++)
    {
    	try {charger(i,args[i]);}
      catch(IOException e)
      {
				System.out.println("Erreur chargement programme "+args[i]);
        if(exit) System.exit(-1);
      }
    }
    // on fixe le temps d'attente à 0
    temps=0;
    // on détermine la place des programmes en mémoire
    int[] adresses=placer();
    // on installe les programmes en mémoire
    for(int i=0;i<nombre;i++)
    {
    	installer(i,adresses[i]);
      System.out.println("Boot de \""+noms[i]+"\" en : "+adresses[i]);
    }
    System.out.println();
	}

	// constructeur du mode graphique
	public Moteur()
	{
  	// on ne quitte pas en cas d'erreur
  	exit=false;
		// on enregistre le nombre de programmes
    nombre=2;
    // création du tableau des IP
    IP=new int[nombre];
   	// création des tableaux d'int pour les programmes
    prog=new int[nombre][];
    // création du tableau du nom des programmes
    noms=new String[nombre];
    // on doit enregistrer les cases modifiées
    log=true;
	}

  // réinitialise le moteur
  public void nettoyer()
  {
  	// on réinitialise les variables
  	cycle=0;
    // on vide la mémoire
    for(int i=0;i<memoire.length;i++) memoire[i]=0;
  }

  // lit les options de la ligne de commande
  private String[] options(String[] args)
  {
  	// indice du premier paramètre qui n'est pas une option
    int indice=0;
  	while(args[indice].startsWith("-") || args[indice].startsWith("+"))
		{
    	String option=args[indice].substring(1,args[indice].length());
      // option de tracage des instructions
     	if(option.equalsIgnoreCase("tracer")) {tracer=true;}
      else if(option.equalsIgnoreCase("etat")) {etat=true;}
      else
      {
      	System.out.println("Option inconnue : \""+option+"\"");
        System.exit(-1);
      }
      indice++;
    }
    // on élimine les options de la ligne de commande
    String[] fichiers=new String[args.length-indice];
    System.arraycopy(args,indice,fichiers,0,fichiers.length);
    return fichiers;
  }

  // charge un programme en mémoire
  public void charger(int index,String chemin)
  throws IOException
  {
  	// on enregistre le nom du programme
  	int debut=chemin.lastIndexOf(System.getProperty("file.separator"))+1;
    int fin=chemin.lastIndexOf(".");
		if(fin==-1) fin=chemin.length();
    noms[index]=chemin.substring(debut,fin);
   	// on lit la taille du fichier et on crée le tableau
   	File fichier=new File(chemin);
   	int longueur=(int)(fichier.length()/4);
		prog[index]=new int[longueur];
    // on charge les instructions du fichier
		DataInputStream entree=new DataInputStream(
     	new FileInputStream(chemin));
    for(int i=0;i<longueur;i++)
     	prog[index][i]=entree.readInt();
		entree.close();
  }

  // envoie des adresses pour le placement aléatoire des programmes
	public int[] placer()
  {
  	// on crée les tableaux pour le placement et la taille des programmes
		int[] place=new int[nombre];
    // on place les programmes
    Random ran=new Random();
    boolean OK;
    for(int i=0;i<nombre;i++)
    {
    	if(prog[i]==null)
      {
      	place[i]=-1;
      	continue;
      }
      do
      {
      	OK=true;
				place[i]=Math.abs(ran.nextInt())%TAILLE;
        for(int j=0;j<i;j++)
        {
        	if(place[j]==-1) continue;
        	int p1=(place[i]<place[j]?i:j);
          int p2=(place[i]<place[j]?j:i);
          if(place[p2]-place[p1]<prog[p1].length+distance)
          	OK=false;
          if(place[p1]+TAILLE-place[p2]<prog[p2].length+distance)
          	OK=false;
        }
      } while(!OK);
    }
    // on retourne les emplacements
    return place;
  }

  // installation d'un programme en mémoire
  public void installer(int index,int adresse)
  {
   	// on fixe l'IP du programme
    IP[index]=adresse;
    // on recopie le programme en mémoire
    for(int i=0;i<prog[index].length;i++)
    	memoire[(adresse+i)%TAILLE]=prog[index][i];
  }

  // désassemble un programme
  public String[] programme(int couleur)
  {
  	if(prog[couleur]==null) return null;
    String[] lignes=new String[prog[couleur].length];
    for(int i=0;i<lignes.length;i++)
    {
			lignes[i]=desassembler(prog[couleur][i]);
    }
    return lignes;
  }

  // lancement du moteur
  public void start()
  {
  	stop=false;
		thread=new Thread(this);
    thread.start();
  }

  // arrêt de l'exécution des programmes
  public void stop() {stop=true;}

  // avance l'exécution du programme d'un pas (retourne la nouvelle IP)
  public int[] step(int trait) throws RuntimeErrorException
  {
  	modif=-1;
		executer(trait);
    int[] rens=new int[2];
    rens[0]=IP[trait];
    rens[1]=modif;
    cycle++;
    return rens;
  }

  // avance l'exécution du programme d'un pas (retourne la nouvelle IP)
  public void turbo(int trait) throws RuntimeErrorException
  {
  	log=false;
		executer(trait);
    cycle++;
    log=true;
  }

  // méthode run() du thread d'exécution des programmes
  public void run()
  {
		// initialisations pour tracage des programmes
  	if(tracer) traceInit();
		// on boucle tant que le programme n'est pas arreté et que pas de nul
  	while(!stop && cycle<nul)
    {
    	if(tracer) tracer(trait);
			// on essaie d'exécuter la prochaine instruction
    	try {executer(trait);}
			// on intercepte une erreur runtime
      catch(RuntimeErrorException e)
      {
      	if(tracer)
        {
        	System.out.println();
          System.out.println();
        }
      	System.out.println(e);
        if(etat) etat(trait);
        if(exit) System.exit(trait);
      }
      trait=(trait+1)%nombre;
      cycle++;
    }
    System.out.println("Partie nulle : pas de crash en "+nul+" cycles");
  }

  // exécution d'une instruction du programme ayant le trait
  private void executer(int trait) throws RuntimeErrorException
  {
  	// on détermine l'ip
    int ip=IP[trait];
		// on extrait l'instruction à exécuter
    int code=memoire[ip];
    // on extrait les mnemonic et arguments
    int mnemonic=mnemonic(code);
    int arg1=arg1(code);
    int mod1=mod1(code);
    int arg2=arg2(code);
    int mod2=mod2(code);
		// on eppelle la fonction correspondante
    switch(mnemonic)
    {
    	case MOV : mov(trait,ip,arg1,mod1,arg2,mod2); break;
      case ADD : add(trait,ip,arg1,mod1,arg2,mod2); break;
      case SUB : sub(trait,ip,arg1,mod1,arg2,mod2); break;
      case JMP : jmp(trait,ip,arg1,mod1); break;
      case JMZ : jmz(trait,ip,arg1,mod1,arg2,mod2); break;
      case JMG : jmg(trait,ip,arg1,mod1,arg2,mod2); break;
      case DJZ : djz(trait,ip,arg1,mod1,arg2,mod2); break;
      case CMP : cmp(trait,ip,arg1,mod1,arg2,mod2); break;
			// on tente d'exécuter un DAT
      default : throw new RuntimeErrorException(
      	"Tentative d'execution d'un DAT",noms[trait],ip,cycle);
    }
  }

  // extraction d'un mnemonic
  private int mnemonic(int code) {return ((code >> 28) & 0xF);}
  // extrait l'argument 1
  private int arg1(int code) {return ((code >> 14) & 0xFFF);}
  // extrait le mode 1
  private int mod1(int code) {return ((code >> 26) & 0x3);}
	// extrait l'argument 2
  private int arg2(int code) {return (code & 0xFFF);}
  // extrait le mode 2
  private int mod2(int code) {return ((code >> 12) & 0x3);}

  // mnemonic MOV
  private void mov(int trait,int ip,int arg1,int mod1,int arg2,int mod2)
  throws RuntimeErrorException
  {
  	int contenu=contenu(trait,ip,arg1,mod1);
    int adresse=adresse(trait,ip,arg2,mod2);
    memoire[adresse]=contenu;
    increment(trait);
    if(log) modif=adresse;
  }

  // mnemonic ADD
  private void add(int trait,int ip,int arg1,int mod1,int arg2,int mod2)
  throws RuntimeErrorException
  {
  	int valeur1=valeur(trait,ip,arg1,mod1);
    int valeur2=valeur(trait,ip,arg2,mod2);
    int adresse=adresse(trait,ip,arg2,mod2);
    memoire[adresse]=(valeur1+valeur2)%TAILLE << 14;
    increment(trait);
    if(log) modif=adresse;
  }

  // mnemonic SUB
  private void sub(int trait,int ip,int arg1,int mod1,int arg2,int mod2)
  throws RuntimeErrorException
  {
  	int valeur1=valeur(trait,ip,arg1,mod1);
    int valeur2=valeur(trait,ip,arg2,mod2);
    int adresse=adresse(trait,ip,arg2,mod2);
    memoire[adresse]=(valeur2-valeur1+TAILLE)%TAILLE << 14;
    increment(trait);
    if(log) modif=adresse;
  }

  // mnemonic JMP
  private void jmp(int trait,int ip,int arg,int mod)
  throws RuntimeErrorException
  {
		int adresse=adresse(trait,ip,arg,mod);
    IP[trait]=adresse;
  }

  // mnemonic JMZ
  private void jmz(int trait,int ip,int arg1,int mod1,int arg2,int mod2)
  throws RuntimeErrorException
  {
  	int valeur=valeur(trait,ip,arg2,mod2);
    int adresse=adresse(trait,ip,arg1,mod1);
    if(valeur==0) IP[trait]=adresse;
    else increment(trait);
  }

  // mnemonic JMG
  private void jmg(int trait,int ip,int arg1,int mod1,int arg2,int mod2)
  throws RuntimeErrorException
  {
  	int valeur=valeur(trait,ip,arg2,mod2);
    int adresse=adresse(trait,ip,arg1,mod1);
    if(valeur>0) IP[trait]=adresse;
    else increment(trait);
  }

  // mnemonic DJZ
  private void djz(int trait,int ip,int arg1,int mod1,int arg2,int mod2)
  throws RuntimeErrorException
  {
  	int valeur=valeur(trait,ip,arg2,mod2);
    valeur=(valeur-1+TAILLE)%TAILLE;
    int adresse=adresse(trait,ip,arg2,mod2);
    memoire[adresse]=valeur << 14;
    int saut=adresse(trait,ip,arg1,mod1);
    if(valeur==0) IP[trait]=saut;
    else increment(trait);
    if(log) modif=adresse;
  }

  // mnemonic CMP
  private void cmp(int trait,int ip,int arg1,int mod1,int arg2,int mod2)
  throws RuntimeErrorException
  {
  	int contenu1=contenu(trait,ip,arg1,mod1);
    int contenu2=contenu(trait,ip,arg2,mod2);
    if(contenu1==contenu2) IP[trait]=(IP[trait]+1)%TAILLE;
    else IP[trait]=(IP[trait]+2)%TAILLE;
  }

  // extraction d'une valeur (toujours dans un DAT)
  private int valeur(int trait,int ip,int arg,int mod)
  throws RuntimeErrorException
  {
		// mode d'adressage immédiat
  	if(mod==DIRECT) return arg;
		// mode d'adressage relatif
    else if(mod==RELATIF)
    {
    	int adresse=(ip+arg)%TAILLE;
      if(mnemonic(memoire[adresse])!=DAT) throw new
      	RuntimeErrorException("Erreur arithmetique",noms[trait],ip,cycle);
    	return arg1(memoire[adresse]);
    }
		// mode d'adressage indirect
    else
    {
    	// si l'adresse pointée n'est pas un DAT : erreur indirection
      int adresse=(ip+arg)%TAILLE;
      if(mnemonic(memoire[adresse])!=DAT) throw new
      	RuntimeErrorException("Erreur d'indirection",noms[trait],ip,cycle);
      // on calcule l'ardresse
      adresse=(ip+arg+arg1(memoire[adresse]))%TAILLE;
      if(mnemonic(memoire[adresse])!=DAT) throw new RuntimeErrorException(
				"Erreur arithmetique",noms[trait],ip,cycle);
      return arg1(memoire[adresse]);
    }
  }

  // extraction du contenu d'une adresse
  private int contenu(int trait,int ip,int arg,int mod)
  throws RuntimeErrorException
  {
		// mode immédiat
  	if(mod==DIRECT) return arg << 14;
		// mode relatif
    else if(mod==RELATIF)
    {
    	int adresse=(ip+arg)%TAILLE;
    	return memoire[adresse];
    }
		// mode indirect
    else
    {
    	// si l'adresse pointée n'est pas un DAT : erreur indirection
      int adresse=(ip+arg)%TAILLE;
      if(mnemonic(memoire[adresse])!=DAT) throw new RuntimeErrorException(
				"Erreur d'indirection",noms[trait],ip,cycle);
      // on calcule l'ardresse
      adresse=(ip+arg+arg1(memoire[adresse]))%TAILLE;
      return memoire[adresse];
    }
  }

  // extraction d'une adresse
  private int adresse(int trait,int ip,int arg,int mod)
  throws RuntimeErrorException
  {
		// mode d'adressage immédiat
  	if(mod==DIRECT) return arg;
		// mode d'adressage relatif
    else if(mod==RELATIF)
    {
    	int adresse=(ip+arg)%TAILLE;
    	return adresse;
    }
		// mode d'adressage indirect
    else
    {
    	// si l'adresse pointée n'est pas un DAT : erreur indirection
      int adresse=(ip+arg)%TAILLE;
      if(mnemonic(memoire[adresse])!=DAT) throw new
      	RuntimeErrorException("Erreur d'indirection",noms[trait],ip,cycle);
      // on calcule l'ardresse
      adresse=(ip+arg+arg1(memoire[adresse]))%TAILLE;
      return adresse;
    }
  }

  // incrémente l'IP pour la faire pointer vers l'adresse suivante
	private void increment(int trait)
  {
  	IP[trait]=(IP[trait]+1)%TAILLE;
  }

  // affichage du début de la trace
	private void traceInit()
  {
  	StringBuffer ligne=new StringBuffer("cycle   ");
    for(int i=0;i<nombre;i++) ligne.append(completer(noms[i],24));
    System.out.println(ligne.toString());
  }

  // trace l'instruction en cours d'exécution
  private void tracer(int trait)
  {
  	// on passe à la ligne et écrit l'IP si premier programme
    if(trait==0)
    {
    	System.out.println();
      System.out.print(zero(cycle,5)+":  ");
    }
    // on écrit l'instruction
    int ip=IP[trait];
    System.out.print(completer("["+zero(ip,4)+"] "+
    	desassembler(memoire[ip]),24));
  }

  // complète une chaine jusqu'à la longueur souhaitée
  private String completer(String chaine,int longueur)
  {
  	int n=longueur-chaine.length();
    StringBuffer complement=new StringBuffer();
    for(int i=0;i<n;i++) complement.append(' ');
    return chaine+complement.toString();
  }

  // complète un entier avec le nombre de zéros nécessaires
  private String zero(int nombre,int longueur)
  {
  	String chaine=Integer.toString(nombre);
  	int n=longueur-chaine.length();
    StringBuffer complement=new StringBuffer();
    for(int i=0;i<n;i++) complement.append('0');
    return complement.toString()+chaine;
  }

  // affiche le contenu de la mémoire autour du point de crash
  private void etat(int trait)
  {
  	// on calcule l'adresse du début du dump
		int debut=(IP[trait]-10+TAILLE)%TAILLE;
    // on affiche le contenu des cases mémoire
    System.out.println();
    for(int i=0;i<21;i++)
    {
    	int ip=(debut+i)%TAILLE;
      System.out.print("["+zero(ip,4)+"] "+desassembler(memoire[ip]));
      if(ip==IP[trait]) System.out.print(" <<");
    	System.out.println();
    }
  }

  // dump de la mémoire
  public String[] dump(int ip,int etendue)
  {
    String[] dump=new String[etendue];
    for(int i=0;i<etendue;i++)
    	dump[i]=desassembler(memoire[(ip+i)%TAILLE]);
    return dump;
  }

  // dump de la mémoire
  public String dump(int ip)
  {
    return desassembler(memoire[ip]);
  }

  // désassemblage d'une instruction
  private String desassembler(int code)
  {
  	String ligne="";
    int instruction=mnemonic(code);
    int arg1=arg1(code);
    int mod1=mod1(code);
    int arg2=arg2(code);
    int mod2=mod2(code);
    ligne=ligne+desassemblerMnemonic(instruction)+" "+
    	desassemblerArgument(arg1,mod1)+" ";
    if(instruction!=DAT && instruction!=JMP)
			ligne=ligne+desassemblerArgument(arg2,mod2);
    return ligne;
  }

  // désassemble une mnémonic
  private String desassemblerMnemonic(int mnemonic)
  {
  	if(mnemonic==DAT) return "DAT";
    else if(mnemonic==MOV) return "MOV";
    else if(mnemonic==ADD) return "ADD";
    else if(mnemonic==SUB) return "SUB";
    else if(mnemonic==JMP) return "JMP";
    else if(mnemonic==JMZ) return "JMZ";
    else if(mnemonic==JMG) return "JMG";
    else if(mnemonic==DJZ) return "DJZ";
    else if(mnemonic==CMP) return "CMP";
    else return "???";
  }

  // désassemble un argument
  private String desassemblerArgument(int arg,int mod)
  {
  	String chaine="";
    // on décode le mode
    if(mod==DIRECT) chaine+="#";
    else if(mod==INDIRECT) chaine+="@";
    // on décode la valeur
    if(arg>=TAILLE/2) arg-=TAILLE;
    chaine+=Integer.toString(arg);
    return chaine;
  }

  // fixe les caractéristiques du moteur (distance et nul)
  public void setCaracteristiques(int distance,int nul,int trait)
  {
  	this.distance=distance;
    this.nul=nul;
    this.trait=trait;
  }

  // renvoie le cycle actuel
  public int getCycle() {return cycle;}

  // renvoie l'IP du programme
  public int getIP(int couleur) {return IP[couleur];}

  // renvoie le contenu d'une adresse mémoire
  public int getMem(int adresse) {return memoire[adresse];}
}
