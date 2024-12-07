package com.questionerx5.voistella.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.FloatFilters;
import squidpony.squidgrid.gui.gdx.FloatFilter;

public abstract class SimpleScreen implements Screen{
    private Stage stage;
    protected SparseLayers display;
    
    private Color bgColor;

    protected SimpleScreen(){
        this(SColor.BLACK);
    }

    protected SimpleScreen(Color bgColor){
        FloatFilter filter = new FloatFilters.IdentityFilter();
        FilterBatch batch = new FilterBatch(filter);
        StretchViewport viewport = new StretchViewport(GRID_WIDTH * CELL_WIDTH, GRID_HEIGHT * CELL_HEIGHT);
        viewport.setScreenBounds(0, 0, GRID_WIDTH * CELL_WIDTH, GRID_HEIGHT * CELL_HEIGHT);
        stage = new Stage(viewport, batch);

        display = new SparseLayers(GRID_WIDTH, GRID_HEIGHT, CELL_WIDTH, CELL_HEIGHT, DefaultResources.getCrispSlabFamily());
        display.setPosition(0f, 0f);

        this.bgColor = bgColor;

        stage.addActor(display);
    }

    @Override
    public void displayOutput(){
        Gdx.gl.glClearColor(bgColor.r / 255.0f, bgColor.g / 255.0f, bgColor.b / 255.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        display.clear();
        display.fillBackground(bgColor);
        displayText();
    }
    @Override
    public void draw(){
        stage.getViewport().apply(false);
        stage.draw();
    }

    public abstract void displayText();

    @Override
    public void updateBounds(int width, int height){
        stage.getViewport().update(width, height, false);
        stage.getViewport().setScreenBounds(0, 0, width, height);
    }

    @Override
    public Stage mainStage(){
        return stage;
    }
}