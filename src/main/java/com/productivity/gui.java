package src.main.java.com.productivity;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.nio.file.Files;

public class gui extends JFrame {
	//TODO fix both checklist deleting last line when ever you re open
	public static int length = 400;
	public static int height = 300;
	public static boolean onTop = false;
	
	static JFrame frame = new JFrame("Produtivity");
	static File checkListFile = new File("Saves\\list.TXT");
	static File checkStateFile = new File("Saves\\listCheck.TXT");
	static File colorFile = new File("Saves\\listColor.TXT");
	public static void main(String[] args) {
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
}