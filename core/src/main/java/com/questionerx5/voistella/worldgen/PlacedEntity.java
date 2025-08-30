package com.questionerx5.voistella.worldgen;

import com.github.yellowstonegames.grid.Coord;
import com.questionerx5.voistella.data.EntityData;

public class PlacedEntity{
    public final EntityData<?> entity;
    public final Coord pos;
    public PlacedEntity(EntityData<?> entity, Coord pos){
        this.entity = entity;
        this.pos = pos;
    }
}
