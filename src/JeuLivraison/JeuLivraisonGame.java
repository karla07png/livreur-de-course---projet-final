package JeuLivraison;

import Engine.Canvas;
import Engine.Game;
import Engine.GamePad;
import java.awt.Color;
import Engine.Direction;
import java.awt.Toolkit;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.event.AWTEventListener;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.MouseAdapter;

public class JeuLivraisonGame extends Game {

    private GamePad clavier;
    private Livreur livreur;
    private CarteVille carte;
    private Livraison livraison;
    private Chien chien;
    private Voiture[] voitures;
    private int tempsRestant = 60;
    private int vies = 3;
    private boolean contactVoiture = false;
    private boolean collisionVoiture = false;
    private long debutLivraison;
    private volatile boolean recommencerDemande = false;
    private AWTEventListener souris;

    @Override

    public void initialize() {

        clavier = new GamePad();

        addKeyListener(clavier);

        for (Window fenetre : Window.getWindows()) {
            installerSouris(fenetre);
        }

        recommencerPartie();
    }

    private void recommencerPartie() {
        carte = new CarteVille();

        livreur = new Livreur(clavier,
                carte.getLargeur(), carte.getHauteur());

        livraison = new Livraison();

        chien = new Chien(carte.getLargeur(), carte.getHauteur());

        voitures = new Voiture[] {

                // voitures V

                new Voiture(1920, 1440, 426, 100, 3, 0, Direction.DOWN),
                new Voiture(1920, 1440, 474, 900, 4, 1, Direction.UP),

                new Voiture(1920, 1440, 938, 500, 3, 2, Direction.DOWN),
                new Voiture(1920, 1440, 986, 900, 4, 3, Direction.UP),

                new Voiture(1920, 1440, 1450, 600, 3, 4, Direction.DOWN),
                new Voiture(1920, 1440, 1498, 1250, 4, 5, Direction.UP),

                // voitures H

                new Voiture(1920, 1440, 100, 378, 3, 0, Direction.RIGHT),
                new Voiture(1920, 1440, 1700, 330, 4, 1, Direction.LEFT),

                new Voiture(1920, 1440, 100, 762, 3, 2, Direction.RIGHT),
                new Voiture(1920, 1440, 1700, 714, 4, 3, Direction.LEFT),

                new Voiture(1920, 1440, 100, 1146, 3, 4, Direction.RIGHT),
                new Voiture(1920, 1440, 1700, 1098, 4, 5, Direction.LEFT)
        };

        debutLivraison = System.nanoTime();
        tempsRestant = 60;
        vies = 3;
        contactVoiture = false;
        collisionVoiture = false;

    }

    @Override
    public void update() {

        if (clavier.isQuitPressed()) {

            stop();
            return;
        }

        long tempsEcoule = (System.nanoTime() - debutLivraison)
                / 1_000_000_000L;

        tempsRestant = collisionVoiture
                ? 0 : Math.max(0, 60 - (int) tempsEcoule);

        if (tempsRestant == 0) {

            if (recommencerDemande) {
                recommencerDemande = false;
                recommencerPartie();
            }

            return;
        }

        recommencerDemande = false;

        int ancienX = livreur.getX();
        int ancienY = livreur.getY();

        livreur.update();

        if (!carte.estAccessible(
                livreur.getX(),
                livreur.getY(),
                livreur.getWidth(),
                livreur.getHeight())) {

            livreur.moveTo(ancienX, ancienY);
        }

        chien.poursuivre(livreur);

        int ancienScore = livraison.getScore();

        livraison.update(livreur);

        if (livraison.getScore() > ancienScore) {
            debutLivraison = System.nanoTime();
            tempsRestant = 60;
        }

        boolean touche = false;

        for (Voiture voiture : voitures) {
            voiture.avancer(voitures);

            if (voiture.toucheLivreur(livreur)) {
                touche = true;
            }
        }

        if (touche && !contactVoiture) {
            vies--;

            if (vies == 0) {
                collisionVoiture = true;
                tempsRestant = 0;
            }
        }

        contactVoiture = touche;
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

        livraison.dessinerDirection(canvas, livreur);

        Color couleurTemps = tempsRestant <= 10
                ? Color.RED : new Color(255, 219, 119);

        canvas.drawRectangle(345, 29, 140, 25,
                new Color(20, 32, 40));

        canvas.drawString(355, 43,
                String.format("TEMPS  %02d:%02d",
                        tempsRestant / 60, tempsRestant % 60),
                couleurTemps);

        canvas.drawRectangle(355, 47, 120, 4,
                Color.GRAY);

        if (tempsRestant > 0) {
            canvas.drawRectangle(355, 47,
                    120 * tempsRestant / 60, 4,
                    couleurTemps);
        }


        canvas.drawString(550, 20, "VIES", Color.WHITE);

        for (int i = 0; i < 3; i++) {
            canvas.drawString(550 + i * 25, 43,
                    "♥", i < vies ? Color.RED : Color.GRAY);
        }

        if (tempsRestant == 0) {
            dessinerFinPartie(canvas);
        }

    }

    private void dessinerFinPartie(Canvas canvas) {

        int x = getWidth() / 2 - 170;
        int y = getHeight() / 2 - 120;

        Color dore = new Color(255, 219, 119);

        canvas.drawRectangle(0, 0, getWidth(), getHeight(),
                new Color(0, 0, 0, 150));

        canvas.drawRectangle(x, y, 340, 240, dore);

        canvas.drawRectangle(x + 2, y + 2, 336, 236,
                new Color(32, 42, 52));

        canvas.drawString(x + 95, y + 40,
                "PARTIE TERMINEE", dore);

        canvas.drawString(x + 90, y + 70,
                "Le temps est ecoule !", Color.WHITE);

        canvas.drawRectangle(x + 30, y + 90,
                280, 2, Color.GRAY);

        canvas.drawString(x + 95, y + 120,
                "Livraisons reussies", Color.WHITE);

        canvas.drawString(x + 160, y + 150,
                "" + livraison.getScore(), dore);

        canvas.drawRectangle(x + 50, y + 180,
                240, 40, dore);

        canvas.drawString(x + 110, y + 205,
                "RECOMMENCER", Color.BLACK);
    }

    private void installerSouris(Component composant) {

        for (var ecouteur : composant.getKeyListeners()) {

            if (ecouteur == clavier) {

                composant.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent clic) {

                        int x = getWidth() / 2 - 120;
                        int y = getHeight() / 2 + 60;

                        if (clic.getX() >= x
                                && clic.getX() <= x + 240
                                && clic.getY() >= y
                                && clic.getY() <= y + 40) {

                            recommencerDemande = true;
                        }
                    }
                });

                return;
            }
        }

        if (composant instanceof Container conteneur) {
            for (Component enfant : conteneur.getComponents()) {
                installerSouris(enfant);
            }
        }
    }

}