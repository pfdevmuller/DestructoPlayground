package za.co.pietermuller.playground.destructo;

import za.co.pietermuller.playground.destructo.particlefilter.Measurement;

public interface DestructoController {
    /**
     * Moves the robot.
     * Forward movement is performed first, then rotation.
     *
     * @param movement
     */
    void move(Movement movement);

    Measurement senseDistance();
}
