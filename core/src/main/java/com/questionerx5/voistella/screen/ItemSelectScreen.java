package com.questionerx5.voistella.screen;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.action.PickupAction;

public class ItemSelectScreen extends SelectScreen{
    private Item[] items;
    private Creature player;

    public ItemSelectScreen(BaseScreen superScreen, Item[] items, Creature player){
        String[] choices = new String[items.length];
        for(int i = 0; i < items.length; i++){
            if(items[i] != null){
                choices[i] = items[i].glyph() + " " + items[i].name();
            }
        }
        super(superScreen, choices);
        this.items = items;
        this.player = player;
    }

    @Override
    protected BaseScreen action(int index){
        if(items[index].inWorld()){
            player.setNextAction(new PickupAction(items[index]));
            return superScreen;  // TODO: Could also just go to ItemActionScreen
        }
        return new ItemActionScreen(superScreen, items[index], player);
    }
}
