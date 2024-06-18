package components.enemies;

import components.Component;
import components.Player;
import components.Projectile;
import gameLib.Util;

import java.time.Instant;
import java.util.ArrayList;

public class EnemyTypeTwo extends Enemy {
    private double spawnX;
    private int count;

    public EnemyTypeTwo(int state, double coordinateX, double coordinateY, double speedX, double speedY, double radius, Instant explosionStart, Instant explosionEnd, Instant nextShoot, double angle, double rotationSpeed, long speed, ArrayList<Component> projectiles, int count) {
        super(state, coordinateX, coordinateY, speedX, speedY, radius, explosionStart, explosionEnd, nextShoot, angle, rotationSpeed, speed, projectiles);
        this.spawnX = Util.WIDTH.getValue() * 0.20;
        this.count = count;
    }

    public double getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(double spawnX) {
        this.spawnX = spawnX;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void attack(Player player, Instant currentTime, long delta) {
        if (getState() == Util.EXPLODE.getValue()) {
            if (currentTime.isAfter(getExplosionEnd()))
                setState(Util.INACTIVE.getValue());
        } else if (getState() == Util.ACTIVE.getValue()) {
            if (getCoordinateY() > Util.HEIGHT.getValue() + 10)
                setState(Util.INACTIVE.getValue());
            else {
                boolean shootNow = false;
                double previousCoordinateY = getCoordinateY();
                double threshold = Util.HEIGHT.getValue() * 0.30;

                setCoordinateX(getSpeedX() * Math.cos(getAngle()) * delta);
                setCoordinateY(getSpeedY() * Math.sin(getAngle()) * delta * -1.0);
                setAngle(getRotationSpeed() * delta);

                if (currentTime.isAfter(getNextShoot()) && getCoordinateY() < player.getCoordinateY()) {
                    int free = findFreeIndex();
                    if (free < getProjectiles().size()) {
                        getProjectiles().get(free).setCoordinateX(getCoordinateX());
                        getProjectiles().get(free).setCoordinateY(getCoordinateY());
                        getProjectiles().get(free).setSpeedX(Math.cos(getAngle()) * 0.45);
                        getProjectiles().get(free).setSpeedY(Math.sin(getAngle()) * 0.45 * (-1));
                        getProjectiles().get(free).setState(Util.ACTIVE.getValue());
                    }
                }

                if (previousCoordinateY < threshold && getCoordinateY() > threshold) {
                    if (getCoordinateX() < (double) Util.WIDTH.getValue() / 2) setRotationSpeed(0.003);
                    else setRotationSpeed(-0.003);
                }

                if (getRotationSpeed() > 0 && Math.abs(getAngle() * Math.PI) < 0.05) {
                    setRotationSpeed(0);
                    setAngle(3 * Math.PI);
                    shootNow = true;
                }

                if (shootNow) {
                    double[] angles = {Math.PI / 2 + Math.PI / 8, Math.PI / 2, Math.PI / 2 - Math.PI / 8};
                    int[] freeArray = findFreeIndex(angles.length);

                    for (int i = 0; i < freeArray.length; i++) {
                        int free = freeArray[i];

                        if (free < getProjectiles().size()) {//não tenho certeza dessa linha
                            double a = angles[i] + Math.random() * Math.PI / 6 - Math.PI / 12;
                            double vX = Math.cos(a);
                            double vY = Math.sin(a);

                            getProjectiles().get(free).setCoordinateX(getCoordinateX());
                            getProjectiles().get(free).setCoordinateY(getCoordinateY());
                            getProjectiles().get(free).setSpeedX(vX * 0.30);
                            getProjectiles().get(free).setSpeedY(vY * 0.30);
                            getProjectiles().get(free).setState(Util.ACTIVE.getValue());
                        }
                    }
                }
            }
        }
    }

    @Override
    public Instant cast(Instant currentTime) {
        setCoordinateX(spawnX);
        setCoordinateY(-10.0);
        setSpeed(0.42);
        setAngle((3 * Math.PI) / 2);
        setRotationSpeed(0);
        setState(Util.ACTIVE.getValue());
        count += 1;

        if (count < 10) {
            return currentTime.plusMillis(120);
        } else {
            count = 0;
            spawnX = Math.random() > 0.5 ? Util.WIDTH.getValue() * 0.2 : Util.WIDTH.getValue() * 0.8;
            return currentTime.plusMillis((long) (3000 + Math.random() * 3000));
        }
    }
}