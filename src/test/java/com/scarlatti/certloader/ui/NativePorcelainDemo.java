/* This file is part of JnaFileChooser.
 *
 * JnaFileChooser is free software: you can redistribute it and/or modify it
 * under the terms of the new BSD license.
 *
 * JnaFileChooser is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.scarlatti.certloader.ui;

import com.scarlatti.certloader.utils.WindowsFileChooser;
import com.sun.jna.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class NativePorcelainDemo {
	public static void main(String[] args) throws Exception {
		if (!Platform.isWindows()) {
			System.err.println("Sorry, this only works on Windows");
			JOptionPane.showMessageDialog(null, "Sorry this only works on Windows",
					"OS not supported", JOptionPane.ERROR_MESSAGE);
//			System.exit(1);
		}

//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		final JFrame frame = new JFrame(NativePorcelainDemo.class.getName());
		final JPanel content = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
		final JButton chooseFile = new JButton("Choose File");
		final JButton chooseDir = new JButton("Choose Folder");
		content.add(chooseFile);
		content.add(chooseDir);

		chooseFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final WindowsFileChooser fc = new WindowsFileChooser();
				fc.withFilter("All Files", "*");
				fc.withFilter("Pictures", "jpg", "jpeg", "gif", "bmp", "png");
				fc.withFilter("Text Files", "txt", "log", "nfo", "xml");
				fc.withTitle("Choose Keystore");
//				fc.withInitialFile("C:/Users/pc/Desktop/ReactTest1/package.json");
				fc.withInitialDirectory("C:/Users/pc/Desktop/ReactTest1");
				if (fc.showOpenDialog(frame)) {
					final File f = fc.getSelectedFile();
					JOptionPane.showMessageDialog(frame, f.getAbsolutePath(),
						"Selection", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		frame.setContentPane(content);
		frame.pack();
		frame.setSize(frame.getWidth() * 2, frame.getHeight());
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
}
