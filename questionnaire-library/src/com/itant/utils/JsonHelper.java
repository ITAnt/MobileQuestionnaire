package com.itant.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
	public static String tojson(Object object) {
		if (null == object)
			return "";
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	public static <T> T convert(String json, Class<T> type) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		T result = null;
		try {

			result = mapper.readValue(json, type);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static <T> T convert(String json, TypeReference<T> type) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		T result = null;
		try {

			result = mapper.readValue(json, type);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * convert json to list.
	 * 
	 * @param json
	 * @param type
	 * @return
	 */
	public static <T> List<T> convertList(String json, Class<T> type) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode;
		List<T> result = null;
		try {
			result = new ArrayList<T>();
			rootNode = mapper.readTree(json);
			if (rootNode.isArray()) {
				for (JsonNode jsonNode : rootNode) {
					T childResult = mapper.readValue(jsonNode.traverse(), type);
					if (childResult != null) {
						result.add(childResult);
					}
				}
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
