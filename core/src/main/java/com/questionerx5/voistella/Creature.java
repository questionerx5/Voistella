package com.questionerx5.voistella;

import com.badlogic.gdx.graphics.Color;

import com.questionerx5.voistella.DisplayEvent.EventType;
import com.questionerx5.voistella.action.Action;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;

import squidpony.squidgrid.Radius;
import squidpony.squidgrid.FOV;
import squidpony.squidgrid.Measurement;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidai.DijkstraMap;

public class Creature extends Entity{
    private static final boolean DEBUG_ALL_SEEING = false;

    private ActionSupplier<? super Creature> ai;
    public Creature setAI(ActionSupplier<? super Creature> ai){
        this.ai = ai;
        return this;
    }
    private Action nextAction;
    public void setNextAction(Action action){
        nextAction = action;
    }

    private String deathMessage;
    public String deathMessage(){
        return deathMessage;
    }

    private Stat maxHealth;
    public int maxHealth(){
        return maxHealth.getValue();
    }
    private int health;
    public int health(){
        return health;
    }
    public void modifyHealth(int amount){
        health += amount;
        if(health > maxHealth()){
            health = maxHealth();
        }
        if(health <= 0){
            emitMessage("@Name die$.", true);
            messageNotify("Press enter to exit...");
            for(int i = 0; i < inventory.items().length; i++){
                Item item = inventory.get(i);
                if(item == null){
                    continue;
                }
                item.setLevel(level);
                item.setPos(pos);
                inventory.remove(i);
                displayEvent(new DisplayEvent(item, pos, item.pos(), EventType.DROPPED));
            }
            for(Item item : equipped.values()){
                item.setLevel(level);
                item.setPos(pos);
                displayEvent(new DisplayEvent(item, pos, item.pos(), EventType.DROPPED));
            }
            equipped.clear();
            displayEvent(new DisplayEvent(this, pos, null, EventType.DIE));
            setLevel(null);
        }
    }
    public void modifyHealth(int amount, String deathMessage){
        this.deathMessage = deathMessage;
        modifyHealth(amount);
    }

    private Stat maxMana;
    public int maxMana(){
        return maxMana.getValue();
    }
    private int mana;
    public int mana(){
        return mana;
    }
    public void modifyMana(int amount){
        mana += amount;
        if(mana > maxMana()){
            mana = maxMana();
        }
        if(mana < 0){
            mana = 0;
        }
    }
    private Stat maxStamina;
    public int maxStamina(){
        return maxStamina.getValue();
    }
    private int stamina;
    public int stamina(){
        return stamina;
    }
    public void modifyStamina(int amount){
        stamina += amount;
        if(stamina > maxStamina()){
            stamina = maxStamina();
        }
        if(stamina < 0){
            stamina = 0;
        }
    }

    private Attack attack;
    public Attack attack(){
        return equipped.containsKey(EquipSlot.WEAPON) ? equipped.get(EquipSlot.WEAPON).equippableComponent.attack : attack;
    }

    private int speed;
    public int speed(){
        return speed;
    }

    private Inventory inventory;
    public Inventory inventory(){
        return inventory;
    }

    private EnumMap<EquipSlot, Item> equipped;
    public EnumMap<EquipSlot, Item> equippedItems(){
        return equipped;
    }
    public boolean equip(Item item){
        if(item.equippableComponent == null){
            return false;
        }
        inventory.remove(item);
        unequip(item.equippableComponent.slot);
        equipped.put(item.equippableComponent.slot, item);
        return true;
    }
    public boolean unequip(Item item){
        if(item.equippableComponent == null || item != equipped.get(item.equippableComponent.slot)){
            return false;
        }
        unequip(item.equippableComponent.slot);
        return true;
    }
    public boolean unequip(EquipSlot slot){
        if(equipped.get(slot) != null){
            emitMessage("@Name unequip$ ~.", true, equipped.get(slot).articleName(false));
            inventory.add(equipped.get(slot));
            equipped.remove(slot);
            return true;
        }
        return false;
    }

