package com.questionerx5.voistella.screen;

import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Item;

public class EntityInfoSelectScreen extends SelectScreen{
    private String[] choices;
    private Entity[] entities;
    
    public EntityInfoSelectScreen(Screen superScreen, Entity[] entities){
        super(superScreen, 30);
        this.entities = entities;
        choices = new String[entities.length];
        for(int i = 0; i < entities.length; i++){
            choices[i] = entities[i].glyph() + " " + entities[i].name();
        }
    }
    @Override
    protected String[] choices(){
        return choices;
    }
    @Override
    protected String caption(){
        return "Things here";
    }
    @Override
    public Screen action(int choice){
        Entity entity = entities[choice];
        if(entity instanceof Creature){
            return new CreatureInfoScreen((Creature) entity, superScreen);
        }
        if(entity instanceof Item){
            return new ItemInfoScreen((Item) entity, superScreen);
        }
        return superScreen;
    }
}
