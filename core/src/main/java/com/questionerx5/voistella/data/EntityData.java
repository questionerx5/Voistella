package com.questionerx5.voistella.data;

import squidpony.squidmath.Coord;
import com.badlogic.gdx.graphics.Color;
import com.questionerx5.voistella.Entity;
import com.questionerx5.voistella.Level;

public abstract class EntityData<T extends Entity>{
    protected char glyph;
    protected Color color;
    protected String name;
    protected boolean unique;

    public abstract T create(Level level, Coord pos);
}