package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.DisplayEvent;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.DisplayEvent.EventType;
import com.questionerx5.voistella.Entity;
import squidpony.squidmath.Coord;

public class ChangeLevelAction extends Action{
    private final Level level;
    private final Coord destination;

    public ChangeLevelAction(Level level, Coord destination){
        this.level = level;
        this.destination = destination;
    }

    @Override
    public ActionResult perform(){
        if(actor instanceof Entity){
            Entity actorEntity = (Entity) actor;
            Creature c = level.creatureAt(destination);
            if(c != null){
                return new ActionResult(new AttackAction(c));
            }
            // Remove entity from current level.
            actorEntity.emitMessage("@Name leave$ ~.", true, actorEntity.level().name());
            actorEntity.displayEvent(new DisplayEvent(actorEntity, actorEntity.pos(), destination, EventType.LEAVE_LEVEL));
            // Add entity to destination level.
            actorEntity.setLevel(level, destination);
            actorEntity.emitMessage("@Name enter$ ~.", false, actorEntity.level().name());
            actorEntity.displayEvent(new DisplayEvent(actorEntity, actorEntity.pos(), destination, EventType.ENTER_LEVEL));
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}