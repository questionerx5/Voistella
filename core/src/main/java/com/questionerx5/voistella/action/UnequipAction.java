package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Item;

public class UnequipAction extends Action{
    private final Item item;
    
    public UnequipAction(Item item){
        this.item = item;
    }

    @Override
    public ActionResult perform(){
        if(item == null){
            return new ActionResult(false);
        }
        if(actor instanceof Creature actorCreature){
            boolean success = actorCreature.unequip(item);
            return new ActionResult(success);
        }
        return new ActionResult(false);
    }
}