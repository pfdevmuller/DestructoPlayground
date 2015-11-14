package za.co.pietermuller.playground.destructo;

import com.google.common.base.Throwables;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import math.geom2d.Point2D;
import za.co.pietermuller.playground.destructo.particlefilter.NoisyMovementFactory;
import za.co.pietermuller.playground.destructo.particlefilter.ParticleFilter;
import za.co.pietermuller.playground.destructo.particlefilter.RandomParticleSource;
import za.co.pietermuller.playground.destructo.particlefilter.RobotModel;
import za.co.pietermuller.playground.destructo.particlefilter.SamplingStrategy;
import za.co.pietermuller.playground.destructo.particlefilter.SimpleRandomSamplingStrategy;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
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
            int numberOfSamples = 100;
            SamplingStrategy<RobotModel> samplingStrategy = new SimpleRandomSamplingStrategy<RobotModel>(randomGenerator);
            NoisyMovementFactory noisyMovementFactory = new NoisyMovementFactory(robotDescription, randomGenerator);
            ParticleFilter particleFilter =
                    new ParticleFilter(randomParticleSource, numberOfSamples, samplingStrategy, noisyMovementFactory);

            DestructoOrchestrator orchestrator = new DestructoOrchestrator(controller, particleFilter);

            statusServer = new StatusServer();
            statusServer.addToServables("particleFilter", particleFilter);
            statusServer.addToServables("worldModel", worldModel);
            statusServer.addToOrderListeners(orchestrator);

            statusServer.start();

            orchestrator.run(); // Should block until exception is thrown
        } catch (Exception e) {
            System.out.println("Crashing out with: " + e);
            LCD.drawString("Crashing out with: " + e, 0, 0);
            Sound.beep();
            Sound.beep();
            Sound.beep();
            Sound.beep();
            try {
                Thread.sleep(60000);
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
                .withBoundaryPoint(new Point2D(100, 0))
                .withBoundaryPoint(new Point2D(100, 100))
                .withBoundaryPoint(new Point2D(0, 100))
                .build();
    }
}
