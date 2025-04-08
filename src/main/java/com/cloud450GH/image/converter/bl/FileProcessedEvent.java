package com.cloud450GH.image.converter.bl;

import java.io.File;

import com.cloud450GH.image.converter.bl.Converter.ConvertResult;

/**
 * Simple record to indicate a file was processed and the result of its conversion.
 * @param file - The file that was converted.
 * @param result - The result of the conversion.
 */
public record FileProcessedEvent(File file, ConvertResult result) {
}