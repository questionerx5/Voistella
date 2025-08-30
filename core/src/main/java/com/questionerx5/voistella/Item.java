package com.questionerx5.voistella;

import com.badlogic.gdx.graphics.Color;
import com.github.yellowstonegames.grid.Coord;
import com.questionerx5.voistella.action.Action;
import com.questionerx5.voistella.action.WaitAction;
import com.questionerx5.voistella.component.item.*;

public class Item extends Entity{
    public EquippableComponent equippableComponent;
    public PotionComponent potionComponent;

    public Item(Level level, Coord pos, char glyph, Color color, String name){
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
            this.level.removeItem(this);
        }
        this.level = level;
        setPos(pos);
        if(level != null){
            level.addItem(this);
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
        Item other = (Item) obj;
        if (id != other.id)
            return false;
        return true;
    }
}