package com.cloud450GH.image.converter.ui;

import com.cloud450GH.image.converter.ui.i18n.Str;
import com.cloud450GH.image.converter.ui.i18n.StrKeys;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

/**
 * Fairly simple main window.
 * <p>
 * author: cloud450GH on GitHub
 */
public class MainWindow extends JFrame {

	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(450, 150);

	public static final int TOOLTIP_DISMISS_DELAY_MS = 15000;

	// Icon files, found in the resources folder
	protected static final List<String> IMAGE_LIST = List.of(
			"/images/app-image-24.png",
			"/images/app-image-32.png",
			"/images/app-image-48.png"
	);

	protected void setupIcons() {
		List<Image> images = IMAGE_LIST.stream()
				.map(getClass()::getResource)
				.filter(Objects::nonNull)
				.map(Toolkit.getDefaultToolkit()::getImage)
				.filter(Objects::nonNull)
				.toList();

		if (!images.isEmpty()) {
			setIconImages(images);
		}
	}

	public MainWindow() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		setupIcons();

		ToolTipManager.sharedInstance().setDismissDelay(TOOLTIP_DISMISS_DELAY_MS);
		add(new MainPanel());
		
		setSize(DEFAULT_WINDOW_SIZE);
		setTitle(Str.t(StrKeys.APP_TITLE));
		setResizable(false);
		
		centerWindow();
	}
	
	/**
	 * Center the window on the screen.
	 */
	public void centerWindow() {
		// Center window
		Dimension winDim = getToolkit().getScreenSize();
		int xLoc = winDim.width / 2 - this.getWidth() / 2;
		int yLoc = winDim.height / 2 - this.getHeight() / 2;
		setLocation(xLoc, yLoc);
	}

	@SuppressWarnings("unused")
	protected void setupMenuBar() {
		JMenuBar mb = new JMenuBar();
		
		JMenu menu = new JMenu("[PH] File");
		JMenuItem mi = new JMenuItem("[PH] Quit");
		mi.addActionListener(e -> System.exit(0));
		
		menu.add(mi);
		mb.add(menu);
		
		setJMenuBar(mb);
	}
}
