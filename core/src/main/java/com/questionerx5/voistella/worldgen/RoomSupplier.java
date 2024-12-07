package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.Tile;

import squidpony.squidmath.Coord;

import java.util.List;
import java.util.ArrayList;

public abstract class RoomSupplier{
    protected Tile[][] room;
    public Tile[][] room(){
        return room;
    }
    protected List<Coord> stairsUp = new ArrayList<>(), stairsDown = new ArrayList<>(); //TODO: be able to associate these with other levels
    public List<Coord> stairsUp(){
        return stairsUp;
    }
    public List<Coord> stairsDown(){
        return stairsDown;
    }
    protected List<PlacedEntity> entities = new ArrayList<>();
    public List<PlacedEntity> entities(){
        return entities;
    }
    public RoomSupplier addEntity(PlacedEntity entity){
        if(entities == null){
            entities = new ArrayList<>();
        }
        entities.add(entity);
        return this;
    }

    public abstract void generate();
}