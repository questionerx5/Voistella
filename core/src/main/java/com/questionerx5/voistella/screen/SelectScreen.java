package com.questionerx5.voistella.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.FloatFilters;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.FloatFilter;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidInput;

public abstract class SelectScreen implements Screen{
    private Stage stage;
    private SparseLayers display;
    private Color bgColor;

    private static final int OPTIONS_PER_PAGE = GRID_HEIGHT - 2;
    // First two rows are caption and line.
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz".substring(0, OPTIONS_PER_PAGE);
    private int width;

    protected Screen superScreen;
    private int page;
    
    protected abstract String[] choices();
    protected abstract String caption();
    public abstract Screen action(int choice);

    protected SelectScreen(Screen superScreen, int width){
        this(superScreen, SColor.BLACK, width);
    }

    protected SelectScreen(Screen superScreen, Color bgColor, int width){
        this.superScreen = superScreen;
        this.width = width;

        FloatFilter filter = new FloatFilters.IdentityFilter();
        FilterBatch batch = new FilterBatch(filter);
        StretchViewport viewport = new StretchViewport(width * CELL_WIDTH, GRID_HEIGHT * CELL_HEIGHT);
        viewport.setScreenBounds(0, 0, width * CELL_WIDTH, GRID_HEIGHT * CELL_HEIGHT);
        stage = new Stage(viewport, batch);

        display = new SparseLayers(width, GRID_HEIGHT, CELL_WIDTH, CELL_HEIGHT, DefaultResources.getCrispSlabFamily());
        display.setPosition(0f, 0f);

        this.bgColor = bgColor;

        stage.addActor(display);

        page = 0;
    }

    @Override
    public void displayOutput(){
        superScreen.displayOutput();
        
        display.clear();
        display.fillBackground(bgColor);
        display.put(1, 0, GDXMarkup.instance.colorString(caption()));

        String[] choices = choices();
        if(choices.length > OPTIONS_PER_PAGE){
            display.put(1, 1, GDXMarkup.instance.colorString("--page " + (page + 1) + "--"));
        }
        else{
            display.put(1, 1, GDXMarkup.instance.colorString("----------"));
        }
        int y = 2;
        for(int i = 0; i + page * OPTIONS_PER_PAGE < choices.length && i < OPTIONS_PER_PAGE; i++){
            if(choices[i + page * OPTIONS_PER_PAGE] != null){
                display.put(1, y, GDXMarkup.instance.colorString(LETTERS.charAt(i) + " - " + choices[i + page * OPTIONS_PER_PAGE]));
                y++;
            }
        }
    }
    @Override
    public void draw(){
        superScreen.draw();
        stage.getViewport().apply(false);
        stage.draw();
    }

    @Override
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift){
        if(key == SquidInput.ESCAPE){
            return superScreen;
        }
        String[] choices = choices();
        if(key == SquidInput.LEFT_ARROW){
            if(page > 0){
                page--;
            }
            return this;
        }
        if(key == SquidInput.RIGHT_ARROW){
            if((page + 1) * OPTIONS_PER_PAGE < choices().length){
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

    @Override
    public void updateBounds(int width, int height){
        superScreen.updateBounds(width, height);
        display.setBounds(0, 0, (float) width / GRID_WIDTH * this.width, height);
        stage.getViewport().update(width, height, false);
        stage.getViewport().setScreenBounds(0, 0, (int) display.getWidth(), height);
    }

    @Override
    public Stage mainStage(){
        return stage;
    }
}