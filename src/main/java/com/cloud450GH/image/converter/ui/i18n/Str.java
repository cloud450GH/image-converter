package com.cloud450GH.image.converter.ui.i18n;

import com.cloud450GH.image.converter.bl.Converter.ConvertResult;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Utility class to map data objects to String labels.
 */
public class Str {

	protected static final ResourceBundle BUNDLE;

	protected static final Map<ConvertResult, String> crMap;
	protected static final String MISSING;

	// lazy hand... t for translate
	public static String t(String key) {return BUNDLE.getString(key);}

	public static String t(String key, Object... args) {
		String format = t(key);
		return MessageFormat.format(format, args);
	}

	static {
		// Location of our localized property files (just English/default for now)
		BUNDLE = ResourceBundle.getBundle("i18n/strings");

		MISSING = t(StrKeys.MISSING);

		crMap = new HashMap<>();
		crMap.put(ConvertResult.CANCELED, t(StrKeys.RESULT_CANCELLED));
		crMap.put(ConvertResult.DIR_NOT_FOUND, t(StrKeys.RESULT_DIR_NOT_FOUND));
		crMap.put(ConvertResult.FILE_NOT_FOUND, t(StrKeys.RESULT_FILE_NOT_FOUND));
		crMap.put(ConvertResult.FILE_ALREADY_EXISTS, t(StrKeys.RESULT_FILE_ALREADY_EXISTS));
		crMap.put(ConvertResult.FILE_TYPE_MISSING, t(StrKeys.RESULT_FILE_TYPE_MISSING));
		crMap.put(ConvertResult.FILE_CREATED, t(StrKeys.RESULT_FILE_CREATED));
	}

	public static String getLabel(ConvertResult result) {
		return crMap.getOrDefault(result, MISSING);
	}
}
