package com.cloud450GH.image.converter.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.greenrobot.eventbus.Subscribe;

import com.cloud450GH.image.converter.ImageTypes.SupportedImageType;
import com.cloud450GH.image.converter.Main;
import com.cloud450GH.image.converter.bl.Converter;
import com.cloud450GH.image.converter.bl.Converter.ConvertResult;
import com.cloud450GH.image.converter.bl.FileProcessedEvent;

/**
 * The UI panel for the app. Creates the controls, lays them out, responds to actions from
 * the user.
 * <p>
 * author: cloud450GH on GitHub
 */
public class MainPanel extends JPanel {

	protected SelectButton sBtn; // pick the file or directory
	
	protected JComboBox<SupportedImageType> targetTypeCombo; // pick the target image file type
	
	protected JLabel status; // report status (success, error, whatever)
	
	protected JButton go; // Begin!

	protected JButton stop;

	protected JCheckBox parallelCB;
	
	protected List<JComponent> widgets; // for setEnabled(false/true) when we start/stop conversion.
	
	public MainPanel() {
		
		// Just playing with borders
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		//border = BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(12, 12, 12), new Color(200, 200, 200));
		this.setBorder(border);
		
		createWidgets();
		
		layoutPanel();
	}
	
	protected void layoutPanel() {
		// Layout the controls
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// We'll put the widgets on the top
		top.add(sBtn);
		top.add(targetTypeCombo);
		top.add(go);
		top.add(stop);

		JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEADING));
		middle.add(parallelCB);
		
		// We'll put a status/result text on the bottom
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEADING));
		bottom.add(status = new JLabel());
		
		this.setLayout(new GridLayout(0, 1));
		this.add(top);
		this.add(middle);
		this.add(bottom);
	}
	
	protected void createWidgets() {
		// The various types we can convert to...
		targetTypeCombo = new JComboBox<>(SupportedImageType.values());
		targetTypeCombo.setSelectedItem(SupportedImageType.JPG);
		
		// When the user wants to execute...
		go = new JButton("Go");
		go.addActionListener((evt) -> {
			widgets.forEach(w -> w.setEnabled(false));
			stop.setEnabled(true);

			status.setText("Processing...");
			File f = sBtn.getChooser().getSelectedFile();
			SupportedImageType type = (SupportedImageType)targetTypeCombo.getSelectedItem();
			
			CompletableFuture<List<ConvertResult>> future = CompletableFuture.supplyAsync(() -> {
				try {
					return Converter.go(f, type, Main.BUS);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			
			future = future.exceptionally((t) -> {
				MsgBox.error(t.getMessage());
				status.setText("Unexpected error occurred: " + t.getMessage());
				return null;
			});
			
			future.thenAccept((results) -> {
				showResults(results);
				widgets.forEach(w -> w.setEnabled(true));
				stop.setEnabled(false);
			});
		});
		
		Main.BUS.register(this);
		
		// Create select button
		sBtn = new SelectButton();

		// Stop Button
		stop = new JButton(("Stop"));
		stop.setEnabled(false);
		stop.addActionListener((evt) -> {
			stop.setEnabled(false);
			Converter.cancel();
		});

		parallelCB = new JCheckBox("Parallel Processing");
		parallelCB.setSelected(Converter.isParallelProcessing());
		parallelCB.addActionListener(evt ->
				Converter.setParallelProcessing(parallelCB.isSelected())
		);
		
		// Track widgets to disable during processing
		// This prevents spam clicking and we run the conversions off
		// of the Swing thread so the program doesn't lock up.
		widgets = new ArrayList<>();
		widgets.add(go);
		widgets.add(targetTypeCombo);
		widgets.add(sBtn);
		widgets.add(parallelCB);
	}

	protected void showResults(Collection<ConvertResult> results) {
		Objects.requireNonNull(results);
		int totalFiles = results.size();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Processed %d files!", totalFiles));

		// Count how many occurrences of each status we've seen
		Map<ConvertResult, Integer> map = results.stream()
				.collect(Collectors.toMap(
						res -> res,
						r -> 1,
						Integer::sum));

		for (var entry : map.entrySet()) {
			int count = entry.getValue();
			if (count > 0) {
				sb.append(' ')
					.append(Str.getLabel(entry.getKey()))
					.append(String.format("(%d).", count));
			}
		}

		status.setText(sb.toString());
	}
	
	@Subscribe/*(threadMode = ThreadMode.POSTING)*/
	public void onProcess(FileProcessedEvent evt) {
		status.setText("Processed: " + evt.file().getName());
	}
}
