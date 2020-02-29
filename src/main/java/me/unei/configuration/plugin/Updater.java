package me.unei.configuration.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

public class Updater implements IUpdater {

	public static final String MAVEN_GROUPID = "me.unei";
	public static final String POM_RESOURCE_PATH = "META-INF/maven/<groupId>/<artifactId>/pom.properties";
	public static final String MAVEN_REPO_URL = "https://unei.gitlab.io/maven/<group>/<artifact>/maven-metadata.xml";

	private static final Map<IPlugin, Updater> cachedUpdaters = new HashMap<IPlugin, Updater>();
//	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private IPlugin plugin;
	private String pomResourcePath;
	private String mavenRepoUrl;
	private String cachedCurrentVersion;
	private MavenMeta cachedMavenMeta;

	private Updater(IPlugin plugin) {
		this.plugin = plugin;
		this.pomResourcePath = Updater.POM_RESOURCE_PATH.replace("<groupId>", MAVEN_GROUPID).replace("<artifactId>",
				this.getArtifactName());
		this.mavenRepoUrl = Updater.MAVEN_REPO_URL.replace("<group>", MAVEN_GROUPID.replace('.', '/'))
				.replace("<artifact>", this.getClassName());
		this.cachedCurrentVersion = null;

		cachedUpdaters.put(plugin, this);
	}

	private String getClassName() {
		return this.plugin.getClass().getSimpleName();
	}

	private String getArtifactName() {
		return this.plugin.getClass().getSimpleName().toLowerCase();
	}

	private MavenMeta getMavenMeta() {
		if (this.cachedMavenMeta != null)
			return this.cachedMavenMeta;

		MavenMeta meta = MavenMeta.parseData(this.mavenRepoUrl);
		if (meta != null)
			this.cachedMavenMeta = meta;
		return meta;
	}

	public static Updater getUpdater(IPlugin plugin) {
		if (!Updater.cachedUpdaters.containsKey(plugin))
			cachedUpdaters.put(plugin, new Updater(plugin));

		return cachedUpdaters.get(plugin);
	}

	public void checkVersionAsync(final IUpdater.ICallback callback) {
		Thread thread = new Thread("Version Checker Thread") {
			public void run() {
				IUpdater.Result result = Updater.this.checkVersion();

				if (callback != null)
					callback.run(Updater.this, result);
				else
					new Callback() {
					}.run(Updater.this, result);
			}
		};
		thread.start();
	}

	public IUpdater.Result checkVersion() {
		this.plugin.getLogger().fine("Checking plugin version...");

		String latestVersion = this.getLatestVersion();
		String currentVersion = this.getCurrentVersion();

		this.plugin.getLogger().fine("Finished checking latest version for " + this.getClassName() + ":");
		this.plugin.getLogger()
				.fine("Current version is " + currentVersion + ". Latest version is " + latestVersion + ".");

		if (latestVersion == null || currentVersion == null)
			return IUpdater.Result.FAILED;
		if (latestVersion.equalsIgnoreCase(currentVersion))
			return IUpdater.Result.NO_UPDATE;
		return IUpdater.Result.UPDATE_AVAILABLE;
	}

