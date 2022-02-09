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
	
	public CheckBoxes(int height, int length, File name, File check, File color, Boolean daily) {
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
			for (int i = 0; i < text.length(); i++) {
				if (isEmoji(Character.toString(text.charAt(i)))) {
					input.setText("");
					return;
				}
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
	
	private static boolean isEmoji(String message){
		return message.matches("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" +
		"[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
		"[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
		"[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
		"[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
		"[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
		"[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
		"[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" +
		"[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
		"[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" +
		"[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");
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
			saveCheckBoxes();
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
			saveCheckBoxes();
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
			saveCheckBoxes();
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
		gui.repaintFrame();
		numCheckBox = 0;
	}
	
	public void loadCheckBoxes() {
		String[] name = readData(nameFile);
		String[] states = readData(checkFile);
		String[] color = readData(colorFile);
		for (int i = 0; i < name.length; i++) {
			addCheckBox(name[i], new Color(Integer.parseInt(color[i])), Boolean.parseBoolean(states[i]));
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
		writeData(name, nameFile);
		writeData(state, checkFile);
		writeData(color, colorFile);
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
