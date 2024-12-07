package com.questionerx5.voistella.action;

import com.questionerx5.voistella.RegenTimer;
import com.questionerx5.voistella.Creature;

public class RegenAction extends Action{
    public RegenAction(){}

    @Override
    public ActionResult perform(){
        if(actor instanceof RegenTimer){
            Creature target = ((RegenTimer) actor).creature();
            target.modifyHealth(1);
            target.modifyMana(1);
            target.modifyStamina(1);
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}