	public String getCurrentVersion() {
		if (this.cachedCurrentVersion != null)
			return this.cachedCurrentVersion;

		InputStream in = null;

		try {
			in = this.plugin.getResource(this.pomResourcePath);

			Properties pom = new Properties();
			pom.load(in);
			in.close();
			in = null;

			String version = pom.containsKey("version") ? pom.getProperty("version") : null;

			if (version != null) {
				this.cachedCurrentVersion = version;
				return version;
			}
		} catch (IOException e) {
			e.printStackTrace();

			if (in != null) {

				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return "UNKNOWN";
	}

	public String getLatestVersion() {
		MavenMeta meta = this.getMavenMeta();
		return (meta != null) ? meta.getLatestVersion() : null;
	}

	public Date getLastUpdateTime() {
		MavenMeta meta = this.getMavenMeta();
		return (meta != null) ? meta.getLastUpdateTime() : null;
	}

	public String getReadableLastUpdateTime() {
		Date updateTime = this.getLastUpdateTime();
		if (updateTime == null)
			return "UNKNOWN";

		Date now = new Date();
		long delta = now.getTime() - updateTime.getTime();

		int second = 1000;
		int minute = second * 60;
		int hour = minute * 60;
		int day = hour * 24;
		int week = day * 7;
		int month = day * 30;
		int year = day * 365;

		if (delta < second)
			return "just now";
		if (delta < minute)
			return Math.floor(delta / second) + " seconds ago";

		if (delta < 2 * minute)
			return "a minute ago";
		if (delta < hour)
			return Math.floor(delta / minute) + " minutes ago";

		if (delta < hour * 2)
			return "an hour ago";
		if (delta < day)
			return Math.floor(delta / hour) + " hours ago";

		if (delta < day * 2)
			return "1 day ago";
		if (delta < week)
			return Math.floor(delta / day) + " days ago";

		if (delta < week * 2)
			return "a week ago";
		if (delta < month)
			return Math.floor(delta / week) + " weeks ago";

		if (delta < month * 2)
			return "a month ago";
		if (delta < year)
			return Math.floor(delta / month) + " months ago";

		if (delta < year * 2)
			return "a year ago";
		return Math.floor(delta / year) + " years ago";
	}

	@Deprecated
	public static abstract class UpdateCheckCallback {
		@Deprecated
		public abstract void run(boolean res);
	}

	public static abstract class Callback implements IUpdater.ICallback {

		public void run(IUpdater iUpdater, IUpdater.Result result) {
			Updater updater = (Updater) iUpdater;

			switch (result) {
				case NO_UPDATE:
					updater.plugin.getLogger().fine("You are using the latest version of " + updater.getClassName()
							+ ": v" + updater.getCurrentVersion() + "!");
					break;

				case UPDATE_AVAILABLE:
					updater.plugin.getLogger().warning("Oops.. " + updater.getClassName()
							+ " seems to be out of date! This could cause problems.");
					updater.plugin.getLogger().warning("The latest version is v" + updater.getLatestVersion()
							+ ", but you are running v" + updater.getCurrentVersion() + ".");
					updater.plugin.getLogger()
							.warning("This updated version was released " + updater.getReadableLastUpdateTime() + ".");
					updater.plugin.getLogger().warning(
							"We recommend updating it. More information on how to do so here: https://unei.gitlab.io/");
					break;

				case FAILED:
					updater.plugin.getLogger()
							.warning("Wasn't able to check the latest version of " + updater.getClassName() + ".");
					updater.plugin.getLogger().warning(
							"Check your internet connection, or there might be an update available to fix this.");
					break;
			}
		}
	}

	public static enum Result {

		NO_UPDATE,
		UPDATE_AVAILABLE,
		FAILED;

	}

	private static class MavenMeta {

		public static final String LATEST_VERSION_PATH = "//metadata/versioning/latest";
		public static final String LAST_UPDATE_TIME_PATH = "//metadata/versioning/lastUpdated";

		private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		private String latestVersion = null;
		private Date lastUpdateTime = null;

		private MavenMeta(Document document) throws XPathExpressionException, ParseException {
			XPath path = XPathFactory.newInstance().newXPath();
			Node root = document.getDocumentElement();

			this.latestVersion = path.evaluate(MavenMeta.LATEST_VERSION_PATH, root);
			this.lastUpdateTime = MavenMeta.dateFormat.parse(path.evaluate(MavenMeta.LAST_UPDATE_TIME_PATH, root));
		}

		public static MavenMeta parseData(String url) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setIgnoringElementContentWhitespace(true);
				DocumentBuilder builder = factory.newDocumentBuilder();

				return new MavenMeta(builder.parse(url));
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			} catch (XPathExpressionException e) {
			} catch (ParseException e) {
			}

			return null;
		}

		public String getLatestVersion() {
			return this.latestVersion;
		}

		public Date getLastUpdateTime() {
			return this.lastUpdateTime;
		}
	}
}
