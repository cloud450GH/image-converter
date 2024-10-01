package com.cloud450GH.image.converter.ui;

import javax.swing.JOptionPane;

/**
 * Shorthand for message boxes.
 * 
 * author: cloud450GH on GitHub
 */
public class MsgBox {

	public static void error(String errStr) {
		JOptionPane.showMessageDialog(null, errStr, "Error", JOptionPane.ERROR_MESSAGE, null);
	}
}
