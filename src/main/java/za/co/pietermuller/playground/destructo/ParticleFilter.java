package za.co.pietermuller.playground.destructo;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class ParticleFilter {

    private final WorldModel worldModel;
    private final SamplingStrategy samplingStrategy;
    private List<RobotModel> particles;

    public ParticleFilter(WorldModel worldModel,
                          SamplingStrategy samplingStrategy) {
        this.worldModel = worldModel;
        this.samplingStrategy = samplingStrategy;
    }

    public void movementUpdate(Movement movement) {
        for (RobotModel robotModel: particles) {
            // TODO add noise here? Wrap model in something that adds the noise?
            robotModel.move(movement);
        }
    }

    public void measurementUpdate(Measurement measurement) {
        ImmutableList.Builder<WeightedObject<RobotModel>> weightsBuilder = ImmutableList.builder();
        for (RobotModel particle: particles) {
            double weight = particle.getMeasurementProbability(measurement, worldModel);
            weightsBuilder.add(new WeightedObject<RobotModel>(particle, weight));
        }
        particles = samplingStrategy.sampleFrom(weightsBuilder.build());
    }
}
