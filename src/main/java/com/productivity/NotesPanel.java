package com.productivity;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.nio.file.Files;
import java.util.Scanner;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;

public class NotesPanel extends JDesktopPane {
	
	private static File namesFile = new File(gui.currentPath + "Notes\\names.TXT");
	private static String filePath = gui.currentPath + "Notes\\";
	
	public NotesPanel() {
		String[] names = readData(namesFile);
		
		JComboBox<String> noteChoose = new JComboBox<>(names);
		noteChoose.setFocusable(false);
		JTextField nameField = new JTextField();
		nameField.addActionListener(e -> {
			String text  = nameField.getText();
			for (int i = 0; i < text.length(); i++) {
				if (isEmoji(Character.toString(text.charAt(i)))) {
					nameField.setText((String)noteChoose.getSelectedItem());
					return;
				}
			}
			File file = new File(filePath + (String)noteChoose.getSelectedItem() + ".txt");
			File newFile = new File(filePath + text + ".txt");
			file.renameTo(newFile);
			int index = noteChoose.getSelectedIndex();
			names[index] = text;
			writeData(names, namesFile);
			System.out.print(index);
			noteChoose.setSelectedIndex(index);
			noteChoose.setModel(new JComboBox<>(names).getModel());
		});
		
		JTextArea textArea = new JTextArea();
		textArea.addCaretListener(e -> {
			File file = new File(filePath + (String)noteChoose.getSelectedItem() + ".txt");
			String[] data = textArea.getText().split("\\r?\\n");
			writeData(data, file);
		});
		
		noteChoose.addActionListener(e -> {
			File file = new File(filePath + (String)noteChoose.getSelectedItem() + ".txt");
			String[] data = new String[0];
			try {
				data = readData(file);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error when loading file");
				textArea.setText("");
				nameField.setText("");
				return;
			}
			if (data.length > 0) {
				textArea.setText(data[0] + "\n");
				for (int i = 1; i < data.length; i++) {
					textArea.append(data[i] + "\n");
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

	public static boolean isEmoji(String message){
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
