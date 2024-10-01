package com.cloud450GH.image.converter.ui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Fairly simple main window.
 * 
 * author: cloud450GH on GitHub
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(450, 130);
	
	public MainWindow() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		
//		setupMenuBar();
		
		this.add(new MainPanel());
		
		this.setSize(DEFAULT_WINDOW_SIZE);
		this.setTitle("Convert Image(s)");
		
		centerWindow();
	}
	
	/**
	 * Center the window on the screen.
	 */
	public void centerWindow() {
		// Center window
		Dimension winDim = getToolkit().getScreenSize();
		int xLoc = winDim.width / 2 - this.getWidth() / 2;
		int yLoc = winDim.height / 2 - this.getHeight() /2;
		this.setLocation(xLoc, yLoc);
	}
	
	protected void setupMenuBar() {
		JMenuBar mb = new JMenuBar();
		
		JMenu menu = new JMenu("File");
		JMenuItem mi = new JMenuItem("Quit");
		mi.addActionListener(e -> {
			System.exit(0);
		});
		
		menu.add(mi);
		mb.add(menu);
		
		this.setJMenuBar(mb);
	}
}
