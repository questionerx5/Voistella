package com.questionerx5.voistella.screen;

import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.action.*;
import com.questionerx5.voistella.Creature;

public class ItemActionScreen extends SelectScreen{
    private Item item;
    private Creature player;
    private boolean inWorld, equipped;

    public ItemActionScreen(Screen superScreen, Item item, Creature player){
        super(superScreen);
        this.item = item;
        this.player = player;
        this.inWorld = item.inWorld();
        this.equipped = player.equippedItems().containsValue(item);
    }

    @Override
    protected String[] choices(){
        String[] choices = new String[4];

        choices[0] = "info";

        if(inWorld){
            choices[1] = "pick up";
        }
        else{
            choices[1] = "drop";
        }

        if(equipped){
            choices[2] = "unwield";
        }
        else if(item.equippableComponent != null){
            choices[2] = "wield";
        }

        if(item.potionComponent != null){
            choices[3] = "drink";
        }

        return choices;
    }

    @Override
    public String caption(){
        return item.glyph() + " " + item.name();
    }

    @Override
    public Screen action(int choice){
        Action action = null;
        boolean pickup = false, unequip = false;
        switch(choice){
            case 0:
                return new ItemInfoScreen(item, superScreen);
            case 1:
                if(inWorld){
                    action = new PickupAction(item);
                    pickup = true;
                }
                else{
                    action = new DropAction(item);
                }
                break;
            case 2:
                if(equipped){
                    action = new UnequipAction(item);
                    unequip = true;
                }
                else{
                    action = new EquipAction(item);
                }
                break;
            case 3:
                action = new ConsumeAction(item);
                break;
        }
        if(inWorld && !pickup){
            action = new RedirectAction(new PickupAction(item), action);
        }
        if(equipped && !unequip){
            action = new RedirectAction(new UnequipAction(item), action);
        }

        player.setNextAction(action);
        return superScreen;
    }
}