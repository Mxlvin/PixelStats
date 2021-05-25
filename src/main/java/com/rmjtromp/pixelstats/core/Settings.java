package com.rmjtromp.pixelstats.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rmjtromp.pixelstats.PixelStats;
import com.rmjtromp.pixelstats.core.utils.Console;

public class Settings extends JSONObject {
	
	private static Settings settings = null;
	private static final File file = new File(PixelStats.getModDir() + File.separator + "settings.json");
	
	public static Settings init() throws IOException, JSONException {
		if(settings == null) {
			if(file.exists()) {
				try {
					Console.debug("Loading settings from file");
					settings = new Settings(FileUtils.readFileToString(file, "UTF-8"));
				} catch(JSONException e) {
					Console.debug("Loading default settings from resource");
					try(InputStream stream = Settings.class.getResourceAsStream("/assets/pixelstats/settings.json")) {
						settings = new Settings(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
						settings.save();
					}
				}
			} else {
				Console.debug("Loading default settings from resource");
				try(InputStream stream = Settings.class.getResourceAsStream("/assets/pixelstats/settings.json")) {
					settings = new Settings(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
					settings.save();
				}
			}
		}
		return settings;
	}

	private Settings(String content) throws JSONException {
		super(content);
	}
	
	public void save() {
		Console.debug("Saving settings...");
//		if(!file.exists()) {
//			try {
//				Files.createParentDirs(file);
//				file.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(this.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Object get(String arg0) {
		try {
			return super.get(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean getBoolean(String arg0) {
		try {
			return super.getBoolean(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public double getDouble(String arg0) {
		try {
			return super.getDouble(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int getInt(String arg0) {
		try {
			return super.getInt(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONArray getJSONArray(String arg0) {
		try {
			return super.getJSONArray(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject getJSONObject(String arg0) {
		try {
			return super.getJSONObject(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public long getLong(String arg0) {
		try {
			return super.getLong(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getString(String arg0) {
		try {
			return super.getString(arg0);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject put(String arg0, boolean arg1) {
		try {
			return super.put(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public JSONObject put(String arg0, Collection arg1) {
		try {
			return super.put(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject put(String arg0, double arg1) {
		try {
			return super.put(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject put(String arg0, int arg1) {
		try {
			return super.put(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject put(String arg0, long arg1) {
		try {
			return super.put(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject put(String arg0, @SuppressWarnings("rawtypes") Map arg1) {
		try {
			return super.put(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject put(String arg0, Object arg1) {
		try {
			return super.put(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject putOnce(String arg0, Object arg1) {
		try {
			return super.putOnce(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public JSONObject putOpt(String arg0, Object arg1) {
		try {
			return super.putOpt(arg0, arg1);
		} catch(JSONException e) {
			throw new RuntimeException(e);
		}
	}

}
