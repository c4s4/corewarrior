package casa.corewarrior.compilateur;

import java.io.*;
import java.util.*;

public final class Compilateur {
    // aide au programme
    private static final String usage = "Syntaxe : casa.corewarrior.compilateur.Compilateur fichier";
    // taille de la mÃ©moire pour le jeu
    private static final int TAILLE = 4096;
    // valeur du mode d'adressage direct
    private static final int DIRECT = 0;
    // valeur du mode d'adressage relatif
    private static final int RELATIF = 1;
    // valeur du mode d'adressage indirect
    private static final int INDIRECT = 2;
    // valeur des diffÃ©rentes mnÃ©monics
    private static final int DAT = 0, MOV = 1, ADD = 2, SUB = 3, JMP = 4, JMZ = 5, JMG = 6, DJZ = 7, CMP = 8;

    // main pour appel de la ligne de commande
    public static void main(String[] args) {
        if (args.length == 1) {
            String fichierEntree = args[0];
            String fichierSortie = fichierEntree.substring(0,
                    fichierEntree.lastIndexOf(".")) + ".bin";
            try {
                FileInputStream entree = new FileInputStream(fichierEntree);
                FileOutputStream sortie = new FileOutputStream(fichierSortie);
                compiler(entree, sortie);
                sortie.close();
                System.out.println("Compilation OK");
            } catch (FileNotFoundException e) {
                System.out.println("Fichier " + fichierEntree + " introuvable");
            } catch (IOException e) {
                System.out.println(e);
            } catch (SyntaxErrorException e) {
                System.out.println(e);
            }
            // debug
            System.out.println(fichierEntree + " -> " + fichierSortie);
        } else {
            System.out.print(usage);
        }
    }

    // mÃ©thode de compilation, rejette une SyntaxErrorException
    public static void compiler(InputStream fluxEntree, OutputStream fluxSortie)
            throws SyntaxErrorException, IOException {
        // on convertit les flux
        BufferedReader entree = new BufferedReader(
                new InputStreamReader(fluxEntree));
        DataOutputStream sortie = new DataOutputStream(fluxSortie);
        // on extrait les lignes du programme
        Vector lignes = new Vector();
        String ligne = "";
        try {
            while ((ligne = entree.readLine()) != null) {
                lignes.addElement(ligne);
            }
        } catch (IOException e) {
            throw new IOException("Erreur lecture source : " + e.getMessage());
        }
        // on compile les lignes une ï¿½ une
        try {
            for (int i = 0; i < lignes.size(); i++) {
                // on met la ligne en forme
                String[] jetons = decouper((String) lignes.elementAt(i));
                // on la compile si elle n'est pas vide
                if (jetons != null) {
                    int code = compilerLigne(jetons, i);
                    sortie.writeInt(code);
                }
            }
        } catch (IOException e) {
            throw new IOException("Erreur ecriture binaire : " + e.getMessage());
        }
    }

    // met une ligne en forme
    private static String[] decouper(String ligne) {
        // on enlÃ¨ve les blancs en dÃ©but et fin de ligne
        ligne = ligne.trim();
        // on cherche les caractÃ¨res * pour Ã©liminer la fin de ligne
        if (ligne.indexOf("*") != -1)
            ligne = ligne.substring(0, ligne.indexOf("*"));
        ligne = ligne.trim();
        // on remplace les TAB par des espaces
        ligne = ligne.replace('\t', ' ');
        // on dÃ©coupe la chaine en jetons
        Vector listeJetons = new Vector();
        StringTokenizer tokenizer = new StringTokenizer(ligne, " ");
        while (tokenizer.hasMoreElements()) {
            String jeton = tokenizer.nextToken();
            if (jeton != null)
                listeJetons.addElement(jeton);
        }
        // on convertit le vecteur en tableau
        if (listeJetons.size() == 0)
            return null;
        String[] jetons = new String[listeJetons.size()];
        listeJetons.copyInto(jetons);
        return jetons;
    }

    // mÃ©thode de compilation d'une ligne
    private static int compilerLigne(String[] jetons, int numero)
            throws SyntaxErrorException {
        // on dÃ©coupe la ligne
        String mnemonic = jetons[0];
        String[] arg = new String[jetons.length - 1];
        System.arraycopy(jetons, 1, arg, 0, arg.length);
        // on convertit le mnemonic et les arguments en binaire
        int mnemonicBin = mnemonic2bin(mnemonic, numero, arg);
        int[] argBin = { 0, 0 };
        for (int i = 0; i < arg.length; i++)
            argBin[i] = arg2bin(arg[i], numero);
        // on fusionne le mnemonic et les arguments en un code
        int code = (mnemonicBin << 28) | (argBin[0] << 14) | argBin[1];
        return code;
    }

