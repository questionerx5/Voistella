package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;

public class HealAction extends Action{
    private final Creature target;
    private final int amount;
    
    public HealAction(Creature target, int amount){
        this.target = target;
        this.amount = amount;
    }

    @Override
    public ActionResult perform(){
        if(target == null){
            return new ActionResult(false);
        }
        if(target.health() < target.maxHealth()){
            target.emitMessage("@Name @am healed.", true);
            target.modifyHealth(amount);
        }
        return new ActionResult(true);
    }
}