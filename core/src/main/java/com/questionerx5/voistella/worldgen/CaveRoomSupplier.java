package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.Tile;

import squidpony.squidmath.GreasedRegion;

public class CaveRoomSupplier extends RoomSupplier{
    private final int width, height;
    private final Tile floorTile, wallTile;

    public CaveRoomSupplier(int width, int height){
        this(width, height, Tile.FLOOR, Tile.WALL);
    }
    public CaveRoomSupplier(int width, int height, Tile floorTile, Tile wallTile){
        this.width = width;
        this.height = height;
        this.floorTile = floorTile;
        this.wallTile = wallTile;
    }

    @Override
    public void generate(){
        for(int attempt = 0; attempt < 100; attempt++){
            // True corresponds to floors, false corresponds to walls.
            boolean[][] untrimmed = new boolean[width + 2][height + 2];
            for(int x = 0; x < width + 2; x++){
                for(int y = 0; y < height + 2; y++){
                    if(x == 0 || x == width + 1 || y == 0 || y == height + 1){
                        untrimmed[x][y] = false;
                    }
                    else{
                        untrimmed[x][y] = RNGVars.genRNG.nextBoolean();
                    }
                }
            }
            for(int time = 0; time < 20; time++){
                boolean[][] copy = new boolean[width + 2][height + 2];
                for(int i = 0; i < width + 2; i++){
                    System.arraycopy(untrimmed[i], 0, copy[i], 0, height + 2);
                }
                //Edges stay walls permanently.
                for(int x = 1; x < width + 1; x++){
                    for(int y = 1; y < height + 1; y++){
                        // Count the number of nearby floors/walls.
                        int walls = 0, floors = 0;
                        for(int dx = -1; dx <= 1; dx++){
                            for(int dy = -1; dy <= 1; dy++){
                                if(copy[x + dx][y + dy]){
                                    floors++;
                                }
                                else{
                                    walls++;
                                }
                            }
                        }
                        if(floors > walls){
                            untrimmed[x][y] = true;
                        }
                        if(walls > floors){
                            untrimmed[x][y] = false;
                        }
                    }
                }
            }
            GreasedRegion largest = new GreasedRegion(untrimmed).largestPart();
            // If every tile is a wall, try again. 
            //isEmpty() doesn't work??? squidlib devs!!!!!!!!!!1!!
            if(largest.first().x == -1){
                continue;
            }
            
            largest.translate(1 - largest.xBound(true), 1 - largest.yBound(true));
            largest.alterBounds(largest.getWidth() - width + 1, largest.getHeight() - height + 1);
            boolean[][] walls = largest.getExternalBorder().decode();
            boolean[][] floors = largest.decode();
            
            room = new Tile[walls.length][walls[0].length];

            for(int x = 0; x < room.length; x++){
                for(int y = 0; y < room[x].length; y++){
                    room[x][y] = floors[x][y] ? floorTile : (walls[x][y] ? wallTile : Tile.BLANK);
                }
            }
            return;
        }
        // bruh
        room = new Tile[][]{{wallTile, wallTile, wallTile}, {wallTile, floorTile, wallTile}, {wallTile, wallTile, wallTile}};
    }
}