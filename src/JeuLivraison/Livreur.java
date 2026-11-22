package JeuLivraison;

import Engine.Canvas;
import Engine.ControllableActor;
import Engine.MovementController;
import java.awt.Color;

public class Livreur extends ControllableActor {

    public Livreur(MovementController controller, int largeur, int hauteur) {
        super(controller, largeur, hauteur);

        setDimensions(28, 36);
        setSpeed(4);
        moveTo(largeur / 2 - 14, hauteur / 2 - 18);
    }

    @Override
    public void update() {
        super.update();
        moveWithController();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRectangle(getX(), getY(),
                getWidth(), getHeight(), new Color(255, 200, 60));
    }
}