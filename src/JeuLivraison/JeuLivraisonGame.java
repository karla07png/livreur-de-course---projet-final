package JeuLivraison;

import Engine.Canvas;
import Engine.Game;
import Engine.GamePad;
import java.awt.Color;

public class JeuLivraisonGame extends Game {

    private GamePad clavier;
    private Livreur livreur;
    private CarteVille carte;
    private Livraison livraison;
    private Chien chien;
    private Voiture[] voitures;

    @Override
    public void initialize() {
        carte = new CarteVille();
        clavier = new GamePad();
        addKeyListener(clavier);

        livreur = new Livreur(clavier,
                carte.getLargeur(), carte.getHauteur());

        livraison = new Livraison();

        chien = new Chien(carte.getLargeur(), carte.getHauteur());

        voitures = new Voiture[] {

                new Voiture(carte.getLargeur(), carte.getHauteur(),
                        426, 100, 3, 0, true),

                new Voiture(carte.getLargeur(), carte.getHauteur(),
                        474, 900, 4, 1, false),

                new Voiture(carte.getLargeur(), carte.getHauteur(),
                        938, 352, 3, 2, true),

                new Voiture(carte.getLargeur(), carte.getHauteur(),
                        986, 736, 4, 3, false),

                new Voiture(carte.getLargeur(), carte.getHauteur(),
                        1450, 500, 3, 4, true),

                new Voiture(carte.getLargeur(), carte.getHauteur(),
                        1498, 1120, 4, 5, false)
        };
    }

    @Override
    public void update() {

        if (clavier.isQuitPressed()) {
            stop();
            return;
        }

        livreur.update();

        chien.poursuivre(livreur);

        livraison.update(livreur);

        for (Voiture voiture : voitures) {
            voiture.update();

            if (voiture.toucheLivreur(livreur)) {
                livreur.moveTo(448, 480);
            }


        }
    }

    @Override
    public void drawOnBuffer(Canvas canvas) {
        canvas.drawRectangle(0, 0, getWidth(), getHeight(),
                new Color(50, 120, 85));

        int cameraX = Math.max(0, Math.min(
                livreur.getX() - getWidth() / 2,
                carte.getLargeur() - getWidth()
        ));

        int cameraY = Math.max(0, Math.min(
                livreur.getY() - getHeight() / 2,
                carte.getHauteur() - getHeight()
        ));

        carte.dessiner(canvas, cameraX, cameraY);

        for (Voiture voiture : voitures) {
            voiture.dessiner(canvas, cameraX, cameraY);
        }

        canvas.drawRectangle(0, 0, getWidth(), 56,
                new Color(30, 50, 60));

        canvas.drawString(20, 24, "JEU DE LIVRAISON", Color.WHITE);
        canvas.drawString(20, 43,
                "Fleches : deplacer | Echap : quitter", Color.WHITE);

        livreur.dessiner(canvas, cameraX, cameraY);

        livraison.dessiner(canvas, livreur, cameraX, cameraY);

        canvas.drawString(350, 24,
                "Livraisons : " + livraison.getScore(), Color.WHITE);

        chien.dessiner(canvas, cameraX, cameraY);
    }
}