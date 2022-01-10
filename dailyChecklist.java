import java.io.*;
import java.util.*;
import javax.swing.*;

import java.awt.*;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class dailyChecklist extends JPanel {
	static File checkListFile = new File("Saves\\daily.TXT");
	static File stateListFile = new File("Saves\\dailyCheck.TXT");
	static File timeFile = new File("Saves\\time.TXT");
	static JPanel checkListPanel = new JPanel(new GridLayout(gui.height/30, gui.length/200));
	static ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	
	public dailyChecklist() {
		boolean reset = false;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
		LocalDateTime now = LocalDateTime.now();
		if (!dtf.format(now).equals(readData(timeFile)[0])) {
			reset = true;
			writeData("", checkListFile);
		}
		writeData(dtf.format(now), timeFile);
		resetBoxes(reset);
		super.setLayout(new BorderLayout());
		super.add(BorderLayout.WEST, checkListPanel);
		super.setVisible(true);
	}
	
	public static void resetBoxes(boolean reset) {
		for (int i = 0; i < checkBoxes.size(); i++) {
			checkListPanel.remove(checkBoxes.get(i));
		}
		String[] names = readData(checkListFile);
		String[] checked = readData(stateListFile);
		for (int i = 0; i < names.length; i++) {
			if (!reset) {
				addCheckBox(names[i], Boolean.parseBoolean(checked[i]));
			}
			else {
				addCheckBox(names[i], false);
			}
			
		}
	}
	
	public static void add(JCheckBox box) {
		checkListPanel.add(box);
		gui.repaintFrame();
	}
	
	public static void addCheckBox(String name, Boolean checked) {
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.addActionListener(e -> {
			saveCheckBoxes();
		});
		checkBoxes.add(checkBox);
		checkBox.setSelected(checked);
		add(checkBox);
	}
	
	public static void saveCheckBoxes() {
		String[] state = new String[checkBoxes.size()];
		for (int i = 0; i < checkBoxes.size(); i++) {
			state[i] = Boolean.toString(checkBoxes.get(i).isSelected());
		}
		writeData(state, stateListFile);
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
}
