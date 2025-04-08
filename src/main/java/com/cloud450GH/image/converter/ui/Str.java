package com.cloud450GH.image.converter.ui;

import com.cloud450GH.image.converter.bl.Converter.ConvertResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to map data objects to String labels.
 */
public class Str {

	protected static final Map<ConvertResult, String> crMap;
	protected static final String MISSING = "Missing";

	static {
		crMap = new HashMap<>();
		crMap.put(ConvertResult.CANCELED, "Cancelled");
		crMap.put(ConvertResult.DIR_NOT_FOUND, "Dir Not Found");
		crMap.put(ConvertResult.FILE_NOT_FOUND, "File Not Found");
		crMap.put(ConvertResult.FILE_ALREADY_EXISTS, "File Exists");
		crMap.put(ConvertResult.FILE_TYPE_MISSING, "File Type Missing");
		crMap.put(ConvertResult.FILE_CREATED, "Created");
	}

	public static String getLabel(ConvertResult result) {
		return crMap.getOrDefault(result, MISSING);
	}
}
