package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.collect.ImmutableList;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.WorldModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;
import static za.co.pietermuller.playground.destructo.Rotation.degrees;

public class RandomParticleSource {

    private final RobotDescription robotDescription;
    private final WorldModel worldModel;
    private final Random randomGenerator;

    private Box2D worldBoundary;
    private double xSpan;
    private double ySpan;

    public RandomParticleSource(RobotDescription robotDescription,
                                WorldModel worldModel,
                                Random randomGenerator) {
        this.robotDescription = checkNotNull(robotDescription, "robotDescription is null!");
        this.worldModel = checkNotNull(worldModel, "worldModel is null!");
        this.randomGenerator = checkNotNull(randomGenerator, "randomGenerator is null!");

        worldBoundary = worldModel.getBoundingBox();
        xSpan = worldBoundary.getMaxX() - worldBoundary.getMinX();
        ySpan = worldBoundary.getMaxY() - worldBoundary.getMinY();
    }

    public RobotModel getRandomParticle() {
        Point2D point = null;
        do {
            double x = worldBoundary.getMinX() + randomGenerator.nextDouble() * xSpan;
            double y = worldBoundary.getMinY() + randomGenerator.nextDouble() * ySpan;
            point = new Point2D(x, y);
        } while (!worldModel.containsPoint(point));

        double angle = randomGenerator.nextDouble() * 360.0;
        return new RobotModel(robotDescription, point, degrees(angle), worldModel);
    }

    public List<RobotModel> getRandomParticles(int samples) {
        List<RobotModel> particles = new ArrayList<RobotModel>(samples);
        while (particles.size() < samples) {
            particles.add(getRandomParticle());
        }
        return ImmutableList.copyOf(particles);
    }
}
