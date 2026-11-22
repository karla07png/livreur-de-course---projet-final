package JeuLivraison;

import Engine.Canvas;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CarteVille {
    private final BufferedImage image;

    public CarteVille() {
        try {
            image = ImageIO.read(new File("resources/images/ville_2d.png"));
            if (image == null) throw new IOException("Image invalide");
        } catch (IOException e) {
            throw new RuntimeException("Carte introuvable", e);
        }
    }

    public int getLargeur() {
        return image.getWidth();
    }

    public int getHauteur() {
        return image.getHeight();
    }

    public void dessiner(Canvas canvas, int cameraX, int cameraY) {
        canvas.drawImage(image, -cameraX, -cameraY);
    }
}
