package com.productivity;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.nio.file.Files;

public class gui extends JFrame {
	public static int length = 400;
	public static int height = 300;
	public static boolean onTop = false;
	
	public static Boolean debug = true;
	public static String debugPath = "src\\main\\java\\com\\productivity\\Saves\\";
	static JFrame frame = new JFrame("Produtivity");
	/*
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

	static timer Timer;
	public static void main(String[] args) throws IOException {
		start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
					blockSites.unBlockSites();
			}
		}, "Shutdown-thread"));
	}
	
	public static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
		Timer = new timer();
		tabbedPane.addTab("Timers", Timer);
		tabbedPane.addTab("Settings", new settings());
		frame.add(tabbedPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(length, height);
		frame.setVisible(true);
	}

	static void blockVisibility(boolean value) {
		Timer.setAllowBlock(value);
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

	public static void runOnStartup(Boolean value) {
		String path = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\productivity.bat";

		if (value) {
			File startupFile = new File(path);
			try {
				if (!startupFile.exists()) {
					startupFile.createNewFile();
				}
				String currentDir = System.getProperty("user.dir");
				String data = "start " + currentDir + "\\Productivity.exe";
				writeData(data, startupFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			File startupFile = new File(path);
			try {
				if (startupFile.exists()) {
					startupFile.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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