package com.questionerx5.voistella.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidmath.DDALine;
import squidpony.squidmath.Coord;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.FloatFilter;
import squidpony.squidgrid.gui.gdx.FloatFilters;
import squidpony.squidgrid.gui.gdx.SColor;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class TargetScreen implements Screen{
    protected Screen superScreen;
    private Stage stage;
    private SparseLayers display;
    private Color bgColor;

    private Coord start, end;
    private int offX, offY;
    private List<Coord> line;
    protected Collection<Coord> area;

    protected TargetScreen(Screen superScreen, Coord point, int offX, int offY){ //TODO: Add a caption somewhere
        this.superScreen = superScreen;
        start = point;
        end = point;
        this.offX = offX;
        this.offY = offY;
        line = new ArrayList<>();
        line.add(point);

        FloatFilter filter = new FloatFilters.IdentityFilter();
        FilterBatch batch = new FilterBatch(filter);
        StretchViewport viewport = new StretchViewport(GRID_WIDTH * CELL_WIDTH, superScreen.height() * CELL_HEIGHT);
        viewport.setScreenBounds(0, 0, GRID_WIDTH * CELL_WIDTH, superScreen.height() * CELL_HEIGHT);
        stage = new Stage(viewport, batch);

        display = new SparseLayers(GRID_WIDTH, superScreen.height(), CELL_WIDTH, CELL_HEIGHT, DefaultResources.getCrispSlabFamily());
        display.setPosition(0f, 0f);

        bgColor = SColor.TRANSPARENT;

        stage.addActor(display);
    }

    protected abstract Screen select(int x, int y);
    protected abstract boolean acceptable(int x, int y);
    protected abstract boolean lineObstructed(List<Coord> line);

    @Override
    public void displayOutput(){
        superScreen.displayOutput();

        display.clear();
        display.fillBackground(bgColor);
        if(area != null){
            for(Coord point : area){
                display.put(point.x - offX + GRID_WIDTH / 2, point.y - offY + superScreen.height() / 2, SColor.translucentColor(SColor.WHITE, 0.1f));
            }
        }
        for(Coord point : line){
            display.put(point.x - offX + GRID_WIDTH / 2, point.y - offY + superScreen.height() / 2, SColor.translucentColor(SColor.WHITE, 0.3f));
        }
        boolean acceptable = (area == null || area.contains(end)) && acceptable(end.x, end.y) && !lineObstructed(line);
        display.put(end.x - offX + GRID_WIDTH / 2, end.y - offY + superScreen.height() / 2, SColor.translucentColor(acceptable ? SColor.MAGENTA : SColor.RED, 0.3f));
    }

    @Override
    public void draw(){
        superScreen.draw();
        stage.getViewport().apply(false);
        stage.draw();
    }

    @Override
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift){
        switch(key){
            case SquidInput.LEFT_ARROW: {
                end = end.translate(-1, 0);
                updateLine();
                break;
            }
            case SquidInput.RIGHT_ARROW: {
                end = end.translate(1, 0);
                updateLine();
                break;
            }
            case SquidInput.UP_ARROW: {
                end = end.translate(0, -1);
                updateLine();
                break;
            }
            case SquidInput.DOWN_ARROW: {
                end = end.translate(0, 1);
                updateLine();
                break;
            }
            case SquidInput.UP_LEFT_ARROW: {
                end = end.translate(-1, -1);
                updateLine();
                break;
            }
            case SquidInput.UP_RIGHT_ARROW: {
                end = end.translate(1, -1);
                updateLine();
                break;
            }
            case SquidInput.DOWN_LEFT_ARROW: {
                end = end.translate(-1, 1);
                updateLine();
                break;
            }
            case SquidInput.DOWN_RIGHT_ARROW: {
                end = end.translate(1, 1);
                updateLine();
                break;
            }
            case SquidInput.ESCAPE: {
                return superScreen;
            }
            case SquidInput.ENTER: {
                if((area == null || area.contains(end)) && !lineObstructed(line)){
                    return select(end.x, end.y);
                }
                break;
            }
        }
        return this;
    }

    private void updateLine(){
        line = DDALine.line(start, end);
        if(lineObstructed(line)){
            line = DDALine.line(end, start);
            Collections.reverse(line);
        }
    }

    @Override
    public void updateBounds(int width, int height){
        superScreen.updateBounds(width, height);
        display.setBounds(0, 0, width, (float) height / GRID_HEIGHT * superScreen.height());
        stage.getViewport().update(width, height, false);
        stage.getViewport().setScreenBounds(0, height - (int) display.getHeight(), width, (int) display.getHeight());
    }

    @Override
    public Stage mainStage(){
        return stage;
    }
    @Override
    public int height(){
        return superScreen.height();
    }
}
