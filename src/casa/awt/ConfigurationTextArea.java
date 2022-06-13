/********************************************************************
* ConfigurationTextArea : configuration d'une TextArea              *
********************************************************************/

package casa.awt;

import java.awt.*;

public final class ConfigurationTextArea extends Panel
{
	// fonte en cours d'élaboration
	public Font fonte=new Font(fontes[0],Integer.parseInt(tailles[0]),
		transStyles[0]);
	// couleur de fond de la zone de texte
	public Color couleurFond=Color.black;
	public Color couleurTexte=Color.white;
	
	// éléments de l'interface
	Label lblFonte=new Label("Fonte");
	Label lblTaille=new Label("Taille");
	Label lblStyle=new Label("Style");
	Label lblCouleurFond=new Label("Fond");
	Label lblCouleurTexte=new Label("Texte");
	
	Choice chcFonte=new Choice();
	Choice chcTaille=new Choice();
	Choice chcStyle=new Choice();
	Choice chcCouleurFond=new Choice();
	Choice chcCouleurTexte=new Choice();
	
	TextArea txaTexte=new TextArea("abcdefghijklmnopq\n"
		+"ABCDEFGHIJKLMNOPQ\n0123456789",5,10);
	
	// éléments des Choices
	static final String[] fontes={"Serif","SansSerif","Monospaced",
		"Dialog","DialogInput","ZapfDingbats"};
	static final String[] tailles={"8","10","12","14","16","20","24","30"};
	static final String[] styles={"normal","gras","italic","gras-italic"};
	static final String[] couleurs={"rouge","vert","bleu","blanc","noir",
		"gris clair","gris","gris foncé","cyan","magenta","jaune","orange"};
	// transformées des éléments des Choices
	static final int[] transStyles={Font.PLAIN,Font.BOLD,Font.ITALIC,
		Font.BOLD+Font.ITALIC};
	static final Color[] transCouleurs={Color.red,Color.green,Color.blue,
		Color.white,Color.black,Color.lightGray,Color.gray,Color.darkGray,
		Color.cyan,Color.magenta,Color.yellow,Color.orange};

	// constructeur avec arguments
	ConfigurationTextArea(TextArea texte)
	{
		// on stocke les fontes
		fonte=texte.getFont();
		couleurFond=texte.getBackground();
		couleurTexte=texte.getForeground();
    // initialisation de l'interface utilisateur
		initInterface();
    // on preselectionne la valeur de la fonte
    String nom=fonte.getName();
    for(int i=0;i<fontes.length;i++)
    {
      if(fontes[i].equals(nom)) chcFonte.select(i);
    }
    // on preselectionne la taille
    String taille=Integer.toString(fonte.getSize());
    for(int i=0;i<tailles.length;i++)
    {
      if(tailles[i].equals(taille)) chcTaille.select(i);
    }
    // on preselectionne le style
    int style=fonte.getStyle();
    for(int i=0;i<styles.length;i++)
    {
      if(transStyles[i]==style) chcStyle.select(i);
    }
    // on preselectionne la couleur du fond
    Color fond=texte.getBackground();
    for(int i=0;i<transCouleurs.length;i++)
    {
      if(transCouleurs[i].equals(fond)) chcCouleurFond.select(i);
    }
    // on preselectionne la couleur du texte
    Color text=texte.getForeground();
    for(int i=0;i<transCouleurs.length;i++)
    {
      if(transCouleurs[i].equals(text)) chcCouleurTexte.select(i);
    }
	}
	
	// initialisation de l'interface utilisateur
	private final void initInterface()
	{
		setLayout(new GridBagLayout());
		// initialisation du panel du réseau
		ajouter(this,lblFonte,0,0,2,1,GridBagConstraints.NONE,
			GridBagConstraints.WEST,1,1);
		ajouter(this,chcFonte,2,0,2,1,GridBagConstraints.NONE,
			GridBagConstraints.EAST,1,1);
		ajouter(this,lblTaille,0,1,1,1,GridBagConstraints.NONE,
			GridBagConstraints.WEST,1,1);
		ajouter(this,chcTaille,1,1,1,1,GridBagConstraints.NONE,
			GridBagConstraints.EAST,1,1);
		ajouter(this,lblStyle,2,1,1,1,GridBagConstraints.NONE,
			GridBagConstraints.WEST,1,1);
		ajouter(this,chcStyle,3,1,1,1,GridBagConstraints.NONE,
			GridBagConstraints.EAST,1,1);
		ajouter(this,lblCouleurFond,0,2,1,1,GridBagConstraints.NONE,
			GridBagConstraints.WEST,1,1);
		ajouter(this,chcCouleurFond,1,2,1,1,GridBagConstraints.NONE,
			GridBagConstraints.EAST,1,1);
		ajouter(this,lblCouleurTexte,2,2,1,1,GridBagConstraints.NONE,
			GridBagConstraints.WEST,1,1);
		ajouter(this,chcCouleurTexte,3,2,1,1,GridBagConstraints.NONE,
			GridBagConstraints.EAST,1,1);
		ajouter(this,txaTexte,0,3,4,1,GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,1,1);
		// on ajoute les éléments aux choices
		ajouterChoice(chcFonte,fontes);
		ajouterChoice(chcTaille,tailles);
		ajouterChoice(chcStyle,styles);
		ajouterChoice(chcCouleurFond,couleurs);
		ajouterChoice(chcCouleurTexte,couleurs);
		// on fixe la fonte de la zone de texte et les couleurs
		actualiserZoneTexte();
	}
	
	// ajoute des éléments à un Choice
	private void ajouterChoice(Choice choice,String[] elements)
	{
		for(int i=0;i<elements.length;i++)
			choice.addItem(elements[i]);
	}
	
	// actualise la zone de texte avec les paramètres actuels
	private void actualiserZoneTexte()
	{
		txaTexte.setFont(fonte);
		txaTexte.setBackground(couleurFond);
		txaTexte.setForeground(couleurTexte);
	}

	// gestion des composants de l'interface
	public boolean action(Event evt,Object what)
	{
		// on change la fonte
		if(evt.target==chcFonte)
		{
			fonte=new Font(chcFonte.getSelectedItem(),fonte.getStyle(),
				fonte.getSize());
			actualiserZoneTexte();
		}
		// on change le style
		else if(evt.target==chcStyle)
		{
			fonte=new Font(fonte.getName(),transStyles[chcStyle.getSelectedIndex()],
				fonte.getSize());
			actualiserZoneTexte();
		}
		// on change la taille
		else if(evt.target==chcTaille)
		{
			fonte=new Font(fonte.getName(),fonte.getStyle(),
				Integer.parseInt(chcTaille.getSelectedItem()));
			actualiserZoneTexte();
		}
		// on change la couleur de fond du texte
		else if(evt.target==chcCouleurFond)
		{
			couleurFond=transCouleurs[chcCouleurFond.getSelectedIndex()];
			actualiserZoneTexte();
		}
		// on change la couleur du texte
		else if(evt.target==chcCouleurTexte)
		{
			couleurTexte=transCouleurs[chcCouleurTexte.getSelectedIndex()];
			actualiserZoneTexte();
		}
		return true;
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
