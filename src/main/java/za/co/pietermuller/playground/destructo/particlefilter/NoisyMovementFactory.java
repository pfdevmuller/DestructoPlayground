package za.co.pietermuller.playground.destructo.particlefilter;

import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.MovementNoiseModel;
import za.co.pietermuller.playground.destructo.RobotDescription;
import za.co.pietermuller.playground.destructo.Rotation;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

public class NoisyMovementFactory {

    private final RobotDescription robotDescription;
    private final Random randomGenerator;

    public NoisyMovementFactory(RobotDescription robotDescription, Random randomGenerator) {
        this.robotDescription = checkNotNull(robotDescription, "robotDescription is null!");
        this.randomGenerator = checkNotNull(randomGenerator, "randomGenerator is null!");
    }

    public Movement createNoisyMovement(Movement movement) {
        Movement noiseDueToForwardMovement =
                noiseDueToMovementComponent(
                        robotDescription.getDistanceMovementNoiseModel(),
                        movement.getDistance());

        Movement noiseDueToRotationMovement =
                noiseDueToMovementComponent(
                        robotDescription.getRotationMovementNoiseModel(),
                        movement.getRotation().radians());

        // TODO implement adding two movements together
        return new Movement(
                movement.getDistance()
                + noiseDueToForwardMovement.getDistance()
                + noiseDueToRotationMovement.getDistance(),
                Rotation.radians(
                        movement.getRotation().radians()
                        + noiseDueToForwardMovement.getRotation().radians()
                        + noiseDueToRotationMovement.getRotation().radians()));
    }

    private Movement noiseDueToMovementComponent(MovementNoiseModel noiseModel, double movementComponentMagnitude) {
        double unityDistanceNoise = noiseModel.scaleDistanceGuassian(randomGenerator.nextGaussian());
        double unityRotationNoise = noiseModel.scaleRotationGaussian(randomGenerator.nextGaussian());

        return new Movement(
                movementComponentMagnitude * unityDistanceNoise,
                Rotation.radians(movementComponentMagnitude * unityRotationNoise));
    }

}
