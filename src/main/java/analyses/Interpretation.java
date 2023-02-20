package analyses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tla.*;

public class Interpretation {

	private int xSpawn;
	private int ySpawn;
	//Coordonees Murs et Spawn / Exit
	private HashMap<String, Integer> xWall;
	private HashMap<String, Integer> yWall;
	private HashMap<String, Integer> valeurWall;
	private HashMap<String, String> orientationWall;
	private int nbWall;

	private ArrayList<Fantome> fantomes;

	private ArrayList<Commutateur> commutateurs;
	private HashMap<String, Integer> lienIdentNumCommutateur;
	private int nbCommutateurs;

	private ArrayList<Trappe> trappes;

	//Doors
	private HashMap<Integer, Noeud> doors;
	private Integer nbDoorsWithSwitch;
	HashMap<Integer, Integer> xDoors;
	HashMap<Integer, Integer> yDoors;

	
	
	public Interpretation() {


		/* Wall */
		nbWall = 0;
		this.xWall = new HashMap<String, Integer>();
		this.yWall = new HashMap<String, Integer>();
		this.valeurWall = new HashMap<String, Integer>();
		this.orientationWall = new HashMap<String, String>();

		//Fantomes
		this.fantomes = new ArrayList<Fantome>();

		this.commutateurs = new ArrayList<Commutateur>();
		this.lienIdentNumCommutateur = new HashMap<String, Integer>();
		this.nbCommutateurs = 0;

		this.trappes = new ArrayList<Trappe>();

		this.nbDoorsWithSwitch = 0;
		this.doors = new HashMap<Integer, Noeud>();
		this.xDoors = new HashMap<Integer, Integer>();
		this.yDoors = new HashMap<Integer, Integer>();

	}

	public int getxSpawn() {
		return xSpawn;
	}

	public int getySpawn() {
		return ySpawn;
	}

	public List<Fantome> getFantomes(){
		return this.fantomes;
	}

	public List<Trappe> getTrappes() {
		return this.trappes;
	}

	public List<Commutateur> getCommutateurs() {
		return this.commutateurs;
	}

	public HashMap<String, Integer> getxWall(){
		return this.xWall;
	}
	
	public HashMap<String, Integer> getyWall(){
		return this.yWall;
	}

	/*
	interprete le noeud n
	et appel récursif sur les noeuds enfants de n
	 */
	public void interpreter(Noeud n) {
		/* A COMPLETER */
		if (n.getTypeDeNoeud() == TypeDeNoeud.niveau) {
//			this.numNiveau = Integer.valueOf(n.getValeur());
			//Enregistrement Spawn
			String[] spawnValue = n.enfant(0).getValeur().split(",");
			this.xSpawn = Integer.valueOf(spawnValue[0]);
			this.ySpawn = Integer.valueOf(spawnValue[1]);

			//Enregistrement Exit
			String[] exitValue = n.enfant(1).getValeur().split(",");
			xWall.put("exit", Integer.valueOf(exitValue[0]));
			yWall.put("exit", Integer.valueOf(exitValue[1]));

			interpreter(n.enfant(2));
		}

		
		if (n.getTypeDeNoeud() == TypeDeNoeud.statement) {
			if (n.nombreEnfants() == 2) {

				interpreter(n.enfant(0));
				interpreter(n.enfant(1));
			} else {
				interpreter(n.enfant(0));
			}
		}
		
		if (n.getTypeDeNoeud() == TypeDeNoeud.wall) {
			//Identification des wall avec un nom dynamique ex : wall1
			String name = "wall" + String.valueOf(nbWall);
			nbWall++;

			Noeud coord = n.enfant(0);
			ajoutCoordonneeWall(name, coord.getValeur());

			if (coord.nombreEnfants() > 0) {
				Noeud valeur = coord.enfant(0);
				Noeud orientation = valeur.enfant(0);

				this.valeurWall.put(name, Integer.valueOf(valeur.getValeur()));
				this.orientationWall.put(name, orientation.getValeur());
			}
		}

		if (n.getTypeDeNoeud() == TypeDeNoeud.trap) {
			Noeud coordTrap = n.enfant(0);
            Noeud orientTrap = coordTrap.enfant(0);
            Noeud coordDestTrap = coordTrap.enfant(1);

            String[] splitTrap = coordTrap.getValeur().split(",");
            int xTrap = Integer.valueOf(splitTrap[0]);
            int yTrap = Integer.valueOf(splitTrap[1]);

            String orientation = orientTrap.getValeur();

            String[] splitDestTrap = coordDestTrap.getValeur().split(",");
            int xTrapDest = Integer.valueOf(splitDestTrap[0]);
            int yTrapDest = Integer.valueOf(splitDestTrap[1]);

            Direction direction = null;

            if (orientation.equals("up")) {
                direction = Direction.HAUT;
            }
            if (orientation.equals("down")) {
                direction = Direction.BAS;
            }
            if (orientation.equals("right")) {
                direction = Direction.DROITE;
            }
            if (orientation.equals("left")) {
                direction = Direction.GAUCHE;
            }
            Trappe trap = new Trappe(xTrap, yTrap, direction, xTrapDest, yTrapDest);
			this.trappes.add(trap);
		}

		if (n.getTypeDeNoeud() == TypeDeNoeud.switchblock) {
			Noeud ident = n.enfant(0);
			String identifiant = ident.getValeur();
			Noeud coordNoeudSwitch = ident.enfant(0);
			String[] coordSwitch = coordNoeudSwitch.getValeur().split(",");
			int xSwitch = Integer.valueOf(coordSwitch[0]);
			int ySwitch = Integer.valueOf(coordSwitch[1]);
			this.commutateurs.add(new Commutateur(xSwitch,ySwitch));
			this.lienIdentNumCommutateur.put(identifiant, nbCommutateurs);
			this.nbCommutateurs++;
		}

		if (n.getTypeDeNoeud() == TypeDeNoeud.door) {
			Noeud coordDoor = n.enfant(0);

			String[] splitDoor = coordDoor.getValeur().split(",");
            this.xDoors.put(nbDoorsWithSwitch,Integer.valueOf(splitDoor[0]));
			this.yDoors.put(nbDoorsWithSwitch,Integer.valueOf(splitDoor[1]));

			if (n.nombreEnfants() == 2) {
				Noeud racine = makeArbreDecisionDoors(n);
				this.doors.put(nbDoorsWithSwitch, racine);
				nbDoorsWithSwitch++;
				Noeud.afficheNoeud(racine, 0);
			}
		}

		if (n.getTypeDeNoeud() == TypeDeNoeud.ghost) {
			Noeud coordGhost = n.enfant(0);
			String[] split = coordGhost.getValeur().split(",");
			int xGhost = Integer.valueOf(split[0]);
			int yGhost = Integer.valueOf(split[1]);

			List<Direction> directionsGhost = this.getDirectionsGhost(n);
			Fantome fantome = new Fantome(xGhost, yGhost, directionsGhost);
			this.fantomes.add(fantome);
		}
	}

