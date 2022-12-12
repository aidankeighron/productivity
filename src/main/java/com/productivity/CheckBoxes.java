package com.productivity;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.productivity.Util.JTextFieldLimit;
import com.productivity.Util.Popup;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import net.miginfocom.swing.MigLayout;

public class CheckBoxes extends JPanel {
	
	public static final int kColumns = 2;
	
	private static final String[] kColorNames = {"White", "Red", "Blue", "Green"};
	private static final Color[] kColors = {Color.WHITE, new Color(190, 50, 50), new Color(90, 90, 255), new Color(50, 200, 50)};
	private static final int kCharLimit = 35;
	private static Productivity mProductivity = Productivity.getInstance();
	// mChecklist Panel Height / JCheckList Height
	private static int kRows;
	private int mCheckBoxLimit;
	private ArrayList<JCheckBox> mCheckBoxes = new ArrayList<JCheckBox>();
	private JPanel mChecklistPanel;
	private File mNameFile;
	private File mCheckFile;
	private File mColorFile;
	private Color mSelectedColor = kColors[0];
	private int mNumCheckBox = 0;
	
	private enum FileType {
		name,
		check,
		color
	}
	
	public CheckBoxes(File name, File check, File color, boolean custom) {
		kRows = (int)((custom?180:214) / 20);
		mCheckBoxLimit = kRows * kColumns;
		mChecklistPanel = new JPanel(new MigLayout("gap 0px 0px, ins 0, flowy, wrap " + kRows));
		mNameFile = name;
		mCheckFile = check;
		mColorFile = color;
		JTextField input = new JTextField(kCharLimit);
		input.setDocument(new JTextFieldLimit(kCharLimit));
		input.addActionListener(e -> {
			String text  = input.getText();
			if (!testValidFileName(text)) {
				input.setText("");
				return;
			}
			addCheckBox(text, mSelectedColor, false, false);
			input.setText("");
			mProductivity.repaintFrame();
		});
		JComboBox<String> colorChooser = new JComboBox<>(kColorNames);
		colorChooser.addActionListener(e -> {
			mSelectedColor = kColors[colorChooser.getSelectedIndex()];
		});
		JButton reset = new JButton("Reset");
		reset.addActionListener( e -> {
			removeCheckBoxes();
		});
		// reset.setMargin(new Insets(0, 0, 0, 0));
		JButton clear = new JButton("Clear Selected");
		clear.addActionListener(e -> {
			clearSelected();
		});
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadCheckBoxes();
			}
		}
		);
		
		super.setLayout(new MigLayout("gap 0px 0px, ins 0" + ((Productivity.kMigDebug)?",debug":"")));
		super.add(input, "");
		super.add(colorChooser, "wrap");
		super.add(mChecklistPanel, "grow, push, span, wrap");
		super.add(reset, "span, center, split 2");
		super.add(clear, "");
	}
	
	public int setToFalse(int streak) {
		boolean allChecked = true;
		if (mCheckBoxes.size() > 0) {
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				if (!mCheckBoxes.get(i).isSelected()) {
					allChecked = false;
				}
				mCheckBoxes.get(i).setSelected(false);
			}
			saveCheckBoxes(FileType.check);
		}
		if (allChecked && mCheckBoxes.size() > 0) streak++;
		else streak = 0;
		return streak;
	}
	
	private boolean testValidFileName(String text) {
		return text.matches("^[a-zA-Z0-9._ <>{}\\[\\]\\|\\\\`~!@#$%^&*()-=+;:'\",?\\/]+$");
	}
	
	private void addCheckBox(String name, Color color, boolean state, boolean loading) {
		if (mNumCheckBox < mCheckBoxLimit) {
			mNumCheckBox++;
		}
		else {
			return;
		}
		JCheckBox checkBox = new JCheckBox(name);
		mCheckBoxes.add(checkBox);
		checkBox.addActionListener(e -> {
			if (checkBox.isSelected())
			Productivity.showConfetti();
			saveCheckBoxes(FileType.check);
		});
		checkBox.setForeground(color);
		checkBox.setSelected(state);
		JMenuItem[] items = new JMenuItem[3];
		items[0] = new JMenuItem("Edit");
		items[0].addActionListener(e -> {
			String input = JOptionPane.showInputDialog(this, "new name", checkBox.getText());
			if (input != null) {
				checkBox.setText(input);
			}
			saveCheckBoxes(FileType.name);
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
			saveCheckBoxes(FileType.color);
		});
		items[2] = new JMenuItem("Remove");
		items[2].addActionListener(e -> {
			mCheckBoxes.remove(checkBox);
			mChecklistPanel.remove(checkBox);
			mNumCheckBox--;
			saveCheckBoxes(false);
			mProductivity.repaintFrame();
		});
		Popup pop = new Popup(items);
		checkBox.addMouseListener(pop.new PopClickListener());
		mChecklistPanel.add(checkBox, "width "+ (int)(Productivity.kWidth/kColumns) +", wmax " + (int)(Productivity.kWidth/kColumns));
		if (!loading)
		saveCheckBoxes(true);
	}
	
	private void clearSelected() {
		boolean changed = false;
		for (int i = mCheckBoxes.size() - 1; i >= 0; i--) {
			if (mCheckBoxes.get(i).isSelected()) {
				mChecklistPanel.remove(mCheckBoxes.get(i));
				mCheckBoxes.remove(mCheckBoxes.get(i));
				mNumCheckBox--;
				changed = true;
			}
		}
		if (changed)
		saveCheckBoxes(false);
		mProductivity.repaintFrame();
	}
	
	private void removeCheckBoxes() {
		for (JCheckBox checkBox : mCheckBoxes) {
			mChecklistPanel.remove(checkBox);
		}
		mCheckBoxes = new ArrayList<JCheckBox>();
		writeData("", mNameFile);
		writeData("", mCheckFile);
		writeData("", mColorFile);
		mNumCheckBox = 0;
		mProductivity.repaintFrame();
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
				if (name.length != states.length && name.length != color.length) JOptionPane.showMessageDialog(this, "Failed loading checkboxes because of name length", "Warning", JOptionPane.ERROR_MESSAGE);
				if (states.length != name.length && states.length != color.length) JOptionPane.showMessageDialog(this, "Failed loading checkboxes because of states length", "Warning", JOptionPane.ERROR_MESSAGE);
				if (color.length != name.length && color.length != states.length) JOptionPane.showMessageDialog(this, "Failed loading checkboxes because of color length", "Warning", JOptionPane.ERROR_MESSAGE);
				return;
			}
			for (int i = 0; i < name.length; i++) {
				addCheckBox(name[i], new Color(Integer.parseInt(color[i])), Boolean.parseBoolean(states[i]), true);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Failed loading checkboxes", "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			writeData("", mNameFile);
			writeData("", mCheckFile);
			writeData("", mColorFile);
		}
		
	}
	
	private void saveCheckBoxes(boolean append) {
		if (!append) {
			String[] name = new String[mCheckBoxes.size()];
			String[] state = new String[mCheckBoxes.size()];
			String[] color = new String[mCheckBoxes.size()];
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				name[i] = mCheckBoxes.get(i).getText();
				state[i] = Boolean.toString(mCheckBoxes.get(i).isSelected());
				color[i] = Integer.toString(mCheckBoxes.get(i).getForeground().getRGB());
			}
			name[name.length-1] += "\n";
			state[state.length-1] += "\n";
			color[color.length-1] += "\n";
			try {
				if (!(name.length == state.length && state.length == color.length)) {
					writeData("", mNameFile);
					writeData("", mCheckFile);
					writeData("", mColorFile);
					if (name.length != state.length && name.length != color.length) JOptionPane.showMessageDialog(this, "Failed saving checkboxes because of name length", "Warning", JOptionPane.ERROR_MESSAGE);
					if (state.length != name.length && state.length != color.length) JOptionPane.showMessageDialog(this, "Failed saving checkboxes because of states length", "Warning", JOptionPane.ERROR_MESSAGE);
					if (color.length != name.length && color.length != state.length) JOptionPane.showMessageDialog(this, "Failed saving checkboxes because of color length", "Warning", JOptionPane.ERROR_MESSAGE);
					return;
				}
				writeData(name, mNameFile);
				writeData(state, mCheckFile);
				writeData(color, mColorFile);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Failed saving checkboxes", "Warning", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				writeData("", mNameFile);
				writeData("", mCheckFile);
				writeData("", mColorFile);
			}
		}
		else {
			int index = mCheckBoxes.size()-1;
			appendFile(mCheckBoxes.get(index).getText(), mNameFile);
			appendFile(Boolean.toString(mCheckBoxes.get(index).isSelected()), mCheckFile);
			appendFile(Integer.toString(mCheckBoxes.get(index).getForeground().getRGB()), mColorFile);
		}
	}
	
	private void saveCheckBoxes(FileType type) {
		switch (type) {
			case name:
			String[] name = new String[mCheckBoxes.size()];
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				name[i] = mCheckBoxes.get(i).getText();
			}
			name[name.length-1] += "\n";
			try {
				writeData(name, mNameFile);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Failed saving checkboxes of type:"+type.name(), "Warning", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				writeData("", mNameFile);
			}
			break;
			case check:
			String[] check = new String[mCheckBoxes.size()];
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				check[i] = Boolean.toString(mCheckBoxes.get(i).isSelected());
			}
			check[check.length-1] += "\n";
			try {
				writeData(check, mCheckFile);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Failed saving checkboxes of type:"+type.name(), "Warning", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				writeData("", mCheckFile);
			}
			break;
			case color:
			String[] color = new String[mCheckBoxes.size()];
			for (int i = 0; i < mCheckBoxes.size(); i++) {
				color[i] = Integer.toString(mCheckBoxes.get(i).getForeground().getRGB());
			}
			color[color.length-1] += "\n";
			try {
				writeData(color, mColorFile);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Failed saving checkboxes of type:"+type.name(), "Warning", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				writeData("", mColorFile);
			}
			break;
			default:
			break;
		}		
	}
	
	private static void appendFile(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file, true);
			writer.write(data+"\n");
			writer.close();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed appending: "+data, "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
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
			JOptionPane.showMessageDialog(this, "Failed reading data in CheckBoxes", "Warning", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(this, "Failed writing data in CheckBoxes", "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private void writeData(String[] dataArr, File file) {
		String data = String.join("\n", dataArr);
		writeData(data, file);
	}
	
	public void setSelected(boolean state, int index) {
		mCheckBoxes.get(index).setSelected(state);
		saveCheckBoxes(FileType.check);
	}
	
	public JCheckBox[] getBoxes() {
		JCheckBox[] data = new JCheckBox[mCheckBoxes.size()];
		data = mCheckBoxes.toArray(data);
		return data;
	}
}
