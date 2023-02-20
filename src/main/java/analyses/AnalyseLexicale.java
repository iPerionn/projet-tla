package analyses;

import java.util.ArrayList;
import java.util.List;

//import tla.IllegalCharacterException;
//import tla.LexicalErrorException;
//import tla.Token;
//import tla.TypeDeToken;

public class AnalyseLexicale {

	private static Integer TRANSITIONS[][] = {

			// 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
			// espace lettre chiffre ( ) + * / , [ ] { } & | ; :
			/* 0 */{ 0, 1, 2, 5, 104, 105, 106, 107, 108, 109, 110, 111, 112, 3, 4, 115, 116 },
			/* 1 */{ 101, 1, 1, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101 },
			/* 2 */{ 102, 102, 2, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102 },
			/* 3 */{ null, null, null, null, null, null, null, null, null, null, null, null, null, 113, null, null,
					null },
			/* 4 */{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, 114, null,
					null },
			/* 5 */{ 5, 103, 6, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103 },
			/* 6 */{ 6, null, 6, null, null, null, null, null, 7, null, null, null, null, null, null, null, null },
			/* 7 */{ 7, null, 8, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			/* 8 */{ 8, null, 8, null, 9, null, null, null, null, null, null, null, null, null, null, null, null },
			/* 9 */{ 117, 117, 117, 117, 117, 117, 117, 117, 117, 117, 117, 117, 117, 117, 117, 117, 117 }
	};

	// 101 acceptation d'un identifiant (retour en arri�re) + comparaison avec mots
	// clefs
	// 102 acceptation d'une valeur (retour en arri�re)
	// 103 accepation d'une ( (retour en arri�re)
	// 104 accepation d'une )
	// 105 accepation d'un +
	// 106 accepation d'un *
	// 107 accepation d'un /
	// 108 accepation d'un ,
	// 109 accepation d'un [
	// 110 accepation d'un ]
	// 111 accepation d'un {
	// 112 accepation d'un }
	// 113 accepation d'un &
	// 114 accepation d'un |
	// 115 accepation d'un ;
	// 116 accepation d'un :
	// 117 acceptation d'une Coordonn�e : (valeur,valeur) (retour en arri�re)

	private String entree;
	private int pos;
	private static int ETAT_INITIAL = 0;

	public List<Token> analyse(String entree) throws Exception {
		this.pos = 0;
		this.entree = entree;

		ArrayList<Token> listeToken = new ArrayList<Token>();

		int etat = ETAT_INITIAL;
		Character c;
		int i;
		Integer prochainEtat;
		String buffer = "";

		do {
			c = lireCaractere();
			i = indiceSymbole(c);
			prochainEtat = TRANSITIONS[etat][i];
			if (prochainEtat == null)
				throw new LexicalErrorException("Pas de transition");
			if (prochainEtat > 100) {

				switch (prochainEtat) {
					case 101:
						if (buffer.contentEquals("wall")) {
							listeToken.add(new Token(TypeDeToken.wall));
							break;
						}
						if (buffer.contentEquals("trap")) {
							listeToken.add(new Token(TypeDeToken.trap));
							break;
						}
						if (buffer.contentEquals("ghost")) {
							listeToken.add(new Token(TypeDeToken.ghost));
							break;
						}
						if (buffer.contentEquals("switch")) {
							listeToken.add(new Token(TypeDeToken.switchblock));
							break;
						}
						if (buffer.contentEquals("door")) {
							listeToken.add(new Token(TypeDeToken.door));
							break;
						}
						if (buffer.contentEquals("on") || buffer.contentEquals("off")) {
							listeToken.add(new Token(TypeDeToken.etat, buffer));
							break;
						}
						if (buffer.contentEquals("up") || buffer.contentEquals("down") || buffer.contentEquals("right")
								|| buffer.contentEquals("left")) {
							listeToken.add(new Token(TypeDeToken.orientation, buffer));
							break;
						}
						if (buffer.contentEquals("Niveau") || buffer.contentEquals("niveau")) {
							listeToken.add(new Token(TypeDeToken.niveau));
							break;
						}
						listeToken.add(new Token(TypeDeToken.ident, buffer));
						break;
					case 102:
						listeToken.add(new Token(TypeDeToken.valeur, buffer));
						break;
					case 103:
						listeToken.add(new Token(TypeDeToken.parGauche));
						break;
					case 104:
						listeToken.add(new Token(TypeDeToken.parDroite));
						break;
					case 105:
						listeToken.add(new Token(TypeDeToken.plus));
						break;
					case 106:
						listeToken.add(new Token(TypeDeToken.asterisque));
						break;
					case 107:
						listeToken.add(new Token(TypeDeToken.slash));
						break;
					case 108:
						listeToken.add(new Token(TypeDeToken.virgule));
						break;
					case 109:
						listeToken.add(new Token(TypeDeToken.crochetGauche));
						break;
					case 110:
						listeToken.add(new Token(TypeDeToken.crochetDroit));
						break;
					case 111:
						listeToken.add(new Token(TypeDeToken.accoGauche));
						break;
					case 112:
						listeToken.add(new Token(TypeDeToken.accoDroite));
						break;
					case 113:
						listeToken.add(new Token(TypeDeToken.etLogique));
						break;
					case 114:
						listeToken.add(new Token(TypeDeToken.ouLogique));
						break;
					case 115:
						listeToken.add(new Token(TypeDeToken.pointVirgule));
						break;
					case 116:
						listeToken.add(new Token(TypeDeToken.deuxPoints));
						break;
					case 117:
						listeToken.add(new Token(TypeDeToken.coordonnee, buffer));
						break;
				}

				if (prochainEtat == 101 ||
						prochainEtat == 102 ||
						prochainEtat == 103 ||
						prochainEtat == 117) {
					this.retourArriere();
				}

				etat = ETAT_INITIAL;
				buffer = "";

			} else {
				etat = prochainEtat;
				if (etat > 0 && etat != 5 && etat != 9)
					buffer += c;
			}
		} while (c != null);

		return listeToken;

	}

	private Character lireCaractere() {
		if (this.pos == this.entree.length())
			return null;
		Character readChar = this.entree.charAt(this.pos);
		this.pos++;
		return readChar;
	}

	private void retourArriere() {
		this.pos--;
	}

	private int indiceSymbole(Character c) throws IllegalCharacterException {

		if (c == null)
			return 0;
		if (Character.isWhitespace(c))
			return 0;
		if (Character.isDigit(c))
			return 2;
		if (Character.isLetter(c))
			return 1;

		int returnValue = 0;

		switch (c) {
			case '(':
				returnValue = 3;
				break;
			case ')':
				returnValue = 4;
				break;
			case '+':
				returnValue = 5;
				break;
			case '*':
				returnValue = 6;
				break;
			case '/':
				returnValue = 7;
				break;
			case ',':
				returnValue = 8;
				break;
			case '[':
				returnValue = 9;
				break;
			case ']':
				returnValue = 10;
				break;
			case '{':
				returnValue = 11;
				break;
			case '}':
				returnValue = 12;
				break;
			case '&':
				returnValue = 13;
				break;
			case '|':
				returnValue = 14;
				break;
			case ';':
				returnValue = 15;
				break;
			case ':':
				returnValue = 16;
				break;
			default:
				throw new IllegalCharacterException("Erreur : Le caract�re " + c + " n'est pas reconnu");

		}
		return returnValue;
	}

}
