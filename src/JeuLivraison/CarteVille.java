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

    private boolean toucheObstacle(int x, int y) {

        int[][] obstacles = {
                // Arbre
                {32, 32}, {288, 64}, {608, 96},
                {800, 96}, {1120, 96}, {1792, 96},
                {32, 544}, {320, 576}, {608, 576},
                {1248, 576}, {1696, 608},
                {64, 960}, {640, 960}, {1184, 960},
                {1728, 960}, {32, 1312},
                {320, 1312}, {672, 1312},

                // Cage
                {288, 160}, {1248, 160},
                {1792, 544}, {288, 960},
                {1248, 1312}, {1792, 1312}
        };

        for (int[] obstacle : obstacles) {
            if (x >= obstacle[0] && x < obstacle[0] + 32
                    && y >= obstacle[1] && y < obstacle[1] + 32) {
                return true;
            }
        }

        return false;
    }

    public boolean estAccessible(int x, int y) {

        if (toucheObstacle(x, y)) {
            return false;
        }

        int[] maisonsX = {96, 640, 1152, 1664};
        int[] maisonsY = {96, 512, 896, 1248};

        for (int mx : maisonsX) {
            for (int my : maisonsY) {
                if (x >= mx && x < mx + 64
                        && y >= my && y < my + 64) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean estAccessible(int x, int y,
                                 int largeur, int hauteur) {

        if (x < 0 || y < 0
                || x + largeur > getLargeur()
                || y + hauteur > getHauteur()) {
            return false;
        }

        for (int px = x / 32 * 32; px < x + largeur; px += 32) {
            for (int py = y / 32 * 32; py < y + hauteur; py += 32) {
                if (!estAccessible(px, py)) {
                    return false;
                }
            }
        }

        return true;
    }

}
