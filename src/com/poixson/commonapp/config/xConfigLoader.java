package com.poixson.commonapp.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import com.poixson.commonjava.Utils.utils;
import com.poixson.commonjava.Utils.utilsDirFile;
import com.poixson.commonjava.Utils.utilsString;
import com.poixson.commonjava.xLogger.xLog;


public final class xConfigLoader {
	private xConfigLoader() {}
	private static final String LOG_NAME = "CONFIG";



	// file
	public static xConfig Load(final String file) {
		return Load(
				file,
				xConfig.class
		);
	}
	// file, class
	public static xConfig Load(final String file, final Class<? extends xConfig> clss) {
		return Load(
				file,
				clss,
				false
		);
	}
	// file, class, injar
	public static xConfig Load(final String file,
			final Class<? extends xConfig> clss, boolean checkInJar) {
		return Load(
				(String) null,
				file,
				clss,
				checkInJar
		);
	}
	// path, file, class, injar
	public static xConfig Load(final String path, final String file,
			final Class<? extends xConfig> clss, boolean checkInJar) {
		if(utils.isEmpty(file)) throw new NullPointerException("file argument is required!");
		if(clss == null)        throw new NullPointerException("clss argument is required!");
		// load file.yml
		{
			final String fileStr = (utils.isEmpty(path) ? "" : utilsString.ensureEnds(File.separator, path))+file;
			log().fine("Loading config file: "+fileStr);
			final InputStream in = utilsDirFile.OpenFile(
					new File(fileStr)
			);
			if(in != null)
				return Load(in, clss);
		}
		// try loading as resource
		if(checkInJar) {
			final InputStream in = utilsDirFile.OpenResource(file);
			if(in != null) {
				log().fine("Loaded config from jar: "+file);
				final xConfig config = Load(in, clss);
				if(config != null) {
					config.loadedFromResource = true;
					Save(
							(utils.isEmpty(path) ? null : new File(path)),
							new File(file),
							config.datamap
					);
					return config;
				}
			}
		}
		return null;
	}



	// load from jar
	public static xConfig LoadJar(final File jarFile, final String ymlFile) {
		return LoadJar(jarFile, ymlFile, xConfig.class);
	}
	public static xConfig LoadJar(final File jarFile, final String ymlFile, final Class<? extends xConfig> clss) {
		if(jarFile == null)        throw new NullPointerException("jarFile argument is required!");
		if(utils.isEmpty(ymlFile)) throw new NullPointerException("yamlFile argument is required!");
		if(clss == null)           throw new NullPointerException("clss argument is required!");
		final utilsDirFile.InputJar in = utilsDirFile.OpenJarResource(jarFile, ymlFile);
		if(in == null) return null;
		try {
			return Load(in.fileInput, clss);
		} finally {
			utils.safeClose(in);
		}
	}



	public static <T> xConfig Load(final InputStream in, final Class<? extends xConfig> clss) {
		if(in   == null) throw new NullPointerException("in argument is required!");
		if(clss == null) throw new NullPointerException("clss argument is required!");
		try {
			final Yaml yml = new Yaml();
			@SuppressWarnings("unchecked")
			final Map<String, Object> datamap = (HashMap<String, Object>) yml.load(in);
			if(utils.isEmpty(datamap))
				return null;
			@SuppressWarnings("unchecked")
			final Constructor<? extends Map<String, Object>> construct =
				(Constructor<? extends Map<String, Object>>) clss.getDeclaredConstructor(Map.class);
			return (xConfig) construct.newInstance(datamap);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
				InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log().trace(e);
		} finally {
			utils.safeClose(in);
		}
		return null;
	}



	public static boolean Save(final File file,
			final Map<String, Object> datamap) {
		return Save(
				(File) null,
				file,
				datamap
		);
	}
	public static boolean Save(final File path, final File file,
			final Map<String, Object> datamap) {
		if(file == null)           throw new NullPointerException("file argument is required!");
		if(utils.isEmpty(datamap)) throw new NullPointerException("datamap argument is required!");
		if(path != null && !path.isDirectory()) {
			if(path.mkdirs()) {
				log().info("Created directory: "+path.toString());
			} else {
				log().severe("Failed to create directory: "+path.toString());
				return false;
			}
		}
		PrintWriter out = null;
		try {
			final Yaml yml = new Yaml();
			out = new PrintWriter(file);
			out.print(
				yml.dumpAs(datamap, Tag.MAP, FlowStyle.BLOCK)
			);
			log().fine("Saved config file: "+file.toString());
			return true;
		} catch (FileNotFoundException e) {
			log().trace(e);
			return false;
		} finally {
			utils.safeClose(out);
		}
	}



	// logger
	public static xLog log() {
		return xLog.getRoot(LOG_NAME);
	}



}
