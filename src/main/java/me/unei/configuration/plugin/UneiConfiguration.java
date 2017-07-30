package me.unei.configuration.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import me.unei.configuration.reflection.NMSReflection;

public final class UneiConfiguration implements IPlugin {

	/**
	 * NEVER USE THIS.
	 */
    private static UneiConfiguration Instance = null;
    
    private static final String CURRENT_VERSION = "META-INF/maven/me.unei/uneiconfiguration/pom.properties";
    
    private static final String VERSION_URL = "http://unei.gitlab.io/maven/me/unei/UneiConfiguration/maven-metadata.xml";

    private final IPlugin source;

    public UneiConfiguration(IPlugin plugin) {
        this.source = plugin;
        NMSReflection.doNothing();
        UneiConfiguration.Instance = this;
    }

    public void onLoad() {
        this.getLogger().fine("Loading UNEI Configuration API...");
        this.checkVersion();

        if (NMSReflection.canUseNMS()) {
            this.getLogger().fine("NMS classes detected !");
            this.getLogger().fine("NMS Version = " + NMSReflection.getVersion());
        }
    }

    public void onEnable() {
        this.getLogger().fine("Enabling UNEI Configuration API...");
    }

    public void onDisable() {
        this.getLogger().fine("Disabling UNEI Configuration API...");
    }

    public File getDataFolder() {
        return source.getDataFolder();
    }

    public Logger getLogger() {
    	try
    	{
    		return source.getLogger();
    	}
    	catch (AbstractMethodError t)
    	{
    		return Logger.getLogger("UneiConfiguration");
    	}
    }

    public InputStream getResource(String path) {
        return source.getResource(path);
    }

    public IPlugin.Type getType() {
        return source.getType();
    }
    
    private final void checkVersion() {
        String latest = UneiConfiguration.getLatestVersion();
        String myversion = this.getVersion();
        if (latest == null || myversion == null) {
        	this.getLogger().info("UNEI Configuration was enable to check his version.");
        } else if (!latest.equalsIgnoreCase(myversion)) {
        	this.getLogger().warning("UNEI Configuration seems to be out of date !");
        	this.getLogger().warning("Latest version is '" + latest + "' but current is '" + myversion + "'.");
        	this.getLogger().warning("We recomend to update UNEI Configuration, more infos at http://unei.gitlab.io/");
        }
    }
    
    private static final String getLatestVersion() {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		try {
    			XPathFactory xpf = XPathFactory.newInstance();
    			XPath path = xpf.newXPath();
    			factory.setIgnoringElementContentWhitespace(true);
    			DocumentBuilder builder = factory.newDocumentBuilder();
    			Document doc = builder.parse(VERSION_URL);
    			Node root = doc.getDocumentElement();
    			return path.evaluate("//metadata/versioning/latest", root);
    		} catch (ParserConfigurationException ignored) {
    			//
    		} catch (SAXException ignored) {
    			//
    		} catch (XPathExpressionException ignored) {
    			//
			} catch (IOException ignored) {
				//
			}
    	return null;
    }
    
    private final String getVersion() {
    	Properties pom = new Properties();
    	InputStream in = this.getResource(CURRENT_VERSION);
    	try {
    		pom.load(in);
    		in.close();
    	} catch (IOException e) {
			e.printStackTrace();
			return null;
    	}
    	return pom.getProperty("version", "UNKNOWN");
    }
    
    /**
     * This method is safe as long as the {@link Standalone} class is present.
     * 
     * @return A not null instance of {@link UneiConfiguration}.
     */
    public static UneiConfiguration getInstance() {
    	if (UneiConfiguration.Instance == null) {
    		new Standalone();
    	}
        return UneiConfiguration.Instance;
    }
}