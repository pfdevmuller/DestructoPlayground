package za.co.pietermuller.playground.destructo;

import java.util.List;

/**
 * Picks samples from a list of weighted objects.
 * @param <T> type of weighted object
 */
public interface SamplingStrategy<T> {

    List<T> sampleFrom(List<WeightedObject<T>> weightedObjects);
}
