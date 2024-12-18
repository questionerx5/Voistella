package com.questionerx5.voistella.worldgen;

import com.questionerx5.voistella.ActorFactory;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.World;
import com.questionerx5.voistella.data.CreatureData;
import squidpony.squidmath.Coord;

public class WorldConstructor{
    private WorldConstructor(){}

    public static World generate(CreatureData player){
        return linkLevelBuilders(
            new LevelBuilder(30, 30)
            .addStartRoom(new FixedRoomSupplier("##########\n#........#\n#.##..#..#\n#........#\n#........#\n#..#..#..#\n#..####..#\n#........#\n##########"))
            .addRooms(100, new BoxRoomSupplier(2, 2, 4, 4), new BoxRoomSupplier(2, 2, 4, 4), new BoxRoomSupplier(6, 6, 8, 8))
            .addLoops()
            .placeEntitiesNonBlocking(ActorFactory.item("health potion"), 10)
            .placeEntitiesNonBlocking(ActorFactory.item("regen potion"), 10)
            .placePlayer(player)
            .placeEntities(ActorFactory.creature("fungus"), 5)
            .placeEntities(ActorFactory.creature("bat"), 5)
            .placeEntities(ActorFactory.creature("zombie"), 1)
            .randomStairs(),

            new LevelBuilder(25, 25)
            .addStartRoom(new BoxRoomSupplier(8, 8))
            .addRooms(50, new BoxRoomSupplier(3, 3, 7, 7))
            .addLoops()
            .placeEntities(ActorFactory.creature("fungus"), 50)
            .placeEntities(ActorFactory.creatureNewInstance("rogue").setAlly(true), 1)
            //.placeEntities(ActorFactory.creatureNewInstance("goblin").setEquipment(ActorFactory.item("sword")), 1)
            .randomStairs(),

            new LevelBuilder(30, 30)
            .addStartRoom(
                new FixedRoomSupplier("#############\n#...........#\n#.####.####.#\n#.#.......#.#\n#.#.#####.#.#\n#.#.#...#.#.#\n#.#.......#.#\n#.#.#...#.#.#\n#.#.#####.#.#\n#.#.......#.#\n#.####.####.#\n#...........#\n#############")
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(5, 5)))
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(5, 6)))
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(5, 7)))
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(6, 5)))
                .addEntity(new PlacedEntity(ActorFactory.creatureNewInstance("goblin").setEquipment(ActorFactory.item("bow")), Coord.get(6, 6)))
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(6, 7)))
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(7, 5)))
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(7, 6)))
                .addEntity(new PlacedEntity(ActorFactory.creature("zombie"), Coord.get(7, 7)))
            )
            .addRooms(150, new BoxRoomSupplier(2, 2, 4, 4))
            .addLoops()
            .placeEntities(ActorFactory.creature("zombie"), 15)
            .randomStairs(),

            new LevelBuilder(50, 50)
            .addStartRoom(new CaveRoomSupplier(48, 48))
            .addRooms(50, new CaveRoomSupplier(15, 15))
            .addLoops()
            .placeEntitiesNonBlocking(ActorFactory.item("junk"), 50)
            .placeEntities(ActorFactory.creature("bat"), 20)
            .placeEntities(ActorFactory.creature("rogue"), 5)
            .placeEntities(ActorFactory.creature("stealer"), 25) //TODO: Determine source of lag with large numbers of entities
            .placeEntities(ActorFactory.creatureNewInstance("goblin").setEquipment(ActorFactory.item("bow")), 5)
            .randomStairs()
        );
    }

    private static Creature player;
    public static Creature player(){
        return player;
    }
    
    public static World linkLevelBuilders(LevelBuilder... builders){
        Level[] levels = new Level[builders.length];
        for(int i = 0; i < builders.length; i++){
            levels[i] = builders[i].build("Level " + i);
        }
        player = builders[0].player();
        for(int i = 0; i < builders.length; i++){
            if(i != 0){
                for(Coord stairsUp : builders[i].stairsUp()){
                    ActorFactory.stairsUp(levels[i], stairsUp, levels[i - 1], builders[i - 1].stairsDown().get(0));
                }
            }
            if(i != builders.length - 1){
                for(Coord stairsDown : builders[i].stairsDown()){
                    ActorFactory.stairsDown(levels[i], stairsDown, levels[i + 1], builders[i + 1].stairsUp().get(0));
                }
            }
        }
        return new World(levels);
    }
}