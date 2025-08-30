package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Item;

public class EquipAction extends Action{
    private final Item item;
    
    public EquipAction(Item item){
        this.item = item;
    }

    @Override
    public ActionResult perform(){
        if(item == null){
            return new ActionResult(false);
        }
        if(actor instanceof Creature actorCreature){
            boolean success = actorCreature.equip(item);
            if(success){
                actorCreature.emitMessage("@Name equip$ ~.", true, item.articleName(false));
            }
            return new ActionResult(success);
        }
        return new ActionResult(false);
    }
}