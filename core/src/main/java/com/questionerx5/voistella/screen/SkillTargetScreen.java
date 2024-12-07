package com.questionerx5.voistella.screen;

import java.util.List;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Skill;
import com.questionerx5.voistella.Tile;
import com.questionerx5.voistella.action.SkillAction;

import squidpony.squidmath.Coord;

public class SkillTargetScreen extends TargetScreen{
    private Creature player;
    private Skill skill;
    public SkillTargetScreen(Screen superScreen, int offX, int offY, Creature player, Skill skill){
        super(superScreen, player.pos(), offX, offY);
        this.player = player;
        this.skill = skill;
    }

    @Override
    protected Screen select(int x, int y){
        if(!acceptable(x, y)){
            return this;
        }
        player.setNextAction(new SkillAction(skill, Coord.get(x, y)));
        return superScreen;
    }

    @Override
    protected boolean acceptable(int x, int y){
        return true;
    }
    @Override
    protected boolean lineObstructed(List<Coord> line){
        for(Coord point : line){
            if(player.level().tile(point).testFlag(Tile.TileFlag.BLOCKING)){
                return true;
            }
        }
        return false;
    }
}
