package za.co.pietermuller.playground.destructo;

public class MovementNoiseModel {

    private class Gaussian {
        private double mean;
        private double sigma;

        Gaussian(double mean, double sigma) {
            this.mean = mean;
            this.sigma = sigma;
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
    }

    private final Gaussian distanceNoise;
    private final Gaussian rotationNoise;

    /**
     * Creates a new instance.
     *
     * Mean and standard deviations define Gaussian distributions modelling the expected
     * distance and rotation noise in response to a unity movement (ie 1 meter distance or 1 radian rotation).
     *
     * @param distanceNoiseMean in meters
     * @param distanceNoiseSigma in meters
     * @param rotationNoiseMean in radians
     * @param rotationNoiseSigma in radians
     */
    public MovementNoiseModel(double distanceNoiseMean,
                              double distanceNoiseSigma,
                              double rotationNoiseMean,
                              double rotationNoiseSigma) {
        this.distanceNoise = new Gaussian(distanceNoiseMean, distanceNoiseSigma);
        this.rotationNoise = new Gaussian(rotationNoiseMean, rotationNoiseSigma);
    }

    /**
     * Returns the distance noise modelled by this instance, in response to a gaussian random.
     * The input Gaussian Random must have mean 0, sigma 1.
     *
     * @param gaussianRandom
     * @return distance noise, in meters
     */
    public double scaleDistanceGuassian(double gaussianRandom) {
        return distanceNoise.scaleGaussian(gaussianRandom);
    }

    /**
     * Returns the rotation noise modelled by this instance, in response to a gaussian random.
     * The input Gaussian Random must have mean 0, sigma 1.
     *
     * @param gaussianRandom
     * @return rotation noise, in radians
     */
    public double scaleRotationGaussian(double gaussianRandom) {
        return rotationNoise.scaleGaussian(gaussianRandom);
    }
}
