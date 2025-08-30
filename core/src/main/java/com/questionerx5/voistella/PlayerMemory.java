package com.questionerx5.voistella;

import com.github.tommyettinger.ds.ObjectObjectMap;
import com.github.yellowstonegames.grid.Coord;

public class PlayerMemory implements Memory{
    private static class PlayerLevelMemory{
        private Tile[][] tiles;
        private boolean inBounds(int x, int y){
            return !(x < 0 || x >= tiles.length || y < 0 || y >= tiles[0].length);
        }
        public Tile tile(int x, int y){
            return inBounds(x, y) ? tiles[x][y] : Tile.BOUNDS;
        }
        public boolean setTile(int x, int y, Tile tile){
            if(inBounds(x, y)){
                if(tiles[x][y] == tile){
                    return false;
                }
                tiles[x][y] = tile;
                return true;
            }
            return false;
        }

        private ObjectObjectMap<Entity, Coord> entities;
        public void addEntity(Entity entity, Coord pos){
            entities.put(entity, pos);
        }
        public void removeEntity(Entity entity){
            entities.remove(entity);
        }
        public ObjectObjectMap<Entity, Coord> getEntities(){
            return entities;
        }

        public PlayerLevelMemory(int width, int height){
            tiles = new Tile[width][height];
            entities = new ObjectObjectMap<>();
        }
    }

    private ObjectObjectMap<Level, PlayerLevelMemory> levelMemories;
    private Level lastUsedLevel;
    private PlayerLevelMemory lastUsed;
    private PlayerLevelMemory levelMemory(Level level){
        if(level == null){
            return null;
        }
        if(level == lastUsedLevel){
            return lastUsed;
        }
        if(!levelMemories.containsKey(level)){
            levelMemories.put(level, new PlayerLevelMemory(level.width(), level.height()));
        }
        lastUsedLevel = level;
        lastUsed = levelMemories.get(level);
        return lastUsed;
    }

    public PlayerMemory(){
        levelMemories = new ObjectObjectMap<>();
    }

    @Override
    public boolean tracksTiles(){
       return true;
    }

    @Override
    public Tile tileAt(Level level, int x, int y){
        return levelMemory(level).tile(x, y);
    }

    @Override
    public boolean setTile(Level level, int x, int y, Tile tile){
        return levelMemory(level).setTile(x, y, tile);
    }
    
    @Override
    public boolean tracksEntities(){
       return true;
    }

    @Override
    public void addEntity(Entity entity, Coord pos){
        for(PlayerLevelMemory memory : levelMemories.values()){
            memory.removeEntity(entity);
        }
        levelMemory(entity.level()).addEntity(entity, pos);
    }

    @Override
    public void removeEntity(Level level, Entity entity){
        levelMemory(level).removeEntity(entity);
    }

    @Override
    public ObjectObjectMap<Entity, Coord> getEntities(Level level){
        return levelMemory(level).getEntities();
    }
}

