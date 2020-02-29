package uneiConfiguration;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.io.TempDir;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import me.unei.configuration.UneiConfiguration;
import me.unei.configuration.api.Configurations;
import me.unei.configuration.api.IFlatConfiguration;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.Configurations.ConfigurationType;
import me.unei.configuration.api.IConfiguration;

import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("load/save/reload and tree state tests")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class TreeSavedTest {
	private static final String FILE_NAME = "config_test";

	@TempDir
	static File tempDir;

	private List<IConfiguration> configs;

	private static final Logger logger = LoggerFactory.getLogger(SavedTest.class);

	public void logFine(String message) {
		logger.trace(() -> message);
	}

	public void logInfo(String message) {
		logger.info(() -> message);
	}

	@BeforeAll
	public void configurationTests() {
		UneiConfiguration.tryInstanciate();

		assertNotNull(tempDir);

		logFine(String.format("Temporary path: %s", tempDir.getAbsolutePath()));
	}

	@AfterAll
	public void tearDown() {
		if (this.configs != null) {

			for (IFlatConfiguration config : this.configs) {

				if (config instanceof Closeable) {

					try {
						((Closeable) config).close();
					} catch (IOException e) {
						logger.warn(e, () -> "Failed to close a configuration resource:");
					}
				}
			}
			this.configs.clear();
		}
	}

	@Test
	@Order(1)
	public void firstLoad() {
		logFine("Loading configurations...");

		this.configs = new ArrayList<IConfiguration>();
		this.configs.add((IConfiguration) Configurations.newConfig(ConfigurationType.NBT, tempDir, FILE_NAME, null));
		this.configs.add((IConfiguration) Configurations.newConfig(ConfigurationType.YAML, tempDir, FILE_NAME, null));
		this.configs.add((IConfiguration) Configurations.newConfig(ConfigurationType.JSON, tempDir, FILE_NAME, null));
		this.configs.add((IConfiguration) Configurations.newConfig(ConfigurationType.Binary, tempDir, FILE_NAME, null));
		this.configs.add((IConfiguration) Configurations.newConfig(ConfigurationType.SQLite, tempDir, FILE_NAME,
				"testtable_normal"));

		assertNotNull(configs);
		assertEquals(5, configs.size());
		assertFalse(configs.contains(null));
	}

	@Test
	@Order(2)
	public void assertKeepsTree() {
		for (IConfiguration config : this.configs) {
			IConfiguration child1 = config.getSubSection("ChildSub");

			child1.setBoolean("TrueValue", true);
			child1.set("IntValue", null);

			assertTrue(config.getBoolean("ChildSub.TrueValue"), "Failed to keep track of childrens");
			assertFalse(child1.contains("IntValue"));

			IConfiguration child2 = config.getSubSection("ChildSub");

			assertTrue(child2.getBoolean("TrueValue"));

			child2.setInteger("IntValue", 42);

			assertEquals(42, child1.getInteger("IntValue"));

			child2.remove("IntValue");
			child1.remove("TrueValue");

			assertFalse(config.contains("ChildSub.TrueValue"));
			assertFalse(config.contains("ChildSub.IntValue"));
		}
	}

	@Test
	@Order(3)
	public void assertReloadKeepsTree() {
		for (IConfiguration config : this.configs) {
			IConfiguration child1 = config.getSubSection("ChildSub");

			child1.setBoolean("TrueValue", true);
			child1.set("IntValue", null);

			assertTrue(config.getBoolean("ChildSub.TrueValue"), "Failed to keep track of childrens");
			assertFalse(child1.contains("IntValue"));

			child1.save();

			assertTrue(config.getBoolean("ChildSub.TrueValue"), "Failed to keep track of childrens");
			assertFalse(child1.contains("IntValue"));

			try {
				config.reload();
			} catch (FileFormatException ffe) {
				fail(ffe);
			}

			assertTrue(config.getBoolean("ChildSub.TrueValue"), "Failed to save configuration");
			assertFalse(child1.contains("IntValue"));

			child1.setInteger("IntValue", 76294);

			assertEquals(76294, config.getInteger("ChildSub.IntValue"), "Failed to keep tracks of child nodes");

			config.remove("ChildSub.TrueValue");

			assertFalse(child1.contains("TrueValue"), "Failed to keep tracks of parent");
		}
	}
}
