package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Pairs an object with a probability weight.
 * @param <T> type of weighted object
 */
public class WeightedObject<T> {

    private final T object;

    private final double weight;

    public WeightedObject(T object, double weight) {
        this.object = checkNotNull(object, "object is null!");
        this.weight = weight;
    }

    public T getObject() {
        return object;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("object", object)
                .add("weight", weight)
                .toString();
    }
}