    private boolean isAlly;
    public boolean isAlly(){
        return isAlly;
    }
    public void setAlly(boolean isAlly){
        this.isAlly = isAlly;
    }

    // The last level the player was on. Stored to display after the player dies.
    private Level lastNonNullLevel;
    public Level lastNonNullLevel(){
        return lastNonNullLevel;
    }
    private double visionRadius;
    private double[][] visible;
    private boolean visibleDirty;
    public boolean canSee(int x, int y){
        return DEBUG_ALL_SEEING || Radius.CIRCLE.radius(pos.x, pos.y, x, y) <= visionRadius && getVisible()[x][y] > 0.0;
    }
    public double[][] getVisible(){
        if(visibleDirty){
            // Regenerate visibility map.
            if(visible == null || visible.length != lastNonNullLevel.width() || visible[0].length != lastNonNullLevel.height()){
                visible = new double[lastNonNullLevel.width()][lastNonNullLevel.height()];
            }
            if(DEBUG_ALL_SEEING){
                for(int i = 0; i < visible.length; i++){
                    Arrays.fill(visible[i], 1.0);
                }
            }
            else{
                try {
                    FOV.reuseFOVSymmetrical(lastNonNullLevel.sightResistances(), visible, pos.x, pos.y, visionRadius, Radius.CIRCLE);       
                } catch (ArrayIndexOutOfBoundsException aioobe) {
                    System.out.println(pos);
                    throw aioobe;
                }
            }
            visibleDirty = false;
            updateRemembered();
        }
        return visible;
    }

    public void setVisibleDirty(){
        visibleDirty = true;
    }

    private Memory memory;
    public Tile memTileAt(int x, int y){
        return memory.tileAt(lastNonNullLevel, x, y);
    }
    public Map<Entity, Coord> memEntities(){
        return memory.getEntities(lastNonNullLevel);
    }
    public boolean tracksEntities(){
        return memory.tracksEntities();
    }
    public void memAddEntity(Entity other){
        if(canSee(other.pos.x, other.pos.y)){
            memory.addEntity(other);
        }
    }
    public void memAddEntity(Entity other, Coord pos){
        memory.addEntity(other, pos);
    }

    private void updateRemembered(){
        // Update tiles.
        if(memory.tracksTiles()){
            for(Coord point : new GreasedRegion(getVisible(), 0.0).not()){
                if(memory.setTile(lastNonNullLevel, point.x, point.y, lastNonNullLevel.tile(point))){
                    //initMap();
                }
            }
        }
        if(memory.tracksEntities()){
            // Add new entities, and update the position of any already existing.
            //TODO: Fails to work sometimes? (probably when enemy is not moving and you step in range and then don't move)
            for(Entity entity : lastNonNullLevel.entities()){
                memAddEntity(entity);
            }
            // Remove entities that aren't where they were remembered to be.
            Set<Entity> toRemove = new HashSet<>();
            for(Map.Entry<Entity, Coord> entity : memEntities().entrySet()){
                if(canSee(entity.getValue().x, entity.getValue().y) &&
                    !(entity.getKey().level() == lastNonNullLevel && canSee(entity.getKey().pos().x, entity.getKey().pos().y))
                ){
                    toRemove.add(entity.getKey());
                }
            }
            for(Entity entity : toRemove){
                memory.removeEntity(lastNonNullLevel, entity);
            }
        }
    }

