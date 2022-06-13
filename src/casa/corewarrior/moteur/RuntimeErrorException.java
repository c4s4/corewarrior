/********************************************************************
* RuntimeErrorException (C) Michel CASABIANCA 1998                  *
********************************************************************/

package casa.corewarrior.moteur;

public final class RuntimeErrorException extends Exception
{
	// nom du programme planté
  private String prog;
	// adresse de l'erreur
	private int adresse;
  // cycle pendant lequel se produit l'erreur
  private int cycle;

	// constructeur : message + programme + numéro du cycle
	RuntimeErrorException(String message,String prog,int adresse,int cycle)
	{
		super(message);
    this.prog=prog;
    this.adresse=adresse;
    this.cycle=cycle;
	}

	// conversion en chaine de caractères
	public String toString()
	{
		return "Crash \""+prog+"\" [cycle="+cycle+", adresse="+adresse+"] : "+
    getMessage();
	}
}
