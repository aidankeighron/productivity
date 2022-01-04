import java.io.*;
import java.util.*;
import javax.swing.*;
import java.nio.file.Files;

public class gui extends JFrame {
	//TODO fix both checklist deleting last line when ever you re open
	public static int numRows = 10;
	public static int numCollums = 3;
	public static boolean onTop = false;

	static JFrame frame = new JFrame("Produtivity");
	static File checkListFile = new File("Saves\\list.TXT");
	static File checkStateFile = new File("Saves\\listCheck.TXT");
	public static void main(String[] args) {
		start();
	}
	
	public static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		settings.loadSettings();
		loadSettings();
		frame.setAlwaysOnTop(onTop);
		frame.setLocationByPlatform(true);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Checklist", new checkBoxes(numRows, numCollums, checkListFile, checkStateFile, false));
		tabbedPane.addTab("Daily Checklist", new dailyChecklist());
		tabbedPane.addTab("Timers", new timer());
		tabbedPane.addTab("Settings", new settings());
		frame.add(tabbedPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
	
	static void loadSettings() {
		numRows = Integer.parseInt(settings.getSetting("checkRows"));
		numCollums = Integer.parseInt(settings.getSetting("checkCollums"));
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

	public static void repaintFrame() {
		frame.repaint();
		frame.setVisible(true);
	}
}