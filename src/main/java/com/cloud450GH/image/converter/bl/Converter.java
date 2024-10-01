package com.cloud450GH.image.converter.bl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import com.cloud450GH.image.converter.ImageTypes;
import com.cloud450GH.image.converter.ImageTypes.SupportedImageType;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.GifWriter;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.nio.JpegWriter;
import com.sksamuel.scrimage.nio.PngWriter;
import com.sksamuel.scrimage.webp.WebpWriter;

/**
 * Utility methods to do actual conversions between image types.
 * 
 * author: cloud450GH on GitHub
 */
public class Converter {

	public enum ConvertResult {
		DIR_NOT_FOUND,
		FILE_NOT_FOUND,
		FILE_ALREADY_EXISTS,
		FILE_CREATED,
		FILE_TYPE_MISSING,
		UNKNOWN
	}
	
	protected static Map<SupportedImageType, ImageWriter> writerMap;
	
	static {
		writerMap = new HashMap<>();
		writerMap.put(SupportedImageType.GIF, GifWriter.Default);
		writerMap.put(SupportedImageType.JPG, JpegWriter.Default);
		writerMap.put(SupportedImageType.PNG, PngWriter.MinCompression);
		writerMap.put(SupportedImageType.WEBP, WebpWriter.DEFAULT);
	}
	
	public static ImageWriter getWriter(SupportedImageType type) {
		return writerMap.get(type);
	}
	
	public static List<ConvertResult> go(File sourceFile, SupportedImageType targetType) throws IOException {
		if (sourceFile == null || !sourceFile.exists()) {
			return Collections.singletonList(ConvertResult.FILE_NOT_FOUND);
		}
		
		return sourceFile.isFile() ? Collections.singletonList(goFile(sourceFile, targetType)) : goDir(sourceFile, targetType);
	}
	
	public static ConvertResult goFile(File sourceFile, SupportedImageType targetType) throws IOException {
		if (!sourceFile.exists() || !sourceFile.isFile()) {
			return ConvertResult.FILE_NOT_FOUND;
		}
		
		if (targetType == null) {
			return ConvertResult.FILE_TYPE_MISSING;
		}
		
		ImmutableImage image = ImmutableImage.loader().fromFile(sourceFile);
		String newFileName = FilenameUtils.removeExtension(sourceFile.getName()) + ImageTypes.getDotExt(targetType);
		File newPath = sourceFile.getParentFile().toPath().resolve(newFileName).toFile();
		
		if (newPath.exists()) {
			// if this file exists, abort?
			return ConvertResult.FILE_ALREADY_EXISTS;
		}
		
		ImageWriter writer = getWriter(targetType);
		image.output(writer, newPath);
		
		return ConvertResult.FILE_CREATED;
	}
	
	public static List<ConvertResult> goDir(File sourceDir, SupportedImageType targetType) throws IOException {
		if (sourceDir == null || !sourceDir.exists()) {
			return Collections.singletonList(ConvertResult.DIR_NOT_FOUND);
		}
		
		if (targetType == null) {
			return Collections.singletonList(ConvertResult.FILE_TYPE_MISSING);
		}
		
		Collection<String> targetExts = ImageTypes.getSupportedExtensions();
		targetExts.remove(ImageTypes.getExt(targetType));
		
		List<File> targetFiles = Stream.of(sourceDir.listFiles())
				.filter(file -> targetExts.contains(FilenameUtils.getExtension(file.getName())))
				.collect(Collectors.toList());
		
		List<ConvertResult> retVal = targetFiles.stream()
				.map(f -> {
					try {
						return goFile(f, targetType);
					}
					catch (Throwable t) {
						return ConvertResult.UNKNOWN;
					}
				})
				.collect(Collectors.toList());
		
		return retVal;
	}
}
