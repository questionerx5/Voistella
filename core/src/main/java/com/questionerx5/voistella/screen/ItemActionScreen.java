package com.questionerx5.voistella.screen;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Item;
import com.questionerx5.voistella.action.*;

public class ItemActionScreen extends SelectScreen{
    private Item item;
    private Creature player;

    private static String[] getChoices(Item item, Creature player){
        String[] choices = new String[4];

        choices[0] = "info";

        if(item.inWorld()){
            choices[1] = "pick up";
        }
        else{
            choices[1] = "drop";
        }

        if(player.equippedItems().containsValue(item)){
            choices[2] = "unequip";
        }
        else if(item.equippableComponent != null){
            choices[2] = "equip";
        }

        if(item.potionComponent != null){
            choices[3] = "drink";
        }

        return choices;
    }

    public ItemActionScreen(BaseScreen superScreen, Item item, Creature player){
        super(superScreen, getChoices(item, player));
        this.item = item;
        this.player = player;
    }

    @Override
    protected BaseScreen action(int index){
        String choice = getChoices(item, player)[index];
        if("info".equals(choice)){
            return new ItemInfoScreen(superScreen, item);
        }
        Action action = switch(choice){
            case "pick up" -> new PickupAction(item);
            case "drop" -> new DropAction(item);
            case "unequip" -> new UnequipAction(item);
            case "equip" -> new EquipAction(item);
            case "drink" -> new ConsumeAction(item);
            default -> throw new RuntimeException("Action for \"" + choice + "\" not found");
        };
        if(item.inWorld() && !"pick up".equals(choice)){
            action = new RedirectAction(new PickupAction(item), action);
        }
        if(player.equippedItems().containsValue(item) && !"unequip".equals(choice)){
            action = new RedirectAction(new UnequipAction(item), action);
        }
        player.setNextAction(action);
        return superScreen;
    }
}
