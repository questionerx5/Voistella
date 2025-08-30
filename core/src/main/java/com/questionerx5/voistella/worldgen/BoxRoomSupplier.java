package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.Tile;
import java.util.Arrays;

public class BoxRoomSupplier extends RoomSupplier{
    private final int minX, minY, maxX, maxY;
    private final Tile floorTile, wallTile;

    public BoxRoomSupplier(int x, int y){
        this(x, y, x, y);
    }
    public BoxRoomSupplier(int minX, int minY, int maxX, int maxY){
        this(minX, minY, maxX, maxY, Tile.FLOOR, Tile.WALL);
    }
    public BoxRoomSupplier(int minX, int minY, int maxX, int maxY, Tile floorTile, Tile wallTile){
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.floorTile = floorTile;
        this.wallTile = wallTile;
    }

    @Override
    public void generate(){
        int x = RNGVars.genRNG.nextInt(minX, maxX + 1);
        int y = RNGVars.genRNG.nextInt(minY, maxY + 1);
        room = new Tile[x + 2][y + 2];
        Arrays.fill(room[0], wallTile);
        Arrays.fill(room[x + 1], wallTile);
        for(int i = 1; i < x + 1; i++){
            Arrays.fill(room[i], floorTile);
            room[i][0] = wallTile;
            room[i][y + 1] = wallTile;
        }
    }
}