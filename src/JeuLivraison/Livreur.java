package JeuLivraison;

import Engine.Canvas;
import Engine.ControllableActor;
import Engine.MovementController;
import java.awt.Color;
import Engine.Direction;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Livreur extends ControllableActor {

    private BufferedImage animationRepos;
    private BufferedImage animationMarche;
    private int compteurAnimation = 0;

    public Livreur(MovementController controller, int largeur, int hauteur) {
        super(controller, largeur, hauteur);

        setDimensions(32, 64);
        setSpeed(4);
        moveTo(896, 604);


        setDirection(Direction.DOWN);

        try {
            animationRepos = ImageIO.read(new File(
                    "resources/images/idle_spritesheet_character_01.png"));

            animationMarche = ImageIO.read(new File(
                    "resources/images/walk_spritesheet_character_01.png"));
        } catch (IOException e) {
            throw new RuntimeException("Images du livreur introuvables", e);
        }
    }

    @Override
    public void update() {
        super.update();
        moveWithController();
        compteurAnimation++;
    }


    @Override
    public void draw(Canvas canvas) {
        dessiner(canvas, 0, 0);
    }

    public void dessiner(Canvas canvas, int cameraX, int cameraY) {

        int ligne = switch (getDirection()) {
            case DOWN -> 0;
            case UP -> 1;
            case RIGHT -> 2;
            case LEFT -> 3;
        };

        BufferedImage image;
        int colonne;

        if (hasMoved()) {
            image = animationMarche;
            colonne = (compteurAnimation / 8) % 8;
        } else {
            image = animationRepos;
            colonne = (compteurAnimation / 30) % 2;
        }

        BufferedImage pose = image.getSubimage(
                colonne * 32, ligne * 64, 32, 64);

        canvas.drawImage(pose,
                getX() - cameraX,
                getY() - cameraY);
    }

    public void adapterVitesse(boolean surPelouse) {
        if (surPelouse) {
            setSpeed(2);
        } else {
            setSpeed(4);
        }
    }
}