package com.questionerx5.voistella;

import com.github.tommyettinger.ds.ObjectDeque;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.ObjectSet;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.grid.Region;
import com.questionerx5.voistella.Tile.TileFlag;
import com.questionerx5.voistella.action.Action;

public class Level{
    private Tile[][] tiles;
    private int width, height;
    public int width(){
        return width;
    }
    public int height(){
        return height;
    }
    private String name;
    public String name(){
        return name;
    }

    // Only contains Actors that take turns.
    private ObjectDeque<Actor> actors;
    // Actors that should be deleted.
    private ObjectSet<Actor> toRemove;
    public void addActor(Actor actor){
        if(actor.takesTurns()){
            actors.offer(actor);
        }
    }
    public void removeActor(Actor actor){
        toRemove.add(actor);
    }
    private ObjectList<Entity> entities;
    public ObjectList<Entity> entities(){
        return entities;
    }
    // Not necessary outside this class.
    private void addEntity(Entity entity){
        addActor(entity);
        entities.add(entity);
    }
    private void removeEntity(Entity entity){
        removeActor(entity);
        entities.remove(entity);
        for(Creature creature : creatures){
            creature.memRemoveEntity(entity);
        }
    }

    private ObjectList<Creature> creatures;
    public Creature creatureAt(Coord point){
        for(Creature c : creatures){
            if(c.pos().equals(point)){
                return c;
            }
        }
        return null;
    }
    public ObjectList<Creature> creatures(){
        return creatures;
    }
    public void addCreature(Creature creature){
        addEntity(creature);
        creatures.add(creature);
    }
    public void removeCreature(Creature creature){
        removeEntity(creature);
        creatures.remove(creature);
    }

    private ObjectList<Feature> features;
    public Feature featureAt(Coord point){
        for(Feature f : features){
            if(f.pos().equals(point)){
                return f;
            }
        }
        return null;
    }
    public ObjectList<Feature> features(){
        return features;
    }
    public void addFeature(Feature feature){
        addEntity(feature);
        features.add(feature);
    }
    public void removeFeature(Feature feature){
        removeEntity(feature);
        features.remove(feature);
    }

    private ObjectList<Item> items;
    public ObjectList<Item> itemsAt(Coord point){
        ObjectList<Item> itemsHere = new ObjectList<>();
        for(Item i : items){
            if(i.pos().equals(point)){
                itemsHere.add(i);
            }
        }
        return itemsHere;
    }
    public ObjectList<Item> items(){
        return items;
    }
    public void addItem(Item item){
        addEntity(item);
        items.add(item);
    }
    public void removeItem(Item item){
        removeEntity(item);
        items.remove(item);
    }

    // TODO: is this better or worse than running multiple instanceofs?
    // ...maybe polymorphism is better.
    public void add(Actor actor){
        // Check the Actor's type.
        Class<? extends Actor> c = actor.getClass();
        // Use the corresponding add* function.
        if(c == Creature.class){
            addCreature((Creature) actor);
        }
        else if(c == Feature.class){
            addFeature((Feature) actor);
        }
        else if(c == Item.class){
            addItem((Item) actor);
        }
        else if(actor instanceof Entity){
            addEntity((Entity) actor);
        }
        else{
            addActor(actor);
        }
    }
    public void remove(Actor actor){
        // Check the Actor's type.
        Class<? extends Actor> c = actor.getClass();
        // Use the corresponding remove* function.
        if(c == Creature.class){
            removeCreature((Creature) actor);
        }
        else if(c == Feature.class){
            removeFeature((Feature) actor);
        }
        else if(c == Item.class){
            removeItem((Item) actor);
        }
        else if(actor instanceof Entity){
            removeEntity((Entity) actor);
        }
        else{
            removeActor(actor);
        }
    }

    private ObjectList<DisplayEvent> events;
    public void setEvents(ObjectList<DisplayEvent> events){
        this.events = events;
    }
    public void addEvent(DisplayEvent event){
        events.add(event);
    }

    public Level(Tile[][] tiles, String name){
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.name = name;
        this.actors = new ObjectDeque<>();
        this.toRemove = new ObjectSet<>();
        this.entities = new ObjectList<>();
        this.creatures = new ObjectList<>();
        this.features = new ObjectList<>();
        this.items = new ObjectList<>();
    }

    public boolean inBounds(int x, int y){
        return !(x < 0 || x >= width || y < 0 || y >= height);
    }
    public Tile tile(int x, int y){
        return inBounds(x, y) ? tiles[x][y] : Tile.BOUNDS;
    }
    public Tile tile(Coord p){
        return inBounds(p.x, p.y) ? tiles[p.x][p.y] : Tile.BOUNDS;
    }

    public Region openRegions(){
        return Tile.unflaggedRegions(tiles, TileFlag.BLOCKING);
    }
    public Region entityRegions(){
        Coord[] occupied = new Coord[entities.size()];
        for(int i = 0; i < entities.size(); i++){
            occupied[i] = entities.get(i).pos();
        }
        return new Region(width, height, occupied);
    }
    public Coord openPoint(){
        return openRegions().andNot(entityRegions()).singleRandom(RNGVars.genRNG);
    }
    //TODO: could this be cached?
    public float[][] sightResistances(){
        float[][] result = new float[this.width][this.height];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                result[x][y] = tiles[x][y].testFlag(TileFlag.BLOCKS_LOS) ? 1 : 0;
            }
        }
        return result;
    }
    public float[][] movementResistances(float blocking, float nonBlocking){
        return Tile.movementResistances(tiles, blocking, nonBlocking);
    }

    // Returns false if the game is waiting on the player.
    public boolean process(){
        if(creatures.isEmpty()){
            return true;
        }
        Actor nextActor = actors.peek();
        if(toRemove.contains(nextActor)){
            toRemove.remove(nextActor);
            actors.poll();
            return true;
        }
        if(!nextActor.turnReady()){
            nextActor.tick();
        }
        while(nextActor.turnReady()){
            Action action = nextActor.getAction();
            if(action == null){
                return false;
            }
            boolean success = action.recursivePerform(nextActor);
            if(!success){
                // If failure, return without advancing turns.
                return false;
            }
            // Advance turn.
            nextActor.resetCooldown();
        }
        actors.poll();
        actors.offer(nextActor);
        return true;
    }
}