package com.cloud450GH.image.converter.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.cloud450GH.image.converter.ImageTypes.SupportedImageType;
import com.cloud450GH.image.converter.Main;
import com.cloud450GH.image.converter.bl.Converter;
import com.cloud450GH.image.converter.bl.Converter.ConvertResult;
import com.cloud450GH.image.converter.bl.FileProcessedEvent;

/**
 * The UI panel for the app. Creates the controls, lays them out, responds to actions from
 * the user.
 * 
 * author: cloud450GH on GitHub
 */
@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	protected SelectButton sBtn; // pick the file or directory
	
	protected JComboBox<SupportedImageType> targetTypeCombo; // pick the target image file type
	
	protected JLabel status; // report status (success, error, whatever)
	
	protected JButton go; // Begin!
	
	protected List<JComponent> widgets; // for setEnabled(false/true) when we start/stop conversion.
	
	public MainPanel() {
		
		// Just playing with borders
		Border border = null;
		border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
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
		
		// We'll put a status/result text on the bottom
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEADING));
		bottom.add(status = new JLabel());
		
		this.setLayout(new GridLayout(0, 1));
		this.add(top);
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
			status.setText("Processing...");
			File f = sBtn.getChooser().getSelectedFile();
			SupportedImageType type = (SupportedImageType)targetTypeCombo.getSelectedItem();
			
			CompletableFuture<List<ConvertResult>> future = CompletableFuture.supplyAsync(() -> {
				try {
					return Converter.go(f, type, Main.SYNC_BUS);
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
				if (results != null) {
					status.setText("Processed " + results.size() + " files!");
				}
				widgets.forEach(w -> w.setEnabled(true));
			});
		});
		
		Main.SYNC_BUS.register(this);
		
		// Create select button
		sBtn = new SelectButton();
		
		// Track widgets to disable during processing
		// This prevents spam clicking and we run the conversions off
		// of the Swing thread so the program doesn't lock up.
		widgets = new ArrayList<>();
		widgets.add(go);
		widgets.add(targetTypeCombo);
		widgets.add(sBtn);
	}
	
	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onProcess(FileProcessedEvent evt) {
		status.setText("Processed: " + evt.getFile().getName());
	}
}
