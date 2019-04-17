package uneiConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import me.unei.configuration.UneiConfiguration;
import me.unei.configuration.api.fs.FSUtils;
import me.unei.configuration.api.fs.IPathComponent;
import me.unei.configuration.api.fs.IPathComponent.IPathComponentsList;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;
import me.unei.configuration.api.fs.PathComponentType;

@DisplayName("File system tests")
public class PathTest
{
	@DisplayName("FileSystem tests Initialization")
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
	@DisplayName("UNIX pathToString tests")
	public void pathToStringTestUnix()
	{
		IPathComponentsList list = FSUtils.createList(PathSymbolsType.UNIX);
		
		list.appendRoot();
		list.appendChild("te.st");
		list.appendChild("na/me");
		list.appendParent();
		list.appendChild("in\\dex");
		list.appendIndex(12);
		list.appendParent();
		list.appendIndex(56);
		list.appendChild("hello");
		
		assertEquals("/te.st/na\\/me/../in\\\\dex[12]/../[56]/hello", list.toString());
	}
	
	@Test
	@DisplayName("BUKKIT pathToString tests")
	public void pathToStringTestBukkit()
	{
		IPathComponentsList list = FSUtils.createList(PathSymbolsType.BUKKIT);
		
		list.appendRoot();
		list.appendChild("te.st");
		list.appendChild("na/me");
		list.appendParent();
		list.appendChild("in\\dex");
		list.appendIndex(12);
		list.appendParent();
		list.appendIndex(56);
		list.appendChild("hello");
		
		assertEquals(".te\\.st.na/me..in\\\\dex[12]..[56].hello", list.toString());
	}
	
	@Test
	@DisplayName("UNIX stringToPath tests")
	public void stringToPathTestUnix()
	{
		IPathComponentsList list = FSUtils.parsePath("/te.st/na\\/me/../in\\\\dex[12]/../[56]/hello", PathSymbolsType.UNIX);
		
		assertEquals(9, list.size());
		
		int i = 0;

		IPathComponent current = list.get(i++);
		assertSame(PathComponentType.ROOT, current.getType());
		assertEquals("/", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("te.st", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("na/me", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.PARENT, current.getType());
		assertEquals("..", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("in\\dex", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.INDEX, current.getType());
		assertEquals("12", current.getValue());
		assertEquals(12, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.PARENT, current.getType());
		assertEquals("..", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.INDEX, current.getType());
		assertEquals("56", current.getValue());
		assertEquals(56, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("hello", current.getValue());
		assertEquals(-1, current.getIndex());
		
		list = FSUtils.cleanPath(list);
		
		assertEquals(5, list.size());
		
		i = 0;

		current = list.get(i++);
		assertSame(PathComponentType.ROOT, current.getType());
		assertEquals("/", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("te.st", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("in\\dex", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.INDEX, current.getType());
		assertEquals("56", current.getValue());
		assertEquals(56, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("hello", current.getValue());
		assertEquals(-1, current.getIndex());
	}
	
	@Test
	@DisplayName("BUKKIT stringToPath tests")
	public void stringToPathTestBukkit()
	{
		IPathComponentsList list = FSUtils.parsePath(".te\\.st.na/me..in\\\\dex[12]..[56].hello", PathSymbolsType.BUKKIT);
		
		assertEquals(9, list.size());
		
		int i = 0;

		IPathComponent current = list.get(i++);
		assertSame(PathComponentType.ROOT, current.getType());
		assertEquals(".", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("te.st", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("na/me", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.PARENT, current.getType());
		assertEquals("..", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("in\\dex", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.INDEX, current.getType());
		assertEquals("12", current.getValue());
		assertEquals(12, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.PARENT, current.getType());
		assertEquals("..", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.INDEX, current.getType());
		assertEquals("56", current.getValue());
		assertEquals(56, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("hello", current.getValue());
		assertEquals(-1, current.getIndex());
		
		list = FSUtils.cleanPath(list);
		
		assertEquals(5, list.size());
		
		i = 0;

		current = list.get(i++);
		assertSame(PathComponentType.ROOT, current.getType());
		assertEquals(".", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("te.st", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("in\\dex", current.getValue());
		assertEquals(-1, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.INDEX, current.getType());
		assertEquals("56", current.getValue());
		assertEquals(56, current.getIndex());

		current = list.get(i++);
		assertSame(PathComponentType.CHILD, current.getType());
		assertEquals("hello", current.getValue());
		assertEquals(-1, current.getIndex());
	}
	
	@Test
	@SuppressWarnings("deprecation")
	@EnabledIfSystemProperty(named = "unei.test.patheval", matches = "([Tt]rue|[Yy]es)")
	public void testPathEval()
	{
		PathSymbolsType type = PathSymbolsType.tryDetectType(".te\\.st.na/me..in\\\\dex[12]..[56].hello..");
		
		assertSame(PathSymbolsType.BUKKIT, type);
		
		type = PathSymbolsType.tryDetectType("/te.st/na\\/me/../in\\\\dex[12]/../[56]/hello");
		
		assertSame(PathSymbolsType.UNIX, type);
		
		fail("Path type detection is not predictable and will most likely fail in production environment");
	}
	
	@DisplayName("FileSystem tests clearing")
	@AfterAll
	public static void tearDownAll()
	{
		//
	}
}