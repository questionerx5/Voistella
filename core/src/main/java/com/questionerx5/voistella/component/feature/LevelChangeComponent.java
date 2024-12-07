package com.questionerx5.voistella.component.feature;

import squidpony.squidmath.Coord;
import com.questionerx5.voistella.Level;

public class LevelChangeComponent implements FeatureComponent{
    public final Coord destination; 
    public final Level level;
    public final boolean up;
    
    public LevelChangeComponent(Coord destination, Level level, boolean up){
        this.destination = destination;
        this.level = level;
        this.up = up;
    }
}