package com.questionerx5.voistella.screen;

import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.Palette;

public class StartScreen extends BaseScreen{
    public StartScreen(final Main game){
        super(game);
    }

    @Override
    public void render(float delta){
        game.fillRect(0, 0, Main.COLUMNS, Main.ROWS, Palette.DARK_GREY);
        game.drawText(1, 1, "Voistella");
        game.drawText(1, 2, "Press Enter to start.");
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER){
            return new PlayScreen(game);
        }
        return null;
    }
}
