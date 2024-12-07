package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.Tile;
import squidpony.squidgrid.mapping.DungeonGenerator;

public class SquidLibRoomSupplier extends RoomSupplier{
    private final int width, height;

    public SquidLibRoomSupplier(int width, int height){
        this.width = width;
        this.height = height;
    }

    @Override
    public void generate(){
        DungeonGenerator gen = new DungeonGenerator(width, height, RNGVars.genRNG);
        gen.generate();
        stairsUp.add(gen.stairsUp);
        stairsDown.add(gen.stairsDown);
        char[][] generated = gen.getBareDungeon();
        room = new Tile[width][height];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                room[x][y] = Tile.getTile(generated[x][y]);
            }
        }
    }
}