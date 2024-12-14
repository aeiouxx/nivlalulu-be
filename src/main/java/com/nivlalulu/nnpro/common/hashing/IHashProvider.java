package com.nivlalulu.nnpro.common.hashing;

public interface IHashProvider {
    /**
     * Generates a hash for the given input string.
     *
     * @param input the input string to hash
     * @return the hashed string
     */
    String hash(String input);
}
