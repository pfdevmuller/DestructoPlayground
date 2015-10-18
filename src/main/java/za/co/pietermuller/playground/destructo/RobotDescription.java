package za.co.pietermuller.playground.destructo;

public interface RobotDescription {

    double getDistanceFromPositionToDistanceSensor();

    double getDriverWheelDiameter();

    double getAxleLength();

    /**
     * Returns the model for the noise expected on a 1 meter forward movement.
     */
    MovementNoiseModel getDistanceMovementNoiseModel();

    /**
     * Returns the model for the noise expected on a 1 radian rotation.
     */
    MovementNoiseModel getRotationMovementNoiseModel();

    /**
     * Returns the standard deviation on a distance measurement of 1 meter.;
     */
    double getUnityDistanceMeasurementNoise();

    /**
     * Noise when the measurement is zero.
     */
    double getMinimumDistanceMeasurementNoise();
}
