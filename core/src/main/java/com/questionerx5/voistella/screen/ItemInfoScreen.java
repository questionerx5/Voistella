package com.questionerx5.voistella.screen;

import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.Palette;

public class ItemInfoScreen extends BaseScreen{
    private BaseScreen superScreen;
    private ObjectList<String> display;

    public ItemInfoScreen(BaseScreen superScreen, Item item){
        super(superScreen.game);
        this.superScreen = superScreen;

        display = new ObjectList<>();
        display.add(item.glyph() + " " + item.name());
        if(item.equippableComponent != null){
            display.add("Damage: " + item.equippableComponent.attack.damage);
            if(item.equippableComponent.attack.range != 1.5){
                display.add("Range: " + item.equippableComponent.attack.range);
            }
        }
        if(item.potionComponent != null){
            display.add("Can be consumed");
        }
    }

    @Override
    public void render(float delta){
        game.fillRect(0, 0, Main.COLUMNS, Main.ROWS, Palette.DARK_GREY);
        int y = 1;
        for(String line : display){
            game.drawText(1, y, line);
            y++;
        }
        game.drawText(1, y, "Press Enter or Escape to return.");
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER || key == SquidInput.ESCAPE){
            return superScreen;
        }
        return this;
    }
}