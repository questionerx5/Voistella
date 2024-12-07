package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.DisplayEvent;
import com.questionerx5.voistella.DisplayEvent.EventType;

public class DropAction extends Action{
    private final Item item;
    
    public DropAction(Item item){
        this.item = item;
    }

    @Override
    public ActionResult perform(){
        if(item == null){
            return new ActionResult(false);
        }
        if(actor instanceof Creature){
            Creature actorCreature = (Creature) actor;
            if(!actorCreature.inventory().remove(item)){
                return new ActionResult(false);
            }
            item.setLevel(actorCreature.level());
            item.setPos(actorCreature.pos());
            actorCreature.emitMessage("@Name drop$ ~.", true, item.articleName(false));
            actorCreature.displayEvent(new DisplayEvent(item, actorCreature.pos(), item.pos(), EventType.DROPPED));
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}