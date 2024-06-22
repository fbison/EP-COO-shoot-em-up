package main;

import components.BackgroundStars;

import java.awt.Color;

import components.Projectile;
import components.enemies.Enemy;
import components.enemies.EnemiesArmy;
import components.enemies.EnemyTypeOne;
import components.enemies.EnemyTypeTwo;
import components.Player;
import graphics.Util;
import graphics.GameLib;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Main {
    public static void busyWait(Instant endTime) {
        while (Instant.now().isBefore(endTime)) {
            Thread.yield();
        }
    }

    public static void main(String[] args) {

        final int enemyProjectiles = 10;
        var currentTime = Instant.now();
        boolean running = true;
        long delta = 0;

        //inicialização do player
        Player player = new Player(Util.ACTIVE, (double) Util.WIDTH / 2, Util.HEIGHT * 0.90, 0.25, 0.25, 12.0, null, null, currentTime, enemyProjectiles, 0);
        //inicialização de Inimigo Tipo 1
        var enemiesOne = new ArrayList<Enemy>();
        //passar pro EnemiesArmy
        for (int i = 0; i < enemyProjectiles; i++) {
            var enemyOne = new EnemyTypeOne(Util.INACTIVE, 0, 0, 0, 0, 9.0, null, null, null, 0, 0, 0, 10, 2);
            enemiesOne.add(enemyOne);
        }
        EnemiesArmy armyEnemyOne = new EnemiesArmy(enemiesOne, currentTime.plusMillis(2000));

        //inicialização de Inimigo Tipo 2
        var enemiesTwo = new ArrayList<Enemy>();
        for (int i = 0; i < enemyProjectiles; i++) {
            var enemyTwo = new EnemyTypeTwo(Util.INACTIVE, 0.0, 0.0, 0.0, 0.0, 9.0, null, null,
                    null, 0.0, 0.0, 0.0, (int) (Util.WIDTH * 0.20), 10, 2);
            enemiesTwo.add(enemyTwo);
        }
        var armyEnemyTwo = new EnemiesArmy(enemiesTwo, currentTime.plusMillis(2000));

        // estrelas que formam o fundo de primeiro plano
        var starsFirst = new BackgroundStars(0.07, 0.0, 20, Color.GRAY);
        var starsSecond = new BackgroundStars(0.045, 0.0, 50, Color.DARK_GRAY);

        //Inicialização da interface
        GameLib.initGraphics();

        while (running) {
            delta = Duration.between(currentTime, Instant.now()).toMillis();
            currentTime = Instant.now();

            //verificação de colisões

            if (player.getState() == Util.ACTIVE) {

                // colisões player - projeteis inimigos
                for (int i = 0; i < armyEnemyOne.getEnemies().size(); i++) {
                    for (int j = 0; j < armyEnemyOne.getEnemies().get(i).getProjectiles().size(); j++) {
                        player.colide(armyEnemyOne.getEnemies().get(i).getProjectiles().get(j));
                    }
                }

                // colisões player - inimigos
                for (int i = 0; i < armyEnemyOne.getEnemies().size(); i++) {
                    player.colide(armyEnemyOne.getEnemies().get(i));
                }
                for (int i = 0; i < armyEnemyTwo.getEnemies().size(); i++) {
                    player.colide(armyEnemyTwo.getEnemies().get(i));
                }
            }

            //colisões projeteis (player) - inimigos
            for (int i = 0; i < player.getProjectiles().size(); i++) {
                for (int j = 0; j < armyEnemyOne.getEnemies().size(); j++) {
                    armyEnemyOne.getEnemies().get(j).colide(player.getProjectiles().get(i));
                }

                for (int j = 0; j < armyEnemyTwo.getEnemies().size(); j++) {
                    armyEnemyTwo.getEnemies().get(j).colide(player.getProjectiles().get(i));
                }
            }

            //atualização de projéteis
            player.updateProjectiles(delta);

            armyEnemyOne.updateProjectiles(delta);
            armyEnemyTwo.updateProjectiles(delta);

            //inimigos tipo 1 e 2
            armyEnemyOne.atack(player, currentTime, delta);
            armyEnemyTwo.atack(player, currentTime, delta);

            // verificando se novos inimigos devem ser lançados
            armyEnemyOne.castEnemies(currentTime);
            armyEnemyTwo.castEnemies(currentTime);

            // Verificando se a explosão do player já acabou. Ao final da explosão, o player volta a ser controlável
            if (player.getState() == Util.EXPLODE && currentTime.isAfter(player.getExplosionEnd())) {
                player.setState(Util.ACTIVE);
            }

            //Verificando entrada do usuário (teclado)
            player.verifyActions(currentTime, delta);
            if (GameLib.isKeyPressed(Util.KEY_ESCAPE)) running = false;

            player.keepInTheScren();
            //Desenho da cena

            //Plano de fundo distante
            starsSecond.update(delta);

            //Plano de fundo
            starsFirst.update(delta);

            //desenho - player
            if (player.getState() == Util.EXPLODE) {
                var alpha = Duration.between(currentTime, player.getExplosionStart()).toMillis() / Duration.between(player.getExplosionEnd(), player.getExplosionStart()).toMillis();
                GameLib.drawExplosion(player.getCoordinateX(), player.getCoordinateY(), Math.abs(alpha));
            } else {
                GameLib.setColor(Color.BLUE);
                GameLib.drawPlayer(player.getCoordinateX(), player.getCoordinateY(), player.getRadius());
            }

            //desenho - projeteis (player)
            for (Projectile projectile : player.getProjectiles()){
                if (projectile.getState() == Util.ACTIVE) {
                    GameLib.setColor(Color.GREEN);
                    GameLib.drawLine(projectile.getCoordinateX(), projectile.getCoordinateY() - 5, projectile.getCoordinateX(), projectile.getCoordinateY() + 5);
                    GameLib.drawLine(projectile.getCoordinateX() - 1, projectile.getCoordinateY() - 3, projectile.getCoordinateX() - 1, projectile.getCoordinateY() + 3);
                    GameLib.drawLine(projectile.getCoordinateX() + 1, projectile.getCoordinateY() - 3, projectile.getCoordinateX() + 1, projectile.getCoordinateY() + 3);
                }
            }

            //desenho - projeteis (inimigo tipo 1)
            for (int i = 0; i < armyEnemyOne.getEnemies().size(); i++) {
                var currentEnemy = armyEnemyOne.getEnemies().get(i);
                if (currentEnemy.getState() == Util.ACTIVE) {
                    for (int j = 0; j < currentEnemy.getProjectiles().size(); j++) {
                        var currentProjectile = armyEnemyOne.getEnemies().get(i).getProjectiles().get(j);
                        GameLib.setColor(Color.RED);
                        GameLib.drawCircle(currentProjectile.getCoordinateX(), currentProjectile.getCoordinateY(), currentProjectile.getRadius());
                    }
                }
            }

            //desenho - projeteis (inimigo tipo 2)
            for (int i = 0; i < armyEnemyTwo.getEnemies().size(); i++) {
                var currentEnemy = armyEnemyTwo.getEnemies().get(i);
                if (currentEnemy.getState() == Util.ACTIVE) {
                    for (int j = 0; j < currentEnemy.getProjectiles().size(); j++) {
                        var currentProjectile = armyEnemyTwo.getEnemies().get(i).getProjectiles().get(j);
                        GameLib.setColor(Color.RED);
                        GameLib.drawCircle(currentProjectile.getCoordinateX(), currentProjectile.getCoordinateY(), currentProjectile.getRadius());
                    }
                }
            }

            //Juntar esses os desenhor abaixo na classe Inimigo

            //desenho - inimigos tipo 1
            for(Enemy enemy : armyEnemyOne.getEnemies()){
                if (enemy.getState() == Util.EXPLODE) {
                    double alpha = (double) Duration.between(currentTime, enemy.getExplosionStart()).toMillis() / Duration.between(enemy.getExplosionStart(), enemy.getExplosionEnd()).toMillis();
                    GameLib.drawExplosion(enemy.getCoordinateX(), enemy.getCoordinateY(), alpha);
                } else if (enemy.getState() == Util.ACTIVE) {
                    GameLib.setColor(Color.CYAN);
                    GameLib.drawCircle(enemy.getCoordinateX(), enemy.getCoordinateY(), enemy.getRadius());
                }
            }
            //inimigos tipo 2
            for(Enemy enemy : armyEnemyTwo.getEnemies()){
                if (enemy.getState() == Util.EXPLODE) {
                    double alpha = (double) Duration.between(currentTime, enemy.getExplosionStart()).toMillis() / Duration.between(enemy.getExplosionStart(), enemy.getExplosionEnd()).toMillis();
                    GameLib.drawExplosion(enemy.getCoordinateX(), enemy.getCoordinateY(), alpha);
                } else if (enemy.getState() == Util.ACTIVE) {
                    GameLib.setColor(Color.MAGENTA);
                    GameLib.drawCircle(enemy.getCoordinateX(), enemy.getCoordinateY(), enemy.getRadius());
                }
            }

            GameLib.display();
            busyWait(currentTime.plusMillis(5));
        }
    }
}