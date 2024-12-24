package com.questionerx5.voistella;

import squidpony.squidmath.Coord;

import com.badlogic.gdx.graphics.Color;

import com.questionerx5.voistella.action.Action;
import com.questionerx5.voistella.action.WaitAction;
import com.questionerx5.voistella.component.feature.*;

public class Feature extends Entity{
    public LevelChangeComponent levelChangeComponent;
    public WinComponent winComponent;

    // TODO: merge tiles and features?
    /*public static enum FeatureFlag{
        
    }
    private EnumSet<FeatureFlag> flags;
    public boolean testFlag(FeatureFlag flag){
        return flags.contains(flag);
    }
    public Feature setFlag(FeatureFlag flag){
        flags.add(flag);
        return this;
    }*/

    /*private ActionSupplier ai;
    public Feature setAI(ActionSupplier ai){
        this.ai = ai;
        return this;
    }*/

    public Feature(Level level, Coord pos, char glyph, Color color, String name){
        setLevel(level, pos);
        this.glyph = glyph;
        this.color = color;
        this.name = name;
    }

    @Override
    protected int speed(){
        return -1;
    }

    @Override
    public void setLevel(Level level, Coord pos){
        if(this.level != null){
            this.level.removeFeature(this);
        }
        this.level = level;
        setPos(pos);
        if(level != null){
            level.addFeature(this);
        } 
    }

    @Override
    public Action getAction(){
        return new WaitAction();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Feature other = (Feature) obj;
        if (id != other.id)
            return false;
        return true;
    }
}