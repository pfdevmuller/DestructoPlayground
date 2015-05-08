package za.co.pietermuller.playground.destructo;

import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;
import za.co.pietermuller.playground.destructo.particlefilter.Movement;

import static za.co.pietermuller.playground.destructo.Rotation.radians;

public class DestructoController {

    private final RobotDescription robotDescription;
    private final RegulatedMotor leftDriverWheel;
    private final RegulatedMotor rightDriverWheel;

    public DestructoController(RobotDescription robotDescription) {
        this.robotDescription = robotDescription;
        leftDriverWheel = Motor.A;
        leftDriverWheel.setSpeed(6);
        rightDriverWheel = Motor.B;
        rightDriverWheel.setSpeed(6);
        leftDriverWheel.synchronizeWith(new RegulatedMotor[]{ rightDriverWheel });
    }

    public void move(Movement movement) {
        rotate(movement.getRotation());
        forward(movement.getDistance());
    }

    private void rotate(Rotation rotation) {
        int degreesToTurnWheels = (int) Math.round(
                rotation.degrees() * (robotDescription.getAxleLength() / robotDescription.getDriverWheelDiameter()));

        leftDriverWheel.startSynchronization();
        leftDriverWheel.rotate(-degreesToTurnWheels, true);
        rightDriverWheel.rotate(degreesToTurnWheels, true);
        leftDriverWheel.endSynchronization();
        leftDriverWheel.waitComplete();
        rightDriverWheel.waitComplete();
    }

    private void forward(double meters) {
        Rotation wheelRotation = radians(2 * meters / robotDescription.getDriverWheelDiameter());

        leftDriverWheel.startSynchronization();
        leftDriverWheel.rotate((int) Math.round(wheelRotation.degrees()), true);
        rightDriverWheel.rotate((int) Math.round(wheelRotation.degrees()), true);
        leftDriverWheel.endSynchronization();
        leftDriverWheel.waitComplete();
        rightDriverWheel.waitComplete();
    }
}
