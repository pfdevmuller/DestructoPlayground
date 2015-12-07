package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Samples from a pool of weighted objects by placing their weights end to end
 * on a continuous spectrum, then picking a random position on the spectrum and selecting
 * the corresponding sample.
 */
public class SimpleRandomSamplingStrategy<T> implements SamplingStrategy<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Random randomGenerator;

    public SimpleRandomSamplingStrategy(Random randomGenerator) {
        this.randomGenerator = checkNotNull(randomGenerator, "randomGenerator is null!");
    }

    public List<T> sampleFrom(List<WeightedObject<T>> weightedObjects) {
        double totalWeight = WeightedObject.Lists.totalWeight(weightedObjects);
        if (totalWeight <= 0.0) {
            // If all the weights are zero, none of this matters. Just return the input as is.
            logger.debug("Total weight is {}, returning all input objects.", totalWeight);
            return Lists.transform(weightedObjects, new Function<WeightedObject<T>, T>() {
                public T apply(WeightedObject<T> weightedObject) {
                    return weightedObject.getObject();
                }
            });
        } else {
            logger.info("Sampling from {} objects. Weight stats: total: {}, average: {}, min: {}, max: {}",
                    weightedObjects.size(),
                    totalWeight,
                    totalWeight / weightedObjects.size(),
                    WeightedObject.Lists.objectWithLowestWeight(weightedObjects).getWeight(),
                    WeightedObject.Lists.objectWithHighestWeight(weightedObjects).getWeight());
            ImmutableList.Builder<T> sampledBuilder = ImmutableList.builder();
            for (int i = 0; i < weightedObjects.size(); i++) {
                double randomPoint = randomGenerator.nextDouble() * totalWeight;
                logger.debug("Sampling... i = {}, randomPoint = {}", i, randomPoint);
                for (WeightedObject<T> weightedObject : weightedObjects) {
                    randomPoint = randomPoint - weightedObject.getWeight();
                    logger.debug("  objectWeight = {}, remaining random value = {}",
                            weightedObject.getWeight(), randomPoint);
                    if (randomPoint < 0) {
                        sampledBuilder.add(weightedObject.getObject());
                        logger.debug("    Selected object.");
                        break;
                    }
                }
            }
            List<T> sampled = sampledBuilder.build();
            checkArgument(sampled.size() == weightedObjects.size(),
                    String.format("expected to sample %d but sampled %d instead", weightedObjects.size(), sampled.size()));

            return sampled;
        }
    }

}
