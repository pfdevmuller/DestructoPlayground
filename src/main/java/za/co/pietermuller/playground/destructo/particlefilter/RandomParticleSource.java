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
    private int samples;

    public RandomParticleSource(RobotDescription robotDescription,
                                WorldModel worldModel,
                                Random randomGenerator,
                                int samples) {
        this.robotDescription = checkNotNull(robotDescription, "robotDescription is null!");
        this.worldModel = checkNotNull(worldModel, "worldModel is null!");
        this.randomGenerator = checkNotNull(randomGenerator, "randomGenerator is null!");
        this.samples = samples;
    }

    public List<RobotModel> getRandomParticles() {
        Box2D worldBoundary = worldModel.getBoundingBox();
        double xSpan = worldBoundary.getMaxX() - worldBoundary.getMinX();
        double ySpan = worldBoundary.getMaxY() - worldBoundary.getMinY();

        List<RobotModel> particles = new ArrayList<RobotModel>(samples);
        while (particles.size() < samples) {
            double x = worldBoundary.getMinX() + randomGenerator.nextDouble() * xSpan;
            double y = worldBoundary.getMinY() + randomGenerator.nextDouble() * ySpan;
            Point2D point = new Point2D(x, y);
            if (worldModel.containsPoint(point)) {
                double angle = randomGenerator.nextDouble() * 360.0;
                particles.add(new RobotModel(robotDescription, point, degrees(angle), worldModel));
            }
        }
        return ImmutableList.copyOf(particles);
    }
}
