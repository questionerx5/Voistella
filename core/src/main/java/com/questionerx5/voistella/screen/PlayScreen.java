package com.questionerx5.voistella.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.ActorFactory;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Feature;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.World;
import com.questionerx5.voistella.action.*;
import com.questionerx5.voistella.worldgen.WorldConstructor;

public class PlayScreen extends BaseScreen{
    private World world;
    private Level level;
    private Creature player;
    private boolean stopped = false;

    public PlayScreen(final Main game){
        super(game);
        RNGVars.init();
        world = WorldConstructor.generateBasic(ActorFactory.creature("player").makePlayer(new ObjectList<>()));
        player = WorldConstructor.player();
        world.setEvents(new ObjectList<>());
        level = world.level(0);
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
        
        boolean requirePlayerInput = false;
        // An iteration is "boring" if the player is dead and no animation (including off-screen ones) played.
        int boringProcesses = 0;
        while(!requirePlayerInput && !stopped && boringProcesses < 50){
            requirePlayerInput = !player.lastNonNullLevel().process();

            if(!player.inWorld()){
                boringProcesses++;
            }
        }

        for(Entity e : level.entities()){
            game.drawText(e.pos().x, e.pos().y, e.glyph(), e.color());
        }

        game.batch.end();
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        if(!player.inWorld()){
            switch(key){
                case SquidInput.ENTER: return new LoseScreen(game, player.deathMessage());
            }
            return this;
        }
        Feature feature = player.level().featureAt(player.pos());
        if(feature != null && feature.winComponent != null){
            switch(key){
                case SquidInput.ENTER: return new WinScreen(game);
            }
            return this;
        }
        switch(key){
            case SquidInput.ESCAPE: return new StartScreen(game);
            case SquidInput.LEFT_ARROW: {
                player.setNextAction(new MoveAction(-1, 0));
                break;
            }
            case SquidInput.RIGHT_ARROW: {
                player.setNextAction(new MoveAction(1, 0));
                break;
            }
            case SquidInput.UP_ARROW: {
                player.setNextAction(new MoveAction(0, -1));
                break;
            }
            case SquidInput.DOWN_ARROW: {
                player.setNextAction(new MoveAction(0, 1));
                break;
            }
            case SquidInput.UP_LEFT_ARROW: {
                player.setNextAction(new MoveAction(-1, -1));
                break;
            }
            case SquidInput.UP_RIGHT_ARROW: {
                player.setNextAction(new MoveAction(1, -1));
                break;
            }
            case SquidInput.DOWN_LEFT_ARROW: {
                player.setNextAction(new MoveAction(-1, 1));
                break;
            }
            case SquidInput.DOWN_RIGHT_ARROW: {
                player.setNextAction(new MoveAction(1, 1));
                break;
            }
            case SquidInput.CENTER_ARROW:
            case '.': {
                player.setNextAction(new WaitAction());
                break;
            }
            case 'f': {
                player.setNextAction(new AttackAction(player));
                break;
            }
            /*case 'f': {
                return new AttackTargetScreen(this, player.pos().x, player.pos().y, player);
            }
            case '>': {
                if(feature != null && feature.levelChangeComponent != null && !feature.levelChangeComponent.up){
                    player.setNextAction(new ChangeLevelAction(feature.levelChangeComponent.level, feature.levelChangeComponent.destination));
                }
                else{
                    player.messageError("There's nothing leading down.");
                }
                break;
            }
            case '<': {
                if(feature != null && feature.levelChangeComponent != null && feature.levelChangeComponent.up){
                    player.setNextAction(new ChangeLevelAction(feature.levelChangeComponent.level, feature.levelChangeComponent.destination));
                }
                else{
                    player.messageError("There's nothing leading up.");
                }
                break;
            }
            case 'i': {
                return new ItemSelectScreen(this, player.inventory().items(), player, "Inventory");
            }
            case 'e': {
                return new ItemSelectScreen(this, player.equippedItems().values().toArray(new Item[0]), player, "Equipped");
            }
            case 'g': {
                List<Item> items = player.level().itemsAt(player.pos());
                if(items.isEmpty()){
                    player.messageError("There's no item there.");
                }
                else{
                    Item[] itemsArray = items.toArray(new Item[0]);
                    if(itemsArray.length == 1){
                        player.setNextAction(new PickupAction(itemsArray[0]));
                    }
                    else{
                        return new ItemSelectScreen(this, items.toArray(new Item[0]), player, "On ground");
                    }
                }
                break;
            }
            case 'l': {
                return new LookScreen(this, player.pos().x, player.pos().y, player);
            }
            case 's': {
                return new SkillSelectScreen(this, player);
            }*/
        }
        return this;
    }
}
