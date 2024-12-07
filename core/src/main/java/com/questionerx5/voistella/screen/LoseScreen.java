package com.questionerx5.voistella.screen;

import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SColor;

public class LoseScreen extends SimpleScreen{
    private String deathMessage;

    public LoseScreen(String deathMessage){
        this.deathMessage = deathMessage;
    }
    
    @Override
    public void displayText(){
        display.put(1, 1, "HAHAHAHAHA skill issue", SColor.WHITE);
        display.put(1, 2, deathMessage, SColor.WHITE);
        display.put(1, 3, "YOU LOST.", SColor.RED);
        display.put(1, 4, "Press Enter to restart.", SColor.WHITE);
    }
    
    @Override
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER) {return new PlayScreen();}
        return this;
    }
}