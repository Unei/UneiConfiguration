package uneiConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import me.unei.configuration.SavedFile;
import me.unei.configuration.UneiConfiguration;
import me.unei.configuration.api.Configurations;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;

@DisplayName("Child<->Parent relation tests")
public class ChildishTest
{
	@DisplayName("Inheritance tests Initialization")
	@BeforeAll
	public static void initAll()
	{
		UneiConfiguration.tryInstanciate();
	}
	
	@Test
	@DisplayName("UneiConfiguration API instance test")
	public void testConfigurationInstance()
	{
		assertNotNull(UneiConfiguration.getInstance(), "Could not find any instance of UneiConfiguration within the API");
	}
	
	@Test
	@DisplayName("Inheritance basic tests using dummy file")
	public void basicInheritanceTest()
	{
		Configurations.DefaultPathSymbolsType = PathSymbolsType.BUKKIT;
		
		IConfiguration cfgParent = Configurations.newBinaryConfig(new SavedFile(), null);
		
		IConfiguration cfgChild = Configurations.newBinaryConfig(cfgParent, "test");
		
		IConfiguration thirdTier = cfgChild.getSubSection("test");
		
		assertSame(cfgParent, cfgChild.getParent(), "Parent of child sould be same instance as parent");
		assertSame(cfgChild, thirdTier.getParent(), "Parent of second child sould be same instance as first child");
		
		assertSame(cfgParent, thirdTier.getRoot(), "Root element should be cfgParent (1)");
		assertSame(cfgParent, cfgChild.getRoot(), "Root element should be cfgParent (2)");
		assertSame(cfgParent, cfgParent.getRoot(), "getRoot on root element should return itself");
		
		thirdTier.setString("value", "emptyHere");
		
		assertTrue(cfgChild.contains("test.value"), "Setting values should be propagated correctly");
		
		cfgChild.setString("test.value", "coucou");
		
		assertEquals("coucou", cfgParent.get("test.test.value"), "Changing values should be propagated properly");
		
		cfgParent.remove("test.test.value");
		cfgParent.setFloat("test.value", 42.12F);
		
		assertFalse(thirdTier.contains("value"), "Removing values should be propagated properly");
		assertEquals(42.12F, cfgChild.getFloat("value"), "Setting values in parent should be propagated to childrens"); 
		
		cfgChild.lock();
		
		assertFalse(cfgChild.canAccess(), "Locking resource should disable write access");
		assertFalse(cfgParent.canAccess(), "Locking resource should lock even parental hierarchy");
		assertFalse(thirdTier.canAccess(), "Locking resource should lock even child hierarchy");
	}
	
	@SuppressWarnings("deprecation")
	@AfterAll
	public static void endAll()
	{
		UneiConfiguration.INSTANCE.clear();
	}
}
