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
}
