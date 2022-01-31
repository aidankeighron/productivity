package com.productivity;

import java.io.*;
import javax.swing.*;
import com.productivity.Custom.addCustomCheckList;
import com.productivity.Custom.customCheckList;

public class gui extends JFrame {
	
	public static int length = 400;
	public static int height = 300;
	public static String debugPath = "src\\main\\java\\com\\productivity\\Saves\\";
	public static Boolean debug = false;
	
	private static boolean onTop = false;
	private static JFrame frame = new JFrame("Produtivity");
	private static JTabbedPane tabbedPane;
	public static customCheckList customCheckList = new customCheckList();
	
	//privare static File nameFile = new File(debugPath+"list.TXT");
	//private static File stateFile = new File(debugPath+"listCheck.TXT");
	//private static File colorFile = new File(debugPath+"listColor.TXT");
	private static File nameFile = new File((!debug)?"classes\\list.TXT":debugPath+"list.TXT");
	private static File stateFile = new File((!debug)?"classes\\listCheck.TXT":debugPath+"listCheck.TXT");
	private static File colorFile = new File((!debug)?"classes\\listColor.TXT":debugPath+"listColor.TXT");
	
	static String[] lookAndFeel = {"Motif", "Metal"};
	static String[] lookAndFeelValues = {"com.sun.java.swing.plaf.motif.MotifLookAndFeel", "javax.swing.plaf.metal.MetalLookAndFeel"};
	
	public static void main(String[] args) throws IOException {
		start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				BlockSites.unBlockSites();
			}
		}, "Shutdown-thread"));
	}
	
	private static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			//SwingUtilities.updateComponentTreeUI(frame);
			//frame.pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
		load();
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Checklist", new CheckBoxes(height, length, nameFile, stateFile, colorFile, false));
		tabbedPane.addTab("Daily Checklist", new DailyChecklist());
		
		tabbedPane.addTab("Timers", new TimerPanel());
		tabbedPane.addTab("Settings", new SettingsPanel());
		if (addCustomCheckList.getNumberOfChecklists() > 0) {
			tabbedPane.addTab("Custom Checklist", customCheckList);
		}
		frame.add(tabbedPane);
		frame.setAlwaysOnTop(onTop);
		//frame.setLocationByPlatform(true); TODO fix
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(length, height);
		frame.setVisible(true);
	}
	
	private static void load() {
		SettingsPanel.loadSettings();
		addCustomCheckList.loadCheckLists();
		onTop = Boolean.parseBoolean(SettingsPanel.getSetting("onTop"));
	}
	
	public static void customCheckListVisibility(boolean value) {
		if (value && tabbedPane.indexOfComponent(customCheckList) == -1) {
			tabbedPane.addTab("Custom Checklist", customCheckList);
			repaintFrame();
		}
		else if (tabbedPane.indexOfComponent(customCheckList) != -1) {
			tabbedPane.remove(customCheckList);
			repaintFrame();
		}
	}
	
	public static void setOnTop(boolean top) {
		frame.setAlwaysOnTop(top);
	}
	
	public static void repaintFrame() {
		frame.repaint();
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
	
	private static void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}