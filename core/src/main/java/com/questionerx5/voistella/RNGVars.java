package com.questionerx5.voistella;

import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.IRNG;

public class RNGVars{
    public static IRNG genRNG, aiRNG;
    
    public static void init(){
        IRNG rng = new GWTRNG();
        genRNG = new GWTRNG(rng.nextInt());
        aiRNG = new GWTRNG(rng.nextInt());
    }
    public static void init(int seed){
        IRNG rng = new GWTRNG(seed);
        genRNG = new GWTRNG(rng.nextInt());
        aiRNG = new GWTRNG(rng.nextInt());
    }
    
    private RNGVars(){}
}
