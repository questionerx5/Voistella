package com.questionerx5.voistella.component.item;

import com.questionerx5.voistella.Attack;
import com.questionerx5.voistella.EquipSlot;

public class EquippableComponent implements ItemComponent{
    public final Attack attack;
    public final EquipSlot slot;
    
    public EquippableComponent(Attack attack, EquipSlot slot){
        this.attack = attack;
        this.slot = slot;
    }
}