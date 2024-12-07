package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.Tile;

public class FixedRoomSupplier extends RoomSupplier{

    public FixedRoomSupplier(Tile[][] room){
        this.room = room;
    }
    public FixedRoomSupplier(char[][] room){
        this.room = new Tile[room.length][room[0].length];
        for(int x = 0; x < room.length; x++){
            for(int y = 0; y < room[x].length; y++){
                this.room[x][y] = Tile.getTile(room[x][y]);
            }
        }
    }
    public FixedRoomSupplier(String room){
        String[] rows = room.split("\n");
        this.room = new Tile[rows[0].length()][rows.length];
        for(int x = 0; x < rows.length; x++){
            for(int y = 0; y < rows[x].length(); y++){
                this.room[y][x] = Tile.getTile(rows[x].charAt(y));
            }
        }
    }

    @Override
    public void generate(){} // The room was initialized in the constructors.
}