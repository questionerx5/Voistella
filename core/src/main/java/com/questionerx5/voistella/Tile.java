package com.questionerx5.voistella;

import com.github.tommyettinger.ds.EnumSet;
import com.github.yellowstonegames.grid.Region;

public enum Tile{
    // Normal tiles.
    FLOOR('.', Palette.GREY, Palette.DARKER_GREY, TileFlag.UNREPLACEABLE),
    WALL('#', Palette.GREY, Palette.DARK_GREY, TileFlag.BLOCKING, TileFlag.BLOCKS_LOS, TileFlag.BECOMES_ENTRANCE),
    
    // Tiles used for level generation.
    BLANK(' ', Palette.BLACK, TileFlag.BLOCKING, TileFlag.BLOCKS_LOS, TileFlag.BLANK),

    // Out of bounds.
    BOUNDS('X', Palette.RED, TileFlag.BLOCKING, TileFlag.BLOCKS_LOS);
    
    private char glyph;
    public char glyph(){
        return glyph;
    }

    private int fg, bg;
    public int fg(){
        return fg;
    }
    public int bg(){
        return bg;
    }
    
    public enum TileFlag{
        BLOCKING,
        BLOCKS_LOS,
        BECOMES_ENTRANCE,
        UNREPLACEABLE,
        BLANK
    }

    private EnumSet flags;
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
    
    Tile(char glyph, int fg, TileFlag... flags){
        this(glyph, fg, Palette.BLACK, flags);
    }
    Tile(char glyph, int fg, int bg, TileFlag... flags){
        this.glyph = glyph;
        this.fg = fg;
        this.bg = bg;
        this.flags = new EnumSet(flags);
    }

    //TODO: don't create object every time?
    public static Region flaggedRegions(Tile[][] tiles, TileFlag flag){
        boolean[][] success = new boolean[tiles.length][tiles[0].length];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x][y] = tiles[x][y].testFlag(flag);
            }
        }
        return new Region(success);
    }
    public static Region unflaggedRegions(Tile[][] tiles, TileFlag flag){
        boolean[][] success = new boolean[tiles.length][tiles[0].length];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x][y] = !tiles[x][y].testFlag(flag);
            }
        }
        return new Region(success);
    }
    // TODO: check if translate() is bugged in Region like i think it was in GreasedRegion
    public static Region flaggedRegions(Tile[][] tiles, TileFlag flag, int xOffset, int yOffset){
        boolean[][] success = new boolean[tiles.length + xOffset][tiles[0].length + yOffset];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x + xOffset][y + yOffset] = tiles[x][y].testFlag(flag);
            }
        }
        return new Region(success);
    }
    public static Region unflaggedRegions(Tile[][] tiles, TileFlag flag, int xOffset, int yOffset){
        boolean[][] success = new boolean[tiles.length + xOffset][tiles[0].length + yOffset];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                success[x + xOffset][y + yOffset] = !tiles[x][y].testFlag(flag);
            }
        }
        return new Region(success);
    }

    public static float[][] movementResistances(Tile[][] tiles, float blocking, float nonBlocking){
        float[][] result = new float[tiles.length][tiles[0].length];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[0].length; y++){
                result[x][y] = tiles[x][y].testFlag(TileFlag.BLOCKING) ? blocking : nonBlocking;
            }
        }
        return result;
    }
}