package com.questionerx5.voistella;

import com.badlogic.gdx.graphics.Color;

import squidpony.squidgrid.gui.gdx.SColor;
import java.util.EnumSet;
import squidpony.squidmath.GreasedRegion;

public enum Tile{
    // Normal tiles.
    FLOOR('.', SColor.GRAY, new SColor(32, 32, 32), TileFlag.UNREPLACEABLE),
    WALL('#', SColor.GRAY, SColor.DARK_GRAY, TileFlag.BLOCKING, TileFlag.BLOCKS_LOS, TileFlag.BECOMES_ENTRANCE),
    
    // Tiles used for level generation.
    BLANK(' ', SColor.BLACK, SColor.DARK_GRAY, TileFlag.BLOCKING, TileFlag.BLOCKS_LOS, TileFlag.BLANK),

    // Out of bounds.
    BOUNDS('X', SColor.RED, TileFlag.BLOCKING);
    
    private char glyph;
    public char glyph(){
        return glyph;
    }

    private float fg, bg;
    public float fg(){
        return fg;
    }
    public float bg(){
        return bg;
    }
    
    public enum TileFlag{
        BLOCKING,
        BLOCKS_LOS,
        BECOMES_ENTRANCE,
        UNREPLACEABLE,
        BLANK
    }

    private EnumSet<TileFlag> flags;
    public boolean testFlag(TileFlag flag){
        return flags.contains(flag);
    }

    public static Tile getTile(char tile){
        switch(tile){
            case '.': return FLOOR;
            case '#': return WALL;
            case ' ': return BLANK;
            default: return BOUNDS;
        }
    }
    
    Tile(char glyph, float fg, TileFlag... flags){
        this(glyph, fg, SColor.FLOAT_BLACK, flags);
    }
    Tile(char glyph, Color fg, TileFlag... flags){
        this(glyph, fg.toFloatBits(), SColor.FLOAT_BLACK, flags);
    }
    Tile(char glyph, Color fg, Color bg, TileFlag... flags){
        this(glyph, fg.toFloatBits(), bg.toFloatBits(), flags);
    }
    Tile(char glyph, float fg, float bg, TileFlag... flags){
        this.glyph = glyph;
        this.fg = fg;
        this.bg = bg;
        this.flags = EnumSet.noneOf(TileFlag.class);
        for(TileFlag flag : flags){
            this.flags.add(flag);
        }
    }

    public static GreasedRegion flaggedRegions(Tile[][] tiles, TileFlag flag){
        boolean[][] success = new boolean[tiles.length][tiles[0].length];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x][y] = tiles[x][y].testFlag(flag);
            }
        }
        return new GreasedRegion(success);
    }
    public static GreasedRegion unflaggedRegions(Tile[][] tiles, TileFlag flag){
        boolean[][] success = new boolean[tiles.length][tiles[0].length];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x][y] = !tiles[x][y].testFlag(flag);
            }
        }
        return new GreasedRegion(success);
    }
    public static GreasedRegion flaggedRegions(Tile[][] tiles, TileFlag flag, int xOffset, int yOffset){
        boolean[][] success = new boolean[tiles.length + xOffset][tiles[0].length + yOffset];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x + xOffset][y + yOffset] = tiles[x][y].testFlag(flag);
            }
        }
        return new GreasedRegion(success);
    }
    public static GreasedRegion unflaggedRegions(Tile[][] tiles, TileFlag flag, int xOffset, int yOffset){
        boolean[][] success = new boolean[tiles.length + xOffset][tiles[0].length + yOffset];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x + xOffset][y + yOffset] = !tiles[x][y].testFlag(flag);
            }
        }
        return new GreasedRegion(success);
    }

    public static double[][] movementResistances(Tile[][] tiles, double blocking, double nonBlocking){
        double[][] result = new double[tiles.length][tiles[0].length];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                result[x][y] = tiles[x][y].testFlag(TileFlag.BLOCKING) ? blocking : nonBlocking;
            }
        }
        return result;
    }
}