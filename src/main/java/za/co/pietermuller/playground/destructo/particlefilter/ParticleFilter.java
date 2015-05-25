package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.collect.ImmutableList;
import za.co.pietermuller.playground.destructo.Movement;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ParticleFilter {

    private final SamplingStrategy samplingStrategy;
    private final NoisyMovementFactory noisyMovementFactory;
    private List<RobotModel> particles;

    public ParticleFilter(RandomParticleSource randomParticleSource,
                          SamplingStrategy samplingStrategy,
                          NoisyMovementFactory noisyMovementFactory) {
        checkNotNull(randomParticleSource, "randomParticleSource is null!");
        this.particles = randomParticleSource.getRandomParticles();
        this.samplingStrategy = checkNotNull(samplingStrategy, "samplingStrategy is null!");
        this.noisyMovementFactory = checkNotNull(noisyMovementFactory, "noisyMovementFactory is null!");
    }

    public void movementUpdate(Movement noiselessMovement) {
        for (RobotModel robotModel : particles) {
            Movement noisyMovement = noisyMovementFactory.createNoisyMovement(noiselessMovement);
            robotModel.move(noisyMovement);
        }
    }

    public void measurementUpdate(Measurement measurement) {
        ImmutableList.Builder<WeightedObject<RobotModel>> weightsBuilder = ImmutableList.builder();
        for (RobotModel particle : particles) {
            double weight = particle.getMeasurementProbability(measurement);
            weightsBuilder.add(new WeightedObject<RobotModel>(particle, weight));
        }
        particles = samplingStrategy.sampleFrom(weightsBuilder.build());
    }
}
