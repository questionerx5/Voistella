package com.questionerx5.voistella;

import com.github.yellowstonegames.grid.Coord;

public class DisplayEvent{
    public final Entity entity;
    public final Coord prevPos, newPos;
    public final EventType type;
    public final Level level;
    public enum EventType{
        MOVE, BUMP, PROJECTILE,
        DIE, HIT, PICKED_UP,
        LEAVE_LEVEL, ENTER_LEVEL, DROPPED
    }

    public DisplayEvent(Entity entity, Coord prevPos, Coord newPos, EventType type){
        this.entity = entity;
        this.prevPos = prevPos;
        this.newPos = newPos;
        this.type = type;
        this.level = entity.level;
    }
}