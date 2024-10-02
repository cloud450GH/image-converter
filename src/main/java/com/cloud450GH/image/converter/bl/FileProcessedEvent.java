package com.cloud450GH.image.converter.bl;

import java.io.File;

import com.cloud450GH.image.converter.bl.Converter.ConvertResult;

public class FileProcessedEvent {

	protected File f;
	protected ConvertResult result;
	
	public FileProcessedEvent(File file, ConvertResult res) {
		this.f = file;
		this.result = res;
	}
	
	public File getFile() {return f;}
	
	public ConvertResult getResult() {return result;}
}
