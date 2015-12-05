package za.co.pietermuller.playground.destructo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.pietermuller.playground.destructo.particlefilter.Measurement;
import za.co.pietermuller.playground.destructo.particlefilter.ParticleFilter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DestructoOrchestrator implements OrderListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DestructoController controller;
    private final ParticleFilter particleFilter;
    private final BlockingQueue<Movement> orderQueue;

    private volatile boolean isQuiting = false;

    public DestructoOrchestrator(DestructoController controller,
                                 ParticleFilter particleFilter) {
        this.controller = controller;
        this.particleFilter = particleFilter;
        this.orderQueue = new ArrayBlockingQueue<Movement>(1000);
    }

    public void addOrder(Movement movement) {
        orderQueue.add(movement); // Will throw if queue is full
    }

    public void quit() {
        isQuiting = true;
    }

    public void run() throws InterruptedException {
        Movement order;
        while (!isQuiting) {
            logger.info("Waiting for order...");
            order = orderQueue.poll(5, TimeUnit.SECONDS);

            if (order != null) {
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
}
