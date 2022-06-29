package cc.polyfrost.oneconfig.internal;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.ShutdownEvent;
import cc.polyfrost.oneconfig.gui.OneConfigGui;
import cc.polyfrost.oneconfig.internal.command.OneConfigCommand;
import cc.polyfrost.oneconfig.internal.config.OneConfigConfig;
import cc.polyfrost.oneconfig.internal.config.Preferences;
import cc.polyfrost.oneconfig.internal.config.core.ConfigCore;
import cc.polyfrost.oneconfig.internal.config.core.KeyBindHandler;
import cc.polyfrost.oneconfig.internal.gui.BlurHandler;
import cc.polyfrost.oneconfig.internal.hud.HudCore;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * The main class of OneConfig.
 */
@net.minecraftforge.fml.common.Mod(modid = "@ID@", name = "@NAME@", version = "@VER@")
public class OneConfig {

    public OneConfig() {
        EventManager.INSTANCE.register(this);
    }

    public static final File oneConfigDir = new File("./OneConfig");
    public static final Logger LOGGER = LogManager.getLogger("@NAME@");
    public static OneConfigConfig config;
    public static Preferences preferences;
    private static boolean preLaunched = false;
    private static boolean initialized = false;
    private static boolean isObfuscated = true;

    /**
     * Called before mods are loaded.
     * <p><b>SHOULD NOT BE CALLED!</b></p>
     */
    public static void preLaunch() {
        if (preLaunched) return;
        try {
            Class.forName("net.minecraft.world.World");
            LOGGER.warn("OneConfig is NOT obfuscated!");
            isObfuscated = false;
        } catch (Exception ignored) {
        }
        oneConfigDir.mkdirs();
        new File(oneConfigDir, "profiles").mkdirs();
        config = new OneConfigConfig();
        preferences = new Preferences();
        preLaunched = true;
    }

    /**
     * Called after mods are loaded.
     * <p><b>SHOULD NOT BE CALLED!</b></p>
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init() {
        if (initialized) return;
        GuiUtils.getDeltaTime(); // called to make sure static initializer is called
        BlurHandler.INSTANCE.load();
        CommandManager.INSTANCE.registerCommand(OneConfigCommand.class);
        EventManager.INSTANCE.register(new HudCore());
        EventManager.INSTANCE.register(HypixelUtils.INSTANCE);
        EventManager.INSTANCE.register(KeyBindHandler.INSTANCE);
        ConfigCore.sortMods();
        initialized = true;
    }

    /** Returns weather this is an obfuscated environment, using a check for obfuscated name of net.minecraft.world.World.class.
     * @return true if this is an obfuscated environment, which is normal for Minecraft or false if not. */
    public static boolean isObfuscated() {
        return isObfuscated;
    }

    @Subscribe
    private void onShutdown(ShutdownEvent event) {
        ConfigCore.saveAll();
    }
}
