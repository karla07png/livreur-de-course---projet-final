package JeuLivraison;

import Engine.Canvas;
import Engine.MovableActor;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.awt.Rectangle;

public class Chien extends MovableActor {

    private BufferedImage marche;
    private BufferedImage repos;
    private int compteur = 0;
    private int[] directionChien;

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

    public void poursuivre(Livreur livreur, CarteVille carte,
                           Voiture[] voitures) {
        super.update();

        if (directionChien == null || compteur % 8 == 0) {
            directionChien = chercherChemin(livreur, carte);
        }

        int dx = directionChien[0] * 2;
        int dy = directionChien[1] * 2;

        if (estSurRoute() || !voitureProche(dx, dy, voitures)) {
            avancerSiPossible(dx, dy, carte);
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

    public boolean toucheLivreur(Livreur livreur) {
        return getX() + 16 < livreur.getX() + livreur.getWidth()
                && getX() + getWidth() - 16 > livreur.getX()
                && getY() + 16 < livreur.getY() + livreur.getHeight()
                && getY() + getHeight() - 16 > livreur.getY();
    }

    public void reapparaitre(CarteVille carte, Livreur livreur) {

        for (int i = 0; i < 100; i++) {

            int x = (int) (Math.random()
                    * (carte.getLargeur() - getWidth() + 1));

            int y = (int) (Math.random()
                    * (carte.getHauteur() - getHeight() + 1));

            double distance = Math.hypot(
                    x - livreur.getX(),
                    y - livreur.getY());

            if (distance >= 450
                    && carte.estAccessible(x, y, getWidth(), getHeight())
                    && carte.estAccessible(x + 32, y + 32)) {

                moveTo(x, y);

                directionChien = null;
                return;
            }
        }
    }
    public boolean estProche(Livreur livreur) {
        double distance = Math.hypot(
                getX() - livreur.getX(),
                getY() - livreur.getY()
        );

        return distance <= 75;
    }

    public void dessinerImmobile(Canvas canvas, int x, int y) {
        BufferedImage pose = repos.getSubimage(0, 192, 64, 64);
        canvas.drawImage(pose, x, y);
    }

    private boolean avancerSiPossible(int dx, int dy, CarteVille carte) {

        int x = getX() + dx;
        int y = getY() + dy;

        if (!carte.estAccessible(x, y, getWidth(), getHeight())) {
            return false;
        }

        if (dx > 0) moveRight();
        else if (dx < 0) moveLeft();
        else if (dy > 0) moveDown();
        else if (dy < 0) moveUp();

        return true;
    }

    private int[] chercherChemin(Livreur livreur, CarteVille carte) {
        int taille = 16;

        int cibleX = livreur.getX()
                + (livreur.getWidth() - getWidth()) / 2;

        int cibleY = livreur.getY()
                + (livreur.getHeight() - getHeight()) / 2;

        int[][] couts = new int
                [carte.getLargeur() / taille + 1]
                [carte.getHauteur() / taille + 1];

        for (int[] ligne : couts) {
            java.util.Arrays.fill(ligne, Integer.MAX_VALUE);
        }

        PriorityQueue<int[]> file = new PriorityQueue<>(
                Comparator.comparingDouble(p ->
                        p[4] + (Math.abs(p[0] - cibleX)
                                + Math.abs(p[1] - cibleY)) / (double) taille
                                + (directionChien != null
                                && (p[2] != directionChien[0]
                                || p[3] != directionChien[1]) ? 0.25 : 0)));

        int[] depart = {getX(), getY(), 0, 0, 0};
        int[] meilleur = depart;

        file.add(depart);
        couts[getX() / taille][getY() / taille] = 0;

        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        while (!file.isEmpty()) {
            int[] courant = file.poll();

            if (courant[4] > couts[courant[0] / taille]
                    [courant[1] / taille]) {
                continue;
            }

            double distance = Math.hypot(
                    courant[0] - cibleX, courant[1] - cibleY);

            if (distance < Math.hypot(
                    meilleur[0] - cibleX, meilleur[1] - cibleY)) {
                meilleur = courant;
            }

            if (distance <= taille) {
                break;
            }

            for (int[] d : directions) {
                int x = courant[0] + d[0] * taille;
                int y = courant[1] + d[1] * taille;

                if (x < 0 || y < 0
                        || x + getWidth() > carte.getLargeur()
                        || y + getHeight() > carte.getHauteur()) {
                    continue;
                }

                if (!carte.estAccessible(x, y, getWidth(), getHeight())) {
                    continue;
                }

                int cout = courant[4]
                        + (estSurRoute(x, y) ? 20000 : 1);

                if (cout >= couts[x / taille][y / taille]) {
                    continue;
                }

                couts[x / taille][y / taille] = cout;

                file.add(new int[]{
                        x, y,
                        courant == depart ? d[0] : courant[2],
                        courant == depart ? d[1] : courant[3],
                        cout
                });
            }
        }

        return new int[]{meilleur[2], meilleur[3]};
    }

    private boolean voitureProche(int dx, int dy, Voiture[] voitures) {
        Rectangle prochainePosition = new Rectangle(
                getX() + dx + 16,
                getY() + dy + 16,
                32, 32
        );

        for (Voiture voiture : voitures) {
            Rectangle danger = new Rectangle(
                    voiture.getX(),
                    voiture.getY(),
                    voiture.getWidth(),
                    voiture.getHeight()
            );

            danger.grow(50, 50);

            if (prochainePosition.intersects(danger)) {
                return true;
            }
        }

        return false;
    }

    public boolean estSurRoute() {
        return estSurRoute(getX(), getY());
    }

    private boolean estSurRoute(int x, int y) {
        x += getWidth() / 2;
        y += getHeight() / 2;

        for (int rx = 416; rx <= 1440; rx += 512) {
            if (x >= rx && x < rx + 96) {
                return true;
            }
        }

        for (int ry = 320; ry <= 1088; ry += 384) {
            if (y >= ry && y < ry + 96) {
                return true;
            }
        }

        return false;
    }

}