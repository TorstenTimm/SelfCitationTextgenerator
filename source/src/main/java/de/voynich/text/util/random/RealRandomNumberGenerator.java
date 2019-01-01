package de.voynich.text.util.random;

import java.security.SecureRandom;

/**
 * class to use SecureRandom for generating random numbers - each run will generate a new text
 *
 * set method.sourceChooser=real in conf.properties to activate this class
 */
public class RealRandomNumberGenerator implements I_RandomNumberGenerator {
    SecureRandom random = new SecureRandom();

    public int rand(int max){
        if (max == 0) {
            return 0;
        }

        return random.nextInt(max);
    }
}
