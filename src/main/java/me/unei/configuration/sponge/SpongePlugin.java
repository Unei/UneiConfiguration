package me.unei.configuration.sponge;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bstats.sponge.Metrics2;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import me.unei.configuration.plugin.IBasicPlugin;
import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;

@Plugin(id = "uneiconfiguration", name = "UneiConfiguration")
public class SpongePlugin implements IPlugin {
	private final UneiConfiguration plugin;

	private final Logger logger;

	@Inject
	private Metrics2 metrics;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDirectory;

	private final Map<String, Integer> dependers = new HashMap<>();
	private static final Integer ONE = Integer.valueOf(1);

	@Inject
	public SpongePlugin(org.slf4j.Logger logger) {
		this.logger = new MyLogger(logger);

		plugin = new UneiConfiguration(this);
	}

	private static class MyLogger extends Logger {
		private final org.slf4j.Logger realLogger;

		protected MyLogger(org.slf4j.Logger logger) {
			super(logger.getName(), Logger.getAnonymousLogger().getResourceBundleName());
			this.realLogger = logger;
		}

		@Override
		public boolean isLoggable(Level level) {
			if (level == Level.CONFIG || level == Level.ALL) {
				return this.realLogger.isDebugEnabled();
			}

			if (level == Level.FINEST || level == Level.FINER || level == Level.FINE) {
				return this.realLogger.isTraceEnabled();
			}

			if (level == Level.INFO) {
				return this.realLogger.isInfoEnabled();
			}

			if (level == Level.WARNING) {
				return this.realLogger.isWarnEnabled();
			}

			if (level == Level.SEVERE) {
				return this.realLogger.isErrorEnabled();
			}
			return false;
		}

		@Override
		public void log(LogRecord record) {
			if (record.getLevel() == Level.CONFIG || record.getLevel() == Level.ALL) {

				if (record.getThrown() != null) {
					this.realLogger.debug(record.getMessage(), record.getThrown());
				} else {
					this.realLogger.debug(record.getMessage(), record.getParameters());
				}
			}

			if (record.getLevel() == Level.FINEST || record.getLevel() == Level.FINER
					|| record.getLevel() == Level.FINE) {

				if (record.getThrown() != null) {
					this.realLogger.trace(record.getMessage(), record.getThrown());
				} else {
					this.realLogger.trace(record.getMessage(), record.getParameters());
				}
			}

			if (record.getLevel() == Level.INFO) {

				if (record.getThrown() != null) {
					this.realLogger.info(record.getMessage(), record.getThrown());
				} else {
					this.realLogger.info(record.getMessage(), record.getParameters());
				}
			}

			if (record.getLevel() == Level.WARNING) {

				if (record.getThrown() != null) {
					this.realLogger.warn(record.getMessage(), record.getThrown());
				} else {
					this.realLogger.warn(record.getMessage(), record.getParameters());
				}
			}

			if (record.getLevel() == Level.SEVERE) {

				if (record.getThrown() != null) {
					this.realLogger.error(record.getMessage(), record.getThrown());
				} else {
					this.realLogger.error(record.getMessage(), record.getParameters());
				}
			}
		}
	}

	/*
	 * private static <T> T getLogger(final org.slf4j.Logger logger, final Class<T>
	 * loggerClass) {
	 * try {
	 * logger.debug("Logger class: " + logger.getClass().getName());
	 * final Class<? extends org.slf4j.Logger> loggerIntrospected =
	 * logger.getClass();
	 * final Field fields[] = loggerIntrospected.getDeclaredFields();
	 * for (int i = 0; i < fields.length; i++) {
	 * final String fieldName = fields[i].getName();
	 * if (fieldName.equals("logger")) {
	 * fields[i].setAccessible(true);
	 * return loggerClass.cast(fields[i].get(logger));
	 * }
	 * }
	 * } catch (final Exception e) {
	 * logger.error(e.getMessage());
	 * }
	 * return null;
	 * }
	 */

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
		metrics.addCustomChart(new Metrics2.AdvancedPie("usingPlugins", () -> {
			Map<String, Integer> result = SpongePlugin.this.dependers;
			return result;
		}));

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
	public void registerStatsPlName(String name) {
		this.dependers.put(name, SpongePlugin.ONE);
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
