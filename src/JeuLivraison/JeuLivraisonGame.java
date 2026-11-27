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
    private boolean contactVoiture = false;
    private boolean contactChien = false;
    private long dernierChoc;
    private long derniereMorsure;
    private String messageChoc = "";
    private long debutLivraison;
    private volatile boolean recommencerDemande = false;
    private AWTEventListener souris;
    private boolean chienCapture = false;
    private boolean chienEnCage = false;
    private boolean toucheEAppuyee = false;

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
        chienEnCage = false;

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
        contactChien = false;
        dernierChoc = debutLivraison - 3_000_000_000L;
        derniereMorsure = debutLivraison - 3_000_000_000L;
        messageChoc = "";
        contactVoiture = false;


    }

    @Override
    public void update() {

        if (clavier.isQuitPressed()) {

            stop();
            return;
        }

        long tempsEcoule = (System.nanoTime() - debutLivraison)
                / 1_000_000_000L;

        tempsRestant = Math.max(0, 60 - (int) tempsEcoule);

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

        livreur.adapterVitesse(carte.estPelouse(
                livreur.getX() + livreur.getWidth() / 2,
                livreur.getY() + livreur.getHeight() - 1
        ) || System.nanoTime() - derniereMorsure < 3_000_000_000L);

        livreur.update();

        if (!carte.estAccessible(
                livreur.getX(),
                livreur.getY(),
                livreur.getWidth(),
                livreur.getHeight())) {

            livreur.moveTo(ancienX, ancienY);
        }

        boolean appuiE = clavier.isCapturePressed();

        if (appuiE && !toucheEAppuyee) {

            int[] cage = carte.trouverCage(livreur);

            if (chienCapture && cage != null) {
                chienCapture = false;
                chienEnCage = true;
                chien.moveTo(cage[0] - 16, cage[1] - 40);
            } else if (!chienCapture && !chienEnCage
                    && chien.estProche(livreur)) {
                chienCapture = true;
            }
        }

        toucheEAppuyee = appuiE;

        if (!chienCapture && !chienEnCage) {
            chien.poursuivre(livreur);
        }

        int ancienScore = livraison.getScore();

        livraison.update(livreur);

        if (livraison.getScore() > ancienScore) {
            debutLivraison = System.nanoTime();
            tempsRestant = 60;

            if (chienEnCage) {
                chienEnCage = false;
                chien.reapparaitre(carte, livreur);
            }
        }

        boolean touche = false;

        for (Voiture voiture : voitures) {
            voiture.avancer(voitures);

            if (voiture.toucheLivreur(livreur)) {
                touche = true;
            }
        }

        boolean morsure = !chienCapture && !chienEnCage
                && chien.toucheLivreur(livreur);
        long maintenant = System.nanoTime();
        int penalite = 0;

        if (maintenant - dernierChoc >= 2_000_000_000L) {

            if (touche && !contactVoiture) {
                penalite = 10;
                messageChoc = "ACCIDENT : -10 secondes";

            } else if (morsure && !contactChien) {
                penalite = 5;
                messageChoc = "MORSURE : -5 secondes";
                derniereMorsure = maintenant;
            }

            if (penalite > 0) {
                debutLivraison -= penalite * 1_000_000_000L;
                tempsRestant = Math.max(0, tempsRestant - penalite);
                dernierChoc = maintenant;
            }
        }

        if (morsure && !contactChien) {
            chien.reapparaitre(carte, livreur);
        }

        contactVoiture = touche;
        contactChien = morsure;
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

        if (chienEnCage) {
            chien.dessinerImmobile(canvas,
                    chien.getX() - cameraX,
                    chien.getY() - cameraY);

        } else if (chienCapture) {
            chien.dessinerImmobile(canvas,
                    livreur.getX() + livreur.getWidth() - 16 - cameraX,
                    livreur.getY() + livreur.getHeight() - 64 - cameraY);

        } else {
            chien.dessiner(canvas, cameraX, cameraY);
        }

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

        if (System.nanoTime() - dernierChoc < 2_000_000_000L) {
            int x = getWidth() / 2 - 140;

            canvas.drawRectangle(x, 75, 280, 65,
                    new Color(180, 35, 35));

            canvas.drawRectangle(x + 3, 78, 274, 59,
                    new Color(65, 20, 20));

            canvas.drawString(x + 20, 100,
                    "PENALITE DE TEMPS", Color.WHITE);

            canvas.drawString(x + 20, 125,
                    messageChoc, new Color(255, 210, 100));
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