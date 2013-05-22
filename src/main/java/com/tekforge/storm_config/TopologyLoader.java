package com.tekforge.storm_config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class TopologyLoader {
	private static String name;
	
	public static TopologyBuilder Build(String jsonPath)
			throws FileNotFoundException, IOException, ParseException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		TopologyBuilder builder = new TopologyBuilder();

		JSONObject jsonObject = LoadJson(jsonPath);

		TopologyLoader.name = (String) jsonObject.get("name");

		JSONArray boltList = (JSONArray) jsonObject.get("bolts");
		TopologyLoader.LoadBolts(builder, boltList);

		JSONArray spoutList = (JSONArray) jsonObject.get("spouts");
		TopologyLoader.LoadSpouts(builder, spoutList);

		return builder;
	}
	
	public static String GetName() {
		return TopologyLoader.name;
	}

	public static TopologyBuilder LoadSpouts(TopologyBuilder builder,
			String jsonPath) throws FileNotFoundException, IOException,
			ParseException, ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		JSONObject jsonObject = LoadJson(jsonPath);

		JSONArray spoutList = (JSONArray) jsonObject.get("spouts");
		TopologyLoader.LoadSpouts(builder, spoutList);

		return builder;
	}

	public static TopologyBuilder LoadBolts(TopologyBuilder builder,
			String jsonPath) throws FileNotFoundException, IOException,
			ParseException, ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		JSONObject jsonObject = LoadJson(jsonPath);

		JSONArray boltList = (JSONArray) jsonObject.get("bolts");
		TopologyLoader.LoadBolts(builder, boltList);

		return builder;
	}

	private static JSONObject LoadJson(String jsonPath)
			throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();

		Object obj = null;
		obj = parser.parse(new FileReader(jsonPath));

		JSONObject jsonObject = (JSONObject) obj;
		return jsonObject;
	}

	private static void LoadSpouts(TopologyBuilder builder, JSONArray spoutList)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (spoutList == null)
			return;

		for (Object spoutData : spoutList) {
			JSONObject bolt = (JSONObject) spoutData;

			if (bolt.containsKey("id")) {
				if (bolt.containsKey("class")) {
					String id = (String) bolt.get("id");
					String className = (String) bolt.get("class");
					Long task = 1l;
					if (bolt.containsKey("task"))
						task = (Long) bolt.get("task");

					Class<?> clazz = Class.forName(className);
					IRichSpout instance = (IRichSpout) clazz.newInstance();
					builder.setSpout(id, instance, task.intValue());
				} else {
					// no class
				}
			} else {
				// no name
			}
		}
	}

	private static void LoadBolts(TopologyBuilder builder, JSONArray boltList)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (boltList == null)
			return;

		for (Object boltData : boltList) {
			JSONObject bolt = (JSONObject) boltData;

			if (bolt.containsKey("id")) {
				if (bolt.containsKey("class")) {
					String id = (String) bolt.get("id");
					String className = (String) bolt.get("class");
					Long task = 1l;
					if (bolt.containsKey("task"))
						task = (Long) bolt.get("task");

					Class<?> clazz = Class.forName(className);
					IRichBolt instance = (IRichBolt) clazz.newInstance();
					BoltDeclarer b = builder.setBolt(id, instance,
							task.intValue());

					DoShuffleGrouping(bolt, b);
					DoFieldsGrouping(bolt, b);
				} else {
					// no class
				}
			} else {
				// no name
			}
		}
	}

	private static void DoShuffleGrouping(JSONObject bolt, BoltDeclarer b) {
		JSONArray shuffleGrouping = (JSONArray) bolt.get("shuffleGrouping");

		if (shuffleGrouping == null)
			return;

		for (Object sgData : shuffleGrouping) {
			JSONObject sg = (JSONObject) sgData;
			if (sg.containsKey("id")) {
				String sgId = (String) sg.get("id");

				if (sg.containsKey("stream")) {
					String sgStream = (String) sg.get("stream");

					b.shuffleGrouping(sgId, sgStream);
				} else {
					b.shuffleGrouping(sgId);
				}
			} else {
				// No id
			}
		}
	}

	private static void DoFieldsGrouping(JSONObject bolt, BoltDeclarer b) {
		JSONArray fieldGrouping = (JSONArray) bolt.get("fieldsGrouping");

		if (fieldGrouping == null)
			return;

		for (Object sgData : fieldGrouping) {
			JSONObject fg = (JSONObject) sgData;
			if (fg.containsKey("id")) {
				String sgId = (String) fg.get("id");
				if (fg.containsKey("fields")) {
					JSONArray fields = (JSONArray) fg.get("fields");
					List<String> ff = new ArrayList<String>();
					for (Object fData : fields) {
						JSONObject f = (JSONObject) fData;
						ff.add((String) f.get("value"));
					}
					b.fieldsGrouping(sgId, new Fields(ff));
				} else {
					// no fields
				}
			} else {
				// No id
			}
		}
	}
}

