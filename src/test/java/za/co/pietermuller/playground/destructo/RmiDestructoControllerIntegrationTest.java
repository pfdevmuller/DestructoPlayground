package za.co.pietermuller.playground.destructo;

import com.google.common.base.Throwables;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static za.co.pietermuller.playground.destructo.Rotation.degrees;
import static za.co.pietermuller.playground.destructo.Rotation.noRotation;

/**
 * Actually requires an online EV3 at specified ip
 */
public class RmiDestructoControllerIntegrationTest {

    String ev3Ip = "192.168.1.108";

    @Test
    @Ignore // For manual testing only
    public void testRmiController() {
        RobotDescription robotDescription = new DestructoDescription();
        RmiDestructoController rmiDestructoController =
                new RmiDestructoController(robotDescription, ev3Ip);

        rmiDestructoController.move(new Movement(0.1, noRotation()));
        rmiDestructoController.move(new Movement(0, degrees(90)));
        rmiDestructoController.move(new Movement(0.1, noRotation()));
    }

    @Test
    @Ignore // For manual testing only
    public void testDistanceMeasurement() throws RemoteException, InterruptedException {
        RemoteEV3 remoteEV3;
        RMISampleProvider irSensor = null;
        RMISampleProvider usSensor = null;
        try {
            System.out.println("Starting up");
            remoteEV3 = new RemoteEV3(ev3Ip);
            irSensor = remoteEV3.createSampleProvider("S1",
                    "lejos.hardware.sensor.EV3IRSensor", "Distance");
            System.out.println("Got IR sampler");
            usSensor = remoteEV3.createSampleProvider("S2",
                    "lejos.hardware.sensor.NXTUltrasonicSensor", "Distance");
            System.out.println("Got US sampler");

            while (true) {
                Thread.sleep(1000);
                takeMeasurements(irSensor, usSensor);
            }

        } catch (RemoteException e) {
            throw Throwables.propagate(e);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        } catch (NotBoundException e) {
            throw Throwables.propagate(e);
        } finally {
            if (irSensor != null)
                irSensor.close();
            if (usSensor != null)
                usSensor.close();
        }
    }

    private void takeMeasurements(RMISampleProvider irSensor, RMISampleProvider usSensor) throws RemoteException {
        float[] irSample = irSensor.fetchSample();
        float[] usSample = usSensor.fetchSample();
        System.out.println("IR: " + irSample[0] + "\tUS:" + usSample[0]);
    }


}
