package com.questionerx5.voistella;

import com.questionerx5.voistella.action.Action;
import com.questionerx5.voistella.action.ActionResult;
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
            Action action = getAction();
            while(true){
                action.bind(this);
                ActionResult success = action.perform();
                if(!success.success || success.alternate == null){
                    break;
                }
                action = success.alternate;
            }
        }
        else{
            creature.addLinkedActor(this);
        }
    }
    
    private boolean instant;
    public boolean instant(){
        return instant;
    }

    public Effect(int duration, ActionSupplier<Effect> actionSupplier){
        this.duration = duration;
        this.instant = false;
        this.actionSupplier = actionSupplier;
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
    }

    @Override
    protected int speed(){
        return 100;
    }

    @Override
    public Action getAction(){
        Action action = new RedirectAction(new CountdownAction(), actionSupplier.getAction(this));
        return action;
    }

    @Override
    protected void end(){
        creature.removeLinkedActor(this);
    }
}
