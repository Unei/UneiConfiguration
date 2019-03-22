package uneiConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathComponent.PathComponentType;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;

@DisplayName("File system tests")
public class PathTest
{
	@DisplayName("FileSystem tests Initialization")
	@BeforeAll
	public static void initAll()
	{
		//
	}
	
	@Test
	@DisplayName("UNIX pathToString tests")
	public void pathToStringTestUnix()
	{
		PathComponentsList list = new PathComponentsList(PathSymbolsType.UNIX);
		
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
		PathComponentsList list = new PathComponentsList(PathSymbolsType.BUKKIT);
		
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
		PathComponentsList list = PathNavigator.parsePath("/te.st/na\\/me/../in\\\\dex[12]/../[56]/hello", PathSymbolsType.UNIX);
		
		assertEquals(9, list.size());
		
		int i = 0;

		PathComponent current = list.get(i++);
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
		
		list = PathNavigator.cleanPath(list);
		
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
		PathComponentsList list = PathNavigator.parsePath(".te\\.st.na/me..in\\\\dex[12]..[56].hello", PathSymbolsType.BUKKIT);
		
		assertEquals(9, list.size());
		
		int i = 0;

		PathComponent current = list.get(i++);
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
		
		list = PathNavigator.cleanPath(list);
		
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