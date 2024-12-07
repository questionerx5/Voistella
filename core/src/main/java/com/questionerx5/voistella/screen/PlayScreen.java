package com.questionerx5.voistella.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import com.questionerx5.voistella.ActorFactory;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.DisplayEvent;
import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Feature;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.World;
import com.questionerx5.voistella.action.*;
import com.questionerx5.voistella.worldgen.*;
import squidpony.panel.IColoredString;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.FilterBatch;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.FloatFilters;
import squidpony.squidgrid.gui.gdx.FloatFilter;
import squidpony.squidgrid.gui.gdx.GDXMarkup;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;
import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.Tile;

import java.util.ArrayList;
import java.util.List;

public class PlayScreen implements Screen{
    private FloatFilter filter;
    private FilterBatch batch;
    private Stage stage, uiStage;
    private SparseLayers display, uiDisplay;
    private Color bgColor;

    private World world;
    // Used for the size of the display.
    // Also the maximum size of a dungeon before it gets cut off.
    //setting height above 64 causes issues with GreasedRegion.intersects(), blame squidlib devs
    private static final int DUNGEON_WIDTH = 50, DUNGEON_HEIGHT = 50;
    // The height of the UI (messages, etc.).
    // The top-most one is a black line. (not currently true)
    private static final int UI_HEIGHT = 6;
    // The width of a bar on the UI.
    private static final int BAR_WIDTH = 16;

    // The glyph representing the player.
    private TextCellFactory.Glyph pg;
    private OrderedMap<Entity, TextCellFactory.Glyph> glyphs;
    private Creature player;

    private List<String> messagesRaw;
    private List<IColoredString<Color>> messages;
    private List<DisplayEvent> events;

    // Whether or not to call refreshGlyphs next frame.
    // Ensures that, when moving to another level, its entities do not flash for one frame.
    private boolean refreshGlyphsNextFrame;
    
    public PlayScreen(){
        RNGVars.init();

        filter = new FloatFilters.IdentityFilter();
        batch = new FilterBatch(filter);
        StretchViewport mainViewport = new StretchViewport(GRID_WIDTH * CELL_WIDTH, (GRID_HEIGHT - UI_HEIGHT) * CELL_HEIGHT);
        StretchViewport uiViewport = new StretchViewport(GRID_WIDTH * CELL_WIDTH, UI_HEIGHT * CELL_HEIGHT);
        mainViewport.setScreenBounds(0, 0, GRID_WIDTH * CELL_WIDTH, (GRID_HEIGHT - UI_HEIGHT) * CELL_HEIGHT);
        uiViewport.setScreenBounds(0, 0, GRID_WIDTH * CELL_WIDTH, UI_HEIGHT * CELL_HEIGHT);
        stage = new Stage(mainViewport, batch);
        uiStage = new Stage(uiViewport, batch);

        display = new SparseLayers(DUNGEON_WIDTH, DUNGEON_HEIGHT + UI_HEIGHT, CELL_WIDTH, CELL_HEIGHT, DefaultResources.getCrispSlabFamily());
        uiDisplay = new SparseLayers(GRID_WIDTH, UI_HEIGHT - 1 + 1, CELL_WIDTH, CELL_HEIGHT, display.font);
        display.setPosition(0f, 0f);
        uiDisplay.defaultPackedBackground = SColor.DARK_GRAY.toFloatBits();
        
        bgColor = SColor.BLACK;
        
        stage.addActor(display);
        uiStage.addActor(uiDisplay);

        glyphs = new OrderedMap<>();
        messages = new ArrayList<>(UI_HEIGHT);
        messagesRaw = new ArrayList<>();
        events = new ArrayList<>();
        
        createWorld();
        ActorFactory.gameEnder(world.level(3), world.level(3).openPoint());
        refreshGlyphsNextFrame = true;
    }

    private void createWorld(){
        world = WorldConstructor.generate(ActorFactory.creature("player").makePlayer(messagesRaw));
        player = WorldConstructor.player();
        world.setEvents(events);
    }

