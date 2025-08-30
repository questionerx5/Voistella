package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.DisplayEvent;
import com.questionerx5.voistella.DisplayEvent.EventType;
import com.questionerx5.voistella.Item;

public class PickupAction extends Action{
    private final Item item;
    
    public PickupAction(Item item){
        this.item = item;
    }

    @Override
    public ActionResult perform(){
        if(item == null){
            return new ActionResult(false);
        }
        if(actor instanceof Creature actorCreature){
            if(actorCreature.inventory().isFull()){
                actorCreature.messageError("Your inventory is full.");
                return new ActionResult(false);
            }
            actorCreature.emitMessage("@Name pick$ up ~.", true, item.articleName(false));
            actorCreature.displayEvent(new DisplayEvent(item, item.pos(), null, EventType.PICKED_UP));
            actorCreature.inventory().add(item);
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}