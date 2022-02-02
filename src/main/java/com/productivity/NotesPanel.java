package com.productivity;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;
import java.awt.BorderLayout;
import javax.swing.*;

public class NotesPanel extends JDesktopPane {
	
	private static File namesFile = new File(gui.currentPath + "Notes\\names.TXT");
	private static String filePath = gui.currentPath + "Notes\\";
	
	public NotesPanel() {
		String[] names = readData(namesFile);
		
		JComboBox<String> noteChoose = new JComboBox<>(names);
		
		JTextField nameField = new JTextField();
		nameField.addActionListener(e -> {
			File file = new File(filePath + (String)noteChoose.getSelectedItem() + ".txt");
			File newFile = new File(filePath + nameField.getText() + ".txt");
			//DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<>(names);
			//noteChoose.setModel(newModel);
			int index = noteChoose.getSelectedIndex();
			noteChoose.removeAllItems();
			for (String s : names) {
				noteChoose.insertItemAt(s, noteChoose.getItemCount());
			}
			noteChoose.setSelectedIndex(index);
			file.renameTo(newFile);
			names[index] = nameField.getText();
			writeData(names, namesFile);
		});
		
		JTextArea textArea = new JTextArea();
		textArea.addCaretListener(e -> {
			File file = new File(filePath + (String)noteChoose.getSelectedItem() + ".txt");
			String[] data = textArea.getText().split("\\r?\\n");
			writeData(data, file);
		});
		
		noteChoose.addActionListener(e -> {
			File file = new File(filePath + (String)noteChoose.getSelectedItem() + ".txt");
			String[] data = readData(file);
			if (data.length > 0) {
				textArea.setText(data[0]);
				for (int i = 1; i < data.length; i++) {
					textArea.append(data[i]);
				}
			}
			else {
				textArea.setText("");
			}
			nameField.setText((String)noteChoose.getSelectedItem());
		});
		
		super.setLayout(new BorderLayout());
		Box top = Box.createHorizontalBox();
		top.add(noteChoose);
		top.add(nameField);
		super.add(BorderLayout.NORTH, top);
		super.add(BorderLayout.CENTER, textArea);
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
