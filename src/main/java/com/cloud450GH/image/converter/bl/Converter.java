package com.cloud450GH.image.converter.bl;

import com.cloud450GH.image.converter.ImageTypes;
import com.cloud450GH.image.converter.ImageTypes.SupportedImageType;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.GifWriter;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.nio.JpegWriter;
import com.sksamuel.scrimage.nio.PngWriter;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods to do actual conversions between image types.
 * <p>
 * author: cloud450GH on GitHub
 */
public class Converter {

	public enum ConvertResult {
		CANCELED,
		DIR_NOT_FOUND,
		FILE_NOT_FOUND,
		FILE_ALREADY_EXISTS,
		FILE_CREATED,
		FILE_TYPE_MISSING,
		UNKNOWN
	}

	protected static boolean cancelProcessing = false;

	protected static boolean parallelProcessing = false;

	protected static final Map<SupportedImageType, ImageWriter> writerMap;
	
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
	
	public static List<ConvertResult> go(File sourceFile, SupportedImageType targetType, EventBus bus) throws IOException {
		if (sourceFile == null || !sourceFile.exists()) {
			return Collections.emptyList();
		}

		cancelProcessing = false;
		
		return sourceFile.isFile() ? Collections.singletonList(goFile(sourceFile, targetType)) : goDir(sourceFile, targetType, bus);
	}
	
	public static ConvertResult goFile(File sourceFile, SupportedImageType targetType) throws IOException {
		if (!sourceFile.exists() || !sourceFile.isFile()) {
			return ConvertResult.FILE_NOT_FOUND;
		}
		
		if (targetType == null) {
			return ConvertResult.FILE_TYPE_MISSING;
		}

		String newFileName = FilenameUtils.removeExtension(sourceFile.getName()) + ImageTypes.getDotExt(targetType);
		File newPath = sourceFile.getParentFile().toPath().resolve(newFileName).toFile();
		
		if (newPath.exists()) {
			// if this file exists, return that result
			return ConvertResult.FILE_ALREADY_EXISTS;
		}

		ImmutableImage image = ImmutableImage.loader().fromFile(sourceFile);
		ImageWriter writer = getWriter(targetType);
		image.output(writer, newPath);
		
		return ConvertResult.FILE_CREATED;
	}

	@SuppressWarnings("unused")
	public static List<ConvertResult> goDir(File sourceDir, SupportedImageType targetType) {
		return goDir(sourceDir, targetType, null);
	}

	public static void cancel() {
		cancelProcessing = true;
	}

	public static boolean isParallelProcessing() {return parallelProcessing;}

	public static void setParallelProcessing(boolean pp) {
		parallelProcessing = pp;
	}

	public static List<ConvertResult> goDir(File sourceDir, SupportedImageType targetType, EventBus bus) {
		if (sourceDir == null || !sourceDir.exists()) {
			return Collections.singletonList(ConvertResult.DIR_NOT_FOUND);
		}
		
		if (targetType == null) {
			return Collections.singletonList(ConvertResult.FILE_TYPE_MISSING);
		}
		
		Collection<String> targetExtensions = ImageTypes.getSupportedExtensions();
		targetExtensions.remove(ImageTypes.getExt(targetType));

		File[] files = sourceDir.listFiles();
		if (files == null) {
			return Collections.singletonList(ConvertResult.CANCELED);
		}

		// Get the files in the directory
		// Filter out files that do not match our supported extensions
		// Use a map to remove duplicate files that have different extensions (e.g. "abc.jpg" "abc.gif")
		// take the values of this process (File objects) and create the list.
		List<File> targetFiles = Arrays.stream(files)
				.filter(file -> targetExtensions.contains(FilenameUtils.getExtension(file.getName())))
				.collect(Collectors.toMap(
						f -> FilenameUtils.removeExtension(f.getName()),
						f -> f,
						(a, b) -> a)
				)
				.values()
				.stream()
				.toList();

		Stream<File> stream = parallelProcessing ?
				targetFiles.parallelStream() :
				targetFiles.stream();

		return stream.map(f -> {
			ConvertResult res;
			try {
					res = !cancelProcessing ? goFile(f, targetType) : ConvertResult.CANCELED;
				}
				catch (Throwable t) {
					res = ConvertResult.UNKNOWN;
				}
				maybeFireEvent(bus, f, res);
				return res;
			})
			.collect(Collectors.toList());
	}
	
	protected static void maybeFireEvent(EventBus bus, File f, ConvertResult res) {
		if (bus == null || f == null || res == null) {
			return;
		}
		
		bus.post(new FileProcessedEvent(f, res));
	}
}
