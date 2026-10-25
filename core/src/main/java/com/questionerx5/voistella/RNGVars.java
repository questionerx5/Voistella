package com.questionerx5.voistella;

import com.github.tommyettinger.random.AceRandom;
import com.github.tommyettinger.random.EnhancedRandom;

public class RNGVars{
    public static EnhancedRandom genRNG, aiRNG;
    
    public static void init(){
        init(EnhancedRandom.seedFromMath());
    }
    public static void init(long seed){
        EnhancedRandom base = new AceRandom(seed);
        genRNG = new AceRandom(base.nextLong());
        aiRNG = new AceRandom(base.nextLong());
    }
    
    private RNGVars(){}
}
