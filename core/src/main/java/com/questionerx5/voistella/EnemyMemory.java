package com.questionerx5.voistella;

import com.github.tommyettinger.ds.ObjectObjectMap;
import com.github.yellowstonegames.grid.Coord;

public class EnemyMemory implements Memory{
    private ObjectObjectMap<Level, ObjectObjectMap<Entity, Coord>> entities;
    private ObjectObjectMap<Entity, Coord> entitiesOnLevel(Level level){
        if(!entities.containsKey(level)){
            entities.put(level, new ObjectObjectMap<>());
        }
        return entities.get(level);
    }

    private boolean rememberEntities;

    public EnemyMemory(boolean rememberEntities){
        this.rememberEntities = rememberEntities;
        entities = new ObjectObjectMap<>();
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
        for(ObjectObjectMap<Entity, Coord> level : entities.values()){
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
    public ObjectObjectMap<Entity, Coord> getEntities(Level level){
        if(rememberEntities){
            return entitiesOnLevel(level);
        }
        ObjectObjectMap<Entity, Coord> result = new ObjectObjectMap<>();
        for(Entity entity : level.entities()){
            result.put(entity, entity.pos());
        }
        return result;
    }
}
