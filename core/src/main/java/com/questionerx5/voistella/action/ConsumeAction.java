package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Effect;
import com.questionerx5.voistella.Item;

public class ConsumeAction extends Action{
    private final Item item;
    
    public ConsumeAction(Item item){
        this.item = item;
    }

    @Override
    public ActionResult perform(){
        if(item == null || item.potionComponent == null){
            return new ActionResult(false);
        }
        if(actor instanceof Creature actorCreature){
            actorCreature.inventory().remove(item);
            actorCreature.emitMessage("@Name drink$ ~.", true, item.articleName(false));
            new Effect(item.potionComponent.effect).applyTo(actorCreature);
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}