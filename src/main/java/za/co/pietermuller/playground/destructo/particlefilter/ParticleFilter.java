package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import za.co.pietermuller.playground.destructo.AngleDistribution;
import za.co.pietermuller.playground.destructo.Gaussian;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.Rotation;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ParticleFilter {

    private final SamplingStrategy<RobotModel> samplingStrategy;
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

        particles = ImmutableList.copyOf(Iterables.filter(particles, new Predicate<RobotModel>() {
            public boolean apply(RobotModel robotModel) {
                return robotModel.isInsideWorldBorder();
            }
        }));

        System.out.println("    Movement Update Done. Particles left: " + particles.size());
        // TODO: If particles were filtered out, replace them, either with new ones or random samples from the source set
        // (otherwise you'll run out of particles!)
    }

    public void measurementUpdate(Measurement measurement) {
        ImmutableList.Builder<WeightedObject<RobotModel>> weightsBuilder = ImmutableList.builder();
        for (RobotModel particle : particles) {
            double weight = particle.getMeasurementProbability(measurement);
            weightsBuilder.add(new WeightedObject<RobotModel>(particle, weight));
        }
        ImmutableList.Builder<RobotModel> listBuilder = new ImmutableList.Builder<RobotModel>();
        for (RobotModel particle: samplingStrategy.sampleFrom(weightsBuilder.build())) {
            listBuilder.add(RobotModel.copyOf(particle));
        }
        particles = listBuilder.build();
    }

    public Gaussian getDistributionAlongXAxis() {
        return Gaussian.fromValues(Lists.transform(
                particles, new Function<RobotModel, Double>() {
                    public Double apply(RobotModel robotModel) {
                        return robotModel.getPosition().x();
                    }
                }
        ));
    }

    public Gaussian getDistributionAlongYAxis() {
        return Gaussian.fromValues(Lists.transform(
                particles, new Function<RobotModel, Double>() {
                    public Double apply(RobotModel robotModel) {
                        return robotModel.getPosition().y();
                    }
                }
        ));
    }

    public AngleDistribution getDistributionOfOrientations() {
        return AngleDistribution.fromValues(Lists.transform(
                particles, new Function<RobotModel, Rotation>() {
                    public Rotation apply(RobotModel robotModel) {
                        return robotModel.getOrientation();
                    }
                }
        ));
    }
}
