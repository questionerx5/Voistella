package com.questionerx5.voistella;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import com.questionerx5.voistella.screen.*;

import squidpony.squidgrid.gui.gdx.SquidInput;

public class Main extends ApplicationAdapter{
    private Screen screen;

    private SquidInput input;

    private int lastWidth, lastHeight;
    
    @Override
    public void create(){
        screen = new StartScreen();

        input = new SquidInput(new SquidInput.KeyHandler(){
            @Override
            public void handle(char key, boolean alt, boolean ctrl, boolean shift){
                Screen prevScreen = screen;
                screen = screen.handleKeyPress(key, alt, ctrl, shift);
                if(screen != prevScreen){
                    screen.updateBounds(lastWidth, lastHeight);
                }
            }
        });
        // So, the only reason to be passing the stage into here is because it's """preferred"""?
        Gdx.input.setInputProcessor(new InputMultiplexer(screen.mainStage(), input));
    }
    
    @Override
    public void render(){
        if(input.hasNext()){
            input.next();
        }
        
        screen.displayOutput();
        screen.draw();
    }

    @Override
    public void resize(int width, int height){
        super.resize(width, height);

        lastWidth = width;
        lastHeight = height;

        screen.updateBounds(width, height);
    }
}