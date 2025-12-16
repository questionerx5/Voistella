package com.questionerx5.voistella.screen;

import java.util.Collection;

import com.github.tommyettinger.ds.ObjectDeque;
import com.github.tommyettinger.ds.ObjectList;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.Palette;
import com.github.yellowstonegames.core.DescriptiveColorRgb;
import com.github.yellowstonegames.grid.BresenhamLine;

public abstract class TargetScreen extends BaseScreen{ // TODO: caption
    protected BaseScreen superScreen;
    private Coord origin, target;
    private int offX, offY;
    protected Collection<Coord> highlightArea;
    private boolean lastValid;
    protected ObjectDeque<Coord> line;
    protected ObjectList<Coord> tabTargets;
    protected int tabTargetIndex;

    private static final int VALID_COLOR = DescriptiveColorRgb.setAlpha(Palette.MAGENTA, 0.4f);
    private static final int INVALID_COLOR = DescriptiveColorRgb.setAlpha(Palette.RED, 0.4f);
    private static final int HIGHLIGHT_COLOR = DescriptiveColorRgb.setAlpha(Palette.WHITE, 0.125f);

    protected static final Collection<Coord> getCircle(Coord origin, double radius){
        Collection<Coord> inside = new ObjectList<>();
        int edgeX = (int) Math.ceil(radius);
        for(int y = 0; y <= radius; y++){
            while(edgeX * edgeX + y * y > radius * radius){
                edgeX--;
            }
            for(int x = 0; x <= edgeX; x++){
                inside.add(origin.translate(x, y));
                if(x != 0){
                    inside.add(origin.translate(-x, y));
                }
                if(y != 0){
                    inside.add(origin.translate(x, -y));
                }
                if(x != 0 && y != 0){
                    inside.add(origin.translate(-x, -y));
                }
            }
        }
        return inside;
    }

    protected TargetScreen(BaseScreen superScreen, Coord origin, int offX, int offY){
        this(superScreen, origin, origin, offX, offY);
    }
    protected TargetScreen(BaseScreen superScreen, Coord origin, Coord target, int offX, int offY){
        super(superScreen.game);
        this.superScreen = superScreen;
        this.origin = origin;
        this.target = target;
        this.offX = offX;
        this.offY = offY;
        line = new ObjectDeque<>();
        tabTargetIndex = 0; // assume target is set to tabTargets.get(0)
        targetMoved();
    }

    protected abstract BaseScreen select(Coord target);
    protected abstract boolean valid(Coord target);

    @Override
    public void render(float delta){
        superScreen.render(delta);

        if(highlightArea != null){
            for(Coord point : highlightArea){
                if(point.x != target.x || point.y != target.y){
                    game.fillCell(point.x + offX, point.y + offY, HIGHLIGHT_COLOR);
                }
            }
        }
        for(Coord point : line){
            if(point.x != target.x || point.y != target.y){
                game.fillCell(point.x + offX, point.y + offY, HIGHLIGHT_COLOR);
            }
        }
        game.fillCell(target.x + offX, target.y + offY, lastValid ? VALID_COLOR : INVALID_COLOR);
    }

    @Override
    public BaseScreen handle(char key, boolean alt, boolean ctrl, boolean shift){
        switch(key){
            case SquidInput.LEFT_ARROW: { // TODO: determine what happens to tabTargetIndex if target is moved manually
                target = target.translate(-1, 0);
                targetMoved();
                break;
            }
            case SquidInput.RIGHT_ARROW: {
                target = target.translate(1, 0);
                targetMoved();
                break;
            }
            case SquidInput.UP_ARROW: {
                target = target.translate(0, -1);
                targetMoved();
                break;
            }
            case SquidInput.DOWN_ARROW: {
                target = target.translate(0, 1);
                targetMoved();
                break;
            }
            case SquidInput.UP_LEFT_ARROW: {
                target = target.translate(-1, -1);
                targetMoved();
                break;
            }
            case SquidInput.UP_RIGHT_ARROW: {
                target = target.translate(1, -1);
                targetMoved();
                break;
            }
            case SquidInput.DOWN_LEFT_ARROW: {
                target = target.translate(-1, 1);
                targetMoved();
                break;
            }
            case SquidInput.DOWN_RIGHT_ARROW: {
                target = target.translate(1, 1);
                targetMoved();
                break;
            }
            case SquidInput.TAB: {
                if(tabTargets != null && !tabTargets.isEmpty()){
                    tabTargetIndex++;
                    tabTargetIndex %= tabTargets.size();
                    target = tabTargets.get(tabTargetIndex);
                    targetMoved();
                }
                break;
            }
            case SquidInput.ESCAPE: {
                return superScreen;
            }
            case SquidInput.ENTER: {
                return select(target);
            }
        }
        return null;
    }

    private void targetMoved(){
        BresenhamLine.line(origin.x, origin.y, target.x, target.y, line); // TODO: think about rounding and walls
        lastValid = valid(target);
    }
}
