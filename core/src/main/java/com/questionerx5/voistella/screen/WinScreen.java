package com.questionerx5.voistella.screen;

import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SColor;

public class WinScreen extends SimpleScreen{
    public WinScreen(){}
    
    @Override
    public void displayText(){
        display.put(1, 1, "You won!", SColor.LIME);
        display.put(1, 2, "Press Enter to restart.", SColor.WHITE);
    }
    
    @Override
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER) {return new PlayScreen();}
        return this;
    }
}