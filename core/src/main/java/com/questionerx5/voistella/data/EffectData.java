package com.questionerx5.voistella.data;

import com.questionerx5.voistella.ActionSupplier;
import com.questionerx5.voistella.Effect;

public class EffectData{
    private int duration;
    private boolean instant;
    private ActionSupplier<Effect> actionSupplier;

    public EffectData(int duration, ActionSupplier<Effect> actionSupplier){
        this.duration = duration;
        this.instant = false;
        this.actionSupplier = actionSupplier;
    }
    public EffectData(ActionSupplier<Effect> actionSupplier){
        this.duration = 0;
        this.instant = true;
        this.actionSupplier = actionSupplier;
    }

    public Effect create(){
        if(instant){
            return new Effect(actionSupplier);
        }
        else{
            return new Effect(duration, actionSupplier);
        }
    }
}
