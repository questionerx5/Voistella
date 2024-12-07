package com.questionerx5.voistella.screen;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Tile;
import com.questionerx5.voistella.action.AttackAction;

import squidpony.squidmath.Coord;
import squidpony.squidgrid.Radius;
import java.util.HashSet;
import java.util.List;

public class AttackTargetScreen extends TargetScreen{
    private Creature player;
    
    public AttackTargetScreen(Screen superScreen, int offX, int offY, Creature player){
        super(superScreen, player.pos(), offX, offY);
        this.player = player;
        HashSet<Coord> quadrant = new HashSet<>();
        double range = player.attack().range;
        int max = (int) range;
        for(int x = 0; x <= (int) range; x++){
            while(Radius.CIRCLE.radius(x, max) > range){
                max--;
            }
            for(int y = 0; y <= max; y++){
                quadrant.add(Coord.get(x, y));
            }
        }
        area = new HashSet<>();
        for(Coord point : quadrant){
            area.add(player.pos().translate(point.x, point.y));
            if(point.x != 0){
                area.add(player.pos().translate(-point.x, point.y));
                if(point.y != 0){
                    area.add(player.pos().translate(-point.x, -point.y));
                }
            }
            if(point.y != 0){
                area.add(player.pos().translate(point.x, -point.y));
            }
        }
    }

    @Override
    protected Screen select(int x, int y){
        if(!acceptable(x, y)){
            return this;
        }
        Creature target = player.level().creatureAt(Coord.get(x, y));
        if(target == null){
            player.messageError("There's nothing there to attack.");
        }
        else{
            player.setNextAction(new AttackAction(target));
        }
        return superScreen;
    }

    @Override
    protected boolean acceptable(int x, int y){
        return true;
        //return player.level().creatureAt(Coord.get(x, y)) != null;
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
