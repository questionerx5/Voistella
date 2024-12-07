package com.questionerx5.voistella.screen;

import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.action.PickupAction;

public class ItemSelectScreen extends SelectScreen{
    private String[] choices; 
    private Item[] items;
    private Creature player;
    private String caption;

    public ItemSelectScreen(Screen superScreen, Item[] items, Creature player, String caption){
        super(superScreen, 25);
        this.items = items;
        this.player = player;
        this.choices = new String[items.length];
        for(int i = 0; i < items.length; i++){
            if(items[i] != null){
                choices[i] = items[i].glyph() + " " + items[i].name();
            }
        }
        this.caption = caption;
    }

    @Override
    protected String[] choices(){
        return choices;
    }

    @Override
    public String caption(){
        return caption;
    }

    @Override
    public Screen action(int choice){
        if(items[choice].inWorld()){
            player.setNextAction(new PickupAction(items[choice]));
            return superScreen;
        }
        return new ItemActionScreen(superScreen, items[choice], player);
    }
}