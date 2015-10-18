package za.co.pietermuller.playground.destructo.particlefilter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import math.geom2d.Point2D;
import org.slf4j.Logger;
import za.co.pietermuller.playground.destructo.AngleDistribution;
import za.co.pietermuller.playground.destructo.CustomSerializers;
import za.co.pietermuller.playground.destructo.Gaussian;
import za.co.pietermuller.playground.destructo.Movement;
import za.co.pietermuller.playground.destructo.Rotation;
import za.co.pietermuller.playground.destructo.StatusServable;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ParticleFilter implements StatusServable {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private final int numberOfSamples;
    private final SamplingStrategy<RobotModel> samplingStrategy;
    private final NoisyMovementFactory noisyMovementFactory;

    private Optional<ObjectMapper> objectMapper;
    private int movementUpdateCount;
    private int measurementUpdateCount;

    @JsonProperty("particles")
    private List<RobotModel> particles;

    public ParticleFilter(RandomParticleSource randomParticleSource,
                          int numberOfSamples,
                          SamplingStrategy<RobotModel> samplingStrategy,
                          NoisyMovementFactory noisyMovementFactory) {
        checkNotNull(randomParticleSource, "randomParticleSource is null!");
        this.numberOfSamples = numberOfSamples;
        this.particles = randomParticleSource.getRandomParticles(numberOfSamples);
        this.samplingStrategy = checkNotNull(samplingStrategy, "samplingStrategy is null!");
        this.noisyMovementFactory = checkNotNull(noisyMovementFactory, "noisyMovementFactory is null!");
        this.objectMapper = Optional.absent();
        logger.info("Created a ParticleFilter!");
    }

    public void movementUpdate(Movement noiselessMovement) {
        logger.info("Processing movement update: {}", noiselessMovement);
        for (RobotModel robotModel : particles) {
            Movement noisyMovement = noisyMovementFactory.createNoisyMovement(noiselessMovement);
            robotModel.move(noisyMovement);
        }

        particles = ImmutableList.copyOf(Iterables.filter(particles, new Predicate<RobotModel>() {
            public boolean apply(RobotModel robotModel) {
                return robotModel.isInsideWorldBorder();
            }
        }));

        logger.debug("Movement Update Done. Particles left: {}", particles.size());

        movementUpdateCount++;
        // TODO: If particles were filtered out, replace them, either with new ones or random samples from the source set
        // (otherwise you'll run out of particles!)
    }

    public void measurementUpdate(Measurement measurement) {
        logger.info("Processing measurement update: {}", measurement);
        ImmutableList.Builder<WeightedObject<RobotModel>> weightsBuilder = ImmutableList.builder();
        for (RobotModel particle : particles) {
            double weight = particle.getMeasurementProbability(measurement);
            weightsBuilder.add(new WeightedObject<RobotModel>(particle, weight));
        }
        ImmutableList.Builder<RobotModel> listBuilder = new ImmutableList.Builder<RobotModel>();
        for (RobotModel particle : samplingStrategy.sampleFrom(weightsBuilder.build())) {
            listBuilder.add(RobotModel.copyOf(particle));
        }
        particles = listBuilder.build();
        measurementUpdateCount++;
    }

    @JsonProperty("distributionAlongXAxis")
    public Gaussian getDistributionAlongXAxis() {
        return Gaussian.fromValues(Lists.transform(
                particles, new Function<RobotModel, Double>() {
                    public Double apply(RobotModel robotModel) {
                        return robotModel.getPosition().x();
                    }
                }
        ));
    }

    @JsonProperty("distributionAlongYAxis")
    public Gaussian getDistributionAlongYAxis() {
        return Gaussian.fromValues(Lists.transform(
                particles, new Function<RobotModel, Double>() {
                    public Double apply(RobotModel robotModel) {
                        return robotModel.getPosition().y();
                    }
                }
        ));
    }

    @JsonProperty("distributionOfOrientations")
    public AngleDistribution getDistributionOfOrientations() {
        return AngleDistribution.fromValues(Lists.transform(
                particles, new Function<RobotModel, Rotation>() {
                    public Rotation apply(RobotModel robotModel) {
                        return robotModel.getOrientation();
                    }
                }
        ));
    }

    @JsonProperty("numberOfParticles")
    public int getNumberOfParticles() {
        return particles.size();
    }

    @JsonProperty("movementUpdateCount")
    public int getMovementUpdateCount() {
        return movementUpdateCount;
    }

    @JsonProperty("measurementUpdateCount")
    public int getMeasurementUpdateCount() {
        return measurementUpdateCount;
    }

    private synchronized ObjectMapper getObjectMapper() {
        if (objectMapper.isPresent()) {
            return objectMapper.get();
        } else {
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY);
            SimpleModule serializationModule = new SimpleModule("DestructoSerialize");
            serializationModule.addSerializer(new CustomSerializers.Point2DSerializer(Point2D.class));
            om.registerModule(serializationModule);
            this.objectMapper = Optional.of(om);
            return objectMapper.get();
        }
    }

    public String getStatus() {
        try {
            return getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw Throwables.propagate(e);
        }
    }
}
