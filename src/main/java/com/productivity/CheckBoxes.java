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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckBoxes extends JPanel {
	
	private static final String[] kColorNames = {"Black", "Red", "Blue", "Green"};
	private static final Color[] kColors = {Color.BLACK, new Color(250, 0, 0), new Color(0, 0, 230), new Color(0, 220, 0)};
	private static final int kCharLimit = 20;
	private static final int kCheckBoxLimit = 20;
	
	private ArrayList<JCheckBox> mCheckBoxes = new ArrayList<JCheckBox>();
	private JPanel mChecklistPanel;
	private File mNameFile;
	private File mCheckFile;
	private File mColorFile;
	private Color mSelectedColor = Color.BLACK;
	private int mNumCheckBox = 0;
	private boolean mHome;
	private boolean mDaily;
	
	private enum fileType {
		name,
		check,
		color
	}
	
	public CheckBoxes(int height, int length, File name, File check, File color, boolean daily, boolean home) {
		mHome = home;
		mDaily = daily;
		mNameFile = name;
		mCheckFile = check;
		mColorFile = color;
		height = (height/30 == 0) ? 0 : height/30;
		length = (length/200 == 0) ? 1 : length/200;
		mChecklistPanel = new JPanel(new GridLayout(height, length));
		JTextField input = new JTextField();
		input.setDocument(new JTextFieldLimit(kCharLimit));
		input.addActionListener(e -> {
			String text  = input.getText();
			if (!testValidFileName(text)) {
				input.setText("");
				return;
			}
			addCheckBox(text, mSelectedColor, false);
			input.setText("");
			if (mDaily) {
				DailyChecklist.resetBoxes(false);
			}
		});
		JComboBox<String> colorChooser = new JComboBox<>(kColorNames);
		colorChooser.addActionListener(e -> {
			mSelectedColor = kColors[colorChooser.getSelectedIndex()];
		});
		colorChooser.setFocusable(false);
		JButton reset = new JButton("Reset");
		reset.addActionListener( e -> {
			removeCheckBoxes();
			if (mDaily) {
				DailyChecklist.resetBoxes(false);
			}
		});
		reset.setFocusPainted(false);
		JButton clear = new JButton("Clear Selected");
		clear.addActionListener(e -> {
			clearSelected();
			if (mDaily) {
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
		super.add(mChecklistPanel, BorderLayout.CENTER);
		super.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private boolean testValidFileName(String text) {
		return text.matches("^[a-zA-Z0-9._ <>{}\\[\\]\\|\\\\`~!@#$%^&*()-=+;:'\",?\\/]+$");
	}
	
	private void addCheckBox(String name, Color color, Boolean state) {
		if (mNumCheckBox < kCheckBoxLimit) {
			mNumCheckBox++;
		}
		else {
			return;
		}
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.addActionListener(e -> {
			saveCheckBoxes(fileType.check);
			if (mDaily) {
				DailyChecklist.resetBoxes(false);
			}
		});
		checkBox.setForeground(color);
		checkBox.setSelected(state);
		checkBox.setFocusPainted(false);
		mCheckBoxes.add(checkBox);
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
			for (int k = 0; k < kColors.length; k++) {
				if (kColors[k].getRGB() == checkBox.getForeground().getRGB()) {
					index = k;
					break;
				}
			}
			String input = (String)JOptionPane.showInputDialog(this, "Choose new Color", "", JOptionPane.QUESTION_MESSAGE, null, kColorNames, kColorNames[index]);
			Color newColor = checkBox.getForeground();
			for (int j = 0; j < kColorNames.length; j++) {
				if (kColorNames[j].equals(input)) {
					newColor = kColors[j];
					break;
				}
			}
			checkBox.setForeground(newColor);
			saveCheckBoxes(fileType.color);
		});
		items[2] = new JMenuItem("Remove");
		items[2].addActionListener(e -> {
			mCheckBoxes.remove(checkBox);
			mChecklistPanel.remove(checkBox);
			mNumCheckBox--;
			saveCheckBoxes();
			Productivity.repaintFrame();
		});
		Popup pop = new Popup(items);
		checkBox.addMouseListener(pop.new PopClickListener());
		mChecklistPanel.add(checkBox);
		saveCheckBoxes();
		Productivity.repaintFrame();
	}
	
	private void clearSelected() {
		for (int i = mCheckBoxes.size() - 1; i >= 0; i--) {
			if (mCheckBoxes.get(i).isSelected()) {
				mChecklistPanel.remove(mCheckBoxes.get(i));
				mCheckBoxes.remove(mCheckBoxes.get(i));
				mNumCheckBox--;
			}
		}
		saveCheckBoxes();
		Productivity.repaintFrame();
	}
	
	private void removeCheckBoxes() {
		for (JCheckBox checkBox : mCheckBoxes) {
			mChecklistPanel.remove(checkBox);
		}
		mCheckBoxes = new ArrayList<JCheckBox>();
		writeData("", mNameFile);
		writeData("", mCheckFile);
		writeData("", mColorFile);
		Productivity.repaintFrame();
		mNumCheckBox = 0;
		HomePanel.getInstance().reset();
	}
	
	private void loadCheckBoxes() {
		try {
			String[] name = readData(mNameFile);
			String[] states = readData(mCheckFile);
			String[] color = readData(mColorFile);
			if (!(name.length == states.length && states.length == color.length)) {
				writeData("", mNameFile);
				writeData("", mCheckFile);
				writeData("", mColorFile);
				return;
			}
			for (int i = 0; i < name.length; i++) {
				if (mDaily) {
					boolean reset = false; 
					try {
						if (!DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()).equals(readData(new File(Productivity.getCurrentPath()+"Saves\\time.TXT"))[0])) {
							reset = true;
						}
					} catch (Exception e) { e.printStackTrace(); }
					if (!reset) addCheckBox(name[i], new Color(Integer.parseInt(color[i])), false);
					else addCheckBox(name[i], new Color(Integer.parseInt(color[i])), Boolean.parseBoolean(states[i]));
				}
				else {
					addCheckBox(name[i], new Color(Integer.parseInt(color[i])), Boolean.parseBoolean(states[i]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeData("2", mNameFile);
			writeData("2", mCheckFile);
			writeData("2", mColorFile);
		}
		
	}
	
	private void saveCheckBoxes() {
		String[] name = new String[mCheckBoxes.size()];
		String[] state = new String[mCheckBoxes.size()];
		String[] color = new String[mCheckBoxes.size()];
		for (int i = 0; i < mCheckBoxes.size(); i++) {
			name[i] = mCheckBoxes.get(i).getText();
			state[i] = Boolean.toString(mCheckBoxes.get(i).isSelected());
			color[i] = Integer.toString(mCheckBoxes.get(i).getForeground().getRGB());
		}
		try {
			if (!(name.length == state.length && state.length == color.length)) {
				writeData("", mNameFile);
				writeData("", mCheckFile);
				writeData("", mColorFile);
				return;
			}
			writeData(name, mNameFile);
			writeData(state, mCheckFile);
			writeData(color, mColorFile);
		} catch (Exception e) {
			e.printStackTrace();
			writeData("", mNameFile);
			writeData("", mCheckFile);
			writeData("", mColorFile);
		}
		
		HomePanel.getInstance().reset();
	}
	
	private void saveCheckBoxes(fileType type) {
		switch (type) {
			case name:
			String[] name = new String[mCheckBoxes.size()];
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				name[i] = mCheckBoxes.get(i).getText();
			}
			try {
				writeData(name, mNameFile);
			} catch (Exception e) {
				e.printStackTrace();
				writeData("", mNameFile);
			}
			break;
			case check:
			String[] check = new String[mCheckBoxes.size()];
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				check[i] = Boolean.toString(mCheckBoxes.get(i).isSelected());
			}
			try {
				writeData(check, mCheckFile);
			} catch (Exception e) {
				e.printStackTrace();
				writeData("", mCheckFile);
			}
			break;
			case color:
			String[] color = new String[mCheckBoxes.size()];
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				color[i] = Integer.toString(mCheckBoxes.get(i).getForeground().getRGB());
			}
			try {
				writeData(color, mColorFile);
			} catch (Exception e) {
				e.printStackTrace();
				writeData("", mColorFile);
			}
			break;
			default:
			break;
		}		
		HomePanel.getInstance().reset();
	}
	
	private String[] readData(File file) {
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
	
	private void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeData(String[] dataArr, File file) {
		String data = "";
		for (int i = 0; i < dataArr.length; i++) {
			data += (dataArr[i] + "\n");
		}
		writeData(data, file);
	}
	
	public void setSelected(boolean state, int index) {
		mCheckBoxes.get(index).setSelected(state);
		saveCheckBoxes(fileType.check);
	}
	
	public JCheckBox[] getBoxes() {
		JCheckBox[] data = new JCheckBox[mCheckBoxes.size()];
		data = mCheckBoxes.toArray(data);
		return data;
	}
	
	public boolean getHome() {
		return mHome;
	}
}
