package com.questionerx5.voistella.screen;

import squidpony.panel.IColoredString;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidInput;
import com.badlogic.gdx.graphics.Color;

import java.util.List;
import java.util.ArrayList;

import com.questionerx5.voistella.Item;

public class ItemInfoScreen extends SimpleScreen{
    private List<IColoredString<Color>> data;
    private Screen returnTo;

    public ItemInfoScreen(Item item, Screen returnTo){
        this.returnTo = returnTo;
        initData(item);
    }

    private void initData(Item item){
        data = new ArrayList<>();
        addLine(item.glyph() + " " + item.name());
        if(item.equippableComponent != null){
            addLine("Damage: " + item.equippableComponent.attack.damage);
            if(item.equippableComponent.attack.range != 1.5){
                addLine("Range: " + item.equippableComponent.attack.range);
            }
        }
        if(item.potionComponent != null){
            addLine("Can be consumed");
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
