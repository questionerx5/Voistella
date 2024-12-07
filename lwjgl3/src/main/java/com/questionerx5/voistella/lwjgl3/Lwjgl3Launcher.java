package com.questionerx5.voistella.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.screen.Screen;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher{
    public static void main(String[] args){
        createApplication();
    }

    private static Lwjgl3Application createApplication(){
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(){
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Voistella");
        configuration.setWindowedMode(Screen.GRID_WIDTH * Screen.CELL_WIDTH, Screen.GRID_HEIGHT * Screen.CELL_HEIGHT);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}