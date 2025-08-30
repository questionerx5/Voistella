package com.questionerx5.voistella;

import com.github.tommyettinger.ds.ObjectBag;
import com.github.tommyettinger.function.ObjConsumer;

public class Stat{
    private double base;
    public void changeBase(double change){
        base += change;
        changeStats();
    }
    private ObjectBag<StatMod> flatIncreases;
    private ObjectBag<StatMod> multIncreases;
    private ObjectBag<StatMod> mults;
    public void addModifier(StatMod modifier){
        switch(modifier.type()){
            case FLAT_INCREASE: flatIncreases.add(modifier); break;
            case MULT_INCREASE: multIncreases.add(modifier); break;
            case MULT: mults.add(modifier); break;
        }
        changeStats();
    }
    public void removeModifier(StatMod modifier){
        switch(modifier.type()){
            case FLAT_INCREASE: flatIncreases.remove(modifier); break;
            case MULT_INCREASE: multIncreases.remove(modifier); break;
            case MULT: mults.remove(modifier); break;
        }
        changeStats();
    }

    private boolean calculated;
    private double calculatedValue;
    private ObjConsumer<Void> onChange;
    private void changeStats(){
        calculated = false;
        if(onChange != null){
            onChange.accept(null);
        }
    }

    public Stat(double base){
        this(base, null);
    }
    public Stat(double base, ObjConsumer<Void> onChange){
        this.base = base;
        flatIncreases = new ObjectBag<>();
        multIncreases = new ObjectBag<>();
        mults = new ObjectBag<>();
        this.onChange = onChange; 
    }

    public double getValue(){
        if(calculated){
            return calculatedValue;
        }
        double result = base;
        for(StatMod flatIncrease : flatIncreases){
            result += flatIncrease.amount();
        }
        double multTotal = 1;
        for(StatMod multIncrease : multIncreases){
            multTotal += multIncrease.amount();
        }
        result *= multTotal;
        for(StatMod mult : mults){
            result *= mult.amount();
        }
        calculated = true;
        calculatedValue = result;
        return result;
    }
    public int getValueAsInt(){
        return (int) getValue();
    }
}
