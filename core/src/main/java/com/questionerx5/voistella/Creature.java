//TODO: New library is untested
package com.questionerx5.voistella;

import java.util.Arrays;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.github.tommyettinger.ds.EnumMap;
import com.github.tommyettinger.ds.ObjectDeque;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.ObjectObjectMap;
import com.github.tommyettinger.ds.ObjectSet;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.grid.FOV;
import com.github.yellowstonegames.grid.Measurement;
import com.github.yellowstonegames.grid.Radius;
import com.github.yellowstonegames.grid.Region;
import com.github.yellowstonegames.path.DijkstraMap;
import com.questionerx5.voistella.DisplayEvent.EventType;
import com.questionerx5.voistella.Tile.TileFlag;
import com.questionerx5.voistella.action.Action;
import com.github.yellowstonegames.grid.CoordSet;

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
        return maxHealth.getValueAsInt();
    }
    public Stat maxHealthStat(){
        return maxHealth;
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
                item.setLevel(level, pos);
                inventory.remove(i);
                displayEvent(new DisplayEvent(item, pos, item.pos(), EventType.DROPPED));
            }
            for(Item item : equipped.values()){
                item.setLevel(level, pos);
                displayEvent(new DisplayEvent(item, pos, item.pos(), EventType.DROPPED));
            }
            equipped.clear();
            displayEvent(new DisplayEvent(this, pos, null, EventType.DIE));
            setLevel(null, pos);
        }
    }
    public void modifyHealth(int amount, String deathMessage){
        this.deathMessage = deathMessage;
        modifyHealth(amount);
    }

    private RegenTimer regenTimer;
    public Stat regenStat(){
        return regenTimer.speedStat();
    }

    private Stat maxMana;
    public int maxMana(){
        return maxMana.getValueAsInt();
    }
    public Stat maxManaStat(){
        return maxMana;
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
        return maxStamina.getValueAsInt();
    }
    public Stat maxStaminaStat(){
        return maxStamina;
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

    private Stat speed;
    public int speed(){
        return speed.getValueAsInt();
    }
    public Stat speedStat(){
        return speed;
    }

    private Inventory inventory;
    public Inventory inventory(){
        return inventory;
    }

    private EnumMap<Item> equipped;
    public EnumMap<Item> equippedItems(){
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
    private float visionRadius;
    private float[][] visible;
    // If true, this creature needs to update what's visible.
    private boolean visibleDirty;
    public boolean canSee(int x, int y){
        return DEBUG_ALL_SEEING || Radius.CIRCLE.radius(pos.x, pos.y, x, y) <= visionRadius && getVisible()[x][y] > 0;
    }
    public float[][] getVisible(){
        if(visibleDirty){
            // Regenerate visibility map.
            if(visible == null || visible.length != lastNonNullLevel.width() || visible[0].length != lastNonNullLevel.height()){
                visible = new float[lastNonNullLevel.width()][lastNonNullLevel.height()];
            }
            if(DEBUG_ALL_SEEING){
                for(int i = 0; i < visible.length; i++){
                    Arrays.fill(visible[i], 1);
                }
            }
            else{
                FOV.reuseFOVSymmetrical(lastNonNullLevel.sightResistances(), visible, pos.x, pos.y, visionRadius, Radius.CIRCLE);       
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
    public ObjectObjectMap<Entity, Coord> memEntities(){
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
    public void memRemoveEntity(Entity other){
        if(canSee(other.pos.x, other.pos.y)){
            memory.removeEntity(other.level, other);
        }
    }

    //TODO: don't create object every time?
    private void updateRemembered(){
        // Update tiles.
        if(memory.tracksTiles()){
            for(Coord point : new Region(getVisible(), 0).not()){
                if(memory.setTile(lastNonNullLevel, point.x, point.y, lastNonNullLevel.tile(point))){
                    //initMap();
                }
            }
        }
        if(memory.tracksEntities()){
            // Add new entities. Also unintentionally update the position of any already existing.
            for(Entity entity : lastNonNullLevel.entities()){
                memAddEntity(entity);
            }
            // Remove entities that aren't where they were remembered to be.
            ObjectSet<Entity> toRemove = new ObjectSet<>();
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
        }
    }

    public ObjectDeque<Coord> pathTo(Coord... destinations){
        dijkstraMap.reset();
        CoordSet allies = new CoordSet();
        for(Creature creature : level.creatures()){
            if(this.isAlly == creature.isAlly){
                allies.add(creature.pos);
            }
        }
        for(Coord pos : allies){
            dijkstraMap.setCost(pos, MOVE_THROUGH_CREATURE_COST);
        }
        ObjectDeque<Coord> result = dijkstraMap.findPath(1, null, null, pos, destinations);
        for(Coord pos : allies){
            dijkstraMap.setCost(pos, 1);
        }
        return result;
    }
    public ObjectDeque<Coord> pathTo(ObjectList<Coord> destinations){
        return pathTo(destinations.toArray(new Coord[0]));
    }
    public ObjectDeque<Coord> pathAway(ObjectList<Coord> fears){
        CoordSet creatures = new CoordSet();
        for(Creature creature : level.creatures()){
            if(creature != this){
                creatures.add(creature.pos);
            }
        }
        // Find distance to fears.
        dijkstraMap.reset();
        dijkstraMap.setGoals(fears);
        dijkstraMap.scan(); //TODO: Don't scan the entire level
        double furthest = -1;
        CoordSet goals = new CoordSet();
        // Find nearby places to run to.
        Region nearby = new Region(pos, level.width(), level.height());
        for(int i = 0; i < 4; i++){
            nearby.expand8way();
            CoordSet toRemove = new CoordSet();
            //TODO: use a Region for part of this?
            for(Coord point : nearby){
                if(level.tile(point).testFlag(TileFlag.BLOCKING) || creatures.contains(point)){
                    toRemove.add(point);
                }
            }
            nearby.removeAll(toRemove);
        }
        // Determine the nearby place that is furthest from any fear.
        for(Coord point : nearby){
            double dist = dijkstraMap.gradientMap[point.x][point.y];
            if(dist >= DijkstraMap.FLOOR){
                continue;
            }
            if(dist > furthest){
                goals.clear();
                furthest = dist;
            }
            if(dist >= furthest){
                goals.add(point);
            }
        }
        if(goals.isEmpty()){
            return null;
        }
        dijkstraMap.reset();
        return dijkstraMap.findPath(1, creatures, null, pos, goals.toArray(new Coord[0]));
    }

    private ObjectList<String> messages;
    public void setMessages(ObjectList<String> messages){
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
    //TODO: use [] instead of this replace stuff
    public boolean messageError(String message){
        return message("[#FF8080]" + message.replace("[WHITE]", "[#FF8080]") + "[WHITE]");
    }
    public boolean messageNotify(String message){
        return message("[#FFFF80]" + message.replace("[WHITE]", "[#FFFF80]") + "[WHITE]");
    }

    // The actors that should be added/removed to levels this goes to.
    private ObjectList<Actor> linkedActors;
    public void addLinkedActor(Actor actor){
        this.linkedActors.add(actor);
        level.addActor(actor);
    }
    public void removeLinkedActor(Actor actor){
        this.linkedActors.remove(actor);
        level.removeActor(actor);
    }

    private ObjectList<Skill> skills;
    public ObjectList<Skill> skills(){
        return skills;
    }
    public void addSkill(Skill skill){
        skills.add(skill);
        addLinkedActor(skill);
    }

    public Creature(Level level, Coord pos, char glyph, Color color, String name, int maxHealth, int attack, double speed, Memory memory){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.health = maxHealth;
        this.maxHealth = new Stat(maxHealth, x -> modifyHealth(0, "Killed by max health reduction."));
        this.mana = 20;
        this.maxMana = new Stat(20, x -> modifyMana(0));
        this.stamina = 20;
        this.maxStamina = new Stat(20, x -> modifyStamina(0));
        this.attack = new Attack(attack);
        this.speed = new Stat(speed);
        this.cooldown = 0;
        this.inventory = new Inventory(20);
        this.equipped = new EnumMap<>(EquipSlot.class);
        this.memory = memory;
        this.messages = null;
        this.visionRadius = 10;
        this.visibleDirty = true;
        this.linkedActors = new ObjectList<>();
        this.regenTimer = new RegenTimer(10, this);
        this.linkedActors.add(regenTimer);
        this.skills = new ObjectList<>();
        this.dijkstraMap = new DijkstraMap();
        this.dijkstraMap.measurement = Measurement.EUCLIDEAN;
        this.dijkstraMap.setBlockingRequirement(0);
        setLevel(level, pos);
    }

    @Override
    public void setPos(Coord pos){
        setVisibleDirty();
        this.pos = pos;
        if(level == null){
            return;
        }
        updateCreatureSights();
        Feature feature = level.featureAt(pos);
        if(feature != null && feature.winComponent != null){
            emitMessage("@Name win$.", true);
            messageNotify("Press enter to exit...");
        }
    }
    @Override
    public void setLevel(Level level, Coord pos){
        setVisibleDirty();
        if(this.level != null){
            this.level.removeCreature(this);
            for(Actor actor : linkedActors){
                this.level.removeActor(actor);
            }
        }
        this.level = level;
        setPos(pos);
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