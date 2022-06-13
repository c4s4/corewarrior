/********************************************************************
* Spice Wars 0.4 du 23.10.98 (C) Michel CASABIANCA                  *
********************************************************************/

package casa.awt;

import java.awt.*;

public final class BoiteConfigurationTextArea extends Dialog
{
	/** titre de la boite */
	private final static String titre="Choix de la fonte du forum";
	/** configurateur du TextArea */
	private ConfigurationTextArea config;
	/** r�f�rence d'une fen�tre ouverte */
	static private BoiteConfigurationTextArea boite=null;
	/** TextArea � modifier */
	private TextArea texte;
	
	/** �l�ments de l'interface */
	Button btnOK=new Button(" OK ");
	Button btnAnnuler=new Button("Annuler");
	
	/** ouvre une boite de s�lection de la police */
	static public void ouvrir(Frame frame,TextArea texte)
	{
		if(boite==null)
		{
			boite=new BoiteConfigurationTextArea(frame,texte);
			boite.show();
		}
		else
		{
			boite.toFront();
		}
	}
	
	/** constructeur de la boite de configuration */
	BoiteConfigurationTextArea(Frame frame,TextArea texte)
	{
		/* on appelle le constructeur parent */
		super(frame,titre,false);
		/* on stocke la r�f�rence sur le TextArea */
		this.texte=texte;
		/* on construit le configurateur */
		config=new ConfigurationTextArea(texte);
		/* on construit l'interface */
		initInterface();
	}
	
	/** initialisation de l'interface */
	private void initInterface()
	{
		setLayout(new BorderLayout(5,5));
		/* on ajoute les �l�ments de l'interface */
		add("Center",config);
		Panel barre=new Panel();
		barre.setLayout(new FlowLayout(FlowLayout.RIGHT,5,5));
		barre.add(btnOK);
		barre.add(btnAnnuler);
		add("South",barre);
		/* on dimensionne la fen�tre */
		setResizable(false);
		pack();
	}
	
	/** gestion des composants de l'interface */
	public boolean action(Event evt,Object what)
	{
		/* on a cliqu� sur OK */
		if(evt.target==btnOK)
		{
			texte.setFont(config.fonte);
			texte.setBackground(config.couleurFond);
			texte.setForeground(config.couleurTexte);
			boite=null;
			dispose();
		}
		/* on a cliqu� sur Annuler */
		else if(evt.target==btnAnnuler)
		{
			boite=null;
			dispose();
		}
		return true;
	}
	
	/** gestion de la fermeture de la fen�tre */
	public boolean handleEvent(Event evt)
	{
		if(evt.id==Event.WINDOW_DESTROY)
    {
    	boite=null;
      dispose();
      return true;
    }
    else return super.handleEvent(evt);
	}
}