    // conversion d'un mnÃ©monic en binaire
    private static int mnemonic2bin(String mnemonic, int numero, String[] args)
            throws SyntaxErrorException {
        if (mnemonic.equalsIgnoreCase("dat")) {
            if (args.length > 1)
                throw new SyntaxErrorException(
                        "Le mnemonic DAT demande 1 argument", numero);
            if (getMode(args[0]) != RELATIF)
                throw new SyntaxErrorException(
                        "Le mnemonic DAT demande 1 argument relatif", numero);
            return DAT;
        } else if (mnemonic.equalsIgnoreCase("mov")) {
            if (args.length < 2)
                throw new SyntaxErrorException(
                        "Le mnemonic MOV demande 2 arguments", numero);
            if (getMode(args[1]) == DIRECT)
                throw new SyntaxErrorException(
                        "Le deuxiï¿½me argument du mnemonic MOV ne peut etre immï¿½diat", numero);
            return MOV;
        } else if (mnemonic.equalsIgnoreCase("add")) {
            if (args.length < 2)
                throw new SyntaxErrorException(
                        "Le mnemonic ADD demande 2 arguments", numero);
            if (getMode(args[1]) == DIRECT)
                throw new SyntaxErrorException(
                        "Le deuxiï¿½me argument du mnemonic ADD ne peut etre immï¿½diat", numero);
            return ADD;
        } else if (mnemonic.equalsIgnoreCase("sub")) {
            if (args.length < 2)
                throw new SyntaxErrorException(
                        "Le mnemonic SUB demande 2 arguments", numero);
            if (getMode(args[1]) == DIRECT)
                throw new SyntaxErrorException(
                        "Le deuxiï¿½me argument du mnemonic SUB ne peut etre immï¿½diat", numero);
            return SUB;
        } else if (mnemonic.equalsIgnoreCase("jmp")) {
            if (args.length > 1)
                throw new SyntaxErrorException(
                        "Le mnemonic JMP demande 1 argument", numero);
            if (getMode(args[0]) == DIRECT)
                throw new SyntaxErrorException(
                        "L'argument du mnemonic JMP ne peut etre immï¿½diat", numero);
            return JMP;
        } else if (mnemonic.equalsIgnoreCase("jmz")) {
            if (args.length < 2)
                throw new SyntaxErrorException(
                        "Le mnemonic JMZ demande 2 arguments", numero);
            if (getMode(args[0]) == DIRECT || getMode(args[1]) == DIRECT)
                throw new SyntaxErrorException(
                        "Un argument du mnemonic JMZ ne peut etre immï¿½diat", numero);
            return JMZ;
        } else if (mnemonic.equalsIgnoreCase("jmg")) {
            if (args.length < 2)
                throw new SyntaxErrorException(
                        "Le mnemonic JMG demande 2 arguments", numero);
            if (getMode(args[0]) == DIRECT || getMode(args[1]) == DIRECT)
                throw new SyntaxErrorException(
                        "Un argument du mnemonic JMG ne peut etre immï¿½diat", numero);
            return JMG;
        } else if (mnemonic.equalsIgnoreCase("djz")) {
            if (args.length < 2)
                throw new SyntaxErrorException(
                        "Le mnemonic DJZ demande 2 arguments", numero);
            if (getMode(args[0]) == DIRECT || getMode(args[1]) == DIRECT)
                throw new SyntaxErrorException(
                        "Un argument du mnemonic DJZ ne peut etre immï¿½diat", numero);
            return DJZ;
        } else if (mnemonic.equalsIgnoreCase("cmp")) {
            if (args.length < 2)
                throw new SyntaxErrorException(
                        "Le mnemonic CMP demande 2 arguments", numero);
            return CMP;
        } else
            throw new SyntaxErrorException("Mnemonic \"" + mnemonic + "\" inconnu", numero);
    }

    // conversion d'un argument en binaire
    private static int arg2bin(String arg, int numero)
            throws SyntaxErrorException {
        // si l'argument est null, on retourne 0
        if (arg == null)
            return 0;
        // on code le mode de l'argument (relatif, @ ou #)
        int mode = getMode(arg);
        // on Ã©limine le prï¿½fixe
        if (arg.startsWith("#") || arg.startsWith("@"))
            arg = arg.substring(1, arg.length());
        // on code la valeur de l'argument
        int valeur = 0;
        try {
            valeur = (TAILLE + Integer.parseInt(arg) % TAILLE) % TAILLE;
        } catch (Exception e) {
            throw new SyntaxErrorException(
                    "La valeur \"" + arg + "\" n'est pas valide", numero);
        }
        // on fusionne le tout pour obtenir l'argument en binaire
        int bin = (mode << 12) | valeur;
        return bin;
    }

    // renvoie le mode de l'argument
    private static int getMode(String arg) {
        int mode = RELATIF;
        if (arg.startsWith("#"))
            mode = DIRECT;
        else if (arg.startsWith("@"))
            mode = INDIRECT;
        return mode;
    }
}
