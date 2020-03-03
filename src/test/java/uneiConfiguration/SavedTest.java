package uneiConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import me.unei.configuration.UneiConfiguration;
import me.unei.configuration.api.Configurations;
import me.unei.configuration.api.Configurations.ConfigurationType;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.IFlatConfiguration;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.exceptions.NoFieldException;

@DisplayName("load/save/reload tests")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class SavedTest {
	private static final String FILE_NAME = "config_test";
	private static final String CLASS_NAME = SavedTest.class.getName();

	@TempDir
	static File tempDir;

	private List<IFlatConfiguration> configs;

	public void logFine(String message) {
		UneiConfiguration.getInstance().getLogger().logp(Level.FINE, CLASS_NAME, "logFine", message);
	}

	public void logInfo(String message) {
		UneiConfiguration.getInstance().getLogger().logp(Level.INFO, CLASS_NAME, "logInfo", message);
	}

	public void logWarn(String message, Throwable t) {
		if (t == null) {
			UneiConfiguration.getInstance().getLogger().logp(Level.WARNING, CLASS_NAME, "logWarn", message);
		} else {
			UneiConfiguration.getInstance().getLogger().logp(Level.WARNING, CLASS_NAME, "logWarn", message, t);
		}
	}

	@BeforeAll
	public void configurationTests() {
		UneiConfiguration.tryInstanciate();

		try {
			File logFolder = new File("logs");

			if (!logFolder.isDirectory()) {
				logFolder.mkdirs();
			}
			UneiConfiguration.getInstance().getLogger()
					.addHandler(new FileHandler("logs/unei.log", 1024 * 1024 * 30, 5, false));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
						logWarn("Failed to close a configuration resource:", e);
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

		loadConfigMap();

		resetConfigs();

		assertFalse(configs.contains(null));
	}

	private void loadConfigMap() {
		if (this.configs != null) {
			this.configs.clear();
			this.configs = null;
		}

		this.configs = new ArrayList<IFlatConfiguration>();
		this.configs.add(Configurations.newConfig(ConfigurationType.NBT, tempDir, FILE_NAME, null));
		this.configs.add(Configurations.newConfig(ConfigurationType.YAML, tempDir, FILE_NAME, null));
		this.configs.add(Configurations.newConfig(ConfigurationType.JSON, tempDir, FILE_NAME, null));
		this.configs.add(Configurations.newConfig(ConfigurationType.Binary, tempDir, FILE_NAME, null));
		this.configs.add(Configurations.newConfig(ConfigurationType.Properties, tempDir, FILE_NAME, null));
		this.configs.add(Configurations.newConfig(ConfigurationType.SQLite, tempDir, FILE_NAME, "testtable_normal"));
		this.configs.add(Configurations.newConfig(ConfigurationType.FlatSQLite, tempDir, FILE_NAME, "testtable_flat"));

		assertNotNull(configs);
		assertEquals(7, configs.size());
	}

	private void closeCloseable() {
		if (this.configs == null)
			return;

		for (IFlatConfiguration config : this.configs) {

			if (config instanceof Closeable) {

				try {
					((Closeable) config).close();
				} catch (IOException e) {
					logWarn("Failed to close a configuration resource:", e);
				}
			}
		}
	}

	private void resetConfigs() {
		logFine("Clearing configurations...");
		if (this.configs == null)
			return;

		closeCloseable();

		for (IFlatConfiguration config : this.configs) {

			if (config.getFile().getFile() != null) {
				// Cannot assert file deletion because the SQLite file appears twice and will
				// cause direct failure.
				/*
				 * assertTrue(config.getFile().getFile().delete(),
				 * "Could not delete old config file");
				 */
				config.getFile().getFile().delete();
			}

			try {
				config.reload();
				logFine("Reloaded config is such: " + config.toString());
			} catch (FileFormatException ffe) {
				logWarn("Could not reload empty resource:", ffe);
				fail("Enable to reload configuration resource properly", ffe);
			}
		}
	}

	/*
	 * private static final java.util.concurrent.atomic.AtomicInteger fileIdx = new
	 * java.util.concurrent.atomic.AtomicInteger(42);
	 * private void saveFolder()
	 * {
	 * me.unei.configuration.FileUtils.copyDirs(SavedTest.tempDir, new
	 * File("./tests" + fileIdx.getAndIncrement()));
	 * }
	 */

	@Test
	@Order(2)
	public void assertWrite() {
		logFine("Writing into configurations...");
		assertNotNull(configs, "Test order is not respected");

		for (IFlatConfiguration config : this.configs) {
			assertEquals(0, config.getKeys().size(), "configuration section must have been empty");
			assertWrite(config);
		}
	}

	@Test
	@Order(3)
	public void saveToFile() {
		logFine("Saving configurations to file...");

		for (IFlatConfiguration config : this.configs) {
			config.save();

			if (config.getFile().getFile() != null) {
				assertTrue(config.getFile().getFile().isFile());
			}
		}
	}

	@Test
	@Order(4)
	public void assertRead() {
		logFine("Reading from cached configurations...");
		assertNotNull(configs, "Test order is not respected");

		for (IFlatConfiguration config : this.configs) {
			assertNotEquals(0, config.getKeys().size(), "configuration section must not have been empty");
			assertRead(config);
		}
	}

	@Test
	@Order(5)
	public void reinitCfg() {
		logFine("Closing configurations");

		for (IFlatConfiguration config : this.configs) {

			if (config instanceof Closeable) {

				try {
					((Closeable) config).close();
				} catch (IOException e) {
					logWarn("Failed to close a configuration resource:", e);
				}
			}
		}
		this.configs.clear();

		loadConfigMap();
	}

	@Test
	@Order(6)
	public void fileassertRead() {
		logFine("Reading from configurations...");
		assertNotNull(configs, "Test order is not respected");

		/* saveFolder(); */

		for (IFlatConfiguration config : this.configs) {
			assertNotEquals(0, config.getKeys().size(),
					"configuration section " + config.getClass().getSimpleName() + " must not have been empty");
			assertRead(config);
		}
	}

	@Test
	@Order(7)
	@DisplayName("RemoveTest - not saving")
	public void removeTestStep1() {
		logFine("Adding and removing certain keys from configurations...");

		for (IFlatConfiguration config : this.configs) {
			assertNotNull(config);
			logFine(String.format("assertRemove - step1 (not saving) started for config %s",
					config.toString().substring(0, config.toString().indexOf("="))));

			setRemoveTestValues(config);

			try {
				config.reload();
			} catch (FileFormatException ffe) {
				fail(ffe);
			}

			checkRemoveTestValues(config, "Failed not to save %s value!");
		}
	}

	@Test
	@Order(8)
	@DisplayName("RemoveTest - set to null")
	public void removeTestStep2() {
		for (IFlatConfiguration config : this.configs) {
			assertNotNull(config);
			logFine(String.format("assertRemove - step2 (set to null) started for config %s",
					config.toString().substring(0, config.toString().indexOf("="))));

			setRemoveTestValues(config);

			config.setString("removeTest.Boolean", null);
			config.setString("removeTest.Byte", null);
			config.setString("removeTest.Double", null);
			config.setString("removeTest.Float", null);
			config.setString("removeTest.Integer", null);
			config.setString("removeTest.Long", null);
			config.setString("removeTest.String", null);

			if (config instanceof IConfiguration) {
				((IConfiguration) config).set("removeTest.Untyped", null);
				((IConfiguration) config).setByteList("removeTest.ByteList", null);
				((IConfiguration) config).setIntegerList("removeTest.IntegerList", null);
				((IConfiguration) config).setLongList("removeTest.LongList", null);
			}

			checkRemoveTestValues(config, "Failed to set to null %s value!");
		}
	}

	@Test
	@Order(9)
	@DisplayName("RemoveTest - removing")
	public void removeTestStep3() {
		for (IFlatConfiguration config : this.configs) {
			assertNotNull(config);
			logFine(String.format("assertRemove - step3 (removing) started for config %s",
					config.toString().substring(0, config.toString().indexOf("="))));

			setRemoveTestValues(config);

			config.remove("removeTest.Boolean");
			config.remove("removeTest.Byte");
			config.remove("removeTest.Double");
			config.remove("removeTest.Float");
			config.remove("removeTest.Integer");
			config.remove("removeTest.Long");
			config.remove("removeTest.String");

			if (config instanceof IConfiguration) {
				config.remove("removeTest.Untyped");
				config.remove("removeTest.ByteList");
				config.remove("removeTest.IntegerList");
				config.remove("removeTest.LongList");
			}

			checkRemoveTestValues(config, "Failed to cached remove %s value!");
		}
	}

	@Test
	@Order(10)
	@DisplayName("RemoveTest - reload not existing")
	public void removeTestStep4() {
		for (IFlatConfiguration config : this.configs) {
			assertNotNull(config);
			logFine(String.format("assertRemove - step4 (removing) started for config %s",
					config.toString().substring(0, config.toString().indexOf("="))));

			setRemoveTestValues(config);

			config.remove("removeTest.Boolean");
			config.remove("removeTest.Byte");
			config.remove("removeTest.Double");
			config.remove("removeTest.Float");
			config.remove("removeTest.Integer");
			config.remove("removeTest.Long");
			config.remove("removeTest.String");

			if (config instanceof IConfiguration) {
				config.remove("removeTest.Untyped");
				config.remove("removeTest.ByteList");
				config.remove("removeTest.IntegerList");
				config.remove("removeTest.LongList");
			}

			checkRemoveTestValues(config, "Failed to cached remove %s value!");

			config.save();

			setRemoveTestValues(config);

			try {
				config.reload();
			} catch (FileFormatException ffe) {
				fail(ffe);
			}

			checkRemoveTestValues(config, "Failed to saved remove %s value!");
		}
	}

	@Test
	@Order(11)
	@DisplayName("RemoveTest - getters returns")
	public void removeTestStep5() {
		for (IFlatConfiguration config : this.configs) {
			assertNotNull(config);
			logFine(String.format("assertRemove - step5 (getters) started for config %s",
					config.toString().substring(0, config.toString().indexOf("="))));

			setRemoveTestValues(config);

			config.remove("removeTest.Boolean");
			config.remove("removeTest.Byte");
			config.remove("removeTest.Double");
			config.remove("removeTest.Float");
			config.remove("removeTest.Integer");
			config.remove("removeTest.Long");
			config.remove("removeTest.String");

			if (config instanceof IConfiguration) {
				config.remove("removeTest.Untyped");
				config.remove("removeTest.ByteList");
				config.remove("removeTest.IntegerList");
				config.remove("removeTest.LongList");
			}

			checkRemoveTestValues(config, "Failed to cached remove %s value!");

			config.save();

			setRemoveTestValues(config);

			try {
				config.reload();
			} catch (FileFormatException ffe) {
				fail(ffe);
			}

			checkCannotGetTestValues(config, "Failed to get null on non-existing %s test value!");
			checkThrowsNoTestValues(config, "Failed to throw exception on non-existing %s test value!");
		}
	}

	private void setRemoveTestValues(IFlatConfiguration config) {
		config.setBoolean("removeTest.Boolean", Boolean.TRUE);
		config.setByte("removeTest.Byte", (byte) 42);
		config.setDouble("removeTest.Double", 42.4242424242424242D);
		config.setFloat("removeTest.Float", 42.4242424242424242F);
		config.setInteger("removeTest.Integer", 42424242);
		config.setLong("removeTest.Long", 424242424242424242L);
		config.setString("removeTest.String", "Hello, World!");

		if (config instanceof IConfiguration) {
			((IConfiguration) config).set("removeTest.Untyped", 42);
			((IConfiguration) config).setByteList("removeTest.ByteList",
					Arrays.asList((byte) 0, (byte) 4, (byte) 2, (byte) 0));
			((IConfiguration) config).setIntegerList("removeTest.IntegerList", Arrays.asList(0, 4, 2, 0));
			((IConfiguration) config).setLongList("removeTest.LongList", Arrays.asList(0L, 4L, 2L, 0L));
		}

		assertTrue(config.contains("removeTest.Boolean"), "Failed to write boolean value!");
		assertTrue(config.contains("removeTest.Byte"), "Failed to write byte value!");
		assertTrue(config.contains("removeTest.Double"), "Failed to write double value!");
		assertTrue(config.contains("removeTest.Float"), "Failed to write float value!");
		assertTrue(config.contains("removeTest.Integer"), "Failed to write integer value!");
		assertTrue(config.contains("removeTest.Long"), "Failed to write long value!");
		assertTrue(config.contains("removeTest.String"), "Failed to write string value!");

		if (config instanceof IConfiguration) {
			assertTrue(config.contains("removeTest.Untyped"), "Failed to write untyped value!");
			assertTrue(config.contains("removeTest.ByteList"), "Failed to write byte list value!");
			assertTrue(config.contains("removeTest.IntegerList"), "Failed to write integer list value!");
			assertTrue(config.contains("removeTest.LongList"), "Failed to write long list value!");
		}
	}

	private void checkRemoveTestValues(IFlatConfiguration config, String msg) {
		assertFalse(config.contains("removeTest.Boolean"), String.format(msg, "boolean"));
		assertFalse(config.contains("removeTest.Byte"), String.format(msg, "byte"));
		assertFalse(config.contains("removeTest.Double"), String.format(msg, "double"));
		assertFalse(config.contains("removeTest.Float"), String.format(msg, "float"));
		assertFalse(config.contains("removeTest.Integer"), String.format(msg, "integer"));
		assertFalse(config.contains("removeTest.Long"), String.format(msg, "long"));
		assertFalse(config.contains("removeTest.String"), String.format(msg, "string"));

		if (config instanceof IConfiguration) {
			assertFalse(config.contains("removeTest.Untyped"), String.format(msg, "untyped"));
			assertFalse(config.contains("removeTest.ByteList"), String.format(msg, "byte list"));
			assertFalse(config.contains("removeTest.IntegerList"), String.format(msg, "integer list"));
			assertFalse(config.contains("removeTest.LongList"), String.format(msg, "long list"));
		}
	}

	private void checkCannotGetTestValues(IFlatConfiguration config, String msg) {
		assertNull(config.getBoolean("removeTest.Boolean"), String.format(msg, "boolean"));
		assertNull(config.getByte("removeTest.Byte"), String.format(msg, "byte"));
		assertNull(config.getDouble("removeTest.Double"), String.format(msg, "double"));
		assertNull(config.getFloat("removeTest.Float"), String.format(msg, "float"));
		assertNull(config.getInteger("removeTest.Integer"), String.format(msg, "integer"));
		assertNull(config.getLong("removeTest.Long"), String.format(msg, "long"));
		assertNull(config.getString("removeTest.String"), String.format(msg, "string"));

		if (config instanceof IConfiguration) {
			assertNull(((IConfiguration) config).get("removeTest.Untyped"), String.format(msg, "untyped"));
			assertNull(((IConfiguration) config).getByteList("removeTest.ByteList"), String.format(msg, "byte list"));
			assertNull(((IConfiguration) config).getIntegerList("removeTest.IntegerList"), String.format(msg, "integer list"));
			assertNull(((IConfiguration) config).getLongList("removeTest.LongList"), String.format(msg, "long list"));
		}
	}

	private void checkThrowsNoTestValues(IFlatConfiguration config, String msg) {
		assertThrows(NoFieldException.class, () -> config.tryGetBoolean("removeTest.Boolean"), String.format(msg, "boolean"));
		assertThrows(NoFieldException.class, () -> config.tryGetByte("removeTest.Byte"), String.format(msg, "byte"));
		assertThrows(NoFieldException.class, () -> config.tryGetDouble("removeTest.Double"), String.format(msg, "double"));
		assertThrows(NoFieldException.class, () -> config.tryGetFloat("removeTest.Float"), String.format(msg, "float"));
		assertThrows(NoFieldException.class, () -> config.tryGetInteger("removeTest.Integer"), String.format(msg, "integer"));
		assertThrows(NoFieldException.class, () -> config.tryGetLong("removeTest.Long"), String.format(msg, "long"));
		assertThrows(NoFieldException.class, () -> config.tryGetString("removeTest.String"), String.format(msg, "string"));

		if (config instanceof IConfiguration) {
			assertThrows(NoFieldException.class, () -> ((IConfiguration) config).tryGet("removeTest.Untyped"), String.format(msg, "untyped"));
//			assertThrows(NoFieldException.class, () -> ((IConfiguration) config).tryGetByteList("removeTest.ByteList"), String.format(msg, "byte list"));
//			assertThrows(NoFieldException.class, () -> ((IConfiguration) config).tryGetIntegerList("removeTest.IntegerList"), String.format(msg, "integer list"));
//			assertThrows(NoFieldException.class, () -> ((IConfiguration) config).tryGetLongList("removeTest.LongList"), String.format(msg, "long list"));
		}
	}

	private void assertWrite(IFlatConfiguration config) {
		assertNotNull(config, "Configuration to check must not be null!");
		logFine(String.format("assertWrite started for config %s",
				config.toString().substring(0, config.toString().indexOf("="))));

		config.setBoolean("booleanTest.True", Boolean.TRUE);
		config.setBoolean("booleanTest.False", Boolean.FALSE);

		config.setByte("byteTest.Zero", (byte) 0);
		config.setByte("byteTest.Thirteen", (byte) 13);
		config.setByte("byteTest.MinusThirteen", (byte) -13);
		config.setByte("byteTest.FourtyTwo", (byte) 42);
		config.setByte("byteTest.MinusFourtyTwo", (byte) -42);
		config.setByte("byteTest.SixtySix", (byte) 66);
		config.setByte("byteTest.MinusSixtySix", (byte) -66);
		config.setByte("byteTest.Max", Byte.MAX_VALUE);
		config.setByte("byteTest.Min", Byte.MIN_VALUE);

		config.setDouble("doubleTest.Zero", 0.0D);
		config.setDouble("doubleTest.OneThird", 1.0D / 3.0D);
		config.setDouble("doubleTest.MinusOneThird", -1.0D / 3.0D);
		config.setDouble("doubleTest.E", Math.E);
		config.setDouble("doubleTest.MinusE", -Math.E);
		config.setDouble("doubleTest.Pi", Math.PI);
		config.setDouble("doubleTest.MinusPi", -Math.PI);
		config.setDouble("doubleTest.Thirteen", 13.0D);
		config.setDouble("doubleTest.MinusThirteen", -13.0D);
		config.setDouble("doubleTest.FourtyTwo", 42.0D);
		config.setDouble("doubleTest.MinusFourtyTwo", -42.0D);
		config.setDouble("doubleTest.SixHundredAndSixtySix", 666.0D);
		config.setDouble("doubleTest.MinusSixHundredAndSixtySix", -666.0D);
		config.setDouble("doubleTest.Max", Double.MAX_VALUE);
		config.setDouble("doubleTest.Min", Double.MIN_VALUE);
		config.setDouble("doubleTest.Infinite", Double.POSITIVE_INFINITY);
		config.setDouble("doubleTest.MinusInfinite", Double.NEGATIVE_INFINITY);
		config.setDouble("doubleTest.NotANumber", Double.NaN);

		config.setFloat("floatTest.Zero", 0.0F);
		config.setFloat("floatTest.OneThird", 1.0F / 3.0F);
		config.setFloat("floatTest.MinusOneThird", -1.0F / 3.0F);
		config.setFloat("floatTest.E", (float) Math.E);
		config.setFloat("floatTest.MinusE", (float) -Math.E);
		config.setFloat("floatTest.Pi", (float) Math.PI);
		config.setFloat("floatTest.MinusPi", (float) -Math.PI);
		config.setFloat("floatTest.Thirteen", 13.0F);
		config.setFloat("floatTest.MinusThirteen", -13.0F);
		config.setFloat("floatTest.FourtyTwo", 42.0F);
		config.setFloat("floatTest.MinusFourtyTwo", -42.0F);
		config.setFloat("floatTest.SixHundredAndSixtySix", 666.0F);
		config.setFloat("floatTest.MinusSixHundredAndSixtySix", -666.0F);
		config.setFloat("floatTest.Max", Float.MAX_VALUE);
		config.setFloat("floatTest.Min", Float.MIN_VALUE);
		config.setFloat("floatTest.Infinite", Float.POSITIVE_INFINITY);
		config.setFloat("floatTest.MinusInfinite", Float.NEGATIVE_INFINITY);
		config.setFloat("floatTest.NotANumber", Float.NaN);

		config.setInteger("integerTest.Zero", 0);
		config.setInteger("integerTest.Thirteen", 13);
		config.setInteger("integerTest.MinusThirteen", -13);
		config.setInteger("integerTest.FourtyTwo", 42);
		config.setInteger("integerTest.MinusFourtyTwo", -42);
		config.setInteger("integerTest.SixHundredAndSixtySix", 666);
		config.setInteger("integerTest.MinusSixHundredAndSixtySix", -666);
		config.setInteger("integerTest.Max", Integer.MAX_VALUE);
		config.setInteger("integerTest.Min", Integer.MIN_VALUE);

		config.setLong("longTest.Zero", 0L);
		config.setLong("longTest.Thirteen", 13L);
		config.setLong("longTest.MinusThirteen", -13L);
		config.setLong("longTest.FourtyTwo", 42L);
		config.setLong("longTest.MinusFourtyTwo", -42L);
		config.setLong("longTest.SixHundredAndSixtySix", 666L);
		config.setLong("longTest.MinusSixHundredAndSixtySix", -666L);
		config.setLong("longTest.Max", Long.MAX_VALUE);
		config.setLong("longTest.Min", Long.MIN_VALUE);

		config.setString("stringTest.Null", null);
		config.setString("stringTest.Empty", "");
		config.setString("stringTest.Digits", "0123456789");
		config.setString("stringTest.Spaces", " 0 1 2 3 4 5 6 7 8 9 ");
		config.setString("stringTest.Azerty", "azertyuiopqsdfghjklmwxcvbn");
		config.setString("stringTest.CapitalAzerty", "AZERTYUIOPQSDFGHJKLMWXCVBN");
		config.setString("stringTest.Alphabet", "abcdefghijklmnopqrstuvwxyz");
		config.setString("stringTest.CapitalAlphabet", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		config.setString("stringTest.SpecialChars", "æÂê®†Úºîœπ‡Ò∂ƒﬁÌÏÈ¬µ‹≈©◊ß~");
		config.setString("stringTest.CapitalSpecialChars", "ÆÂÊ®†ÚºÎŒΠ‡Ò∂ƑFIÌÏÈ¬Μ‹≈©◊SS~");

		if (config instanceof IConfiguration) {
			((IConfiguration) config).set("untypedTest.Double", Double.valueOf(42));
			((IConfiguration) config).set("untypedTest.String", "42");

			((IConfiguration) config).setByteList("byteListTest.Null", null);
			((IConfiguration) config).setByteList("byteListTest.Empty", new ArrayList<Byte>());
			((IConfiguration) config).setByteList("byteListTest.ZeroOneTwoThree",
					Arrays.asList((byte) 0, (byte) 1, (byte) 2, (byte) 3));
			((IConfiguration) config).setByteList("byteListTest.FourTwo", Arrays.asList((byte) 4, (byte) 2));
			((IConfiguration) config).setByteList("byteListTest.SixSixSix",
					Arrays.asList((byte) 6, (byte) 6, (byte) 6));
			((IConfiguration) config).setByteList("byteListTest.PiHundredDecimals",
					Arrays.asList((byte) 3, (byte) 14, (byte) 15, (byte) 92, (byte) 65, (byte) 35, (byte) 89, (byte) 79,
							(byte) 32, (byte) 38, (byte) 46, (byte) 26, (byte) 43, (byte) 38, (byte) 32, (byte) 79,
							(byte) 50, (byte) 28, (byte) 84, (byte) 19, (byte) 71, (byte) 69, (byte) 39, (byte) 93,
							(byte) 75, (byte) 10, (byte) 58, (byte) 20, (byte) 97, (byte) 49, (byte) 44, (byte) 59,
							(byte) 23, (byte) 7, (byte) 81, (byte) 64, (byte) 6, (byte) 28, (byte) 62, (byte) 8,
							(byte) 99, (byte) 86, (byte) 28, (byte) 3, (byte) 48, (byte) 25, (byte) 34, (byte) 21,
							(byte) 17, (byte) 6, (byte) 79));
			((IConfiguration) config).setByteList("byteListTest.MinZeroMax",
					Arrays.asList(Byte.MIN_VALUE, (byte) 0, Byte.MAX_VALUE));

			((IConfiguration) config).setIntegerList("integerListTest.Null", null);
			((IConfiguration) config).setIntegerList("integerListTest.Empty", new ArrayList<Integer>());
			((IConfiguration) config).setIntegerList("integerListTest.ZeroOneTwoThree", Arrays.asList(0, 1, 2, 3));
			((IConfiguration) config).setIntegerList("integerListTest.FourTwo", Arrays.asList(4, 2));
			((IConfiguration) config).setIntegerList("integerListTest.SixSixSix", Arrays.asList(6, 6, 6));
			((IConfiguration) config).setIntegerList("integerListTest.PiHundredDecimals",
					Arrays.asList(3, 14, 15, 92, 65, 35, 89, 79, 32, 38, 46, 26, 43, 38, 32, 79, 50, 28, 84, 19, 71, 69,
							39, 93, 75, 10, 58, 20, 97, 49, 44, 59, 23, 7, 81, 64, 6, 28, 62, 8, 99, 86, 28, 3, 48, 25,
							34, 21, 17, 6, 79));
			((IConfiguration) config).setIntegerList("integerListTest.MinZeroMax",
					Arrays.asList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE));
		}

		logFine(String.format("Write successful ? : \"%s\"", config.toString()));
	}

	public void assertRead(IFlatConfiguration config) {
		assertNotNull(config, "Configuration to check must not be null!");
		logFine(String.format("assertRead started for config %s",
				config.toString().substring(0, config.toString().indexOf("="))));

		assertTrue(config.getBoolean("booleanTest.True"),
				"Failed to read or write boolean value " + Boolean.TRUE + "!");
		assertFalse(config.getBoolean("booleanTest.False"),
				"Failed to read or write boolean value " + Boolean.FALSE + "!");

		assertEquals((byte) 0, config.getByte("byteTest.Zero"), "Failed to read or write byte value " + 0 + "!");
		assertEquals((byte) 13, config.getByte("byteTest.Thirteen"), "Failed to read or write byte value " + 13 + "!");
		assertEquals((byte) -13, config.getByte("byteTest.MinusThirteen"),
				"Failed to read or write byte value -" + 13 + "!");
		assertEquals((byte) 42, config.getByte("byteTest.FourtyTwo"), "Failed to read or write byte value " + 42 + "!");
		assertEquals((byte) -42, config.getByte("byteTest.MinusFourtyTwo"),
				"Failed to read or write byte value -" + 42 + "!");
		assertEquals((byte) 66, config.getByte("byteTest.SixtySix"), "Failed to read or write byte value " + 66 + "!");
		assertEquals((byte) -66, config.getByte("byteTest.MinusSixtySix"),
				"Failed to read or write byte value -" + 66 + "!");
		assertEquals(Byte.MAX_VALUE, config.getByte("byteTest.Max"),
				"Failed to read or write byte value " + Byte.MAX_VALUE + "!");
		assertEquals(Byte.MIN_VALUE, config.getByte("byteTest.Min"),
				"Failed to read or write byte value " + Byte.MIN_VALUE + "!");

		assertEquals(0.0D, config.getDouble("doubleTest.Zero"), "Failed to read or write double value " + 0.0D + "!");
		assertEquals(1.0D / 3.0D, config.getDouble("doubleTest.OneThird"),
				"Failed to read or write double value " + 1.0D / 3.0D + "!");
		assertEquals(-1.0D / 3.0D, config.getDouble("doubleTest.MinusOneThird"),
				"Failed to read or write double value -" + 1.0D / 3.0D + "!");
		assertEquals(Math.E, config.getDouble("doubleTest.E"), "Failed to read or write double value " + Math.E + "!");
		assertEquals(-Math.E, config.getDouble("doubleTest.MinusE"),
				"Failed to read or write double value -" + Math.E + "!");
		assertEquals(Math.PI, config.getDouble("doubleTest.Pi"),
				"Failed to read or write double value " + Math.PI + "!");
		assertEquals(-Math.PI, config.getDouble("doubleTest.MinusPi"),
				"Failed to read or write double value -" + Math.PI + "!");
		assertEquals(13.0D, config.getDouble("doubleTest.Thirteen"),
				"Failed to read or write double value " + 13.0D + "!");
		assertEquals(-13.0D, config.getDouble("doubleTest.MinusThirteen"),
				"Failed to read or write double value -" + 13.0D + "!");
		assertEquals(42.0D, config.getDouble("doubleTest.FourtyTwo"),
				"Failed to read or write double value " + 42.0D + "!");
		assertEquals(-42.0D, config.getDouble("doubleTest.MinusFourtyTwo"),
				"Failed to read or write double value -" + 42.0D + "!");
		assertEquals(666.0D, config.getDouble("doubleTest.SixHundredAndSixtySix"),
				"Failed to read or write double value " + 666.0D + "!");
		assertEquals(-666.0D, config.getDouble("doubleTest.MinusSixHundredAndSixtySix"),
				"Failed to read or write double value -" + 666.0D + "!");
		assertEquals(Double.MAX_VALUE, config.getDouble("doubleTest.Max"),
				"Failed to read or write double value " + Double.MAX_VALUE + "!");
		assertEquals(Double.MIN_VALUE, config.getDouble("doubleTest.Min"),
				"Failed to read or write double value " + Double.MIN_VALUE + "!");
		assertEquals(Double.POSITIVE_INFINITY, config.getDouble("doubleTest.Infinite"),
				"Failed to read or write double value " + Double.POSITIVE_INFINITY + "!");
		assertEquals(Double.NEGATIVE_INFINITY, config.getDouble("doubleTest.MinusInfinite"),
				"Failed to read or write double value " + Double.NEGATIVE_INFINITY + "!");
		assertEquals(Double.NaN, config.getDouble("doubleTest.NotANumber"),
				"Failed to read or write double value " + Double.NaN + "!");

		assertEquals(0.0F, config.getFloat("floatTest.Zero"), "Failed to read or write float value " + 0.0F + "!");
		assertEquals(1.0F / 3.0F, config.getFloat("floatTest.OneThird"),
				"Failed to read or write float value " + 1.0F / 3.0F + "!");
		assertEquals(-1.0F / 3.0F, config.getFloat("floatTest.MinusOneThird"),
				"Failed to read or write float value -" + 1.0F / 3.0F + "!");
		assertEquals((float) Math.E, config.getFloat("floatTest.E"),
				"Failed to read or write float value " + Math.E + "!");
		assertEquals((float) -Math.E, config.getFloat("floatTest.MinusE"),
				"Failed to read or write float value -" + Math.E + "!");
		assertEquals((float) Math.PI, config.getFloat("floatTest.Pi"),
				"Failed to read or write float value " + Math.PI + "!");
		assertEquals((float) -Math.PI, config.getFloat("floatTest.MinusPi"),
				"Failed to read or write float value -" + Math.PI + "!");
		assertEquals(13.0F, config.getFloat("floatTest.Thirteen"),
				"Failed to read or write float value " + 13.0F + "!");
		assertEquals(-13.0F, config.getFloat("floatTest.MinusThirteen"),
				"Failed to read or write float value -" + 13.0F + "!");
		assertEquals(42.0F, config.getFloat("floatTest.FourtyTwo"),
				"Failed to read or write float value " + 42.0F + "!");
		assertEquals(-42.0F, config.getFloat("floatTest.MinusFourtyTwo"),
				"Failed to read or write float value -" + 42.0F + "!");
		assertEquals(666.0F, config.getFloat("floatTest.SixHundredAndSixtySix"),
				"Failed to read or write float value " + 666.0F + "!");
		assertEquals(-666.0F, config.getFloat("floatTest.MinusSixHundredAndSixtySix"),
				"Failed to read or write float value -" + 666.0F + "!");
		assertEquals(Float.MAX_VALUE, config.getFloat("floatTest.Max"),
				"Failed to read or write float value " + Float.MAX_VALUE + "!");
		assertEquals(Float.MIN_VALUE, config.getFloat("floatTest.Min"),
				"Failed to read or write float value " + Float.MIN_VALUE + "!");
		assertEquals(Float.POSITIVE_INFINITY, config.getFloat("floatTest.Infinite"),
				"Failed to read or write float value " + Float.POSITIVE_INFINITY + "!");
		assertEquals(Float.NEGATIVE_INFINITY, config.getFloat("floatTest.MinusInfinite"),
				"Failed to read or write float value " + Float.NEGATIVE_INFINITY + "!");
		assertEquals(Float.NaN, config.getFloat("floatTest.NotANumber"),
				"Failed to read or write float value " + Float.NaN + "!");

		assertEquals(0, config.getInteger("integerTest.Zero"), "Failed to read or write integer value " + 0 + "!");
		assertEquals(13, config.getInteger("integerTest.Thirteen"),
				"Failed to read or write integer value " + 13 + "!");
		assertEquals(-13, config.getInteger("integerTest.MinusThirteen"),
				"Failed to read or write integer value -" + 13 + "!");
		assertEquals(42, config.getInteger("integerTest.FourtyTwo"),
				"Failed to read or write integer value " + 42 + "!");
		assertEquals(-42, config.getInteger("integerTest.MinusFourtyTwo"),
				"Failed to read or write integer value -" + 42 + "!");
		assertEquals(666, config.getInteger("integerTest.SixHundredAndSixtySix"),
				"Failed to read or write integer value " + 666 + "!");
		assertEquals(-666, config.getInteger("integerTest.MinusSixHundredAndSixtySix"),
				"Failed to read or write integer value -" + 666 + "!");
		assertEquals(Integer.MAX_VALUE, config.getInteger("integerTest.Max"),
				"Failed to read or write integer value " + Integer.MAX_VALUE + "!");
		assertEquals(Integer.MIN_VALUE, config.getInteger("integerTest.Min"),
				"Failed to read or write integer value " + Integer.MIN_VALUE + "!");

		assertEquals(0L, config.getLong("longTest.Zero"), "Failed to read or write long value " + 0L + "!");
		assertEquals(13L, config.getLong("longTest.Thirteen"), "Failed to read or write long value " + 13L + "!");
		assertEquals(-13L, config.getLong("longTest.MinusThirteen"),
				"Failed to read or write long value -" + 13L + "!");
		assertEquals(42L, config.getLong("longTest.FourtyTwo"), "Failed to read or write long value " + 42L + "!");
		assertEquals(-42L, config.getLong("longTest.MinusFourtyTwo"),
				"Failed to read or write long value -" + 42L + "!");
		assertEquals(666L, config.getLong("longTest.SixHundredAndSixtySix"),
				"Failed to read or write long value " + 666L + "!");
		assertEquals(-666L, config.getLong("longTest.MinusSixHundredAndSixtySix"),
				"Failed to read or write long value -" + 666L + "!");
		assertEquals(Long.MAX_VALUE, config.getLong("longTest.Max"),
				"Failed to read or write long value " + Long.MAX_VALUE + "!");
		assertEquals(Long.MIN_VALUE, config.getLong("longTest.Min"),
				"Failed to read or write long value " + Long.MIN_VALUE + "!");

		assertFalse(config.contains("stringTest.Null"), "Failed to read or write string value null!");

		assertEquals("", config.getString("stringTest.Empty"), "Failed to read or write string value \"\"!");
		assertEquals(" 0 1 2 3 4 5 6 7 8 9 ", config.getString("stringTest.Spaces"),
				"Failed to read or write string value \" 0 1 2 3 4 5 6 7 8 9 \"!");
		assertEquals("0123456789", config.getString("stringTest.Digits"),
				"Failed to read or write string value \"0123456789\"!");
		assertEquals("azertyuiopqsdfghjklmwxcvbn", config.getString("stringTest.Azerty"),
				"Failed to read or write string value \"azertyuiopqsdfghjklmwxcvbn\"!");
		assertEquals("AZERTYUIOPQSDFGHJKLMWXCVBN", config.getString("stringTest.CapitalAzerty"),
				"Failed to read or write string value \"AZERTYUIOPQSDFGHJKLMWXCVBN\"!");
		assertEquals("abcdefghijklmnopqrstuvwxyz", config.getString("stringTest.Alphabet"),
				"Failed to read or write string value \"abcdefghijklmnopqrstuvwxyz\"!");
		assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", config.getString("stringTest.CapitalAlphabet"),
				"Failed to read or write string value \"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"!");
		assertEquals("æÂê®†Úºîœπ‡Ò∂ƒﬁÌÏÈ¬µ‹≈©◊ß~", config.getString("stringTest.SpecialChars"),
				"Failed to read or write string value \"æÂê®†Úºîœπ‡Ò∂ƒﬁÌÏÈ¬µ‹≈©◊ß~\"!");
		assertEquals("ÆÂÊ®†ÚºÎŒΠ‡Ò∂ƑFIÌÏÈ¬Μ‹≈©◊SS~", config.getString("stringTest.CapitalSpecialChars"),
				"Failed to read or write string value \"ÆÂÊ®†ÚºÎŒΠ‡Ò∂ƑFIÌÏÈ¬Μ‹≈©◊SS~\"!");

		if (config instanceof IConfiguration) {
			assertEquals(Double.valueOf(42), ((IConfiguration) config).get("untypedTest.Double"),
					"Failed to read or write untyped value " + 42 + "!");
			assertEquals("42", ((IConfiguration) config).get("untypedTest.String"),
					"Failed to read or write untyped value \"42\"!");

			assertFalse(((IConfiguration) config).contains("byteListTest.Null"),
					"Failed to read or write byte list value null!");
			assertEquals(new ArrayList<Byte>(), ((IConfiguration) config).getByteList("byteListTest.Empty"),
					"Failed to read or write byte list value []!");
			assertEquals(Arrays.asList((byte) 0, (byte) 1, (byte) 2, (byte) 3),
					((IConfiguration) config).getByteList("byteListTest.ZeroOneTwoThree"),
					"Failed to read or write byte list value [0, 1, 2, 3]!");
			assertEquals(Arrays.asList((byte) 4, (byte) 2),
					((IConfiguration) config).getByteList("byteListTest.FourTwo"),
					"Failed to read or write byte list value [4, 2]!");
			assertEquals(Arrays.asList((byte) 6, (byte) 6, (byte) 6),
					((IConfiguration) config).getByteList("byteListTest.SixSixSix"),
					"Failed to read or write byte list value [6, 6, 6]!");
			assertEquals(
					Arrays.asList((byte) 3, (byte) 14, (byte) 15, (byte) 92, (byte) 65, (byte) 35, (byte) 89, (byte) 79,
							(byte) 32, (byte) 38, (byte) 46, (byte) 26, (byte) 43, (byte) 38, (byte) 32, (byte) 79,
							(byte) 50, (byte) 28, (byte) 84,
							(byte) 19, (byte) 71, (byte) 69, (byte) 39, (byte) 93, (byte) 75, (byte) 10, (byte) 58,
							(byte) 20, (byte) 97, (byte) 49, (byte) 44, (byte) 59, (byte) 23, (byte) 7, (byte) 81,
							(byte) 64, (byte) 6, (byte) 28, (byte) 62,
							(byte) 8, (byte) 99, (byte) 86, (byte) 28, (byte) 3, (byte) 48, (byte) 25, (byte) 34,
							(byte) 21, (byte) 17, (byte) 6, (byte) 79),
					((IConfiguration) config).getByteList("byteListTest.PiHundredDecimals"),
					"Failed to read or write byte list value [3, 14, 15..., 79]!");
			assertEquals(Arrays.asList(Byte.MIN_VALUE, (byte) 0, Byte.MAX_VALUE),
					((IConfiguration) config).getByteList("byteListTest.MinZeroMax"),
					"Failed to read or write byte list value [" + Byte.MIN_VALUE + ", 0, " + Byte.MAX_VALUE + "]!");

			assertFalse(((IConfiguration) config).contains("integerListTest.Null"),
					"Failed to read or write integer list value null!");
			assertEquals(new ArrayList<Integer>(), ((IConfiguration) config).getIntegerList("integerListTest.Empty"),
					"Failed to read or write integer list value []!");
			assertEquals(Arrays.asList(0, 1, 2, 3),
					((IConfiguration) config).getIntegerList("integerListTest.ZeroOneTwoThree"),
					"Failed to read or write integer list value [0, 1, 2, 3]!");
			assertEquals(Arrays.asList(4, 2), ((IConfiguration) config).getIntegerList("integerListTest.FourTwo"),
					"Failed to read or write integer list value [4, 2]!");
			assertEquals(Arrays.asList(6, 6, 6), ((IConfiguration) config).getIntegerList("integerListTest.SixSixSix"),
					"Failed to read or write integer list value [6, 6, 6]!");
			assertEquals(
					Arrays.asList(3, 14, 15, 92, 65, 35, 89, 79, 32, 38, 46, 26, 43, 38, 32, 79, 50, 28, 84, 19, 71, 69,
							39, 93, 75, 10, 58, 20, 97, 49, 44, 59, 23, 7, 81, 64, 6, 28, 62, 8, 99, 86, 28, 3, 48, 25,
							34, 21, 17, 6, 79),
					((IConfiguration) config).getIntegerList("integerListTest.PiHundredDecimals"),
					"Failed to read or write integer list value [3, 14, 15..., 79]!");
			assertEquals(Arrays.asList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE),
					((IConfiguration) config).getIntegerList("integerListTest.MinZeroMax"),
					"Failed to read or write integer list value [" + Integer.MIN_VALUE + ", 0, " + Integer.MAX_VALUE
							+ "]!");
		}
	}
}
