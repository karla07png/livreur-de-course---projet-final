package JeuLivraison;

import Engine.Canvas;
import Engine.MovableActor;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;

public class Voiture extends MovableActor {

    private BufferedImage image;
    private int hauteurCarte;
    private boolean descend;

    public Voiture(int largeur, int hauteur,
                   int x, int y, int vitesse,
                   int modele, boolean descend) {

        super(largeur, hauteur);

        this.descend = descend;

        hauteurCarte = hauteur;

        setDimensions(32, 32);
        setSpeed(vitesse);
        moveTo(x, y);

        try {
            BufferedImage planche = ImageIO.read(new File(
                    "resources/images/voiture.png"));

            image = planche.getSubimage(
                    (modele % 3) * 64 + 18,
                    (modele / 3) * 64 + 4,
                    28, 55);

            if (descend) {
                BufferedImage rotation = new BufferedImage(
                        28, 55, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g = rotation.createGraphics();
                g.translate(28, 55);
                g.rotate(Math.PI);
                g.drawImage(image, 0, 0, null);
                g.dispose();

                image = rotation;
            }

        } catch (IOException e) {
            throw new RuntimeException("Image voiture introuvable", e);
        }
    }

    @Override

    public void update() {
        super.update();

        if (descend) {
            if (getY() >= hauteurCarte - getHeight()) {
                moveTo(getX(), 0);
            } else {
                moveDown();
            }
        } else {
            if (getY() <= 0) {
                moveTo(getX(), hauteurCarte - getHeight());
            } else {
                moveUp();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        dessiner(canvas, 0, 0);
    }

    public void dessiner(Canvas canvas, int cameraX, int cameraY) {
        canvas.drawImage(image,
                getX() - cameraX,
                getY() - cameraY);
    }

    public boolean toucheLivreur(Livreur livreur) {
        return getX() < livreur.getX() + livreur.getWidth()
                && getX() + getWidth() > livreur.getX()
                && getY() < livreur.getY() + livreur.getHeight()
                && getY() + getHeight() > livreur.getY();
    }
}