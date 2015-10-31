package za.co.pietermuller.playground.destructo;

import com.google.common.base.Throwables;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
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

    public static void main(String[] args) {
        LOGGER.info("Destructo starting up.");
        StatusServer statusServer = null;

        try {
            LCD.clear();
            LCD.drawString("Starting Destructo Controller!", 0, 5);
            Sound.beep();
            Sound.beep();
            Sound.beep();
            LCD.clear();

            RobotDescription robotDescription = new DestructoDescription();
            DestructoController controller = new DestructoController(robotDescription);

            WorldModel worldModel = getTestWorldModel();
            Random randomGenerator = new Random();
            RandomParticleSource randomParticleSource =
                    new RandomParticleSource(robotDescription, worldModel, randomGenerator);
            int numberOfSamples = 2500;
            SamplingStrategy<RobotModel> samplingStrategy = new SimpleRandomSamplingStrategy<RobotModel>(randomGenerator);
            NoisyMovementFactory noisyMovementFactory = new NoisyMovementFactory(robotDescription, randomGenerator);
            ParticleFilter particleFilter =
                    new ParticleFilter(randomParticleSource, numberOfSamples, samplingStrategy, noisyMovementFactory);

            DestructoOrchestrator orchestrator = new DestructoOrchestrator(controller, particleFilter);

            statusServer = new StatusServer();
            statusServer.addToServables("particleFilter", particleFilter);
            statusServer.addToServables("worldModel", worldModel);
            statusServer.addToOrderListeners(orchestrator);

            LOGGER.info("Starting up server.");
            statusServer.start();

            orchestrator.run(); // Should block until exception is thrown
        } catch (Exception e) {
            LOGGER.error("Crashed out with: {}", e.getMessage(), e);
            LCD.clear();
            LCD.drawString("Crashed out with: " + e.getMessage(), 0, 0);
            Sound.beep();
            Sound.beep();
            Sound.beep();
            Sound.beep();
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e1) {
                throw Throwables.propagate(e1);
            }
        }
        finally {
            if (statusServer != null) {
                statusServer.stop();
            }
        }
    }

    private static WorldModel getTestWorldModel() {
        return WorldModel.builder()
                .withBoundaryPoint(new Point2D(0, 0))
                .withBoundaryPoint(new Point2D(0.90, 0))
                .withBoundaryPoint(new Point2D(0.90, 0.15))
                .withBoundaryPoint(new Point2D(1.03, 0.15))
                .withBoundaryPoint(new Point2D(1.03, 0.4))
                .withBoundaryPoint(new Point2D(0, 0.4))
                .build();
    }
}
