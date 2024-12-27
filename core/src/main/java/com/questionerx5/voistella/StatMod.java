package com.questionerx5.voistella;

public class StatMod{
    private ModType type;
    public ModType type(){
        return type;
    }
    private double amount;
    public double amount(){
        return amount;
    }
    public StatMod(ModType type, double amount){
        this.type = type;
        this.amount = amount;
    }

    static enum ModType{
        FLAT_INCREASE, MULT_INCREASE, MULT
    }
}
