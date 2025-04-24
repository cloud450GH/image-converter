package com.cloud450GH.image.converter.ui;

import com.cloud450GH.image.converter.ImageTypes;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * A file/directory selection widget. Lets the user select a file or directory
 * and can be accessed by whatever UI is leveraging this widget.
 * <p>
 * author: cloud450GH on GitHub
 */
public class SelectButton extends JPanel {

	public static final String DEFAULT_LABEL = "Select...";
	public static final String DEFAULT_TOOLTIP = "Select an image file or a directory containing images.";
	
	protected JButton fileButton;
	protected JFileChooser fileChooser;
	
	public SelectButton() {
		
		fileButton = new JButton(DEFAULT_LABEL);
		fileButton.setToolTipText(DEFAULT_TOOLTIP);
		fileButton.addActionListener((ae) -> {
			int returnVal = fileChooser.showOpenDialog(SelectButton.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				updateFileDisplay();
			}
		});
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new EventFilesFilter());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		File workingDir = Paths.get("").toFile();
		fileChooser.setCurrentDirectory(workingDir);
		
		fileChooser.addActionListener(e -> updateFileDisplay());
		
		FlowLayout layout = new FlowLayout(FlowLayout.LEADING);
		this.setLayout(layout);
		this.add(new JLabel("Image or Directory:"));
		
		fileButton.setPreferredSize(new Dimension(100, 25));
		this.add(fileButton);
	}
	
	protected void updateFileDisplay() {
		File f = fileChooser.getSelectedFile();
		String name = f != null ? f.getName() : DEFAULT_LABEL;
		fileButton.setText(name);
		if (f != null) {
			fileButton.setToolTipText(f.getAbsolutePath());
		}
	}
	
	@Override
	public void setEnabled(boolean enable) {
		super.setEnabled(enable);
		fileButton.setEnabled(enable);
	}
	
	public JFileChooser getChooser() {return fileChooser;}
	
	protected static class EventFilesFilter extends FileFilter {
		
		protected Set<String> allowedExtensions;
		
		public EventFilesFilter() {
			allowedExtensions = new HashSet<>();
			allowedExtensions.addAll(ImageTypes.getSupportedExtensions());
		}
		
		@Override
		public boolean accept(File file) {
			if (file == null || !file.exists()) {
				return false;
			}
			
			// Need this to navigate directories apparently
			if (file.isDirectory()) {
				return true;
			}
			
			String extension = FilenameUtils.getExtension(file.getName());
			extension = extension.toLowerCase();

			return allowedExtensions.contains(extension);
		}

		@Override
		public String getDescription() {
			return "Supported Image Files";
		}
	}
}