	private Noeud makeArbreDecisionDoors(Noeud noeudLanguage) {

		Noeud statement = null;
		//Premier Statement
		if (noeudLanguage.getTypeDeNoeud() == TypeDeNoeud.door) {

			statement = new Noeud(TypeDeNoeud.statement,null);

			String etatSwitch = noeudLanguage.enfant(1).enfant(0).getValeur();

			Noeud switchBlock = new Noeud(TypeDeNoeud.switchblock, noeudLanguage.enfant(1).getValeur());
			Noeud Etat = new Noeud(TypeDeNoeud.etat, etatSwitch);

			switchBlock.ajout(Etat);
			statement.ajout(switchBlock);

			if (noeudLanguage.enfant(1).nombreEnfants() == 2) {
				//Donner le noeud logique a modifier
				statement.ajout(makeArbreDecisionDoors(noeudLanguage.enfant(1).enfant(1)));
			}

			return statement;

		} else if (noeudLanguage.getTypeDeNoeud() == TypeDeNoeud.logique) {
			
			statement = new Noeud(TypeDeNoeud.statement, noeudLanguage.getValeur());

			Noeud switchBlockNext = new Noeud(TypeDeNoeud.switchblock, noeudLanguage.enfant(0).getValeur());
			Noeud etatNext = new Noeud(TypeDeNoeud.etat,noeudLanguage.enfant(0).enfant(0).getValeur() );

			switchBlockNext.ajout(etatNext);
			statement.ajout(switchBlockNext);

			if (noeudLanguage.enfant(0).nombreEnfants() == 2){
				statement.ajout(makeArbreDecisionDoors(noeudLanguage.enfant(0).enfant(1)));
			}

			return statement;

		}
		return statement;
	}

	private List<Direction> getDirectionsGhost(Noeud n) {
		List<Direction> result = new ArrayList<Direction>();
		Noeud noeudOrientation = n.enfant(1);
		do {
			ajoutDirectionGhost(result, noeudOrientation);

			noeudOrientation = noeudOrientation.enfant(0);
			
		} while (Integer.valueOf(noeudOrientation.nombreEnfants()) == 1);
		//Ne pas oublier le dernier noeud qui n'a pas d'enfants
		ajoutDirectionGhost(result, noeudOrientation);
		
		return result;
	}

	private void ajoutDirectionGhost(List<Direction> result, Noeud noeudOrientation) {
		if (noeudOrientation.getValeur().equals("up")) {
			result.add(Direction.HAUT);
		}
		if (noeudOrientation.getValeur().equals("down")) {
			result.add(Direction.BAS);
		}
		if (noeudOrientation.getValeur().equals("right")) {
			result.add(Direction.DROITE);
		}
		if (noeudOrientation.getValeur().equals("left")) {
			result.add(Direction.GAUCHE);
		}
	}

