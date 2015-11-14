package za.co.pietermuller.playground.destructo;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import za.co.pietermuller.playground.destructo.particlefilter.Measurement;
import za.co.pietermuller.playground.destructo.particlefilter.ParticleFilter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DestructoOrchestrator implements OrderListener {

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
            LCD.drawString("Waiting for order...", 0, 0);
            Movement order = orderQueue.take(); // Will block until order is available
            Sound.beep();
            LCD.drawString("Got an order...", 0, 5);

            // All of the following are blocking:
            controller.move(order); // Will block until movement is complete
            Measurement measurement = controller.senseDistance();
            particleFilter.movementUpdate(order);
            particleFilter.measurementUpdate(measurement);
        }
    }
}
