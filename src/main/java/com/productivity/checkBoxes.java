package com.productivity;

import java.awt.BorderLayout;
import java.io.*;
import java.util.*;
import javax.swing.*;

import java.awt.*;
import java.nio.file.Files;
public class checkBoxes extends JPanel {
	
	ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	JPanel checkListPanel;
	File checkListFile;
	File stateListFile;
	File colorListFile;
	static String[] colorNames = {"Black", "Red", "Blue", "Green"};
	static Color[] colors = {Color.BLACK, new Color(250, 0, 0), new Color(0, 0, 230), new Color(0, 220, 0)};
	Color selectedColor = Color.BLACK;
	
	public checkBoxes(int height, int length, File file, File checkedFile, File colorFile, Boolean daily) {
		checkListFile = file;
		stateListFile = checkedFile;
		colorListFile = colorFile;
		height = (height/30 == 0) ? 0 : height/30;
		length = (length/200 == 0) ? 1 : length/200;
		checkListPanel = new JPanel(new GridLayout(height, length));
		JTextField input = new JTextField();
		input.addActionListener(e -> {
			addCheckBox(input.getText(), selectedColor);
			input.setText("");
			saveCheckBoxes();
			if (daily) {
				dailyChecklist.resetBoxes(false);
			}
		});
		JComboBox<String> colorChooser = new JComboBox<>(colorNames);
		colorChooser.addActionListener(e -> {
			selectedColor = colors[colorChooser.getSelectedIndex()];
		});
		JButton reset = new JButton("Reset");
		reset.addActionListener( e -> {
			removeCheckBoxes();
			if (daily) {
				dailyChecklist.resetBoxes(false);
			}
		});
		
		JButton clear = new JButton("Clear Selected");
		clear.addActionListener(e -> {
			clearSelected();
			if (daily) {
				dailyChecklist.resetBoxes(false);
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(clear);
		
		super.setLayout(new BorderLayout());
		Box inputBox = Box.createHorizontalBox();
		inputBox.add(input);
		inputBox.add(colorChooser);
		super.add(inputBox, BorderLayout.NORTH);
		super.add(checkListPanel, BorderLayout.CENTER);
		super.add(buttonPanel, BorderLayout.SOUTH);
		loadCheckBoxes();
	}
	
	public void add(JCheckBox box) {
		checkListPanel.add(box);
		gui.repaintFrame();
	}
	
	public void addCheckBox(String name, Color color) {
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.addActionListener(e -> {
			saveCheckBoxes();
		});
		checkBox.setForeground(color);
		checkBoxes.add(checkBox);
		JMenuItem[] items = new JMenuItem[3];
		items[0] = new JMenuItem("Edit");
		items[0].addActionListener(e -> {
			checkBox.setText(JOptionPane.showInputDialog(this, "new name", checkBox.getText()));
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
			String input = (String)JOptionPane.showInputDialog(null, "Choose new Color", "", JOptionPane.QUESTION_MESSAGE, null, colorNames, colorNames[index]);
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
			checkListPanel.remove(checkBox);
			saveCheckBoxes();
			gui.repaintFrame();
		});
		popup pop = new popup(items);
		checkBox.addMouseListener(pop.new PopClickListener());
		add(checkBox);
	}
	
	public void clearSelected() {
		for (int i = checkBoxes.size() - 1; i >= 0; i--) {
			if (checkBoxes.get(i).isSelected()) {
				checkListPanel.remove(checkBoxes.get(i));
				checkBoxes.remove(checkBoxes.get(i));
			}
		}
		saveCheckBoxes();
		gui.repaintFrame();
	}
	
	public void removeCheckBoxes() {
		for (JCheckBox checkBox : checkBoxes) {
			checkListPanel.remove(checkBox);
		}
		checkBoxes = new ArrayList<JCheckBox>();
		writeData("", checkListFile);
		writeData("", stateListFile);
		checkListPanel.repaint();
		gui.repaintFrame();
	}
	
	public void clearTheFile(File file) throws IOException {
		FileWriter fwOb = new FileWriter(file); 
		PrintWriter pwOb = new PrintWriter(fwOb, false);
		pwOb.flush();
		pwOb.close();
		fwOb.close();
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
		writeData(name, checkListFile);
		writeData(state, stateListFile);
		writeData(color, colorListFile);
	}
	
	public void loadCheckBoxes() {
		String[] data = readData(checkListFile);
		String[] states = readData(stateListFile);
		String[] color = readData(colorListFile);
		for (int i = 0; i < data.length; i++) {
			JCheckBox checkBox = new JCheckBox(data[i]);
			checkBox.addActionListener(e -> {
				saveCheckBoxes();
			});
			checkBox.setSelected(Boolean.parseBoolean(states[i]));
			checkBox.setForeground(new Color(Integer.parseInt(color[i])));
			JMenuItem[] items = new JMenuItem[3];
			items[0] = new JMenuItem("Edit");
			items[0].addActionListener(e -> {
				String input = JOptionPane.showInputDialog(this, "new name", checkBox.getText());
				if (input != null) {
					checkBox.setText(input);
				}
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
				String input = (String)JOptionPane.showInputDialog(null, "Choose new Color", "", JOptionPane.QUESTION_MESSAGE, null, colorNames, colorNames[index]);
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
				checkListPanel.remove(checkBox);
				saveCheckBoxes();
				gui.repaintFrame();
			});
			popup pop = new popup(items);
			checkBox.addMouseListener(pop.new PopClickListener());
			add(checkBox);
			checkBoxes.add(checkBox);
		}
		saveCheckBoxes();
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
