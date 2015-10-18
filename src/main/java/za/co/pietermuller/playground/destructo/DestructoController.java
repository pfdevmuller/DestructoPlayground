package za.co.pietermuller.playground.destructo;

import com.google.common.base.Joiner;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.RegulatedMotor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.pietermuller.playground.destructo.particlefilter.Measurement;

import java.util.ArrayList;
import java.util.List;

import static za.co.pietermuller.playground.destructo.Rotation.radians;

public class DestructoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RobotDescription robotDescription;
    private final RegulatedMotor leftDriverWheel;
    private final RegulatedMotor rightDriverWheel;
    private final EV3IRSensor irSensor;

    public DestructoController(RobotDescription robotDescription) {
        this.robotDescription = robotDescription;

        leftDriverWheel = Motor.A;
        leftDriverWheel.setSpeed(60);
        rightDriverWheel = Motor.D;
        rightDriverWheel.setSpeed(60);
        leftDriverWheel.synchronizeWith(new RegulatedMotor[]{ rightDriverWheel });

        irSensor = new EV3IRSensor(SensorPort.S1);
    }

    /**
     * Moves the robot.
     * Forward movement is performed first, then rotation.
     *
     * @param movement
     */
    public void move(Movement movement) {
        forward(movement.getDistance());
        rotate(movement.getRotation());
    }

    public Measurement senseDistance() {
        int samplesCount = 5;
        List<Double> samples = new ArrayList<Double>(samplesCount);
        float[] distanceSample = new float[1];
        for (int i = 0; i < samplesCount; i++) {
            irSensor.getDistanceMode().fetchSample(distanceSample, 0);
            samples.add((double) distanceSample[0]);

        }
        logger.debug("Samples from distance measurement: [{}]", Joiner.on(", ").join(samples));
        double total = 0;
        for (double sample: samples) {
            total += sample;
        }
        double distance = total / samplesCount;
        Measurement measurement =
                new Measurement(
                        distance,
                        distance * robotDescription.getUnityDistanceMeasurementNoise());
        logger.info("Took a distance measurement: {}", measurement);
        return measurement;
    }

    private void rotate(Rotation rotation) {
        logger.info("Performing rotation movement of {} degrees.", rotation.degrees());

        int degreesToTurnWheels = (int) Math.round(
                rotation.degrees() * (robotDescription.getAxleLength() / robotDescription.getDriverWheelDiameter()));

        logger.debug("Rotating driver motors {} degrees on the left and {} degrees on the right.",
                degreesToTurnWheels, -degreesToTurnWheels);

        leftDriverWheel.startSynchronization();
        leftDriverWheel.rotate(degreesToTurnWheels, true);
        rightDriverWheel.rotate(-degreesToTurnWheels, true);
        leftDriverWheel.endSynchronization();
        leftDriverWheel.waitComplete();
        rightDriverWheel.waitComplete();
    }

    private void forward(double meters) {
        logger.info("Performing forward movement of {} meters.", meters);

        Rotation wheelRotation = radians(2 * meters / robotDescription.getDriverWheelDiameter());
        int degrees = -(int) Math.round(wheelRotation.degrees());

        logger.debug("Rotating both driver motors {} degrees.", degrees);

        leftDriverWheel.startSynchronization();
        leftDriverWheel.rotate(degrees, true);
        rightDriverWheel.rotate(degrees, true);
        leftDriverWheel.endSynchronization();
        leftDriverWheel.waitComplete();
        rightDriverWheel.waitComplete();
    }

    private void checkForExit() {
        if (Button.ESCAPE.isDown()) {
            throw new RuntimeException("Quiting");
        }
    }
}
