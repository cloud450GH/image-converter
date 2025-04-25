package com.cloud450GH.image.converter.ui;

import com.cloud450GH.image.converter.ui.i18n.Str;
import com.cloud450GH.image.converter.ui.i18n.StrKeys;

import javax.swing.JOptionPane;

/**
 * Shorthand for message boxes.
 * <br>
 * author: cloud450GH on GitHub
 */
public class MsgBox {

	public static void error(String errStr) {
		JOptionPane.showMessageDialog(null, errStr, Str.t(StrKeys.ERROR_TITLE), JOptionPane.ERROR_MESSAGE, null);
	}
}
