package com.productivity;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;
import java.awt.BorderLayout;

import javax.swing.*;

public class NotesPanel extends JDesktopPane {

    private static File namesFile = new File(gui.currentPath + "Notes\\names.TXT");
    
    public NotesPanel() {
        JMenu menu = new JMenu();
        String[] names = readData(namesFile);
        for (int i = 0; i < names.length; i++) {
            JMenuItem menuItem = new JMenuItem(names[i]);
            menu.add(menuItem);
        }
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.PAGE_START, menuBar);
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
