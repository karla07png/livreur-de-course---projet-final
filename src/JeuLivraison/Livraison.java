package JeuLivraison;

import Engine.Canvas;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Livraison {

    private BufferedImage colis;
    private int score = 0;
    private int destination = 0;

    private int[][] destinations = {
            {208, 144}, {752, 144}, {1264, 144}, {1616, 144},

            {208, 560}, {752, 560}, {1264, 560}, {1616, 560},

            {208, 944}, {752, 944}, {1264, 944}, {1616, 944},

            {208, 1296}, {752, 1296}, {1264, 1296}, {1616, 1296}
    };

    private Random hasard = new Random();

    public Livraison() {

        destination = hasard.nextInt(destinations.length);

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

            int ancienneDestination = destination;

            do {
                destination = hasard.nextInt(destinations.length);
            } while (destination == ancienneDestination);
        }
    }

    public void dessiner(Canvas canvas, Livreur livreur,
                         int cameraX, int cameraY) {

        canvas.drawImage(colis,
                livreur.getX() - cameraX + 18,
                livreur.getY() - cameraY + 30);

        int x = destinations[destination][0] - cameraX;
        int y = destinations[destination][1] - cameraY;

        canvas.drawRectangle(x - 35, y - 40, 70, 25,
                new Color(38, 48, 57));

        canvas.drawString(x - 25, y - 23,
                "LIVRER", new Color(255, 219, 119));

        canvas.drawRectangle(x - 3, y - 15, 6, 8,
                new Color(38, 48, 57));
    }

    public void dessinerDirection(Canvas canvas, Livreur livreur) {

        int dx = destinations[destination][0] - livreur.getX();
        int dy = destinations[destination][1] - livreur.getY();

        String[] fleches = {"→", "↘", "↓", "↙", "←", "↖", "↑", "↗"};

        double angle = Math.atan2(dy, dx);

        int index = (int) Math.round(angle / (Math.PI / 4));

        index = (index + 8) % 8;

        canvas.drawRectangle(20, 65, 160, 30, Color.BLACK);

        canvas.drawString(30, 85,
                "Destination : " + fleches[index], Color.GREEN);
    }

    public int getScore() {
        return score;
    }
}