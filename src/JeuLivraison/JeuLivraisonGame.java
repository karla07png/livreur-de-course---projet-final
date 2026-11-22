package JeuLivraison;

import Engine.Canvas;
import Engine.Game;
import Engine.GamePad;
import java.awt.Color;

public class JeuLivraisonGame extends Game {

    private GamePad clavier;
    private Livreur livreur;

    @Override
    public void initialize() {
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

        canvas.drawRectangle(0, 0, getWidth(), 56,
                new Color(30, 50, 60));

        canvas.drawString(20, 24, "JEU DE LIVRAISON", Color.WHITE);
        canvas.drawString(20, 43,
                "Fleches : deplacer | Echap : quitter", Color.WHITE);

        livreur.draw(canvas);
    }
}