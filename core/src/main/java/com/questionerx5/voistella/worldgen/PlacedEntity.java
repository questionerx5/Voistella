package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.data.EntityData;
import squidpony.squidmath.Coord;

public class PlacedEntity{
    public final EntityData<?> entity;
    public final Coord pos;
    public PlacedEntity(EntityData<?> entity, Coord pos){
        this.entity = entity;
        this.pos = pos;
    }
}
