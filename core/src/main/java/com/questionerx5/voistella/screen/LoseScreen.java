package com.questionerx5.voistella.screen;

import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.Palette;

public class LoseScreen extends BaseScreen{
    private final String deathMessage;

    public LoseScreen(final Main game, final String deathMesssage){
        super(game);
        this.deathMessage = deathMesssage;
    }

    @Override
    public void render(float delta){
        game.fillRect(0, 0, Main.COLUMNS, Main.ROWS, Palette.DARK_GREY);
        game.drawText(1, 1, "HAHAHAHAHA skill issue");
        game.drawText(1, 2, deathMessage);
        game.drawText(1, 3, "Y O U   L O S T", Palette.RED);
        game.drawText(1, 4, "Press Enter to restart.");
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER){
            return new PlayScreen(game);
        }
        return null;
    }
}
