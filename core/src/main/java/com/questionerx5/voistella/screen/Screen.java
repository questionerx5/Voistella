package com.questionerx5.voistella.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;

public interface Screen{
    int GRID_WIDTH = 80, GRID_HEIGHT = 24;
    int CELL_WIDTH = 10, CELL_HEIGHT = 20;

    // Code to put stuff on the display and handle logic.
    public void displayOutput();
    // Code to draw the display to the screen.
    public void draw();
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift);
    //TODO: public Screen handleMouseMovement();
    public void updateBounds(int width, int height);
    public Stage mainStage();
    default public int height(){
        return GRID_HEIGHT;
    }
}