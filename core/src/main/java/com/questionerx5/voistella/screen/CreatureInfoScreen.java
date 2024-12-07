package com.questionerx5.voistella.screen;

import squidpony.panel.IColoredString;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidInput;
import com.badlogic.gdx.graphics.Color;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.EquipSlot;

public class CreatureInfoScreen extends SimpleScreen{
    private List<IColoredString<Color>> data;
    private Screen returnTo;

    public CreatureInfoScreen(Creature creature, Screen returnTo){
        this.returnTo = returnTo;
        initData(creature);
    }

    private void initData(Creature creature){
        data = new ArrayList<>();
        addLine(creature.glyph() + " " + creature.name());
        addLine("HP: " + creature.health() + "/" + creature.maxHealth());
        addLine("Attack: " + creature.attack().damage);
        for(Map.Entry<EquipSlot, Item> equipped : creature.equippedItems().entrySet()){
            addLine("Equipped in slot " + equipped.getKey() + ": " + equipped.getValue().name());
        }
        if(creature.isAlly()){
            addLine("Is an ally.");
        }
    }
    private void addLine(String string){
        GDXMarkup.instance.colorString(string).wrap(GRID_WIDTH - 2, data);
    }

    @Override
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER || key == SquidInput.ESCAPE){
            return returnTo;
        }
        return this;
    }

    @Override
    public void displayText(){
        int y = 1;
        for(IColoredString<Color> line : data){
            display.put(1, y, line);
            y++;
        }
        display.put(1, y + 2, "Press Enter or Escape to return.", SColor.WHITE);
    }
    
}
