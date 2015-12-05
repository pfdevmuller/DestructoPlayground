package za.co.pietermuller.playground.destructo;

import math.geom2d.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.pietermuller.playground.destructo.particlefilter.NoisyMovementFactory;
import za.co.pietermuller.playground.destructo.particlefilter.ParticleFilter;
import za.co.pietermuller.playground.destructo.particlefilter.RandomParticleSource;
import za.co.pietermuller.playground.destructo.particlefilter.RobotModel;
import za.co.pietermuller.playground.destructo.particlefilter.SamplingStrategy;
import za.co.pietermuller.playground.destructo.particlefilter.SimpleRandomSamplingStrategy;

import java.util.Random;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("Destructo starting up.");
        StatusServer statusServer = null;

        RobotDescription robotDescription = new DestructoDescription();
        WorldModel worldModel = getTestWorldModel();
        Random randomGenerator = new Random();
        RandomParticleSource randomParticleSource =
                new RandomParticleSource(robotDescription, worldModel, randomGenerator);
        int numberOfSamples = 1000;
        SamplingStrategy<RobotModel> samplingStrategy = new SimpleRandomSamplingStrategy<RobotModel>(randomGenerator);
        NoisyMovementFactory noisyMovementFactory = new NoisyMovementFactory(robotDescription, randomGenerator);
        ParticleFilter particleFilter =
                new ParticleFilter(randomParticleSource, numberOfSamples, samplingStrategy, noisyMovementFactory);
        try (DestructoController controller = new RmiDestructoController(robotDescription, "192.168.1.110")) {

            DestructoOrchestrator orchestrator = new DestructoOrchestrator(controller, particleFilter);

            statusServer = new StatusServer();
            statusServer.addToServables("particleFilter", particleFilter);
            statusServer.addToServables("worldModel", worldModel);
            statusServer.addToOrderListeners(orchestrator);

            LOGGER.info("Starting up server.");
            statusServer.start();

            orchestrator.run(); // Should block until quit or exception is thrown
        } finally {
            if (statusServer != null) {
                statusServer.stop();
            }
        }
    }

    private static WorldModel getTestWorldModel() {
        return WorldModel.builder()
                .withBoundaryPoint(new Point2D(0.15, 0))
                .withBoundaryPoint(new Point2D(0.95, 0))
                .withBoundaryPoint(new Point2D(0.95, 0.53))
                .withBoundaryPoint(new Point2D(0, 0.53))
                .withBoundaryPoint(new Point2D(0, 0.12))
                .withBoundaryPoint(new Point2D(0.15, 0.12))
                .build();
    }
}
