package me.unei.configuration.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class Updater
{
	public static final String MAVEN_GROUPID = "me.unei";
	
	public static final String POM_RESOURCE_PATH = "META-INF/maven/<groupId>/<artifactId>/pom.properties";
	
	public static final String MAVEN_REPO_URL = "https://unei.gitlab.io/maven/<group>/<artifact>/maven-metadata.xml";
	
	private static Updater cachedUpdater = null;
	
	private static DateFormat dateFormat = null;
	
	private final IPlugin plugin;
	private final String pomResourcePath;
	private final String mavenRepoUrl;
	private String cachedCurrentVersion;
	private MavenMeta cachedMavenMeta;
	
	static final Updater getUpdater(IPlugin plugin)
	{
		if (Updater.cachedUpdater != null)
		{
			return Updater.cachedUpdater;
		}
		Updater updater = new Updater(plugin);
		Updater.cachedUpdater = updater;
		return updater;
	}
	
	private Updater(IPlugin plugin)
	{
		this.plugin = plugin;
		this.pomResourcePath = Updater.POM_RESOURCE_PATH.replace("<groupId>", MAVEN_GROUPID).replace("<artifactId>", this.getArtifactName());
		this.mavenRepoUrl = Updater.MAVEN_REPO_URL.replace("<group>", MAVEN_GROUPID.replace('.', '/')).replace("<artifact>", this.getClassName());
		this.cachedCurrentVersion = null;
	}
	
	public void checkVersionAsync(final UpdateCheckCallback callback)
	{
		Thread versionChacker = new Thread("Version Checker Thread"){
			@Override
			public void run()
			{
				boolean res = Updater.this.checkVersion();
				if (callback != null)
				{
					callback.run(res);
				}
			}
		};
		versionChacker.start();
	}
	
	public boolean checkVersion()
	{
		this.plugin.getLogger().fine("Checking plugin version...");
        String latest = this.getLatestVersion();
        String myversion = this.getCurrentVersion();
        if (latest == null || myversion == null) {
        	this.plugin.getLogger().info(this.getClassName() + " was enable to check his version.");
        	return false;
        } else if (!latest.equalsIgnoreCase(myversion)) {
        	this.plugin.getLogger().warning(this.getClassName() + " seems to be out of date !");
        	this.plugin.getLogger().warning("Latest version is '" + latest + "' but current is '" + myversion + "'.");
        	this.plugin.getLogger().warning("Latest version was released the " + this.getReadableLastUpdatedDate());
        	this.plugin.getLogger().warning("We recomend to update " + this.getClassName() + ", more info on http://unei.gitlab.io/");
        	return false;
        }
        this.plugin.getLogger().fine("Version check complete : You are using the latest one !");
        return true;
	}
	
	private String getReadableLastUpdatedDate()
	{
		if (Updater.dateFormat == null)
		{
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Updater.dateFormat = format;
		}
		Date ret = this.getMavenMeta().getLastUpdated();
		if (ret != null)
		{
			return Updater.dateFormat.format(ret);
		}
		return "Unknown date";
	}
	
	public String getCurrentVersion()
	{
		if (this.cachedCurrentVersion != null)
		{
			return this.cachedCurrentVersion;
		}
		String version;
    	Properties pom = new Properties();
    	InputStream in = this.plugin.getResource(this.pomResourcePath);
    	try {
    		pom.load(in);
    		in.close();
    	} catch (IOException e) {
			e.printStackTrace();
			return null;
    	}
    	version = pom.getProperty("version");
    	if (version != null)
    	{
    		this.cachedCurrentVersion = version;
    		return version;
    	}
    	return "UNKNOWN";
	}
	
	public MavenMeta getMavenMeta()
	{
		if (this.cachedMavenMeta != null)
		{
			return this.cachedMavenMeta;
		}
		MavenMeta meta = new MavenMeta();
		if (meta.parseData(this.mavenRepoUrl))
		{
			this.cachedMavenMeta = meta;
		}
		return meta;
	}
	
	public String getLatestVersion()
	{
		return this.getMavenMeta().getLatestVersion();
	}
	
	public Date getLastUpdatedTime()
	{
		return this.getMavenMeta().getLastUpdated();
	}
	
	private String getArtifactName()
	{
		Class<? extends IPlugin> pluginClass = plugin.getClass();
		return pluginClass.getSimpleName().toLowerCase();
	}
	
	private String getClassName()
	{
		Class<? extends IPlugin> pluginClass = plugin.getClass();
		return pluginClass.getSimpleName();
	}
	
	public static abstract class UpdateCheckCallback
	{
		public abstract void run(boolean checkResult);
	}
	
	private static final class MavenMeta
	{
		public static final String LATEST_PATH = "//metadata/versioning/latest";
		public static final String LAST_UPDATED_PATH = "//metadata/versioning/lastUpdated";
		
		private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		private String latest;
		private String lastUpdated;
		private Date lastUpdatedDate;
		
		public boolean parseData(String url)
		{
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		try
    		{
    			XPathFactory xpf = XPathFactory.newInstance();
    			XPath path = xpf.newXPath();
    			factory.setIgnoringElementContentWhitespace(true);
    			DocumentBuilder builder = factory.newDocumentBuilder();
    			
    			Document doc = builder.parse(url);
    			Node root = doc.getDocumentElement();
    			this.latest = path.evaluate(MavenMeta.LATEST_PATH, root);
    			this.lastUpdated = path.evaluate(MavenMeta.LAST_UPDATED_PATH, root);
    			this.lastUpdatedDate = MavenMeta.dateFormat.parse(this.lastUpdated);
    			return true;
    		}
    		catch (ParserConfigurationException ignored)
    		{
    			//
    		}
    		catch (SAXException ignored)
    		{
    			//
    		}
    		catch (XPathExpressionException ignored)
    		{
    			//
			}
    		catch (IOException ignored)
    		{
				//
			}
    		catch (ParseException ignored)
    		{
				//
			}
    		return false;
		}
		
		public String getLatestVersion()
		{
			return this.latest;
		}
		
		public Date getLastUpdated()
		{
			return this.lastUpdatedDate;
		}
	}
}