package com.questionerx5.voistella.screen;

import java.util.Map;

import com.github.tommyettinger.digital.MathTools;
import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.core.DescriptiveColorRgb;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.press.SquidInput;
import com.github.yellowstonegames.smooth.*;
import com.questionerx5.voistella.ActorFactory;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.DisplayEvent;
import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Feature;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.Main;
import com.questionerx5.voistella.Palette;
import com.questionerx5.voistella.RNGVars;
import com.questionerx5.voistella.Tile;
import com.questionerx5.voistella.World;
import com.questionerx5.voistella.action.*;
import com.questionerx5.voistella.worldgen.WorldConstructor;

public class PlayScreen extends BaseScreen{
    private static final float OUT_OF_VIEW_DARKEN = 0.6f;
    private static final int MESSAGE_LINES = 5;
    private static final int LOWER_UI_LINES = 3;
    private static final int BAR_LENGTH = 12;

    private World world;
    private Creature player;
    private ObjectList<String> messages;

    private ObjectList<DisplayEvent> events;

    private class DurationGlider{
        private Glider glider;
        private float duration;

        public DurationGlider(Glider glider, float duration){
            this.glider = glider;
            var baseCompleteRunner = glider.getCompleteRunner();
            if(baseCompleteRunner == null){
                glider.setCompleteRunner(() -> {PlayScreen.this.glider = null; currentEvent = null;});
            }
            else{
                glider.setCompleteRunner(() -> {baseCompleteRunner.run(); PlayScreen.this.glider = null; currentEvent = null;});
            }
            this.duration = duration;
        }

        public float getFloat(String name){
            return glider.getFloat(name);
        }

        public void step(float delta){
            glider.setChange(glider.getChange() + delta / duration);
        }
    }

    // Current event being displayed.
    private DisplayEvent currentEvent;
    // Glider for current event.
    private DurationGlider glider;
    

    public PlayScreen(final Main game){
        super(game);
        RNGVars.init();
        messages = new ObjectList<>();
        world = WorldConstructor.generate(ActorFactory.creature("player").makePlayer(messages));
        player = WorldConstructor.player();
        events = new ObjectList<>();
        world.setEvents(events);
        Level finalLevel = world.level(3);
        ActorFactory.gameEnder(finalLevel, finalLevel.openPoint());
    }

