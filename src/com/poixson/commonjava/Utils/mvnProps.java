package com.poixson.commonjava.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class mvnProps {

	private static final String PROPS_FILE = "/app.properties";

	private static final Map<String, mvnProps> instances = new ConcurrentHashMap<String, mvnProps>();

	// properties
	public final String name;
	public final String title;
	public final String full_title;
	public final String version;
	public final String url;
	public final String org_name;
	public final String org_url;
	public final String issue_name;
	public final String issue_url;



	// get instance
	public static mvnProps get(final Class<?> clss) {
		final String className = clss.getName();
		{
			final mvnProps props = instances.get(className);
			if(props != null)
				return props;
		}
		synchronized(instances) {
			if(instances.containsKey(className))
				return instances.get(className);
			final mvnProps props = new mvnProps(clss);
			instances.put(className, props);
			Keeper.add(props);
			return props;
		}
	}



	// load app.properties file
	public mvnProps(final Class<?> clss) {
		Properties props = null;
		InputStream in = null;
		try {
			props = new Properties();
			in = clss.getResourceAsStream(PROPS_FILE);
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			utils.safeClose(in);
		}
		this.name       = props.getProperty("name");
		this.title      = props.getProperty("title");
		this.version    = props.getProperty("version");
		this.full_title = this.title+" "+this.version;
		this.url        = props.getProperty("url");
		this.org_name   = props.getProperty("org_name");
		this.org_url    = props.getProperty("org_url");
		this.issue_name = props.getProperty("issue_name");
		this.issue_url  = props.getProperty("issue_url");
	}



}