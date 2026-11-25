package JeuLivraison;

import Engine.Canvas;
import Engine.MovableActor;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import Engine.Direction;
import java.awt.Rectangle;

public class Voiture extends MovableActor {

    private BufferedImage image;
    private int hauteurCarte;
    private Direction sens;
    private int largeurCarte;
    private int vitesseVoiture;

    public Voiture(int largeur, int hauteur,
                   int x, int y, int vitesse,
                   int modele, Direction sens) {

        super(largeur, hauteur);

        this.sens = sens;
        this.largeurCarte = largeur;
        this.vitesseVoiture = vitesse;

        hauteurCarte = hauteur;

        boolean horizontal = sens == Direction.LEFT
                || sens == Direction.RIGHT;

        setDimensions(horizontal ? 55 : 28,
                horizontal ? 28 : 55);
        setSpeed(vitesse);
        moveTo(x, y);

        try {
            BufferedImage planche = ImageIO.read(new File(
                    "resources/images/voiture.png"));

            image = planche.getSubimage(
                    (modele % 3) * 64 + 18,
                    (modele / 3) * 64 + 4,
                    28, 55);

            BufferedImage rotation = new BufferedImage(
                    getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = rotation.createGraphics();

            switch (sens) {
                case DOWN -> {
                    g.translate(28, 55);
                    g.rotate(Math.PI);
                }
                case RIGHT -> {
                    g.translate(55, 0);
                    g.rotate(Math.PI / 2);
                }
                case LEFT -> {
                    g.translate(0, 28);
                    g.rotate(-Math.PI / 2);
                }
                case UP -> {}
            }

            g.drawImage(image, 0, 0, null);
            g.dispose();

            image = rotation;

        } catch (IOException e) {
            throw new RuntimeException("Image voiture introuvable", e);
        }
    }

    public void avancer(Voiture[] voitures) {

        int x = getX();
        int y = getY();

        switch (sens) {
            case UP -> y -= vitesseVoiture;
            case DOWN -> y += vitesseVoiture;
            case LEFT -> x -= vitesseVoiture;
            case RIGHT -> x += vitesseVoiture;
        }

        if (x < 0) x = largeurCarte - getWidth();
        if (x > largeurCarte - getWidth()) x = 0;

        if (y < 0) y = hauteurCarte - getHeight();
        if (y > hauteurCarte - getHeight()) y = 0;

        Rectangle prochaine = new Rectangle(
                x, y, getWidth(), getHeight());

        Rectangle actuelle = new Rectangle(
                getX(), getY(), getWidth(), getHeight());

        for (Voiture autre : voitures) {

            if (autre == this) continue;

            Rectangle positionAutre = new Rectangle(
                    autre.getX(), autre.getY(),
                    autre.getWidth(), autre.getHeight());

            if (prochaine.intersects(positionAutre)) {
                return;
            }

            for (int rx = 416; rx <= 1440; rx += 512) {
                for (int ry = 320; ry <= 1088; ry += 384) {

                    Rectangle carrefour = new Rectangle(
                            rx, ry, 96, 96);

                    if (prochaine.intersects(carrefour)
                            && !actuelle.intersects(carrefour)
                            && positionAutre.intersects(carrefour)) {
                        return;
                    }
                }
            }
        }

        moveTo(x, y);
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