    @Override
    public void render(float delta){        
        processWorld();

        final Level level = player.lastNonNullLevel();
        final float left = (currentEvent != null && currentEvent.entity == player ? glider.getFloat("x") : player.pos().x) - Main.COLUMNS / 2;
        final float top = (currentEvent != null && currentEvent.entity == player ? glider.getFloat("y") : player.pos().y) + (-Main.ROWS + MESSAGE_LINES + LOWER_UI_LINES) / 2;
        for(int y = Math.max((int) Math.floor(top), 0); y < Math.min(top + Main.ROWS - MESSAGE_LINES - LOWER_UI_LINES, level.height()); y++){
            for(int x = Math.max((int) Math.floor(left), 0); x < Math.min(left + Main.COLUMNS, level.width()); x++){
                final float drawX = x - left;
                final float drawY = y - top + MESSAGE_LINES;
                if(player.canSee(x, y)){
                    game.fillCell(drawX, drawY, level.tile(x, y).bg());
                    game.drawText(drawX, drawY, level.tile(x, y).glyph(), level.tile(x, y).fg());
                }
                else{
                    Tile memory = player.memTileAt(x, y);
                    if(memory != null){
                        game.fillCell(drawX, drawY, DescriptiveColorRgb.darken(memory.bg(), OUT_OF_VIEW_DARKEN));
                        game.drawText(drawX, drawY, memory.glyph(), DescriptiveColorRgb.darken(memory.fg(), OUT_OF_VIEW_DARKEN));
                    }
                }
            }
        }

        // Entity that the current event is moving, and should therefore not be drawn normally
        Entity eventEntity = null;
        if(currentEvent != null){
            switch(currentEvent.type){
                case BUMP, MOVE -> {
                    eventEntity = currentEvent.entity;
                    final float drawX = glider.getFloat("x") - left;
                    final float drawY = glider.getFloat("y") - top + MESSAGE_LINES;
                    if(inDraw(drawX, drawY)){
                        game.drawText(drawX, drawY, eventEntity.glyph(), eventEntity.color());
                    }
                }
                case PROJECTILE -> {
                    final float drawX = glider.getFloat("x") - left;
                    final float drawY = glider.getFloat("y") - top + MESSAGE_LINES;
                    if(inDraw(drawX, drawY)){
                        game.drawText(drawX, drawY, '·', Palette.WHITE);
                    }
                }
                case HIT, DIE, PICKED_UP, LEAVE_LEVEL, ENTER_LEVEL, DROPPED -> {} // no animation needed
            }
            glider.step(delta);
        }
        for(Entity e : level.entities()){
            final float drawX = e.pos().x - left;
            final float drawY = e.pos().y - top + MESSAGE_LINES;
            if(e != eventEntity && inDraw(drawX, drawY) && player.canSee(e.pos())){
                game.drawText(drawX, drawY, e.glyph(), e.color());
            }
        }
        for(Map.Entry<Entity, Coord> entry : player.memEntities().entrySet()){
            final float drawX = entry.getValue().x - left;
            final float drawY = entry.getValue().y - top + MESSAGE_LINES;
            final Entity entity = entry.getKey();
            if(entity != eventEntity && inDraw(drawX, drawY) && !player.canSee(entry.getValue())){
                game.drawText(drawX, drawY, entity.glyph(), DescriptiveColorRgb.darken(entity.color(), OUT_OF_VIEW_DARKEN));
            }
        }

        game.fillRect(0, 0, Main.COLUMNS, MESSAGE_LINES, Palette.DARK_GREY);
        while(messages.size() > MESSAGE_LINES){
            messages.remove(0);
        }
        for(int i = 0; i < MESSAGE_LINES; i++){
            if(messages.size() > i){
                game.drawText(0, i, messages.get(i));
            }
        }

        game.fillRect(0, Main.ROWS - LOWER_UI_LINES, Main.COLUMNS, LOWER_UI_LINES, Palette.DARK_GREY);

        String stats = String.format("HP: %d/%d", player.health(), player.maxHealth());
        game.fillRect(1, Main.ROWS - 2, BAR_LENGTH, 1, Palette.BLACK); // drawing over the same area twice, oh well
        // decimal? breaks grid but who cares >:)
        game.fillRect(1, Main.ROWS - 2, (float) BAR_LENGTH * player.health() / player.maxHealth(), 1, Palette.LIME);
        // BAR_LENGTH / 2: halfway across the bar
        // -stats.length() / 2: go back half of the text's length
        game.drawText((BAR_LENGTH - stats.length()) / 2f + 1, Main.ROWS - 2, stats);

        stats = String.format("MP: %d/%d", player.mana(), player.maxMana());
        game.fillRect(BAR_LENGTH + 2, Main.ROWS - 2, BAR_LENGTH, 1, Palette.BLACK);
        game.fillRect(BAR_LENGTH + 2, Main.ROWS - 2, (float) BAR_LENGTH * player.mana() / player.maxMana(), 1, Palette.SKY);
        game.drawText((BAR_LENGTH - stats.length()) / 2f + BAR_LENGTH + 2, Main.ROWS - 2, stats);

        stats = String.format("SP: %d/%d", player.stamina(), player.maxStamina());
        game.fillRect(BAR_LENGTH * 2 + 3, Main.ROWS - 2, BAR_LENGTH, 1, Palette.BLACK);
        game.fillRect(BAR_LENGTH * 2 + 3, Main.ROWS - 2, (float) BAR_LENGTH * player.stamina() / player.maxStamina(), 1, Palette.SALMON);
        game.drawText((BAR_LENGTH - stats.length()) / 2f + BAR_LENGTH * 2 + 3, Main.ROWS - 2, stats);
    }

