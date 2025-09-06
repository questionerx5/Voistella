package com.questionerx5.voistella;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.yellowstonegames.press.SquidInput;
import com.questionerx5.voistella.screen.BaseScreen;
import com.questionerx5.voistella.screen.StartScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game{
    public static final int ROWS = 24;
    public static final int COLUMNS = 80;
    public static final float ROW_SCALE = 2f;
    public static final float COLUMN_SCALE = 1f;

    private static final int FONT_QUALITY = 72;
    private static final int FONT_SIZE = 50;

    public SpriteBatch batch;
    public FitViewport viewport;
    public BitmapFont font;
    public Texture pixel;
    public SquidInput input;

    private BaseScreen screen;

    @Override
    public void create(){
        batch = new SpriteBatch();
        viewport = new FitViewport(COLUMNS * COLUMN_SCALE, ROWS * ROW_SCALE);
        pixel = new Texture("one singular pixel.png");

        input = new SquidInput((key, alt, ctrl, shift) -> {
            BaseScreen nextScreen = screen.handle(key, alt, ctrl, shift);
            if(nextScreen != null){
                this.screen = nextScreen;
                setScreen(nextScreen);
            }
        });
        Gdx.input.setInputProcessor(input);

        // Setup font.
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("IosevkaFixed-Extended.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = FONT_QUALITY;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / ROW_SCALE / Gdx.graphics.getHeight() / FONT_QUALITY * FONT_SIZE);
        font.setFixedWidthGlyphs(parameter.characters);

        this.screen = new StartScreen(this);
        setScreen(this.screen);
    }

    @Override
    public void render(){
        super.render();

        if(input.hasNext()){
            input.drain();
        }
    }

    @Override
    public void dispose(){
        batch.dispose();
        font.dispose();
    }

    /** Fills a grid square with a solid color. */
    public void fillCell(float x, float y, Color color){
        fillRect(x, y, 1, 1, color);
    }
    /** Fills a grid square with a solid color.
     * @param color The color, as a packed float (that could be passed into e.g. {@link com.badlogic.gdx.graphics.g2d.Batch#setPackedColor(float)})
     */
    public void fillCell(float x, float y, float color){
        fillRect(x, y, 1, 1, color);
    }
    /** Fills a rectangle with a solid color. */
    public void fillRect(float x, float y, float width, float height, Color color){
        batch.setColor(color);
        batch.draw(pixel, x * COLUMN_SCALE, (ROWS - y - height) * ROW_SCALE, width * COLUMN_SCALE, height * ROW_SCALE);
    }
    /**
     * Fills a rectangle with a solid color.
     * @param color The color, as a packed float (that could be passed into e.g. {@link com.badlogic.gdx.graphics.g2d.Batch#setPackedColor(float)})
     */
    public void fillRect(float x, float y, float width, float height, float color){
        batch.setPackedColor(color);
        batch.draw(pixel, x * COLUMN_SCALE, (ROWS - y - height) * ROW_SCALE, width * COLUMN_SCALE, height * ROW_SCALE);
    }

    /** Draws a character at the given grid coordinates; does not set color. */
    private void drawChar(int x, int y, char glyph){
        font.draw(batch, Character.toString(glyph),
            (x + 0.5f) * COLUMN_SCALE - font.getSpaceXadvance() / 2,
            (ROWS - y - 1 + 0.5f) * ROW_SCALE + font.getCapHeight() / 2);
    }

    /** Draws the given character at the given grid coordinates, in white. */
    public void drawText(int x, int y, char glyph){
        drawText(x, y, glyph, Color.WHITE);
    }
    /** Draws the given character at the given grid coordinates.
     * @param color The color, as a packed float (that could be passed into e.g. {@link com.badlogic.gdx.graphics.g2d.Batch#setPackedColor(float)})
     */
    public void drawText(int x, int y, char glyph, float color){
        Color.abgr8888ToColor(font.getColor(), color);
        drawChar(x, y, glyph);
    }
    /** Draws the given character at the given grid coordinates. */
    public void drawText(int x, int y, char glyph, Color color){
        font.setColor(color);
        drawChar(x, y, glyph);
    }
    
    /** Draws the given String starting at the given grid coordinates, in white. */
    public void drawText(int x, int y, String string){
        drawText(x, y, string, Color.WHITE);
    }
    /** Draws the given String starting at the given grid coordinates. */
    //TODO: Allow color formatting
    public void drawText(int x, int y, String string, Color color){
        int lineNum = 0;
        font.setColor(color);
        for(String line : string.split("\\n")){
            for(int i = 0; i < line.length(); i++){
                drawChar(x + i, y + lineNum, line.charAt(i));
            }
            lineNum++;
        }
    }
}