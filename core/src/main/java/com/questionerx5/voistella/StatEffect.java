package com.questionerx5.voistella;

import java.util.function.Function;

// An Effect that adds a StatMod when it is applied, and removes it when it ends.
public class StatEffect extends Effect{
    public StatEffect(int duration, Function<Creature, Stat> stat, StatMod statMod){
        super(duration,
            e -> new com.questionerx5.voistella.action.Action(){ //TODO: shoudl i really be using anonymous classes?
                public com.questionerx5.voistella.action.ActionResult perform(){
                    if(e.creature() == null){
                        return new com.questionerx5.voistella.action.ActionResult(false);
                    }
                    stat.apply(e.creature()).addModifier(statMod);
                    return new com.questionerx5.voistella.action.ActionResult(true);
                }
            },
            null,
            e -> new com.questionerx5.voistella.action.Action(){
                public com.questionerx5.voistella.action.ActionResult perform(){
                    if(e.creature() == null){
                        return new com.questionerx5.voistella.action.ActionResult(false);
                    }
                    stat.apply(e.creature()).removeModifier(statMod);
                    return new com.questionerx5.voistella.action.ActionResult(true);
                }
            }
        );
    }
}
