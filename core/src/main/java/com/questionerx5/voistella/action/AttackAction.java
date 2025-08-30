package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.DisplayEvent;
import com.questionerx5.voistella.DisplayEvent.EventType;
import com.questionerx5.voistella.EquipSlot;

public class AttackAction extends Action{
    private final Creature target;
    
    public AttackAction(Creature target){
        this.target = target;
    }

    @Override
    public ActionResult perform(){
        if(target == null){
            return new ActionResult(false);
        }
        if(actor instanceof Creature actorCreature){
            if(actorCreature == target){
                // Special message if attacking self.
                actorCreature.emitMessage("@Name attack$ @myself for [RED]~[WHITE] damage.", true, Integer.toString(actorCreature.attack().damage));
            }
            else{
                actorCreature.emitMessage("@Name attack$ ^ for [RED]~[WHITE] damage.", true, target, true, Integer.toString(actorCreature.attack().damage));
            }
            // Don't bump in a direction if entities are on different levels.
            if(actorCreature.level() == target.level()){
                actorCreature.displayEvent(new DisplayEvent(
                    actorCreature, actorCreature.pos(),
                    target.pos(),
                    actorCreature.attack().ranged ? EventType.PROJECTILE : EventType.BUMP)
                );
            }
            actorCreature.displayEvent(new DisplayEvent(
                target, null,
                target.pos(),
                EventType.HIT)
            );
            // Deal damage.
            String deathMessage = "Killed by ";
            deathMessage += actorCreature.articleName(false);
            if(actorCreature.equippedItems().containsKey(EquipSlot.WEAPON)){
                deathMessage += " using " + actorCreature.equippedItems().get(EquipSlot.WEAPON).articleName(false);
            }
            deathMessage += ".";
            target.modifyHealth(-actorCreature.attack().damage, deathMessage);
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}