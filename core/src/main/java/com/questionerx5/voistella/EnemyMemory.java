package com.questionerx5.voistella;

import java.util.Map;
import java.util.HashMap;

import squidpony.squidmath.Coord;

public class EnemyMemory implements Memory{
    private Map<Level, Map<Entity, Coord>> entities;
    private Map<Entity, Coord> entitiesOnLevel(Level level){
        if(!entities.containsKey(level)){
            entities.put(level, new HashMap<>());
        }
        return entities.get(level);
    }

    private boolean rememberEntities;

    public EnemyMemory(boolean rememberEntities){
        this.rememberEntities = rememberEntities;
        entities = new HashMap<>();
    }

    @Override
    public boolean tracksTiles(){
        return false;
    }

    @Override
    public Tile tileAt(Level level, int x, int y){
        return level.tile(x, y);
    }

    @Override
    public boolean setTile(Level level, int x, int y, Tile tile){return false;}

    @Override
    public boolean tracksEntities(){
        return rememberEntities;
    }

    @Override
    public void addEntity(Entity entity, Coord pos){
        if(!rememberEntities){
            return;
        }
        for(Map<Entity, Coord> level : entities.values()){
            level.remove(entity);
        }
        entitiesOnLevel(entity.level()).put(entity, pos);
    }

    @Override
    public void removeEntity(Level level, Entity entity){
        if(!rememberEntities){
            return;
        }
        entitiesOnLevel(level).remove(entity);
    }

    @Override
    public Map<Entity, Coord> getEntities(Level level){
        if(rememberEntities){
            return entitiesOnLevel(level);
        }
        Map<Entity, Coord> result = new HashMap<>();
        for(Entity entity : level.entities()){
            result.put(entity, entity.pos());
        }
        return result;
    }
}
