/********************************************************************
* SyntaxErrorException (C) Michel CASABIANCA 1998                   *
********************************************************************/

package casa.corewarrior.compilateur;

public final class SyntaxErrorException extends Exception
{
	// num�ro de ligne de l'erreur
	private int ligne;

	// constructeur : message d'erreur + num�ro de ligne
	SyntaxErrorException(String message,int ligne)
	{
		super(message);
		this.ligne=ligne;
	}

	// conversion en chaine
	public String toString()
	{
		return "Erreur ligne "+(ligne+1)+" : "+getMessage();
	}

	// renvoie le num�ro de ligne
	public int getLigne() {return ligne;}
}
