package com.questionerx5.voistella;

import com.badlogic.gdx.graphics.Color;
import com.github.tommyettinger.ds.EnumSet;
import com.github.yellowstonegames.grid.Region;

public enum Tile{
    // Normal tiles.
    FLOOR('.', Color.GRAY, new Color(0.125f, 0.125f, 0.125f, 1f), TileFlag.UNREPLACEABLE),
    WALL('#', Color.GRAY, Color.DARK_GRAY, TileFlag.BLOCKING, TileFlag.BLOCKS_LOS, TileFlag.BECOMES_ENTRANCE),
    
    // Tiles used for level generation.
    BLANK(' ', Color.BLACK, Color.DARK_GRAY, TileFlag.BLOCKING, TileFlag.BLOCKS_LOS, TileFlag.BLANK),

    // Out of bounds.
    BOUNDS('X', Color.RED, TileFlag.BLOCKING);
    
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
    
    Tile(char glyph, float fg, TileFlag... flags){
        this(glyph, fg, Color.BLACK.toFloatBits(), flags);
    }
    Tile(char glyph, Color fg, TileFlag... flags){
        this(glyph, fg.toFloatBits(), Color.BLACK.toFloatBits(), flags);
    }
    Tile(char glyph, Color fg, Color bg, TileFlag... flags){
        this(glyph, fg.toFloatBits(), bg.toFloatBits(), flags);
    }
    Tile(char glyph, float fg, float bg, TileFlag... flags){
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