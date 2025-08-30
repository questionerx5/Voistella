package com.questionerx5.voistella.worldgen;

import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.grid.Coord;
import com.questionerx5.voistella.Tile;

public abstract class RoomSupplier{
    protected Tile[][] room;
    public Tile[][] room(){
        return room;
    }
    protected ObjectList<Coord> stairsUp = new ObjectList<>(), stairsDown = new ObjectList<>(); //TODO: be able to associate these with other levels
    public ObjectList<Coord> stairsUp(){
        return stairsUp;
    }
    public ObjectList<Coord> stairsDown(){
        return stairsDown;
    }
    protected ObjectList<PlacedEntity> entities = new ObjectList<>();
    public ObjectList<PlacedEntity> entities(){
        return entities;
    }
    public RoomSupplier addEntity(PlacedEntity entity){
        if(entities == null){
            entities = new ObjectList<>();
        }
        entities.add(entity);
        return this;
    }

    public abstract void generate();
}