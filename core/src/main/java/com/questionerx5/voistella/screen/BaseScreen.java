package com.questionerx5.voistella.screen;

import com.badlogic.gdx.Screen;
import com.questionerx5.voistella.Main;

/** A Screen that comes with a {@link com.questionerx5.voistella.Main} and can handle keyboard inputs.
 */
public abstract class BaseScreen implements Screen{
    protected final Main game;

    public BaseScreen(final Main game){
        this.game = game;
    }

    public abstract BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift);

    @Override
    public void show(){
        // Prepare your screen here.
    }

    @Override
    public void resize(int width, int height){
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause(){
        // Invoked when your application is paused.
    }

    @Override
    public void resume(){
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide(){
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose(){
        // Destroy screen's assets here.
    }
}
