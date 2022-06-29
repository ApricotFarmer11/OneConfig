package cc.polyfrost.oneconfig.hud;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.Stage;
import cc.polyfrost.oneconfig.events.event.TickEvent;
import cc.polyfrost.oneconfig.internal.hud.HudCore;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.libs.universal.UMinecraft;
import cc.polyfrost.oneconfig.renderer.RenderManager;

import java.util.ArrayList;
import java.util.List;

public abstract class TextHud extends Hud {
    protected transient List<String> lines = new ArrayList<>();
    private transient int width;
    private transient int height;

    @Color(
            name = "Text Color"
    )
    public OneColor color = new OneColor(255, 255, 255);

    @Dropdown(
            name = "Text Type",
            options = {"No Shadow", "Shadow", "Full Shadow"}
    )
    public int textType = 0;

    public TextHud(boolean enabled, int x, int y) {
        super(enabled, x, y);
        EventManager.INSTANCE.register(new TickHandler());
    }

    public TextHud(boolean enabled) {
        this(enabled, 0, 0);
    }

    /**
     * This function is called every tick
     *
     * @param lines The current lines of the hud
     */
    protected abstract void getLines(List<String> lines);

    /**
     * This function is called every frame
     *
     * @param lines The current lines of the hud
     */
    protected void getLinesFrequent(List<String> lines) {

    }

    /**
     * This function is called every tick in the move GUI
     *
     * @param lines The current lines of the hud
     */
    protected void getExampleLines(List<String> lines) {
        getLines(lines);
    }

    /**
     * This function is called every frame in the move GUI
     *
     * @param lines The current lines of the hud
     */
    protected void getExampleLinesFrequent(List<String> lines) {
        getLinesFrequent(lines);
    }

    @Override
    public void draw(int x, int y, float scale) {
        if (!HudCore.editing) getLinesFrequent(lines);
        else getExampleLinesFrequent(lines);
        if (lines == null) return;

        int textY = y;
        width = 0;
        for (String line : lines) {
            RenderManager.drawScaledString(line, x, textY, color.getRGB(), RenderManager.TextType.toType(textType), scale);
            width = Math.max(width, UMinecraft.getFontRenderer().getStringWidth(line));
            textY += 12 * scale;
        }
        height = (int) ((textY - y) / scale - 3);
    }

    @Override
    public int getWidth(float scale) {
        return (int) (width * scale);
    }

    @Override
    public int getHeight(float scale) {
        return (int) (height * scale);
    }

    private class TickHandler {
        @Subscribe
        private void onTick(TickEvent event) {
            if (event.stage != Stage.START) return;
            if (!HudCore.editing) getLines(lines);
            else getExampleLines(lines);
        }
    }
}