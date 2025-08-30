package com.questionerx5.voistella;

import com.badlogic.gdx.graphics.Color;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.text.Messaging;
import com.github.yellowstonegames.text.Pronoun;

public abstract class Entity extends Actor{
    protected Coord pos;
    public Coord pos(){
        return pos;
    }
    public void setPos(Coord pos){
        this.pos = pos;
        if(level == null){
            return;
        }
        updateCreatureSights();
    }
    protected void updateCreatureSights(){
        // Update sights of all creatures.
        for(Creature creature : level.creatures()){
            creature.memAddEntity(this);
        }
    }

    protected char glyph;
    public char glyph(){
        return glyph;
    }

    protected Color color;
    public Color color(){
        return color;
    }

    protected String name;
    protected boolean unique = false;
    public String name(){
        return name;
    }
    private boolean indefiniteArticleDetermined = false;
    private boolean usesAn = false;
    private static final String ARTICLES = "aeiouAEIOU";
    public void setUsesAn(boolean usesAn){
        this.usesAn = usesAn;
        this.indefiniteArticleDetermined = true;
    }
    public String articleName(boolean definite){
        if(!indefiniteArticleDetermined){
            usesAn = ARTICLES.contains(Character.toString(name.charAt(0)));
        }
        return (unique ? "" : (definite ? "the " : (usesAn ? "an " : "a "))) + name;
    }

    protected Level level;
    public Level level(){
        return level;
    }
    public boolean inWorld(){
        return level != null;
    }
    // Should be overridden to use something more specific than level.add/level.remove.
    public void setLevel(Level level, Coord pos){
        if(this.level != null){
            this.level.remove(this);
        }
        this.level = level;
        setPos(pos); //TODO: is this the correct spot?
        if(level != null){
            level.add(this);
        } 
    }

    public Entity(){
        setId();
    }

    public void displayEvent(DisplayEvent event){
        level.addEvent(event);
    } 

    public void emitMessage(String message, boolean definite, Creature target, boolean targetDefinite, String... extra){
        for(Creature creature : level.creatures()){
            // Don't message creatures that don't care about messages.
            if(!creature.receivesMessages()){
                continue;
            }
            boolean canSeeActor = creature == this || creature.canSee(this.pos.x, this.pos.y);
            boolean canSeeTarget = creature == target || creature.canSee(target.pos.x, target.pos.y);
            // Don't message the viewer if they can't see the actor or the target.
            if(!canSeeActor && !canSeeTarget){
                continue;
            }
            creature.message(Messaging.transform(
                message,
                canSeeActor ? this.articleName(definite) : "something",
                creature == this ? Pronoun.SECOND_PERSON_SINGULAR : Pronoun.NO_GENDER,
                canSeeTarget ? target.articleName(targetDefinite): "something",
                creature == target ? Pronoun.SECOND_PERSON_SINGULAR : Pronoun.NO_GENDER,
                extra
            ));
        }
    }
    public void emitMessage(String message, boolean definite, String... extra){
        for(Creature creature : level.creatures()){
            // Don't message creatures that don't care about messages.
            if(!creature.receivesMessages()){
                continue;
            }
            if(creature != this && !creature.canSee(this.pos.x, this.pos.y)){
                continue;
            }
            //apparently there's no Messaging.transform() without a target and with an extra. squidsquad devs,
            String text = Messaging.transform(
                message,
                this.articleName(definite),
                creature == this ? Pronoun.SECOND_PERSON_SINGULAR : Pronoun.NO_GENDER
            );
            if(extra != null && extra.length > 0){
                for (int i = 0; i < extra.length; i++){
                    text = text.replaceFirst("~", extra[i]);
                }
            }
            creature.message(text);
        }
    }
}