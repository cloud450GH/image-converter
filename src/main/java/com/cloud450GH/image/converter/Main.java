package com.cloud450GH.image.converter;

import org.greenrobot.eventbus.EventBus;

import com.cloud450GH.image.converter.ui.MainWindow;
import com.cloud450GH.image.converter.ui.MsgBox;

/**
 * Super simple main.
 * 
 * author: cloud450GH on GitHub
 */
public class Main {
	
	public static final EventBus SYNC_BUS = EventBus.getDefault();
	
	public static void main(String[] args) {
		try {
			// Create the window
			MainWindow mw = new MainWindow();
			mw.setVisible(true);
		}
		catch (Throwable t) {
			MsgBox.error(t.getMessage());
		}
	}
}
