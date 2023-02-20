package analyses;

//import java.lang.ProcessBuilder.Redirect.Type;
import java.util.List;

public class AnalyseSyntaxique {

	private int pos;
	private List<Token> tokens;

	/*
	 * effectue l'analyse syntaxique à partir de la liste de tokens
	 * et retourne le noeud racine de l'arbre syntaxique abstrait
	 */
	public Noeud analyse(List<Token> tokens) throws Exception {
		pos = 0;
		this.tokens = tokens;
		Noeud expr = S();
		if (pos != tokens.size()) {
			System.out.println("L'analyse syntaxique s'est terminé avant l'examen de tous les tokens");
			throw new IncompleteParsingException();
		}
		return expr;
	}

	/*
	 * 
	 * Traite la dérivation du symbole non-terminal S
	 * 
	 * S -> niveau intval Coordonnees Coordonnees ; S'
	 * 
	 */

	private Noeud S() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.niveau) {
			lireToken();

			if (getTypeDeToken() == TypeDeToken.valeur) {
				Token t1 = lireToken();
				Noeud niveau = new Noeud(TypeDeNoeud.niveau, t1.getValeur());

				if (getTypeDeToken() == TypeDeToken.coordonnee) {
					Token t2 = lireToken();
					niveau.ajout(new Noeud(TypeDeNoeud.coordonnee, t2.getValeur()));

					if (getTypeDeToken() == TypeDeToken.coordonnee) {
						Token t3 = lireToken();
						niveau.ajout(new Noeud(TypeDeNoeud.coordonnee, t3.getValeur()));
						if (getTypeDeToken() == TypeDeToken.pointVirgule) {
							lireToken();
							Noeud next = S_prime();
							if (next != null) {
								niveau.ajout(next);
							}
							return niveau;

						} else {
							throw new UnexpectedTokenException("; attendu");
						}

					} else {
						throw new UnexpectedTokenException("Coordonnee attendu");
					}
				} else {
					throw new UnexpectedTokenException("Coordonnee attendu");
				}
			} else {
				throw new UnexpectedTokenException("intval attendu");
			}
		} else {
			throw new UnexpectedTokenException("'niveau' attendu");
		}
	}

	/*
	 * 
	 * Traite la dérivation du symbole non-terminal S'
	 * 
	 * S' -> Statement S' | epsilon
	 * 
	 */

	private Noeud S_prime() throws UnexpectedTokenException {
		if (finAtteinte())
			return null;
		Noeud statement = Statement();
		Noeud sprime = S_prime();
		if (sprime != null)
			statement.ajout(sprime);
		return statement;
	}

	/*
	 * 
	 * Traite la dérivation du symbole non-terminal Statement
	 * 
	 * Statement -> Wall : ParamWall ; | Trap : ParamTrap ; | ghost : paramghost ; |
	 * switchblock : paramswitchblock ;
	 * 
	 * 
	 */

	private Noeud Statement() throws UnexpectedTokenException {
		Noeud statement = new Noeud(TypeDeNoeud.statement);
		if (getTypeDeToken() == TypeDeToken.wall) {
			lireToken();
			Noeud wall = new Noeud(TypeDeNoeud.wall);
			statement.ajout(wall);
			if (getTypeDeToken() == TypeDeToken.deuxPoints) {
				lireToken();
				wall.ajout(ParamWall());
				if (getTypeDeToken() == TypeDeToken.pointVirgule) {
					lireToken();
					return statement;
				} else {
					throw new UnexpectedTokenException("; attendu");
				}

			} else {
				throw new UnexpectedTokenException(": attendu");
			}
		}
		if (getTypeDeToken() == TypeDeToken.trap) {
			lireToken();
			Noeud trap = new Noeud(TypeDeNoeud.trap);
			statement.ajout(trap);
			if (getTypeDeToken() == TypeDeToken.deuxPoints) {
				lireToken();
				trap.ajout(ParamTrap());
				if (getTypeDeToken() == TypeDeToken.pointVirgule) {
					lireToken();
					return statement;
				} else {
					throw new UnexpectedTokenException(
							"position token " + pos + " Token Actuel " + getTypeDeToken() + "; ici attendu");
				}
			} else {
				throw new UnexpectedTokenException(": attendu");
			}
		}
		if (getTypeDeToken() == TypeDeToken.ghost) {
			lireToken();
			if (getTypeDeToken() == TypeDeToken.deuxPoints) {
				lireToken();
				statement.ajout(Paramghost());
				if (getTypeDeToken() == TypeDeToken.pointVirgule) {
					lireToken();
					return statement;
				} else {
					throw new UnexpectedTokenException(
							"position token " + pos + " Token Actuel " + getTypeDeToken() + " ; attendu");
				}
			} else {
				throw new UnexpectedTokenException(": attendu");
			}
		}
		if (getTypeDeToken() == TypeDeToken.switchblock) {
			lireToken();
			Noeud switchblock = new Noeud(TypeDeNoeud.switchblock);
			statement.ajout(switchblock);
			if (getTypeDeToken() == TypeDeToken.deuxPoints) {
				lireToken();
				switchblock.ajout(Paramswitchblock());
				if (getTypeDeToken() == TypeDeToken.pointVirgule) {
					lireToken();
					return statement;
				} else {
					throw new UnexpectedTokenException("; attendu");
				}
			} else {
				throw new UnexpectedTokenException(": attendu");
			}
		}

		if (getTypeDeToken() == TypeDeToken.door) {
			lireToken();
			if (getTypeDeToken() == TypeDeToken.deuxPoints) {
				lireToken();
				statement.ajout(Paramdoor());
				if (getTypeDeToken() == TypeDeToken.pointVirgule) {
					lireToken();
					return statement;
				} else {
					throw new UnexpectedTokenException("; attendu");
				}
			} else {
				throw new UnexpectedTokenException(": attendu");
			}
		}

		else {
			throw new UnexpectedTokenException("position token " + pos + " Token Actuel " + getTypeDeToken()
					+ " wall ou trap ou ghost ou switchblock attendu");
		}
	}

	/*
	 * Traite la derviation du symbole non terminal Paramwall
	 * 
	 * ParamWall -> Coordonnees ParamWall'
	 */

	private Noeud ParamWall() throws UnexpectedTokenException {
		if (getTypeDeToken() == TypeDeToken.coordonnee) {
			Token t1 = lireToken();
			Noeud coordonnee = new Noeud(TypeDeNoeud.coordonnee, t1.getValeur());

			Noeud suiteParam = ParamWall_prime();
			if (suiteParam != null) {
				coordonnee.ajout(suiteParam);
			}
			return coordonnee;

		} else {
			throw new UnexpectedTokenException("Coordonnee attendu");
		}
	}

	// ParamWall' -> intval orientation | epsilon
	private Noeud ParamWall_prime() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.valeur) {
			Token t1 = lireToken();
			Noeud valeur = new Noeud(TypeDeNoeud.intVal, t1.getValeur());

			if (getTypeDeToken() == TypeDeToken.orientation) {
				Token t2 = lireToken();
				Noeud orientation = new Noeud(TypeDeNoeud.orientation, t2.getValeur());
				valeur.ajout(orientation);
				return valeur;
			} else {
				throw new UnexpectedTokenException("Orientation attendu");
			}

		}

		return null;
	}

	/*
	 * 
	 * Traite la dérivation du symbole non-terminal ParamTrap
	 * 
	 * Paramtrap -> coordonnee orientation coordonnee pointvirgule
	 * 
	 */

	private Noeud ParamTrap() throws UnexpectedTokenException {
		if (getTypeDeToken() == TypeDeToken.coordonnee) {
			Token t1 = lireToken();
			Noeud trap = new Noeud(TypeDeNoeud.coordonnee, t1.getValeur());
			if (getTypeDeToken() == TypeDeToken.orientation) {
				Token t2 = lireToken();
				Noeud orientation = new Noeud(TypeDeNoeud.orientation, t2.getValeur());
				trap.ajout(orientation);
				if (getTypeDeToken() == TypeDeToken.coordonnee) {
					Token t3 = lireToken();
					Noeud coordonnee = new Noeud(TypeDeNoeud.coordonnee, t3.getValeur());
					trap.ajout(coordonnee);
					if (getTypeDeToken() == TypeDeToken.pointVirgule) {
						// lireToken();
						return trap;
					} else {
						throw new UnexpectedTokenException("pointvirgule attendu");
					}
				} else {
					throw new UnexpectedTokenException("coordonnee attendu");
				}

			} else {
				throw new UnexpectedTokenException("Orientation attendu");
			}
		} else {
			throw new UnexpectedTokenException("coordonnee attendu");
		}
	}

	/**
	 * Traite la dérivation du symbole non-terminal paramghost
	 * 
	 * ParamGhost → Coordonnées crochetGauche orientation orientation’ crochetDroit
	 * 
	 */
	private Noeud Paramghost() throws UnexpectedTokenException {
		Noeud ghost = new Noeud(TypeDeNoeud.ghost);
		if (getTypeDeToken() == TypeDeToken.coordonnee) {
			Token t1 = lireToken();
			Noeud coordonnee = new Noeud(TypeDeNoeud.coordonnee, t1.getValeur());
			ghost.ajout(coordonnee);
			if (getTypeDeToken() == TypeDeToken.crochetGauche) {
				lireToken();
				if (getTypeDeToken() == TypeDeToken.orientation) {
					Token t2 = lireToken();
					Noeud orientation = new Noeud(TypeDeNoeud.orientation, t2.getValeur());
					ghost.ajout(orientation);
					Noeud next = orientation_prime();
					if (next != null) {
						orientation.ajout(next);
					}
					if (getTypeDeToken() == TypeDeToken.crochetDroit) {
						lireToken();
						return ghost;
					} else {
						throw new UnexpectedTokenException("] attendu");
					}
				} else {
					throw new UnexpectedTokenException("orientation attendu");
				}
			} else {
				throw new UnexpectedTokenException("[ attendu");
			}
		} else {
			throw new UnexpectedTokenException("coordonnee attendu");
		}
	}

	/**
	 * Traite la dérivation du symbole non-terminal orientation'
	 * 
	 * orientation’ → , orientation orientation’ | epsilon
	 * 
	 * @throws UnexpectedTokenException
	 * 
	 */

	private Noeud orientation_prime() throws UnexpectedTokenException {
		if (getTypeDeToken() == TypeDeToken.virgule) {
			lireToken();
			if (getTypeDeToken() == TypeDeToken.orientation) {
				Token t1 = lireToken();
				Noeud orientation = new Noeud(TypeDeNoeud.orientation, t1.getValeur());
				Noeud next = orientation_prime();
				if (next != null) {
					orientation.ajout(next);
				}
				return orientation;
			} else {
				throw new UnexpectedTokenException("orientation attendu");
			}
		}
		return null;
	}

	/**
	 * Traite la dérivation du symbole non-terminal orientation'
	 * 
	 * ParamSwitch → Coordonnee ident
	 * 
	 * @throws UnexpectedTokenException
	 * 
	 */

	private Noeud Paramswitchblock() throws UnexpectedTokenException {
		if (getTypeDeToken() == TypeDeToken.coordonnee) {
			Token t1 = lireToken();
			Noeud coordonnee = new Noeud(TypeDeNoeud.coordonnee, t1.getValeur());
			if (getTypeDeToken() == TypeDeToken.ident) {
				Token t2 = lireToken();
				Noeud ident = new Noeud(TypeDeNoeud.ident, t2.getValeur());
				ident.ajout(coordonnee);
				return ident;
			} else {
				throw new UnexpectedTokenException("ident attendu");
			}
		} else {
			throw new UnexpectedTokenException("coordonnee attendu");
		}
	}

	/**
	 * Traite la dérivation du symbole non-terminal Paramdoor
	 * 
	 * ParamDoor → Coordonnee etat ParamDoor’
	 * 
	 * @throws UnexpectedTokenException
	 * 
	 */
	private Noeud Paramdoor() throws UnexpectedTokenException {
		Noeud door = new Noeud(TypeDeNoeud.door);
		if (getTypeDeToken() == TypeDeToken.coordonnee) {
			Token t1 = lireToken();
			Noeud coordonnee = new Noeud(TypeDeNoeud.coordonnee, t1.getValeur());
			door.ajout(coordonnee);
				Noeud next = paramdoor_prime();
				if (next != null) {
					door.ajout(next);
				}
				return door;
		} else {
			throw new UnexpectedTokenException("coordonnee attendu");
		}
	}

	/**
	 * Traite la dérivation du symbole non-terminal Paramdoor'
	 * 
	 * ParamDoor’ → ident etat ParamDoor_deux | epsilon
	 * 
	 * @throws UnexpectedTokenException
	 * 
	 */
	private Noeud paramdoor_prime() throws UnexpectedTokenException {
		if (getTypeDeToken() == TypeDeToken.ident) {
			Token t1 = lireToken();
			Noeud ident = new Noeud(TypeDeNoeud.ident, t1.getValeur());
			if (getTypeDeToken() == TypeDeToken.etat) {
				Token t2 = lireToken();
				Noeud etat = new Noeud(TypeDeNoeud.etat, t2.getValeur());
				ident.ajout(etat);
				Noeud next = paramdoor_deux();
				if (next != null) {
					ident.ajout(next);
				}
				return ident;
			} else {
				throw new UnexpectedTokenException("etat attendu");
			}
		}
		return null;
	}

	/**
	 * Traite la dérivation du symbole non-terminal Paramdoor'
	 * 
	 * ParamDoor_deux → & ParamDoor’ | | ParamDoor’ | epsilon
	 * 
	 * @throws UnexpectedTokenException
	 * 
	 */
	private Noeud paramdoor_deux() throws UnexpectedTokenException {
		if (getTypeDeToken() == TypeDeToken.etLogique) {
			lireToken();
			Noeud etlogique = new Noeud(TypeDeNoeud.logique,"et");
			Noeud next = paramdoor_prime();
			if (next != null) {
				etlogique.ajout(next);
			}
			return etlogique;
		}
		if (getTypeDeToken() == TypeDeToken.ouLogique) {
			lireToken();
			Noeud oulogique = new Noeud(TypeDeNoeud.logique,"ou");
			Noeud next = paramdoor_prime();
			if (next != null) {
				oulogique.ajout(next);
			}
			return oulogique;
		}
		return null;
	}

	/*
	 * 
	 * méthodes utilitaires
	 * 
	 */

	private boolean finAtteinte() {
		return pos >= tokens.size();
	}

	/*
	 * Retourne la classe du prochain token à lire
	 * SANS AVANCER au token suivant
	 */
	private TypeDeToken getTypeDeToken() {
		if (pos >= tokens.size()) {
			return null;
		} else {
			return tokens.get(pos).getTypeDeToken();
		}
	}

	/*
	 * Retourne le prochain token à lire
	 * ET AVANCE au token suivant
	 */
	private Token lireToken() {
		if (pos >= tokens.size()) {
			return null;
		} else {
			Token t = tokens.get(pos);
			pos++;
			return t;
		}
	}

}
