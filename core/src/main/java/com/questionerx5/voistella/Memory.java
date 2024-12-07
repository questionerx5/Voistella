package com.questionerx5.voistella;

import java.util.Map;

import squidpony.squidmath.Coord;

public interface Memory{
    boolean tracksTiles();
    Tile tileAt(Level level, int x, int y);
    boolean setTile(Level level, int x, int y, Tile tile);
    boolean tracksEntities();
    default void addEntity(Entity entity){
        addEntity(entity, entity.pos());
    }
    void addEntity(Entity entity, Coord pos);
    void removeEntity(Level level, Entity entity);
    Map<Entity, Coord> getEntities(Level level);
}
