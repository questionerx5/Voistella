package com.questionerx5.voistella.screen;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Skill;

public class SkillSelectScreen extends SelectScreen{
    private Creature player;
    private String[] choices;

    public SkillSelectScreen(Screen superScreen, Creature player) {
        super(superScreen, 40);
        this.player = player;
        this.choices = new String[player.skills().size()];
        for(int i = 0; i < choices.length; i++){
            choices[i] = player.skills().get(i).name() + " (" + player.skills().get(i).costs(player) + ")";
        }
    }

    @Override
    protected String[] choices(){
        return choices;
    }

    @Override
    protected String caption(){
        return "Skills";
    }

    @Override
    public Screen action(int choice) {
        Skill skill = player.skills().get(choice);
        if(!skill.castable(player)){
            player.messageError(skill.noncastableReason(player));
            return superScreen;
        }
        return new SkillTargetScreen(superScreen, player.pos().x, player.pos().y, player, player.skills().get(choice));
    }
}
