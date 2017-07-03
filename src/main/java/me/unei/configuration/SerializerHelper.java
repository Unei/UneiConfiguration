package me.unei.configuration;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
			gb.serializeNulls().disableHtmlEscaping();
			//gb.setLenient();
			SerializerHelper.GSON = gb.create();
		}
	}
	
	public static void writeCSV(Writer w, List<String> keyNames, Map<String, Object> map) throws IOException {
		List<List<String>> lines = new ArrayList<List<String>>(map.size());
		for (Entry<String, Object> entry : map.entrySet()) {
			List<String> line = new ArrayList<String>();
			line.add(entry.getKey());
			if (entry.getValue() instanceof Iterable) {
				Iterable<?> itable = (Iterable<?>) entry.getValue();
				Iterator<?> it = itable.iterator();
				while (it.hasNext()) {
					Object elem = it.next();
					line.add(SerializerHelper.toJSONString(elem));
				}
			} else {
				line.add(SerializerHelper.toJSONString(entry.getValue()));
			}
			lines.add(line);
		}
		String s = SerializerHelper.toCSVString(keyNames, lines);
		StringReader sr = new StringReader(s);
		int cur = 0;
		while ((cur = sr.read()) > 0) {
			w.write(cur);
		}
	}
	
	public static Map<String, Object> readCSV(Reader r, List<String> keyNames) throws IOException {
		keyNames.clear();
		StringWriter sw = new StringWriter();
		int cur = 0;
		while ((cur = r.read()) > 0) {
			sw.write(cur);
		}
		List<List<String>> result = SerializerHelper.parseCSV(sw.toString());
		if (result == null || result.isEmpty()) {
			return new HashMap<String, Object>();
		}
		List<String> keys = result.get(0);
		keyNames.addAll(keys);
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 1; i < result.size(); i++) {
			List<String> line = result.get(i);
			if (line == null || line.isEmpty()) {
				continue;
			}
			String key = line.get(0);
			Object value = null;
			if (line.size() == 2) {
				value = SerializerHelper.parseJSON(line.get(1));
			} else {
				ArrayList<Object> list = new ArrayList<Object>(line.size() - 1);
				for (int j = 1; j < line.size(); j++) {
					list.add(SerializerHelper.parseJSON(line.get(j)));
				}
				value = list;
			}
			map.put(key, value);
		}
		return map;
	}
	
	public static String toCSVString(List<String> keyNames, List<List<String>> lines) {
		StringBuilder sb = new StringBuilder();
		String str = null;
		if (keyNames != null) {
			for (Iterator<String> it = keyNames.iterator(); it.hasNext(); ) {
				str = it.next();
				sb.append(str);
				if (it.hasNext()) {
					sb.append(';').append(' ');
				}
			}
			if (!keyNames.isEmpty()) {
				sb.append('\n');
			}
		}
		if (lines != null) {
			for (List<String> elems : lines) {
				if (elems == null) {
					continue;
				}
				if (!elems.isEmpty()) {
					for (Iterator<String> it = elems.iterator(); it.hasNext(); ) {
						str = it.next();
						sb.append(str);
						if (it.hasNext()) {
							sb.append(';').append(' ');
						}
					}
				}
				sb.append('\n');
			}
		}
		return sb.toString();
	}
	
	public static List<List<String>> parseCSV(String csv) {
		if (csv == null) {
			return null;
		}
		List<List<String>> lines = new ArrayList<List<String>>();
		boolean quoted = false;
		boolean escaped = false;
		StringBuilder sb = new StringBuilder();
		List<String> currentLine = new ArrayList<String>();
		for (int i = 0; i < csv.length(); i++) {
			char c = csv.charAt(i);
			
			if (escaped) {
				sb.append(c);
				escaped = false;
				continue;
			}
			
			switch (c) {
				case '\\':
					escaped = true;
					break;
					
				case '"':
					quoted = !quoted;
					break;
					
				case ';':
					if (!quoted) {
						currentLine.add(sb.toString());
						sb.setLength(0);
						if (i < csv.length() && csv.charAt(i + 1) == ' ') {
							i++;
						}
					} else {
						sb.append(c);
					}
					break;
					
				case '\n':
					if (!quoted) {
						if (sb.length() > 0) {
							currentLine.add(sb.toString());
						}
						sb.setLength(0);
						lines.add(currentLine);
						currentLine = new ArrayList<String>();
					} else {
						sb.append(c);
					}
					break;
					
				default:
					sb.append(c);
			}
		}
		return lines;
	}

	public static String toJSONString(Object objects) throws IOException {
		if (objects == null/* || objects.length < 1 || objects[0] == null*/) {
			return "";
		}
		SerializerHelper.initGSON();
		MyStringWriter sw = new MyStringWriter();
		JsonWriter jw = new JsonWriter(sw);
		try {
			SerializerHelper.setSpace(jw);
		} catch (Exception e) {
			// Nothing here ...
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