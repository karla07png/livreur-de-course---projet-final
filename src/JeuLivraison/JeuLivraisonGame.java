package JeuLivraison;

import Engine.Canvas;
import Engine.Game;
import Engine.GamePad;
import java.awt.Color;

public class JeuLivraisonGame extends Game {

    private GamePad clavier;
    private Livreur livreur;
    private CarteVille carte;

    @Override
    public void initialize() {
        carte = new CarteVille();
        clavier = new GamePad();
        addKeyListener(clavier);

        livreur = new Livreur(clavier, getWidth(), getHeight());
    }

    @Override
    public void update() {
        if (clavier.isQuitPressed()) {
            stop();
            return;
        }

        livreur.update();
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
        canvas.drawRectangle(0, 0, getWidth(), 56,
                new Color(30, 50, 60));

        canvas.drawString(20, 24, "JEU DE LIVRAISON", Color.WHITE);
        canvas.drawString(20, 43,
                "Fleches : deplacer | Echap : quitter", Color.WHITE);

        canvas.drawRectangle(
                livreur.getX() - cameraX,
                livreur.getY() - cameraY,
                livreur.getWidth(),
                livreur.getHeight(),
                Color.YELLOW
        );
    }
}