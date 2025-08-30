package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.RegenTimer;

public class RegenAction extends Action{
    public RegenAction(){}

    @Override
    public ActionResult perform(){
        if(actor instanceof RegenTimer actorRegenTimer){
            Creature target = actorRegenTimer.creature();
            target.modifyHealth(1);
            target.modifyMana(1);
            target.modifyStamina(1);
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}