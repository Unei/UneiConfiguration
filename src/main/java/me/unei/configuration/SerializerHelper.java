package me.unei.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public final class SerializerHelper
{
	private static Gson GSON = null;
	
	private static void initGSON()
	{
		if (SerializerHelper.GSON == null)
		{
			GsonBuilder gb = new GsonBuilder();
			gb.serializeNulls().serializeSpecialFloatingPointValues().disableHtmlEscaping();
			gb.setLenient();
			SerializerHelper.GSON = gb.create();
		}
	}
	
	public static String toJSONString(Object objects) throws IOException
	{
		if (objects == null/* || objects.length < 1 || objects[0] == null*/)
		{
			return "";
		}
		SerializerHelper.initGSON();
		MyStringWriter sw = new MyStringWriter();
		JsonWriter jw = new JsonWriter(sw);
		try
		{
			SerializerHelper.setSpace(jw);
		}
		catch (Exception e)
		{
			//
		}
		SerializerHelper.GSON.toJson(objects, objects.getClass(), jw);
		String res = sw.toString();
		jw.close();
		sw.close();
		return res;
	}
	
	public static Object parseJSON(String data) throws IOException
	{
		if (data == null || data.isEmpty())
		{
			return null;
		}
		SerializerHelper.initGSON();
		StringReader sr = new StringReader(data);
		JsonReader jr = new JsonReader(sr);
		Object o = SerializerHelper.GSON.fromJson(jr, Object.class);
		jr.close();
		sr.close();
		return o;
	}
	
	private static void setSpace(JsonWriter jsonWriter) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field sep = JsonWriter.class.getDeclaredField("separator");
		sep.setAccessible(true);
		sep.set(jsonWriter, ": ");
	}
	
	private static class MyStringWriter extends StringWriter
	{
		@Override
		public void write(int c)
		{
			super.write(c);
			if (c == ','/* || c == ':'*/)
			{
				super.write(' ');
			}
		}
	}
}