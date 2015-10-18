package za.co.pietermuller.playground.destructo;

import lejos.hardware.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.pietermuller.playground.destructo.particlefilter.Measurement;
import za.co.pietermuller.playground.destructo.particlefilter.ParticleFilter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DestructoOrchestrator implements OrderListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DestructoController controller;
    private final ParticleFilter particleFilter;
    private final BlockingQueue<Movement> orderQueue;

    public DestructoOrchestrator(DestructoController controller,
                                 ParticleFilter particleFilter) {
        this.controller = controller;
        this.particleFilter = particleFilter;
        this.orderQueue = new ArrayBlockingQueue<Movement>(1000);
    }

    public void addOrder(Movement movement) {
        orderQueue.add(movement); // Will throw if queue is full
    }

    public void run() throws InterruptedException {
        while (true) {
            logger.info("Waiting for order...");
            Movement order = orderQueue.take(); // Will block until order is available
            Sound.beep();

            logger.info("Received order: {}", order);

            // All of the following are blocking:
            controller.move(order); // Will block until movement is complete
            logger.info("Received order: {}", order);
            Measurement measurement = controller.senseDistance();
            logger.info("Received measurement: {}", measurement);
            particleFilter.movementUpdate(order);
            particleFilter.measurementUpdate(measurement);
        }
    }
}
