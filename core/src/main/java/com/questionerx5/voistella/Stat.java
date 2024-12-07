package com.questionerx5.voistella;

import java.util.List;
import java.util.ArrayList;

public class Stat{
    private int base;
    private List<Double> flatIncreases;
    private List<Double> multIncreases;
    private List<Double> mults;

    public Stat(int base){
        this.base = base;
        flatIncreases = new ArrayList<>();
        multIncreases = new ArrayList<>();
        mults = new ArrayList<>();
    }
    public int getValue(){
        double result = base;
        for(double flatIncrease : flatIncreases){
            result += flatIncrease;
        }
        double multTotal = 1;
        for(double multIncrease : multIncreases){
            multTotal += multIncrease;
        }
        result *= multTotal;
        for(double mult : mults){
            result *= mult;
        }
        return (int) result;
    }
}
