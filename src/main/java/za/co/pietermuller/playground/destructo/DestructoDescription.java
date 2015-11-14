package za.co.pietermuller.playground.destructo;

public class DestructoDescription implements RobotDescription {

    /**
     * TODO these values are made up
     * Except the -0.2 radians - when driving 2m forward, Destructo ends up at about a 30 degree
     * angle to the right - thus, -15 degrees or -0.2 radians per meter of movement.
     */
    private final MovementNoiseModel distanceNoiseModel =
            new MovementNoiseModel(0, 0.05, -0.2, 0.1);

    // TODO these values are made up
    private final MovementNoiseModel rotationNoiseModel =
            new MovementNoiseModel(0, 0.01, 0, 0.1);

    // TODO these values are made up
    private final double unityDistanceMeasurementNoise =
            0.1;

    public double getDistanceFromPositionToDistanceSensor() {
        return 0.140;
    }

    public double getDriverWheelDiameter() {
        return 0.043;
    }

    public double getAxleLength() {
        return 0.093;
    }

    public MovementNoiseModel getDistanceMovementNoiseModel() {
        return distanceNoiseModel;
    }

    public MovementNoiseModel getRotationMovementNoiseModel() {
        return rotationNoiseModel;
    }

    public double getUnityDistanceMeasurementNoise() {
        return unityDistanceMeasurementNoise;
    }

}
