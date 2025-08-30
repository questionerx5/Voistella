package com.questionerx5.voistella;

import com.github.tommyettinger.random.AceRandom;
import com.github.tommyettinger.random.EnhancedRandom;

public class RNGVars{
    public static EnhancedRandom genRNG, aiRNG;
    
    public static void init(){
        init(EnhancedRandom.seedFromMath());
    }
    public static void init(long seed){
        genRNG = new AceRandom(seed);
        // I don't know how to differentiate these
        aiRNG = new AceRandom(~seed);
    }
    
    private RNGVars(){}
}
