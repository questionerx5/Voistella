package com.questionerx5.voistella.screen;

import squidpony.squidmath.Coord;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Creature;

public class LookScreen extends TargetScreen{
    private Creature player;
    public LookScreen(Screen superScreen, int offX, int offY, Creature player){
        super(superScreen, player.pos(), offX, offY);
        this.player = player;
    }

    @Override
    protected Screen select(int x, int y){
        List<Entity> entities = new ArrayList<>();
        for(Map.Entry<Entity, Coord> memory : player.memEntities().entrySet()){
            if(memory.getValue() == Coord.get(x, y)){
                entities.add(memory.getKey());
            }
        }
        if(entities.isEmpty()){
            player.messageError("There's nothing there.");
            return superScreen;
        }
        if(entities.size() == 1){
            return EntityInfoSelectScreen.entityScreen(superScreen, entities.get(0)); 
        }
        return new EntityInfoSelectScreen(superScreen, entities.toArray(new Entity[0]));
    }

    @Override
    protected boolean acceptable(int x, int y){
        return true;
    }
    @Override
    protected boolean lineObstructed(List<Coord> line){
        return false;
    }
}
