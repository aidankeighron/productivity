package com.productivity;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class DailyChecklist extends JPanel {
	
	private static File nameFile;
	private static File stateFile;
	private static File colorFile;
	private static File timeFile;
	//private static File nameFile = new File((!gui.debug)?"classes\\daily.TXT":gui.debugPath+"daily.TXT");
	//private static File stateFile = new File((!gui.debug)?"classes\\dailyCheck.TXT":gui.debugPath+"dailyCheck.TXT");
	//private static File colorFile = new File((!gui.debug)?"classes\\dailyColor.TXT":gui.debugPath+"dailyColor.TXT");
	//private static File timeFile = new File((!gui.debug)?"classes\\time.TXT":gui.debugPath+"time.TXT");
	private static JPanel checkListPanel = new JPanel(new GridLayout(gui.height/30, gui.length/200));
	private static ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	
	public DailyChecklist() {
		loadFiles();
		boolean reset = false;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
		LocalDateTime now = LocalDateTime.now();
		try {
			if (!dtf.format(now).equals(readData(timeFile)[0])) {
				reset = true;
			}
			writeData(dtf.format(now), timeFile);
		} catch (Exception e) {
			writeData("11/11/2020", timeFile);
		}

		resetBoxes(reset);
		super.setLayout(new BorderLayout());
		super.add(BorderLayout.WEST, checkListPanel);
	}

	public static JCheckBox[] getCheckBoxes() {
		JCheckBox[] data = new JCheckBox[checkBoxes.size()];
        data = checkBoxes.toArray(data);
		return data;
	}

	private void loadFiles() {
		nameFile = new File(gui.currentPath+"Saves\\daily.TXT");
		stateFile = new File(gui.currentPath+"Saves\\dailyCheck.TXT");
		colorFile = new File(gui.currentPath+"Saves\\dailyColor.TXT");
		timeFile = new File(gui.currentPath+"Saves\\time.TXT");
	}
	
	public static void resetBoxes(boolean reset) {
		for (int i = 0; i < checkBoxes.size(); i++) {
			checkListPanel.remove(checkBoxes.get(i));
		}
		try {
			String[] names = readData(nameFile);
			String[] checked = readData(stateFile);
			String[] color = readData(colorFile);
			if (!(names.length == checked.length && checked.length == color.length)) {
				writeData("", nameFile);
				writeData("", stateFile);
				writeData("", colorFile);
				return;
			}
			for (int i = 0; i < names.length; i++) {
				if (!reset) {
					addCheckBox(names[i], new Color(Integer.parseInt(color[i])), Boolean.parseBoolean(checked[i]));
				}
				else {
					addCheckBox(names[i], new Color(Integer.parseInt(color[i])), false);
				}
			}
			gui.homeReset();
		} catch (Exception e) {
			writeData("", nameFile);
			writeData("", stateFile);
			writeData("", colorFile);
		}

	}
	
	private static void addCheckBox(String name, Color color, Boolean checked) {
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.setFocusPainted(false);
		checkBox.addActionListener(e -> {
			saveCheckBoxes();
		});
		checkBox.setForeground(color);
		checkBox.setSelected(checked);
		checkBoxes.add(checkBox);
		checkListPanel.add(checkBox);
		gui.repaintFrame();
	}
	
	private static void saveCheckBoxes() {
		String[] state = new String[checkBoxes.size()];
		for (int i = 0; i < checkBoxes.size(); i++) {
			state[i] = Boolean.toString(checkBoxes.get(i).isSelected());
		}
		try {
			writeData(state, stateFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static String[] readData(File file) {
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
	
	private static void writeData(String[] dataArr, File file) {
		String data = "";
		for (int i = 0; i < dataArr.length; i++) {
			data += (dataArr[i] + "\n");
		}
		writeData(data, file);
	}
}
