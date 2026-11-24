package JeuLivraison;

import Engine.Canvas;
import Engine.MovableActor;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Chien extends MovableActor {

    private BufferedImage marche;
    private BufferedImage repos;
    private int compteur = 0;

    public Chien(int largeur, int hauteur) {
        super(largeur, hauteur);

        setDimensions(64, 64);
        setSpeed(2);
        moveTo(1344, 896);

        try {
            marche = ImageIO.read(new File(
                    "resources/images/Root_Walk.png"));

            repos = ImageIO.read(new File(
                    "resources/images/Root_Idle.png"));

        } catch (IOException e) {
            throw new RuntimeException("Images du chien introuvables", e);
        }
    }

    public void poursuivre(Livreur livreur) {
        super.update();

        int dx = livreur.getX() - getX();
        int dy = livreur.getY() - getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 2) {
                moveRight();
            } else if (dx < -2) {
                moveLeft();
            }
        } else {
            if (dy > 2) {
                moveDown();
            } else if (dy < -2) {
                moveUp();
            }
        }

        compteur++;
    }

    @Override
    public void draw(Canvas canvas) {
        dessiner(canvas, 0, 0);
    }

    public void dessiner(Canvas canvas, int cameraX, int cameraY) {

        int ligne = switch (getDirection()) {
            case UP -> 0;
            case LEFT -> 1;
            case RIGHT -> 2;
            case DOWN -> 3;
        };

        BufferedImage image = hasMoved() ? marche : repos;

        int colonne = (compteur / 10) % 4;

        BufferedImage pose = image.getSubimage(
                colonne * 64, ligne * 64, 64, 64);

        canvas.drawImage(pose,
                getX() - cameraX,
                getY() - cameraY);
    }
}