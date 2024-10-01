package com.cloud450GH.image.converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Meta information about what image types we're going to support.
 * 
 * author: cloud450GH on GitHub
 */
public class ImageTypes {
	
	/**
	 * These are the types of image files we'll convert between.
	 */
	public enum SupportedImageType {
		GIF,
		JPG,
		PNG,
		WEBP
	}
	
	public static Collection<String> getSupportedExtensions() {
		SupportedImageType[] vals = SupportedImageType.values();
		
		Set<String> retVal = new HashSet<>();
		for (var t : vals) {
			retVal.add(getExt(t));
		}
		return retVal;
	}
	
	public static String getExt(SupportedImageType type) {
		return type != null ? type.toString().toLowerCase() : null;
	}
	
	public static String getDotExt(SupportedImageType type) {
		return type == null ? null : "." + getExt(type);
	}
}
