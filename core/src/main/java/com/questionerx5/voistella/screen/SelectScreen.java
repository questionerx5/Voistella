package com.questionerx5.voistella.screen;

import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.Palette;

public abstract class SelectScreen extends BaseScreen{ // TODO readd caption
    private static final int PANEL_WIDTH = 25;
    private static final int OPTIONS_PER_PAGE;
    private static final String LETTERS;
    static {
        final String lettersBase = "abcdefghijklmnopqrstuvwxyz";
        OPTIONS_PER_PAGE = Math.min(Main.ROWS - 2, lettersBase.length());
        LETTERS = lettersBase.substring(0, OPTIONS_PER_PAGE);
    }

    protected BaseScreen superScreen;
    private int page;
    protected final String[] choices;

    protected SelectScreen(BaseScreen superScreen, String[] choices){
        super(superScreen.game);
        this.superScreen = superScreen;
        page = 0;
        this.choices = choices;
    }

    protected abstract BaseScreen action(int index);

    @Override
    public void render(float delta){
        superScreen.render(delta);

        game.fillRect(0, 0, PANEL_WIDTH, Main.ROWS, Palette.DARKER_GREY);
        if(choices.length > OPTIONS_PER_PAGE){
            game.drawText(1, 0, "page " + (page + 1));
        }

        int y = 2;
        for(int i = page * OPTIONS_PER_PAGE; i < (page + 1) * OPTIONS_PER_PAGE && i < choices.length; i++){
            if(choices[i] != null){
                // TODO: Add ellipses ("...")
                game.drawText(1, y, LETTERS.charAt(i - page * OPTIONS_PER_PAGE) + " - " + choices[i]);
                y++;
            }
        }
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ESCAPE){
            return superScreen;
        }
        if(key == SquidInput.LEFT_ARROW){
            if(page > 0){
                page--;
            }
            return this;
        }
        if(key == SquidInput.RIGHT_ARROW){
            if((page + 1) * OPTIONS_PER_PAGE < choices.length){
                page++;
            }
            return this;
        }

        int index = LETTERS.indexOf(key);
        if(index == -1){
            return this;
        }
        index += page * OPTIONS_PER_PAGE;
        if(index >= choices.length || choices[index] == null){
            return this;
        }
        return action(index);
    }
}
