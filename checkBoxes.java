import java.awt.BorderLayout;
import java.io.*;
import java.util.*;
import javax.swing.*;

import java.awt.*;
import java.nio.file.Files;
//TODO save checked/unchecked state
//TODO change layout to be vertical/horizontal and based off of screen size
public class checkBoxes extends JPanel {

    ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	JPanel checkListPanel;
	File checkListFile;
	File stateListFile;

    public checkBoxes(int rows, int collums, File file, File checkedFile, Boolean isDaily) {
		checkListFile = file;
		stateListFile = checkedFile;
		checkListPanel = new JPanel(new GridLayout(rows, collums));
		JTextField input = new JTextField();
		input.addActionListener(e -> {
			addCheckBox(input.getText());
			input.setText("");
			saveCheckBoxes();
			if (isDaily) {
				//dailyChecklist.resetBoxes();
			}
		});
		JButton reset = new JButton("Reset");
		reset.addActionListener( e -> removeCheckBoxes());
		
		JButton clear = new JButton("Clear Selected");
		clear.addActionListener(e -> clearSelected());

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(reset);
		buttonPanel.add(clear);

		super.setLayout(new BorderLayout());

		super.add(input, BorderLayout.NORTH);
		super.add(checkListPanel, BorderLayout.CENTER);
		super.add(buttonPanel, BorderLayout.SOUTH);
		loadCheckBoxes();
		checkListPanel.setVisible(true);
		super.setVisible(true);
	}

	public void add(JCheckBox box) {
		checkListPanel.add(box);
		gui.repaintFrame();
	}
	
	public void addCheckBox(String name) {
		JCheckBox checkBox = new JCheckBox(name);
		checkBoxes.add(checkBox);
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
		try {
			clearTheFile(checkListFile);
			clearTheFile(stateListFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		try {
			clearTheFile(checkListFile);
			clearTheFile(stateListFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] name = new String[checkBoxes.size()];
		String[] state = new String[checkBoxes.size()];
		for (int i = 0; i < checkBoxes.size(); i++) {
			name[i] = checkBoxes.get(i).getText();
			state[i] = Boolean.toString(checkBoxes.get(i).isSelected());
		}
		writeData(name, checkListFile);
		writeData(state, stateListFile);
	}
	
	public void loadCheckBoxes() {
		String[] data = readData(checkListFile);
		String[] states = readData(stateListFile);
		for (int i = 0; i < data.length; i++) {
			JCheckBox checkBox = new JCheckBox(data[i]);
			checkBox.setSelected(Boolean.parseBoolean(states[i]));
			add(checkBox);
			checkBoxes.add(checkBox);
		}
		/*
		for (String s : data) {
			JCheckBox checkBox = new JCheckBox(s);
			add(checkBox);
			checkBoxes.add(checkBox);
		}*/
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
