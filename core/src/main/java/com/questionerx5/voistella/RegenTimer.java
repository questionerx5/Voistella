package com.questionerx5.voistella;

import com.questionerx5.voistella.action.Action;
import com.questionerx5.voistella.action.RegenAction;

public class RegenTimer extends Actor{
    private Stat speed;
    @Override
    protected int speed(){
        return speed.getValueAsInt();
    }
    public Stat speedStat(){
        return speed;
    }

    private Creature creature;
    public Creature creature(){
        return creature;
    }

    public RegenTimer(int speed, Creature creature){
        setId();
        this.speed = new Stat(speed);
        this.cooldown = COOLDOWN_RESET_TO;
        this.creature = creature;
    }

    @Override
    public Action getAction(){
        return new RegenAction(); //TODO: is there a better way to do this?
    }
}
