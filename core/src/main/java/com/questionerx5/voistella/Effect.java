package com.questionerx5.voistella;

import com.questionerx5.voistella.action.Action;
import com.questionerx5.voistella.action.RedirectAction;
import com.questionerx5.voistella.action.CountdownAction;

public class Effect extends Countdown{
    private Creature creature;
    public Creature creature(){
        return creature;
    }

    private ActionSupplier<Effect> actionSupplier;
    public void applyTo(Creature creature){
        if(this.creature != null){
            throw new IllegalStateException("Effect can only be put on one creature!");
        }
        this.creature = creature;
        if(instant){
            getAction().recursivePerform(this);
        }
        else{
            creature.addLinkedActor(this);
        }
    }
    private ActionSupplier<Effect> endActionSupplier;
    
    private boolean instant;
    public boolean instant(){
        return instant;
    }

    public Effect(int duration, ActionSupplier<Effect> actionSupplier){
        this.duration = duration;
        this.instant = false;
        this.actionSupplier = actionSupplier;
    }
    public Effect(int duration, ActionSupplier<Effect> actionSupplier, ActionSupplier<Effect> endActionSupplier){
        this.duration = duration;
        this.instant = false;
        this.actionSupplier = actionSupplier;
        this.endActionSupplier = endActionSupplier;
    }
    public Effect(ActionSupplier<Effect> actionSupplier){
        this.duration = 0;
        this.instant = true;
        this.actionSupplier = actionSupplier;
    }
    public Effect(Effect other){
        this.duration = other.duration;
        this.instant = other.instant;
        this.actionSupplier = other.actionSupplier;
        this.endActionSupplier = other.endActionSupplier;
    }

    @Override
    protected int speed(){
        return 100;
    }

    @Override
    public Action getAction(){
        if(actionSupplier == null){
            return new CountdownAction();
        }
        else{
            return new RedirectAction(new CountdownAction(), actionSupplier.getAction(this));
        }
    }

    @Override
    protected void end(){
        if(endActionSupplier != null){
            endActionSupplier.getAction(this).recursivePerform(this);
        }
        creature.removeLinkedActor(this);
    }
}
