package com.questionerx5.voistella.component.item;

import com.questionerx5.voistella.Effect;

public class PotionComponent implements ItemComponent{
    public final Effect effect;
    
    public PotionComponent(Effect effect){
        this.effect = effect;
    }
}