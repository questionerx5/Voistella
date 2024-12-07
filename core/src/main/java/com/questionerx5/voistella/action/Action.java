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
}