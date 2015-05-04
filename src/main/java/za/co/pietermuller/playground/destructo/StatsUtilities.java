package za.co.pietermuller.playground.destructo;

public class StatsUtilities {

    /**
     * Calculates f(x) of a normal distribution with mean mu and variance sigma.
     *
     * @param mu
     * @param sigma
     * @param x
     * @return
     */
    public static double gaussian(double mu, double sigma, double x) {
        return Math.exp(-(Math.pow(mu - x, 2) / Math.pow(sigma, 2) / 2.0)) / Math.sqrt(2.0 * Math.PI * Math.pow(sigma, 2));
    }
}
