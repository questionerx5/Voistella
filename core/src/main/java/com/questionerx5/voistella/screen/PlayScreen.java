package com.questionerx5.voistella.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.questionerx5.voistella.ActorFactory;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.World;
import com.questionerx5.voistella.worldgen.WorldConstructor;

public class PlayScreen extends BaseScreen{
    private World world;
    private Level level;
    private Creature player;

    public PlayScreen(final Main game){
        super(game);
        RNGVars.init();
        world = WorldConstructor.generateBasic(ActorFactory.creature("player"));
        level = world.level(0);
        player = WorldConstructor.player();
    }

    @Override
    public void render(float delta){
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        for(int y = 0; y < Main.ROWS; y++){
            for(int x = 0; x < Main.COLUMNS; x++){
                game.fillCell(x, y, level.tile(x, y).bg());
                game.drawText(x, y, level.tile(x, y).glyph(), level.tile(x, y).fg());
            }
        }

        for(Entity e : level.entities()){
            game.drawText(e.pos().x, e.pos().y, e.glyph(), e.color());
        }

        game.batch.end();
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        return new StartScreen(game);
    }
}
