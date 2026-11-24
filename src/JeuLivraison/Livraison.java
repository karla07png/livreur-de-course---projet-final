package JeuLivraison;

import Engine.Canvas;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Livraison {

    private BufferedImage colis;
    private int score = 0;
    private int destination = 0;

    private int[][] destinations = {
            {192, 128}, {736, 128}, {1248, 128}, {1600, 128},
            {192, 544}, {736, 544}, {1248, 544}, {1600, 544},
            {192, 928}, {736, 928}, {1248, 928}, {1600, 928},
            {192, 1280}, {736, 1280}, {1248, 1280}, {1600, 1280},
            {224, 224}, {768, 224}, {1280, 224}, {1760, 224},
            {224, 640}, {768, 640}, {1280, 1024}, {1760, 1024}
    };

    public Livraison() {
        try {
            colis = ImageIO.read(new File(
                            "resources/images/boxes_SpriteSheet.png"))
                    .getSubimage(0, 0, 18, 18);
        } catch (IOException e) {
            throw new RuntimeException("Image du colis introuvable", e);
        }
    }

    public void update(Livreur livreur) {

        int x = destinations[destination][0];
        int y = destinations[destination][1];

        if (Math.abs(livreur.getX() - x) < 40
                && Math.abs(livreur.getY() - y) < 40) {

            score++;
            destination = (destination + 1) % destinations.length;
        }
    }

    public void dessiner(Canvas canvas, Livreur livreur,
                         int cameraX, int cameraY) {

        canvas.drawImage(colis,
                livreur.getX() - cameraX + 18,
                livreur.getY() - cameraY + 30);

        canvas.drawCircle(
                destinations[destination][0] - cameraX,
                destinations[destination][1] - cameraY,
                30, Color.GREEN);
    }

    public int getScore() {
        return score;
    }
}