package za.co.pietermuller.playground.destructo.particlefilter;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
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



    public static class Lists {

        public static <U> Ordering<WeightedObject<U>> getWeightOrdering() {
            return new Ordering<WeightedObject<U>>() {
                @Override
                public int compare(WeightedObject<U> a, WeightedObject<U> b) {
                    return Doubles.compare(a.getWeight(), b.getWeight());
                }
            };
        }

        public static <U> WeightedObject<U> objectWithLowestWeight(List<WeightedObject<U>> weightedObjects) {
            Ordering<WeightedObject<U>> ordering = getWeightOrdering();
            return ordering.min(weightedObjects);
        }

        public static <U> WeightedObject<U> objectWithHighestWeight(List<WeightedObject<U>> weightedObjects) {
            Ordering<WeightedObject<U>> ordering = getWeightOrdering();
            return ordering.max(weightedObjects);
        }

        public static <U> double totalWeight(List<WeightedObject<U>> weightedObjects) {
            double sum = 0;
            for (WeightedObject<U> weightedObject : weightedObjects) {
                sum += weightedObject.getWeight();
            }
            return sum;
        }

        public static <U> double averageWeight(List<WeightedObject<U>> weightedObjects) {
            checkArgument(weightedObjects.size() > 0, "WeightedObjects list cannot be empty");
            return totalWeight(weightedObjects) / weightedObjects.size();
        }

    }
}
