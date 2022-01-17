package com.productivity;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.nio.file.Files;

//Runtime.getRuntime().exec("powershell.exe Start-Process notepad.exe -verb RunAs");
//Runtime.getRuntime().exec("powershell.exe Start-Process -FilePath java.exe -Argument '-jar runasadmin.jar' -verb RunAs");

public class gui extends JFrame {
	public static int length = 400;
	public static int height = 300;
	public static boolean onTop = false;
	
	public static Boolean debug = true;
	public static String debugPath = "src\\main\\java\\com\\productivity\\Saves\\";
	static JFrame frame = new JFrame("Produtivity");/*
	static File checkListFile = new File("Saves\\list.TXT");
	static File checkStateFile = new File("Saves\\listCheck.TXT");
	static File colorFile = new File("Saves\\listColor.TXT");
	static File checkListFile = new File("classes\\list.TXT");
	static File checkStateFile = new File("classes\\listCheck.TXT");
	static File colorFile = new File("classes\\listColor.TXT");*/

	static File checkListFile = new File((!debug)?"classes\\list.TXT":debugPath+"list.TXT");
	static File checkStateFile = new File((!debug)?"classes\\listCheck.TXT":debugPath+"listCheck.TXT");
	static File colorFile = new File((!debug)?"classes\\listColor.TXT":debugPath+"listColor.TXT");

	static String[] lookAndFeel = {"Motif", "Metal"};
	static String[] lookAndFeelValues = {"com.sun.java.swing.plaf.motif.MotifLookAndFeel", "javax.swing.plaf.metal.MetalLookAndFeel"};
	public static void main(String[] args) throws IOException {
		start();
	}
	
	public static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			SwingUtilities.updateComponentTreeUI(frame);
			frame.pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
		settings.loadSettings();
		loadSettings();
		frame.setAlwaysOnTop(onTop);
		frame.setLocationByPlatform(true);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Checklist", new checkBoxes(height, length, checkListFile, checkStateFile, colorFile, false));
		tabbedPane.addTab("Daily Checklist", new dailyChecklist());
		tabbedPane.addTab("Timers", new timer());
		tabbedPane.addTab("Block Sites", new blockSites());
		tabbedPane.addTab("Settings", new settings());
		frame.add(tabbedPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(length, height);
		frame.setVisible(true);
	}
	
	static void loadSettings() {
		onTop = Boolean.parseBoolean(settings.getSetting("onTop"));
	}
	
	public static String[] readData(File file) {
		String[] result = new String[0];
		try {
			result = new String[(int)Files.lines(file.toPath()).count()];
			Scanner scanner = new Scanner(file);
			int index = 0;
			while (scanner.hasNextLine()) {
				result[index] = scanner.nextLine();
				index++;
			}
			scanner.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeData(String[] dataArr, File file) {
		String data = "";
		for (int i = 0; i < dataArr.length; i++) {
			data += (dataArr[i] + "\n");
		}
		writeData(data, file);
	}
	
	public static void setOnTop(boolean top) {
		frame.setAlwaysOnTop(top);
	}
	
	public static void repaintFrame() {
		frame.repaint();
		frame.setVisible(true);
	}

	public static void runEvalated(String[] d, File f) {
		String file = f.getAbsolutePath();
		String data = "";
		if (d.length > 1) {
			for (int i = 0; i < d.length; i++) {
				if (i != d.length - 1) {
					data += d[i] + "*";
				}
				else {
					data += d[i];
				}
			}
		}
		else {
			data = d[0];
		}
		if (data.contains("'#'")) {
			int index = data.indexOf("'#'");
			data = data.substring(0, index) + "''#''" + data.substring(index + 4);
		}
		try {
			//Runtime.getRuntime().exec("powershell.exe Start-Process -FilePath java.exe '-jar runasadmin.jar \"" + file + "@" + data + "\"' -verb RunAs");
			Runtime.getRuntime().exec("powershell.exe Start-Process -FilePath java.exe '-jar runasadmin.jar \"C:\\Users\\Billy1301\\Music\\Test.TXT@# Copyright (c) 1993-2009 Microsoft Corp.*#*# This is a sample HOSTS file used by Microsoft TCP/IP for Windows.*#*# This file contains the mappings of IP addresses to host names. Each*# entry should be kept on an individual line. The IP address should*# be placed in the first column followed by the corresponding host name.*# The IP address and the host name should be separated by at least one*# space.*#*# Additionally, comments (such as these) may be inserted on individual*# lines or following the machine name denoted by a # symbol.*#*# For			example:*#*#      102.54.94.97     rhino.acme.com          # source server*#       38.25.63.10     x.acme.com              # x client host**# localhost name resolution is handled within DNS itself.*# 127.0.0.1       localhost*#       ::1             localhost*127.0.0.1    www.youtube.com\"' -verb RunAs");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void runOnStartup(Boolean value) {

	}

	public static void setLookAndFeel(int index) {
		try {
			UIManager.setLookAndFeel(lookAndFeelValues[index]);
			SwingUtilities.updateComponentTreeUI(frame);
			frame.pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String[] getLookAndFeels() {
		return lookAndFeel;
	}
}