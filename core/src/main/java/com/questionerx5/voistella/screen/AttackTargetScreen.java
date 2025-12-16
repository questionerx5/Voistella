package com.questionerx5.voistella.screen;
import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.grid.Radius;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Tile.TileFlag;
import com.questionerx5.voistella.action.AttackAction;

public class AttackTargetScreen extends TargetScreen{
    private Creature player;
    private boolean lineBlocked;

    protected AttackTargetScreen(BaseScreen superScreen, Creature player, int offX, int offY){
        this.player = player;
        ObjectList<Coord> targetableCreatures = new ObjectList<>();
        for(Creature c : player.level().creatures()){
            if(Radius.CIRCLE.radius(player.pos(), c.pos()) <= player.attack().range
               && player.canSee(c.pos())
               && player.isAlly() != c.isAlly()){
                targetableCreatures.add(c.pos());
            }
        }
        super(superScreen, player.pos(), targetableCreatures.isEmpty() ? player.pos() : targetableCreatures.get(0), offX, offY);
        tabTargets = targetableCreatures;
        highlightArea = getCircle(player.pos(), player.attack().range);
    }

    @Override
    protected BaseScreen select(Coord target){
        if(Radius.CIRCLE.radius(player.pos(), target) > player.attack().range){
            player.messageError("That's out of range.");
            return superScreen;
        }
        if(lineBlocked){
            player.messageError("There's no clear line there.");
            return superScreen;
        }
        Creature targetCreature = player.level().creatureAt(target);
        if(targetCreature == null){
            player.messageError("There's nothing to attack there.");
            return superScreen;
        }
        player.setNextAction(new AttackAction(targetCreature));
        return superScreen;
    }

    @Override
    protected boolean valid(Coord target){
        lineBlocked = false;
        for(Coord point : line){
            if(player.level().tile(point).testFlag(TileFlag.BLOCKING)){
                lineBlocked = true;
                break;
            }
        }
        return !lineBlocked && Radius.CIRCLE.radius(player.pos(), target) <= player.attack().range;
    }
}
