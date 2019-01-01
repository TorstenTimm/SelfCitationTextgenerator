package de.voynich.text.util.random;

import java.util.Random;

/**
 * class to use a fake random number generator - two runs will generate the same output
 * default implementation for the random number generator
 *
 * set method.sourceChooser=pseudo in conf.properties to activate this class
 */
public class PseudoRandomNumberGenerator implements I_RandomNumberGenerator {

    private Random generator;

    public PseudoRandomNumberGenerator(int seedValue) {
        seed(seedValue);
    }

    // is set in conf.properties by method.random.pseudo.seed=
    public void seed(int seed) {
        generator = new Random(seed);
    }

    /**
     * generate a RANDOM number between 0 and `max`
     * @param max
     * @return
     */
    public int rand(int max){
        if (max == 0) {
            return 0;
        }
        return generator.nextInt(max);
    }
}
