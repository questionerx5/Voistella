package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.Tile;
import com.questionerx5.voistella.Tile.TileFlag;
import com.questionerx5.voistella.data.EntityData;
import com.questionerx5.voistella.data.CreatureData;

//import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.AStarSearch;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class LevelBuilder{
    private int width, height;
    private Tile[][] level;
    private List<Coord> stairsUp, stairsDown;
    public List<Coord> stairsUp(){
        return stairsUp;
    }
    public List<Coord> stairsDown(){
        return stairsDown;
    }
    private List<PlacedEntity> entities;
    private List<Coord> blocked;
    private GreasedRegion openRegions;
    private Coord openPoint(){
        if(openRegions == null){
            openRegions = Tile.unflaggedRegions(level, TileFlag.BLOCKING).removeSeveral(blocked);
        }
        return openRegions.singleRandom(RNGVars.genRNG);
    }
    private void markOpen(Coord point){
        if(openRegions != null){
            openRegions.add(point);
        }
    }
    private void unmarkOpen(Coord point){
        if(openRegions != null){
            openRegions.remove(point);
        }
    }
        
    private PlacedEntity player;
    private Creature playerResult;
    public Creature player(){
        return playerResult;
    }

    public LevelBuilder(int width, int height){
        this.width = width;
        this.height = height;
        this.level = new Tile[width][height];
        for(Tile[] row : level){
            Arrays.fill(row, Tile.BOUNDS);
        }

        this.stairsUp = new ArrayList<>();
        this.stairsDown = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.blocked = new ArrayList<>();
    }

    public LevelBuilder addStartRoom(RoomSupplier supplier){
        for(int i = 0; i < 10; i++){
            supplier.generate();
            Tile[][] room = supplier.room();
            if(width < room.length || height < room[0].length){
                continue;
            }
            int xOffset = RNGVars.genRNG.between(0, width - room.length + 1);
            int yOffset = RNGVars.genRNG.between(0, height - room[0].length + 1);
            pasteRoom(room, xOffset, yOffset);
            addStairs(supplier.stairsUp(), supplier.stairsDown(), xOffset, yOffset);
            addEntities(supplier.entities(), xOffset, yOffset);
            return this;
        }
        throw new RuntimeException("Didn't find a small enough room that fit");
    }

    public LevelBuilder addRooms(int numAttempts, RoomSupplier... suppliers){
        for(int i = 0; i < numAttempts; i++){
            RoomSupplier supplier = RNGVars.genRNG.getRandomElement(suppliers);
            supplier.generate();
            Tile[][] room = supplier.room();
            
            // Don't try to place rooms that are larger than the level.
            if(width < room.length || height < room[0].length){
                continue;
            }
            GreasedRegion blocked = Tile.flaggedRegions(level, TileFlag.UNREPLACEABLE);
            GreasedRegion entrances = blocked
            .copy()
            .fringe()
            .and(Tile.flaggedRegions(level, TileFlag.BECOMES_ENTRANCE));
            // GreasedRegion roomEntrances = see above
            // Try placing the room.
            for(int j = 0; j < 500; j++){
                // Coord a = roomEntrances.singleRandom(RNGVars.genRNG);
                // Coord b = entrances.singleRandom(RNGVars.genRNG);
                //and then b - a, i guess. make sure to check for out of bounds.
                int xOffset = RNGVars.genRNG.between(0, width - room.length + 1);
                int yOffset = RNGVars.genRNG.between(0, height - room[0].length + 1);
                GreasedRegion temp = Tile.unflaggedRegions(room, TileFlag.BLANK, xOffset, yOffset);
                if(blocked.intersects(temp)){
                    continue;
                }
                temp = Tile.flaggedRegions(room, TileFlag.UNREPLACEABLE, xOffset, yOffset)
                .fringe()
                .and(Tile.flaggedRegions(room, TileFlag.BECOMES_ENTRANCE, xOffset, yOffset))
                .and(entrances);
                if(temp.isEmpty()){
                    continue;
                }
                Coord entrance = temp.singleRandom(RNGVars.genRNG);
                pasteRoom(room, xOffset, yOffset);
                addStairs(supplier.stairsUp(), supplier.stairsDown(), xOffset, yOffset);
                addEntities(supplier.entities(), xOffset, yOffset);
                level[entrance.x][entrance.y] = Tile.FLOOR;
                break;
            }
        }
        return this;
    }

    private void pasteRoom(Tile[][] room, int xOffset, int yOffset){
        openRegions = null;
        for(int x = 0; x < room.length; x++){
            for(int y = 0; y < room[x].length; y++){
                if(!room[x][y].testFlag(TileFlag.BLANK)){
                    level[x + xOffset][y + yOffset] = room[x][y];
                }
            }
        }
    }
    private void addStairs(List<Coord> stairsUp, List<Coord> stairsDown, int xOffset, int yOffset){
        for(Coord pos : stairsUp){
            Coord translated = pos.translate(xOffset, yOffset);
            this.stairsUp.add(translated);
            blocked.add(translated);
            unmarkOpen(translated);
        }
        for(Coord pos : stairsDown){
            Coord translated = pos.translate(xOffset, yOffset);
            this.stairsDown.add(translated);
            blocked.add(translated);
            unmarkOpen(translated);
        }
    }
    private void addEntities(List<PlacedEntity> entities, int xOffset, int yOffset){
        if(entities == null){
            return;
        }
        for(PlacedEntity entity : entities){
            Coord translated = entity.pos.translate(xOffset, yOffset);
            this.entities.add(new PlacedEntity(entity.entity, translated));
            blocked.add(translated);
            unmarkOpen(translated);
        }
    }

    public LevelBuilder addLoops(int tries){
        return addLoops(tries, Tile.FLOOR);
    }
    public LevelBuilder addLoops(int tries, Tile passage){
        for(int i = 0; i < tries; i++){
            int x = RNGVars.genRNG.between(1, width - 1);
            int y = RNGVars.genRNG.between(1, height - 1);
            tryToAddLoop(x, y, passage);
        }
        return this;
    }
    public LevelBuilder addLoops(){
        return addLoops(Tile.FLOOR);
    }
    public LevelBuilder addLoops(Tile passage){
        GreasedRegion entrances = Tile.flaggedRegions(level, TileFlag.BECOMES_ENTRANCE);
        Coord[] remaining = entrances.toArray(new Coord[entrances.size()]);
        RNGVars.genRNG.shuffleInPlace(remaining);
        for(Coord point : remaining){
            tryToAddLoop(point.x, point.y, passage);
        }
        return this;
    }

    private void tryToAddLoop(int x, int y, Tile passage){
        if(x == 0 || x == width - 1 || y == 0 || y == height - 1){
            return;
        }
        if(!level[x][y].testFlag(TileFlag.BECOMES_ENTRANCE)){
            return;
        }
        boolean up = !level[x][y - 1].testFlag(TileFlag.BLOCKING);
        boolean down = !level[x][y + 1].testFlag(TileFlag.BLOCKING);
        boolean left = !level[x - 1][y].testFlag(TileFlag.BLOCKING);
        boolean right = !level[x + 1][y].testFlag(TileFlag.BLOCKING);
        // The tile above and below must be both walls, or both floors. Same for left and right.
        if((up ^ down) || (left ^ right)){
            return;
        }
        // The tile above/below must be different from the tile to the left/right.
        if(up == left){
            return;
        }

        double[][] weights = Tile.movementResistances(level, -1, 1);
        AStarSearch aStar = new AStarSearch(weights, AStarSearch.SearchType.MANHATTAN);
        int pathLength;
        if(up){
            pathLength = aStar.path(x, y - 1, x, y + 1).size();
        }
        else{
            pathLength = aStar.path(x - 1, y, x + 1, y).size();
        }
        // Don't create a connection if the points it's connecting are already close together.
        if(pathLength > 15){
            level[x][y] = passage;
            if(passage.testFlag(TileFlag.BLOCKING)){
                unmarkOpen(Coord.get(x, y));
            }
            else{
                markOpen(Coord.get(x, y));
            }
        }
    }

    public LevelBuilder randomStairs(){
        Coord up = openPoint();
        stairsUp.add(up);
        blocked.add(up);
        unmarkOpen(up);
        Coord down = openPoint();
        stairsDown.add(down);
        blocked.add(down);
        unmarkOpen(down);
        return this;
    }
    public LevelBuilder placePlayer(CreatureData entity){
        Coord pos = openPoint();
        player = new PlacedEntity(entity, pos);
        entities.add(player);
        blocked.add(pos);
        unmarkOpen(pos);
        return this;
    }
    public LevelBuilder placeEntities(EntityData<?> entity, int amount){
        for(int i = 0; i < amount; i++){
            Coord pos = openPoint();
            entities.add(new PlacedEntity(entity, pos));
            blocked.add(pos);
            unmarkOpen(pos);
        }
        return this;
    }
    public LevelBuilder placeEntitiesNonBlocking(EntityData<?> entity, int amount){
        for(int i = 0; i < amount; i++){
            Coord pos = openPoint();
            entities.add(new PlacedEntity(entity, pos));
        }
        return this;
    }

    public Level build(String name){
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(level[x][y] == Tile.BOUNDS){
                    level[x][y] = Tile.WALL;
                }
            }
        }

        Level built = new Level(level, name);
        for(PlacedEntity entity : entities){
            if(entity == player){
                playerResult = (Creature) entity.entity.create(built, entity.pos);
            }
            else{
                entity.entity.create(built, entity.pos);
            }
        }

        return built;
    }
}