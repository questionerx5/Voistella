package com.questionerx5.voistella;

public class Attack {
    public final int damage;
    public final double range;
    public final boolean ranged;

    public Attack(int damage, double range, boolean ranged){
        this.damage = damage;
        this.range = range;
        this.ranged = ranged;
    }
    public Attack(int damage, double range){
        this(damage, range, true);
    }
    public Attack(int damage){
        this(damage, 1.5, false);
    }
}
