package com.productivity;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class CheckBoxes extends JPanel {
	
	private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	private JPanel checklistPanel;
	private File nameFile;
	private File checkFile;
	private File colorFile;
	private static String[] colorNames = {"Black", "Red", "Blue", "Green"};
	private static Color[] colors = {Color.BLACK, new Color(250, 0, 0), new Color(0, 0, 230), new Color(0, 220, 0)};
	private Color selectedColor = Color.BLACK;
	private static int charLimit = 20;
	private static int checkBoxLimit = 20;
	private int numCheckBox = 0;
	public boolean home;
	public boolean alert;
	private boolean daily;

	private enum fileType {
		name,
		check,
		color
	}
	
	public CheckBoxes(int height, int length, File name, File check, File color, boolean daily, boolean home, boolean alert) {
		this.home = home;
		this.alert = alert;
		this.daily = daily;
		nameFile = name;
		checkFile = check;
		colorFile = color;
		height = (height/30 == 0) ? 0 : height/30;
		length = (length/200 == 0) ? 1 : length/200;
		checklistPanel = new JPanel(new GridLayout(height, length));
		JTextField input = new JTextField();
		input.setDocument(new JTextFieldLimit(charLimit));
		input.addActionListener(e -> {
			String text  = input.getText();
			if (!testValidFileName(text)) {
				input.setText("");
				return;
			}
			addCheckBox(text, selectedColor, false);
			input.setText("");
			if (daily) {
				DailyChecklist.resetBoxes(false);
			}
		});
		JComboBox<String> colorChooser = new JComboBox<>(colorNames);
		colorChooser.addActionListener(e -> {
			selectedColor = colors[colorChooser.getSelectedIndex()];
		});
		colorChooser.setFocusable(false);
		JButton reset = new JButton("Reset");
		reset.addActionListener( e -> {
			removeCheckBoxes();
			if (daily) {
				DailyChecklist.resetBoxes(false);
			}
		});
		reset.setFocusPainted(false);
		JButton clear = new JButton("Clear Selected");
		clear.addActionListener(e -> {
			clearSelected();
			if (daily) {
				DailyChecklist.resetBoxes(false);
			}
		});
		clear.setFocusPainted(false);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(clear);
		loadCheckBoxes();
		
		super.setLayout(new BorderLayout());
		Box inputBox = Box.createHorizontalBox();
		inputBox.add(input);
		inputBox.add(colorChooser);
		super.add(inputBox, BorderLayout.NORTH);
		super.add(checklistPanel, BorderLayout.CENTER);
		super.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private boolean testValidFileName(String text) {
		return text.matches("^[a-zA-Z0-9._ <>{}\\[\\]\\|\\\\`~!@#$%^&*()-=+;:'\",?\\/]+$");
	}

	public void setSelected(boolean state, int index) {
		checkBoxes.get(index).setSelected(state);
		saveCheckBoxes(fileType.check);
	}
	
	public JCheckBox[] getBoxes() {
		JCheckBox[] data = new JCheckBox[checkBoxes.size()];
		data = checkBoxes.toArray(data);
		return data;
	}
	
	public void addCheckBox(String name, Color color, Boolean state) {
		if (numCheckBox < checkBoxLimit) {
			numCheckBox++;
		}
		else {
			return;
		}
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.addActionListener(e -> {
			saveCheckBoxes(fileType.check);
			if (daily) {
				DailyChecklist.resetBoxes(false);
			}

		});
		checkBox.setForeground(color);
		checkBox.setSelected(state);
		checkBox.setFocusPainted(false);
		checkBoxes.add(checkBox);
		JMenuItem[] items = new JMenuItem[3];
		items[0] = new JMenuItem("Edit");
		items[0].addActionListener(e -> {
			String input = JOptionPane.showInputDialog(this, "new name", checkBox.getText());
			if (input != null) {
				checkBox.setText(input);
			}
			saveCheckBoxes(fileType.name);
		});
		items[1] = new JMenuItem("Change color");
		items[1].addActionListener(e -> {
			int index = 0;
			for (int k = 0; k < colors.length; k++) {
				if (colors[k].getRGB() == checkBox.getForeground().getRGB()) {
					index = k;
					break;
				}
			}
			String input = (String)JOptionPane.showInputDialog(this, "Choose new Color", "", JOptionPane.QUESTION_MESSAGE, null, colorNames, colorNames[index]);
			Color newColor = checkBox.getForeground();
			for (int j = 0; j < colorNames.length; j++) {
				if (colorNames[j].equals(input)) {
					newColor = colors[j];
					break;
				}
			}
			checkBox.setForeground(newColor);
			saveCheckBoxes(fileType.color);
		});
		items[2] = new JMenuItem("Remove");
		items[2].addActionListener(e -> {
			checkBoxes.remove(checkBox);
			checklistPanel.remove(checkBox);
			numCheckBox--;
			saveCheckBoxes();
			gui.repaintFrame();
		});
		Popup pop = new Popup(items);
		checkBox.addMouseListener(pop.new PopClickListener());
		checklistPanel.add(checkBox);
		saveCheckBoxes();
		gui.repaintFrame();
	}
	
	public void clearSelected() {
		for (int i = checkBoxes.size() - 1; i >= 0; i--) {
			if (checkBoxes.get(i).isSelected()) {
				checklistPanel.remove(checkBoxes.get(i));
				checkBoxes.remove(checkBoxes.get(i));
				numCheckBox--;
			}
		}
		saveCheckBoxes();
		gui.repaintFrame();
	}
	
	public void removeCheckBoxes() {
		for (JCheckBox checkBox : checkBoxes) {
			checklistPanel.remove(checkBox);
		}
		checkBoxes = new ArrayList<JCheckBox>();
		writeData("", nameFile);
		writeData("", checkFile);
		writeData("", colorFile);
		gui.repaintFrame();
		numCheckBox = 0;
	}
	
	public void loadCheckBoxes() {
		try {
			String[] name = readData(nameFile);
			String[] states = readData(checkFile);
			String[] color = readData(colorFile);
			if (!(name.length == states.length && states.length == color.length)) {
				writeData("", nameFile);
				writeData("", checkFile);
				writeData("", colorFile);
				return;
			}
			for (int i = 0; i < name.length; i++) {
				addCheckBox(name[i], new Color(Integer.parseInt(color[i])), Boolean.parseBoolean(states[i]));
			}
		} catch (Exception e) {
			writeData("", nameFile);
			writeData("", checkFile);
			writeData("", colorFile);
		}
		
	}
	
	public void saveCheckBoxes() {
		String[] name = new String[checkBoxes.size()];
		String[] state = new String[checkBoxes.size()];
		String[] color = new String[checkBoxes.size()];
		for (int i = 0; i < checkBoxes.size(); i++) {
			name[i] = checkBoxes.get(i).getText();
			state[i] = Boolean.toString(checkBoxes.get(i).isSelected());
			color[i] = Integer.toString(checkBoxes.get(i).getForeground().getRGB());
		}
		try {
			if (!(name.length == state.length && state.length == color.length)) {
				writeData("", nameFile);
				writeData("", checkFile);
				writeData("", colorFile);
				return;
			}
			writeData(name, nameFile);
			writeData(state, checkFile);
			writeData(color, colorFile);
		} catch (Exception e) {
			writeData("", nameFile);
			writeData("", checkFile);
			writeData("", colorFile);
		}
		
		gui.homeReset();
	}

	public void saveCheckBoxes(fileType type) {
		switch (type) {
			case name:
			String[] name = new String[checkBoxes.size()];
			for (int i = 0; i < checkBoxes.size(); i++) {
				name[i] = checkBoxes.get(i).getText();
			}
			try {
				writeData(name, nameFile);
			} catch (Exception e) {
				writeData("", nameFile);
			}
				break;
			case check:
			String[] check = new String[checkBoxes.size()];
			for (int i = 0; i < checkBoxes.size(); i++) {
				check[i] = Boolean.toString(checkBoxes.get(i).isSelected());
			}
			try {
				writeData(check, checkFile);
			} catch (Exception e) {
				writeData("", checkFile);
			}
				break;
			case color:
			String[] color = new String[checkBoxes.size()];
			for (int i = 0; i < checkBoxes.size(); i++) {
				color[i] = Integer.toString(checkBoxes.get(i).getForeground().getRGB());
			}
			try {
				writeData(color, colorFile);
			} catch (Exception e) {
				writeData("", colorFile);
			}
				break;
			default:
				break;
		}		
		gui.homeReset();
	}
	
	public String[] readData(File file) {
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
	
	public void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(String[] dataArr, File file) {
		String data = "";
		for (int i = 0; i < dataArr.length; i++) {
			data += (dataArr[i] + "\n");
		}
		writeData(data, file);
	}
}
