package de.voynich.text.util.random;

/**
 * interface to choose between different random number implementations
 * @see PseudoRandomNumberGenerator
 * @see RealRandomNumberGenerator
 */
public interface I_RandomNumberGenerator {
    int rand(int maxValue);
}
