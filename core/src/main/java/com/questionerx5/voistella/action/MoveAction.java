package com.questionerx5.voistella.action;

import com.github.yellowstonegames.grid.Coord;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.DisplayEvent;
import com.questionerx5.voistella.DisplayEvent.EventType;
import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Tile.TileFlag;

public class MoveAction extends Action{
    private final int x, y;

    public MoveAction(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public ActionResult perform(){
        if(x == 0 && y == 0){
            return new ActionResult(new WaitAction());
        }
        if(actor instanceof Entity actorEntity){
            Coord targetPos = actorEntity.pos().translate(x, y);
            // Cancel movement into walls, etc.
            if(actorEntity.level().tile(targetPos).testFlag(TileFlag.BLOCKING)){
                return new ActionResult(false);
            }
            // If trying to move into something, attack instead.
            Creature c = actorEntity.level().creatureAt(targetPos);
            if(c != null){
                return new ActionResult(new AttackAction(c));
            }
            actorEntity.displayEvent(new DisplayEvent(actorEntity, actorEntity.pos(), targetPos, EventType.MOVE));
            // Update creatures' memories if entity is going out of sight.
            for(Creature creature : actorEntity.level().creatures()){
                if(creature != actorEntity
                && creature.tracksEntities()
                && creature.canSee(actorEntity.pos().x, actorEntity.pos().y)
                && !creature.canSee(targetPos.x, targetPos.y)){
                    creature.memAddEntity(actorEntity, targetPos);
                }
            }
            actorEntity.setPos(targetPos);
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}