    @Override
    public void displayOutput(){
        if(refreshGlyphsNextFrame){
            player.getVisible();
            refreshGlyphs();
            refreshGlyphsNextFrame = false;
        }
        Gdx.gl.glClearColor(bgColor.r / 255.0f, bgColor.g / 255.0f, bgColor.b / 255.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //comments here make the game less centered but make sure it's aligned to a grid
        //add CELL_WIDTH / 2 if GRID_WIDTH is odd
        stage.getCamera().position.x = pg.getX();
        //add -CELL_HEIGHT / 2 if GRID_HEIGHT is odd; also consider UI_HEIGHT!
        stage.getCamera().position.y = pg.getY();

        display.clear();
        display.fillBackground(bgColor);
        for (int x = Math.max(0, player.pos().x - (GRID_WIDTH >> 1) - 1), i = 0; x < player.lastNonNullLevel().width() && i < GRID_WIDTH + 2; x++, i++){
            for (int y = Math.max(0, player.pos().y - (GRID_HEIGHT >> 1) - 1), j = 0; y < player.lastNonNullLevel().height() && j < GRID_HEIGHT + 2; y++, j++){
                if(player.getVisible()[x][y] > 0.0){
                    Tile tile = player.lastNonNullLevel().tile(x, y);
                    display.put(x, y, tile.glyph(), tile.fg(), tile.bg());
                }
                else{
                    Tile tile = player.memTileAt( x, y);
                    if(tile == null){
                        continue;
                    }
                    display.put(x, y, tile.glyph(),
                    SColor.lerpFloatColors(tile.fg(), SColor.BLACK.toFloatBits(),0.7f),
                    SColor.lerpFloatColors(tile.bg(), SColor.BLACK.toFloatBits(), 0.7f));
                }
            }
        }
        
        //debug to make sure glyphs match up with player's memories of entities
        for(OrderedMap.Entry<Entity, Coord> glyph : player.memEntities().entrySet()){
            display.put(glyph.getValue().x, glyph.getValue().y, SColor.SAFETY_ORANGE);
        }

        for(String message : messagesRaw){
            GDXMarkup.instance.colorString(message).wrap(GRID_WIDTH - 2, messages);
        }
        messagesRaw.clear();
        while(messages.size() > UI_HEIGHT - 2){
            messages.remove(0);
        }

        uiDisplay.clear();
        uiDisplay.fillBackground(uiDisplay.defaultPackedBackground);
        for(int i = 0; i < Math.min(UI_HEIGHT - 2, messages.size()); i++){
            uiDisplay.put(1, i, messages.get(i));
        }

        String stats = String.format("HP: %d/%d", player.health(), player.maxHealth());
        uiDisplay.put((BAR_WIDTH - stats.length()) / 2 + 1, UI_HEIGHT - 2, stats, SColor.WHITE);
        uiDisplay.fillArea(SColor.BLACK, 1, UI_HEIGHT - 2, BAR_WIDTH, 1);
        uiDisplay.fillArea(SColor.LIME, 1, UI_HEIGHT - 2, (int) Math.round((double) player.health() / player.maxHealth() * BAR_WIDTH), 1);
        
        stats = String.format("MP: %d/%d", player.mana(), player.maxMana());
        uiDisplay.put((BAR_WIDTH - stats.length()) / 2 + BAR_WIDTH + 2, UI_HEIGHT - 2, stats, SColor.WHITE);
        uiDisplay.fillArea(SColor.BLACK, BAR_WIDTH + 2, UI_HEIGHT - 2, BAR_WIDTH, 1);
        uiDisplay.fillArea(SColor.SKY_BLUE, BAR_WIDTH + 2, UI_HEIGHT - 2, (int) Math.round((double) player.mana() / player.maxMana() * BAR_WIDTH), 1);

        stats = String.format("SP: %d/%d", player.stamina(), player.maxStamina());
        uiDisplay.put((BAR_WIDTH - stats.length()) / 2 + BAR_WIDTH * 2 + 3, UI_HEIGHT - 2, stats, SColor.WHITE);
        uiDisplay.fillArea(SColor.BLACK, BAR_WIDTH * 2 + 3, UI_HEIGHT - 2, BAR_WIDTH, 1);
        uiDisplay.fillArea(SColor.PINK, BAR_WIDTH * 2 + 3, UI_HEIGHT - 2, (int) Math.round((double) player.stamina() / player.maxStamina() * BAR_WIDTH), 1);
        
        //TODO: process nearby levels as well
        //TODO: Player should always know where they are
        boolean requirePlayerInput = false;
        // Cap maximum iterations, to prevent situations where player is dead and no animations are being played.
        int i = 0;
        while(!requirePlayerInput && !display.hasActiveAnimations() && !refreshGlyphsNextFrame && i < 100){
            requirePlayerInput = !player.lastNonNullLevel().process();
            i++;

            if(!events.isEmpty()){
                DisplayEvent event = events.get(0);
                // Only show events on the player's level.
                if(event.level == player.lastNonNullLevel()){
                    switch(event.type){
                        case BUMP:
                            if(!glyphs.containsKey(event.entity) || !player.canSee(event.prevPos.x, event.prevPos.y)){
                                break;
                            }
                            display.bump(glyphs.get(event.entity), Direction.getRoughDirection(event.newPos.x - event.prevPos.x, event.newPos.y - event.prevPos.y), 0.1f);
                            break;
                        case PROJECTILE:
                            if(!player.canSee(event.prevPos.x, event.prevPos.y) && !player.canSee(event.newPos.x, event.newPos.y)){
                                break;
                            }
                            display.summon(event.prevPos.x, event.prevPos.y, event.newPos.x, event.newPos.y, '•', SColor.WHITE_FLOAT_BITS, SColor.WHITE_FLOAT_BITS, (float) event.prevPos.distance(event.newPos) * 0.03f);
                            break;
                        case MOVE:
                            if(!player.canSee(event.prevPos.x, event.prevPos.y) && !player.canSee(event.newPos.x, event.newPos.y)){
                                break;
                            }
                            if(!glyphs.containsKey(event.entity)){
                                glyphs.put(event.entity, toGlyph(event.entity));
                            }
                            if(event.entity == player){
                                refreshGlyphs();
                            }
                            display.slide(glyphs.get(event.entity), event.prevPos.x, event.prevPos.y, event.newPos.x, event.newPos.y, 0.075f, null);
                            break;
                        case HIT:
                            if(!player.canSee(event.newPos.x, event.newPos.y)){
                                break;
                            }
                            //display.wiggle(glyphs.get(event.entity), 0.02f);
                            // SColor.RED.toFloatBits() = -0x1.0001fep125F
                            //display.summon(event.newPos.x, event.newPos.y, event.newPos.x, event.newPos.y, '*', -0x1.0001fep125F, -0x1.0001fep125F, 0.02f);
                            break;
                        case DIE:
                        case PICKED_UP:
                            if(!player.canSee(event.prevPos.x, event.prevPos.y)){
                                break;
                            }
                            //errors sometimes... maybe it's when something comes into view as soon as it dies?
                            try{
                                display.removeGlyph(glyphs.get(event.entity));
                            }
                            catch(Exception e){
                                System.out.println(e);
                                player.messageError("An exception occurred!");
                            }
                            try{
                                if(glyphs.remove(event.entity) == null){
                                    throw new RuntimeException("sus");
                                }
                            }
                            catch(Exception e){
                                System.out.println(e);
                                player.messageError("An exception occurred!");
                            }
                            break;
                        case LEAVE_LEVEL:
                            if(!player.canSee(event.prevPos.x, event.prevPos.y)){
                                break;
                            }
                            display.removeGlyph(glyphs.get(event.entity));
                            glyphs.remove(event.entity);
                            break;
                        case ENTER_LEVEL:
                        case DROPPED:
                            if(event.entity == player){
                                refreshGlyphsNextFrame = true;
                            }
                            else{
                                if(!player.canSee(event.newPos.x, event.newPos.y)){
                                    break;
                                }
                                glyphs.put(event.entity, toGlyph(event.entity));
                            }
                            break;
                    }
                }
                events.remove(0);
            }
        }
    }
    @Override
    public void draw(){
        uiStage.getViewport().apply(false);
        uiStage.draw();

        stage.act();
        stage.getViewport().apply(false);
        //stage.draw();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        stage.getRoot().draw(batch, 1);
        batch.end();
    }

    private void refreshGlyphs(){
        // Remove all glyphs.
        for(TextCellFactory.Glyph glyph : glyphs.values()){
            display.removeGlyph(glyph);
        }
        glyphs.clear();
        // Re-add player.
        pg = toGlyph(player);
        glyphs.put(player, pg);
        // Add all (other) entities.
        for(OrderedMap.Entry<Entity, Coord> glyph : player.memEntities().entrySet()){
            if(glyph.getKey() == player){
                continue;
            }
            glyphs.put(glyph.getKey(), display.glyph(glyph.getKey().glyph(), glyph.getKey().color(), glyph.getValue().x, glyph.getValue().y));
        }
    }
    private TextCellFactory.Glyph toGlyph(Entity entity){
        return display.glyph(entity.glyph(), entity.color(), entity.pos().x, entity.pos().y);
    }

    @Override
    public Screen handleKeyPress(char key, boolean alt, boolean ctrl, boolean shift){
        if(!player.inWorld()){
            switch(key){
                case SquidInput.ENTER: return new LoseScreen(player.deathMessage());
            }
            return this;
        }
        Feature feature = player.level().featureAt(player.pos());
        if(feature != null && feature.winComponent != null){
            switch(key){
                case SquidInput.ENTER: return new WinScreen();
            }
            return this;
        }
        switch(key){
            case SquidInput.ESCAPE: return new StartScreen();
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
            }
        }
        return this;
    }

    @Override
    public void updateBounds(int width, int height){
        uiDisplay.setBounds(0, 0, width, (float) height / GRID_HEIGHT * UI_HEIGHT);
        uiStage.getViewport().update(width, height, false);
        uiStage.getViewport().setScreenBounds(0, 0, width, (int) uiDisplay.getHeight());
        stage.getViewport().update(width, height, false);
        stage.getViewport().setScreenBounds(0, (int) uiDisplay.getHeight(), width, height - (int) uiDisplay.getHeight());
    }

    @Override
    public Stage mainStage(){
        return stage;
    }
    @Override
    public int height(){
        return GRID_HEIGHT - UI_HEIGHT;
    }
}