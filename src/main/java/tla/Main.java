package tla;

import java.util.List;

import analyses.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {

        

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String cheminDacces = "C:\\Users\\henri\\Documents\\WorkSpaces\\Java\\projet-tla\\src\\main\\resources\\";
        //Analyse des niveaux
        Interpretation niveau1 = new Interpretation();
        String codeNiveau1 = reader.read(cheminDacces.concat("Niveau1.txt"));
        try {
			List<Token> tokens = new AnalyseLexicale().analyse(codeNiveau1);
			Noeud racine = new AnalyseSyntaxique().analyse(tokens);
            niveau1.interpreter(racine);
            Noeud.afficheNoeud(racine, 0);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

        Interpretation niveau2 = new Interpretation();
        String codeNiveau2 = reader.read(cheminDacces.concat("Niveau2.txt"));
        try {
			List<Token> tokens = new AnalyseLexicale().analyse(codeNiveau2);
			Noeud racine = new AnalyseSyntaxique().analyse(tokens);
            niveau2.interpreter(racine);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

        Interpretation niveau3 = new Interpretation();
        String codeNiveau3 = reader.read(cheminDacces.concat("Niveau3.txt"));
        try {
			List<Token> tokens = new AnalyseLexicale().analyse(codeNiveau3);
			Noeud racine = new AnalyseSyntaxique().analyse(tokens);
            niveau3.interpreter(racine);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

        Interpretation niveau4 = new Interpretation();
        String codeNiveau4 = reader.read(cheminDacces.concat("NiveauAllane.txt"));
        try {
			List<Token> tokens = new AnalyseLexicale().analyse(codeNiveau4);
			Noeud racine = new AnalyseSyntaxique().analyse(tokens);
            niveau4.interpreter(racine);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

        // fenêtre principale et panneau de menu
        GridPane menuPane = new GridPane();
        Button btnNiveau1 = new Button("niveau 1");
        menuPane.add(btnNiveau1, 0, 1);
        Button btnNiveau2 = new Button("niveau 2");
        menuPane.add(btnNiveau2, 0, 2);
        Button btnNiveau3 = new Button("niveau 3");
        menuPane.add(btnNiveau3, 0, 3);
        Button btnNiveau4 = new Button("niveau 4");
        menuPane.add(btnNiveau4, 0, 4);
        ImageView imageView = new ImageView(LibrairieImages.imgJoueurGrand);
        menuPane.add(imageView, 1, 0, 1, 5);

        Scene scene = new Scene(menuPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        // panneau racine du jeu

        BorderPane borderPane = new BorderPane();

        Plateau plateau = new Plateau(borderPane);

        btnNiveau1.setOnAction(event -> {
            // affiche le panneau racine du jeu (à la place du panneau de menu)
            scene.setRoot(borderPane);

            // affecte un object correspondant au niveau choisi
            plateau.setNiveau(niveau1);

            // démarre le jeu
            plateau.start();

            // ajuste la taille de la fenêtre
            primaryStage.sizeToScene();
        });

        btnNiveau2.setOnAction(event -> {
            scene.setRoot(borderPane);
            plateau.setNiveau(niveau2);
            plateau.start();
            primaryStage.sizeToScene();
        });

        btnNiveau3.setOnAction(event -> {
            scene.setRoot(borderPane);
            plateau.setNiveau(niveau3);
            plateau.start();
            primaryStage.sizeToScene();
        });

        btnNiveau4.setOnAction(event -> {
            scene.setRoot(borderPane);
            plateau.setNiveau(niveau4);
            plateau.start();
            primaryStage.sizeToScene();
        });
        // gestion du clavier

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.Q) {
                // touche q : quitte le jeu et affiche le menu principal
                plateau.stop();
                scene.setRoot(menuPane);
                primaryStage.sizeToScene();
            }
            if (event.getCode() == KeyCode.R) {
                // touche r : redémarre le niveau en cours
                plateau.start();
            }

            if (event.getCode() == KeyCode.LEFT) {
                plateau.deplGauche();
            }
            if (event.getCode() == KeyCode.RIGHT) {
                plateau.deplDroite();
            }
            if (event.getCode() == KeyCode.UP) {
                plateau.deplHaut();
            }
            if (event.getCode() == KeyCode.DOWN) {
                plateau.deplBas();
            }
        });
    }
}
