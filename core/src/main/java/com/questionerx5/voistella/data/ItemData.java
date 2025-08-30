package com.questionerx5.voistella.data;

import com.badlogic.gdx.graphics.Color;
import com.github.yellowstonegames.grid.Coord;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.component.item.*;

public class ItemData extends EntityData<Item>{
    private EquippableComponent equippableComponent;
    public ItemData setEquippableComponent(EquippableComponent equippableComponent){
        this.equippableComponent = equippableComponent;
        return this;
    }
    private PotionComponent potionComponent;
    public ItemData setPotionComponent(PotionComponent potionComponent){
        this.potionComponent = potionComponent;
        return this;
    }

    public ItemData(char glyph, Color color, String name, boolean unique){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.unique = unique;
    }
    public ItemData(char glyph, Color color, String name){
        this(glyph, color, name, Character.isUpperCase(name.charAt(0)));
    }
    public ItemData(ItemData other){
        this(other.glyph, other.color, other.name, other.unique);
        this.setEquippableComponent(other.equippableComponent);
        this.setPotionComponent(other.potionComponent);
    }

    public Item create(Level level, Coord pos){
        Item item = new Item(level, pos, this.glyph, this.color, this.name);
        item.equippableComponent = equippableComponent;
        item.potionComponent = potionComponent;
        return item;
    }
}
