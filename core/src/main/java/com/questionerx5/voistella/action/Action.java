package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Actor;

public abstract class Action{
    protected Actor actor;
    public void bind(Actor actor){
        if(this.actor != null){
            throw new IllegalStateException("Action can only be binded once");
        } 
        this.actor = actor;
    }

    public abstract ActionResult perform();

    // Recursively perform() on this Action and any subsequent alternates.
    public boolean recursivePerform(Actor actor){
        bind(actor);
        ActionResult success = perform();
        if(success.alternate == null){
            // If no alternates, return whether or not this succeeded.
            return success.success;
        }
        else{
            // Perform the alternate action.
            return success.alternate.recursivePerform(actor);
        }
    }
}