    private DijkstraMap dijkstraMap;
    private static final int MOVE_THROUGH_CREATURE_COST = 7;
    private void initMap(){
        // Used for when the map changes.
        if(memory.tracksTiles()){
            //TODO: use memories
            dijkstraMap.initialize(level.movementResistances(DijkstraMap.WALL, DijkstraMap.FLOOR));
        }
        else{
            dijkstraMap.initialize(level.movementResistances(DijkstraMap.WALL, DijkstraMap.FLOOR));
        };
    }
    public void rescanMap(){
        dijkstraMap.clearGoals();
        dijkstraMap.resetMap();

        dijkstraMap.initializeCost((char[][]) null); //hey squidlib devs did you know that this works?
        Set<Coord> allies = new HashSet<>();
        for(Creature creature : level.creatures()){
            if(this.isAlly == creature.isAlly){
                allies.add(creature.pos);
            }
        }
        for(Coord pos : allies){
            dijkstraMap.setCost(pos, MOVE_THROUGH_CREATURE_COST);
        }

        dijkstraMap.setGoal(pos);
        dijkstraMap.scan();
    }

    public List<Coord> pathTo(Coord destination){
        List<Coord> result = dijkstraMap.findPathPreScanned(destination);
        if(!result.isEmpty()){
            result.remove(0);
        }
        return result;
    }

    private List<String> messages;
    public void setMessages(List<String> messages){
        this.messages = messages;
    }
    public boolean receivesMessages(){
        return messages != null;
    }
    public boolean message(String message){
        if(messages != null){
            messages.add(message);
            return true;
        }
        return false;
    }
    public boolean messageError(String message){
        return message("[#FF8080]" + message.replace("[WHITE]", "[#FF8080]") + "[WHITE]");
    }
    public boolean messageNotify(String message){
        return message("[#FFFF80]" + message.replace("[WHITE]", "[#FFFF80]") + "[WHITE]");
    }

    // The actors that should be added/removed to levels this goes to.
    private List<Actor> linkedActors;
    private RegenTimer regenTimer;
    public void addLinkedActor(Actor actor){
        this.linkedActors.add(actor);
        level.addActor(actor);
    }
    public void removeLinkedActor(Actor actor){
        this.linkedActors.remove(actor);
        level.removeActor(actor);
    }

    private List<Skill> skills;
    public List<Skill> skills(){
        return skills;
    }
    public void addSkill(Skill skill){
        skills.add(skill);
        addLinkedActor(skill);
    }

    public Creature(Level level, char glyph, Color color, String name, int maxHealth, int attack, double speed, Memory memory){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.health = maxHealth;
        this.maxHealth = new Stat(maxHealth);
        this.mana = 20;
        this.maxMana = new Stat(20);
        this.stamina = 20;
        this.maxStamina = new Stat(20);
        this.attack = new Attack(attack);
        this.speed = (int) speed;
        this.cooldown = 0;
        this.inventory = new Inventory(20);
        this.equipped = new EnumMap<>(EquipSlot.class);
        this.memory = memory;
        this.messages = null;
        this.visionRadius = 10;
        this.visibleDirty = true;
        this.linkedActors = new ArrayList<>();
        this.regenTimer = new RegenTimer(10, this);
        this.linkedActors.add(regenTimer);
        this.skills = new ArrayList<>();
        this.dijkstraMap = new DijkstraMap(RNGVars.aiRNG);
        this.dijkstraMap.measurement = Measurement.EUCLIDEAN;
        setLevel(level);
    }

    @Override
    public void setPos(Coord pos){
        setVisibleDirty();
        this.pos = pos;
        Feature feature = level.featureAt(pos);
        if(feature != null && feature.winComponent != null){
            emitMessage("@Name win$.", true);
            messageNotify("Press enter to exit...");
        }
    }
    @Override
    public void setLevel(Level level){
        setVisibleDirty();
        if(this.level != null){
            this.level.removeCreature(this);
            for(Actor actor : linkedActors){
                this.level.removeActor(actor);
            }
        }
        this.level = level;
        if(level != null){
            lastNonNullLevel = level;
            level.addCreature(this);
            for(Actor actor : linkedActors){
                level.addActor(actor);
            }
            initMap();
        } 
    }

    @Override
    public Action getAction(){
        if(nextAction != null){
            Action action = nextAction;
            nextAction = null;
            return action;
        }
        Action action = ai.getAction(this);
        return action;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Creature other = (Creature) obj;
        if (id != other.id)
            return false;
        return true;
    }
}