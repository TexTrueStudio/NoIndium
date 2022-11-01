package me.luligabi.noindium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
//import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fakefabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
//import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

@OnlyIn(Dist.CLIENT)
public class NoIndium {

    public NoIndium() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitializeClient);

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

    }


    //@Override
    public void onInitializeClient(final FMLClientSetupEvent event) {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            if((HAS_RUBIDIUM && CONFIG.showRubidiumScreen) || (HAS_OPTIFINE && CONFIG.showOptifineScreen)) {
                client.setScreen(new NoIndiumWarningScreen());
            }
        });
    }


    private static ModConfig createConfig() {
        ModConfig finalConfig;
        LOGGER.info("Trying to read config file...");
        try {
            if(CONFIG_FILE.createNewFile()) {
                LOGGER.info("No config file found, creating a new one...");
                writeConfig(GSON.toJson(JsonParser.parseString(GSON.toJson(new ModConfig()))));
                finalConfig = new ModConfig();
                LOGGER.info("Successfully created default config file.");
            } else {
                LOGGER.info("A config file was found, loading it..");
                finalConfig = GSON.fromJson(new String(Files.readAllBytes(CONFIG_FILE.toPath())), ModConfig.class);
                if(finalConfig == null) {
                    throw new NullPointerException("The config file was empty.");
                } else {
                    LOGGER.info("Successfully loaded config file.");
                }
            }
        } catch(Exception e) {
            LOGGER.error("There was an error creating/loading the config file!", e);
            finalConfig = new ModConfig();
            LOGGER.warn("Defaulting to original config.");
        }
        return finalConfig;
    }

    public static void saveConfig(ModConfig modConfig) {
        try {
            writeConfig(GSON.toJson(JsonParser.parseString(GSON.toJson(modConfig))));
            LOGGER.info("Saved new config file.");
        } catch(Exception e) {
            LOGGER.error("There was an error saving the config file!", e);
        }
    }

    private static void writeConfig(String json) {
        try(PrintWriter printWriter = new PrintWriter(CONFIG_FILE)) {
            printWriter.write(json);
            printWriter.flush();
        } catch(IOException e) {
            LOGGER.error("Failed to write config file", e);
        }
    }

    public static final boolean HAS_RUBIDIUM;
    public static final boolean HAS_OPTIFINE;

    public static final Logger LOGGER;

    private static final Gson GSON;
    private static final File CONFIG_FILE;
    public static final ModConfig CONFIG;


    static {
        HAS_RUBIDIUM = (ModList.get().isLoaded("rubidium")) && !ModList.get().isLoaded("sodiumextra");
        HAS_OPTIFINE = ModList.get().isLoaded("optifine");

        LOGGER = LoggerFactory.getLogger("Opt 'NOT' Fine");

        GSON = new GsonBuilder().setPrettyPrinting().create();
        CONFIG_FILE = new File(String.format("%s%sOpt-NOT-Fine.json", FMLPaths.CONFIGDIR.get(), File.separator));
        CONFIG = createConfig();
    }

}