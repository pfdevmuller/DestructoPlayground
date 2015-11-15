package za.co.pietermuller.playground.destructo;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.pietermuller.playground.destructo.particlefilter.Measurement;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static za.co.pietermuller.playground.destructo.Rotation.radians;

public class RmiDestructoController implements DestructoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RobotDescription robotDescription;
    private final RMIRegulatedMotor leftDriverWheel;
    private final RMIRegulatedMotor rightDriverWheel;
    private final RMISampleProvider nxtUltrasonicSensor;

    public RmiDestructoController(RobotDescription robotDescription, String ipAddress) {
        this.robotDescription = robotDescription;

        try {
            RemoteEV3 remoteEV3 = new RemoteEV3(ipAddress);
            leftDriverWheel = remoteEV3.createRegulatedMotor("A", 'L');
            leftDriverWheel.setSpeed(180);
            rightDriverWheel = remoteEV3.createRegulatedMotor("D", 'L');
            rightDriverWheel.setSpeed(180);
            nxtUltrasonicSensor = remoteEV3.createSampleProvider("S1",
                    "lejos.hardware.sensor.NXTUltrasonicSensor", "Distance");
        } catch (RemoteException e) {
            throw Throwables.propagate(e);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        } catch (NotBoundException e) {
            throw Throwables.propagate(e);
        }
    }

    public void move(Movement movement) {
        forward(movement.getDistance());
        rotate(movement.getRotation());
    }

    public Measurement senseDistance() {
        int samplesCount = 5;
        List<Double> samples = new ArrayList<Double>(samplesCount);
        try {
            for (int i = 0; i < samplesCount; i++) {
                float[] distanceSample = nxtUltrasonicSensor.fetchSample();
                samples.add((double) distanceSample[0]);
            }
        } catch (RemoteException e) {
            throw Throwables.propagate(e);
        }

        logger.debug("Samples from distance measurement: [{}]", Joiner.on(", ").join(samples));
        double total = 0;
        for (double sample : samples) {
            total += sample;
        }
        double distance = total / samplesCount;
        double noise = Math.max(
                distance * robotDescription.getUnityDistanceMeasurementNoise(),
                robotDescription.getMinimumDistanceMeasurementNoise());

        Measurement measurement =
                new Measurement(
                        distance,
                        noise);
        logger.info("Took a distance measurement: {}", measurement);
        return measurement;
    }

    private void rotate(Rotation rotation) {
        logger.info("Performing rotation movement of {} degrees.", rotation.degrees());

        int degreesToTurnWheels = (int) Math.round(
                rotation.degrees() * (robotDescription.getAxleLength() / robotDescription.getDriverWheelDiameter()));

        logger.debug("Rotating driver motors {} degrees on the left and {} degrees on the right.",
                degreesToTurnWheels, -degreesToTurnWheels);

        try {
            leftDriverWheel.rotate(degreesToTurnWheels, true);
            rightDriverWheel.rotate(-degreesToTurnWheels, true);
            leftDriverWheel.waitComplete();
            rightDriverWheel.waitComplete();
        } catch (RemoteException e) {
            throw Throwables.propagate(e);
        }
    }

    private void forward(double meters) {
        logger.info("Performing forward movement of {} meters.", meters);

        Rotation wheelRotation = radians(2 * meters / robotDescription.getDriverWheelDiameter());
        int degrees = -(int) Math.round(wheelRotation.degrees());

        logger.debug("Rotating both driver motors {} degrees.", degrees);

        try {
            leftDriverWheel.rotate(degrees, true);
            rightDriverWheel.rotate(degrees, true);
            leftDriverWheel.waitComplete();
            rightDriverWheel.waitComplete();
        } catch (RemoteException e) {
            throw Throwables.propagate(e);
        }
    }
}