    private void processWorld(){
        boolean requirePlayerInput = false;
        // An iteration is "boring" if the player is dead and no animation (including off-screen ones) played.
        int boringProcesses = 0;
        while(!requirePlayerInput && currentEvent == null && boringProcesses < 50){
            requirePlayerInput = !player.lastNonNullLevel().process();

            if(!player.inWorld()){
                boringProcesses++;
            }
            
            while(currentEvent == null && !events.isEmpty()){
                currentEvent = events.pop();
                if(currentEvent.level != player.lastNonNullLevel()){
                    currentEvent = null;
                    continue;
                }
                glider = switch(currentEvent.type){
                    case BUMP -> {
                        if(!player.canSee(currentEvent.prevPos)){
                            yield null;
                        }
                        var prevPos = currentEvent.prevPos;
                        var newPos = currentEvent.newPos;
                        float interpAmount = prevPos.equals(newPos) ? 1 : (0.4f / prevPos.distance(newPos));
                        float bumpToX = MathTools.lerp(prevPos.x, newPos.x, interpAmount);
                        float bumpToY = MathTools.lerp(prevPos.y, newPos.y, interpAmount);
                        yield new DurationGlider(
                            new SequenceGlider(
                                new Glider[]{
                                    new Glider(new Glider.Changer("x", prevPos.x, bumpToX),
                                               new Glider.Changer("y", prevPos.y, bumpToY)),
                                    new Glider(new Glider.Changer("x", bumpToX, prevPos.x),
                                               new Glider.Changer("y", bumpToY, prevPos.y)),
                                }, 
                                new float[]{
                                    1f,
                                    1f
                                }),
                            0.125f
                        );
                    }

                    case MOVE -> {
                        if(!player.canSee(currentEvent.prevPos) && !player.canSee(currentEvent.newPos)){
                            yield null;
                        }
                        yield new DurationGlider(
                            new CoordGlider(currentEvent.prevPos, currentEvent.newPos),
                            0.075f
                        );
                    }

                    case PROJECTILE -> {
                        if(!player.canSee(currentEvent.prevPos) && !player.canSee(currentEvent.newPos)){
                            yield null;
                        }
                        yield new DurationGlider(
                            new CoordGlider(currentEvent.prevPos, currentEvent.newPos),
                            currentEvent.prevPos.distance(currentEvent.newPos) * 0.05f
                        );
                    }
                    case HIT, DIE, PICKED_UP, LEAVE_LEVEL, ENTER_LEVEL, DROPPED -> null; // no animation needed
                };
                if(glider == null){
                    currentEvent = null;
                }
                else{
                    boringProcesses = 0;
                }
            }
        }
    }

    private static boolean inDraw(float x, float y){
        return x > -1 && x < Main.COLUMNS && y > MESSAGE_LINES - 1 && y < Main.ROWS - LOWER_UI_LINES;
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
        if(feature != null && feature.winComponent != null){ // TODO don't continue turns after winning
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
                final int left = player.pos().x - Main.COLUMNS / 2; // assume no animation
                final int top = player.pos().y + (-Main.ROWS + MESSAGE_LINES + LOWER_UI_LINES) / 2;
                return new AttackTargetScreen(this, player, -left, -top + MESSAGE_LINES);
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
            /*case 'i': {
                return new ItemSelectScreen(this, player.inventory().items(), player, "Inventory");
            }
            case 'e': {
                return new ItemSelectScreen(this, player.equippedItems().values().toArray(new Item[0]), player, "Equipped");
            }*/
            case 'g': {
                ObjectList<Item> items = player.level().itemsAt(player.pos());
                if(items.isEmpty()){
                    player.messageError("There's no item there.");
                }
                else{
                    if(items.size() == 1){
                        player.setNextAction(new PickupAction(items.get(0)));
                    }
                    else{
                        player.setNextAction(new PickupAction(items.get(0)));
                        //TODO readd
                        //Item[] itemsArray = items.toArray(new Item[0]);
                        //return new ItemSelectScreen(this, items.toArray(new Item[0]), player, "On ground");
                    }
                }
                break;
            }
            /*case 'l': {
                return new LookScreen(this, player.pos().x, player.pos().y, player);
            }
            case 's': {
                return new SkillSelectScreen(this, player);
            }*/
        }
        return null;
    }
}
