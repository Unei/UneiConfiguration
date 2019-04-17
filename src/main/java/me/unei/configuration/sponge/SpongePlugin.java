package me.unei.configuration.sponge;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import me.unei.configuration.plugin.IBasicPlugin;
import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;

@Plugin(id = "uneiconfiguration", name = "UneiConfiguration", version = "Sponge-1.0", authors = { "JësFot", "au2001" })
public class SpongePlugin implements IPlugin
{
    private final UneiConfiguration plugin;

    private final Logger logger;
    
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDirectory;
    
    @Inject
    public SpongePlugin(org.slf4j.Logger logger)
    {
    	this.logger = getLogger(logger, Logger.class);
    	
    	plugin = new UneiConfiguration(this);
    }
    
    private static <T> T getLogger(final org.slf4j.Logger logger, final Class<T> loggerClass) {
        try {
            final Class<? extends org.slf4j.Logger> loggerIntrospected = logger.getClass();
            final Field fields[] = loggerIntrospected.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                final String fieldName = fields[i].getName();
                if (fieldName.equals("logger")) {
                    fields[i].setAccessible(true);
                    return loggerClass.cast(fields[i].get(logger));
                }
            }
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
    

    @Listener
    public void onEnable(GamePreInitializationEvent event) {
    	this.onLoad();

    	this.onEnable();
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        this.onDisable();
    }

    @Override
    public void onLoad() {
        plugin.onLoad();
    }

    @Override
    public void onEnable() {
        plugin.onEnable();
    }

    @Override
    public void onDisable() {
        plugin.onDisable();
    }

	@Override
	public File getDataFolder() {
		return this.configDirectory.toFile();
	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}

	@Override
	public InputStream getResource(String path) {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

	@Override
	public IBasicPlugin.Type getType() {
		return IBasicPlugin.Type.SPONGE;
	}
}