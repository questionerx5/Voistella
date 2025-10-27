package com.questionerx5.voistella.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.Main;

public class WinScreen extends BaseScreen{
    public WinScreen(final Main game){
        super(game);
    }

    @Override
    public void render(float delta){
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        game.fillRect(0, 0, Main.COLUMNS, Main.ROWS, Color.DARK_GRAY);
        game.drawText(1, 1, "You won!", Color.GREEN);
        game.drawText(1, 2, "Press Enter to restart.");

		game.batch.end();
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER){
            return new PlayScreen(game);
        }
        return null;
    }
}
