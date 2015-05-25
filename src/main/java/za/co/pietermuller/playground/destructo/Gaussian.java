package za.co.pietermuller.playground.destructo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.apache.commons.math3.stat.StatUtils;

import java.util.List;

public class Gaussian {

    private final double mean;
    private final double sigma;

    public Gaussian(double mean, double sigma) {
        this.mean = mean;
        this.sigma = sigma;
    }

    public double getMean() {
        return mean;
    }

    public double getSigma() {
        return sigma;
    }

    /**
     * Calculates the value of the guassian given scaling value with zero mean and unity sigma.
     *
     * @param scaler
     * @return
     */
    double scaleGaussian(double scaler) {
        return sigma * scaler + mean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Gaussian gaussian = (Gaussian) o;
        return Objects.equal(mean, gaussian.mean) &&
               Objects.equal(sigma, gaussian.sigma);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mean, sigma);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mean", mean)
                .add("sigma", sigma)
                .toString();
    }

    public static Gaussian fromValues(List<Double> values) {
        double[] valuesArray = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            valuesArray[i] = values.get(i);
        }

        double mean = StatUtils.mean(valuesArray);
        double stdDev = Math.sqrt(StatUtils.variance(valuesArray));

        return new Gaussian(mean, stdDev);
    }
}