	private void ajoutCoordonneeWall(String name, String valeurNoeud){
		String[] coord = valeurNoeud.split(",");
		xWall.put(name, Integer.valueOf(coord[0]));
		yWall.put(name, Integer.valueOf(coord[1]));
	}

	public char[] getWalls(){
		char[][] plateau = new char[14][20];
		String namePattern = "wall";
		//Exit
		plateau[this.yWall.get("exit")][this.xWall.get("exit")] = '*';

		for(int i = 0; i < this.nbWall; i++){
			String nameWall = namePattern + String.valueOf(i);
//			plateau[xWall.get(nameWall)][yWall.get(nameWall)] = '#';
			plateau[yWall.get(nameWall)][xWall.get(nameWall)] = '#';

			//Wall type Valeur + Orientation
			if (valeurWall.containsKey(nameWall)) {
				
				int valeur = valeurWall.get(nameWall);
				String orientation = orientationWall.get(nameWall);
				for(int y =1; y < valeur; y++){
					if (orientation.equals("up")) {
						plateau[yWall.get(nameWall)-y][xWall.get(nameWall)] = '#';
					}
					if (orientation.equals("down")) {
			//			plateau[xWall.get(nameWall)+y][yWall.get(nameWall)] = '#';
						plateau[yWall.get(nameWall)+y][xWall.get(nameWall)] = '#';
					}
					if (orientation.equals("left")) {
						plateau[yWall.get(nameWall)][xWall.get(nameWall)-y] = '#';
					}
					if (orientation.equals("right")) {
						plateau[yWall.get(nameWall)][xWall.get(nameWall)+y] = '#';
					}
				}
			}
		}

		//Convertir char[][] en char[]
		char[] result = new char[280]; //280 = 14*20

		for(int y = 0; y < 14; y++) {
            for(int x = 0; x < 20; x++) {
                result[y* 20 + x] = plateau[y][x];
            }
        }

		return result;
	}

	public void hookApresDeplacement(Plateau plateau) {
		for(int i = 0; i < nbDoorsWithSwitch; i++){
			int x = xDoors.get(i);
			int y = yDoors.get(i);
			if (verifOuverture(this.doors.get(i))) {
				plateau.carreaux[x + y*Plateau.LARGEUR_PLATEAU].setEtat(EtatCarreau.VIDE);
			} else {
				plateau.carreaux[x + y*Plateau.LARGEUR_PLATEAU].setEtat(EtatCarreau.PORTE_FERMEE);
			}
		}
	 }

	private boolean verifOuverture(Noeud noeud) {

		//CAS NOEUD 2 ENFANTS
		if (noeud.nombreEnfants() == 2) {
			Noeud switchBlock = noeud.enfant(0);
			Noeud statementNext = noeud.enfant(1);

			if (statementNext.getValeur().equals("et")) {
				//ET
				//Verification
				Integer numCommutateur = this.lienIdentNumCommutateur.get(noeud.enfant(0).getValeur());
				Commutateur commutateur = this.commutateurs.get(numCommutateur);
				Boolean etatCommutateurOuverture = false;
				if(switchBlock.enfant(0).getValeur().equals("on")){
					etatCommutateurOuverture= true;
				}
				//Comparaison
				if (commutateur.getEtat() == etatCommutateurOuverture) {
					return verifOuverture(statementNext);
				} else {
					while(statementNext.getValeur().equals("et") && statementNext.nombreEnfants() == 2){
						statementNext = statementNext.enfant(1);
					}
					if (statementNext.getValeur().equals("ou")) {
						return verifOuverture(statementNext);
					} else {
						return false;
					}
				}

			} else {
				//OU
				//Verification
				Integer numCommutateur = this.lienIdentNumCommutateur.get(noeud.enfant(0).getValeur());
				Commutateur commutateur = this.commutateurs.get(numCommutateur);
				Boolean etatCommutateurOuverture = false;
				if(switchBlock.enfant(0).getValeur().equals("on")){
					etatCommutateurOuverture= true;
				}
				//Comparaison
				if (commutateur.getEtat() == etatCommutateurOuverture) {
					return true;
				} else {
					return verifOuverture(statementNext);
				}
				
			}
			
		} else {
			//Verif Et ou Ou
			//Verification
			Integer numCommutateur = this.lienIdentNumCommutateur.get(noeud.enfant(0).getValeur());
			Commutateur commutateur = this.commutateurs.get(numCommutateur);
			Boolean etatCommutateurOuverture = false;
			if(noeud.enfant(0).enfant(0).getValeur().equals("on")){
				etatCommutateurOuverture= true;
			}
			//Comparaison
			if (commutateur.getEtat() == etatCommutateurOuverture) {
				return true;
			} else {
				return false;
			}
		}
	};
}
