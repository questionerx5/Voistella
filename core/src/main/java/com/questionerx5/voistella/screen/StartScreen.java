package com.questionerx5.voistella.screen;

import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SColor;

public class StartScreen extends SimpleScreen{
    public StartScreen(){}

    @Override
    public void displayText(){
        display.put(1, 1, "Voistella", SColor.WHITE);
        display.put(1, 2, "Press Enter to start.", SColor.WHITE);
    }

    @Override
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ENTER) {return new PlayScreen();}
        return this;
    }
}