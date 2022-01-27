package com.productivity.Custom;

import java.awt.BorderLayout;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.swing.*;
import com.productivity.checkBoxes;
import com.productivity.gui;

public class addCustomCheckList extends JPanel {
    
    static ArrayList<String> names = new ArrayList<String>();
    public static String customPath = "src\\main\\java\\com\\productivity\\Custom\\Saves\\";
    public static File customNames = new File(customPath + "customNames.TXT");
    
    public addCustomCheckList() {
        JTextField name = new JTextField();
        name.addActionListener(e -> {
            addCheckList(name.getText(), true);
        });

        super.setLayout(new BorderLayout());
        super.add(BorderLayout.NORTH, name);
    }
    
    public static JTabbedPane loadCheckLists() {
        String[] data = readData(customNames);
        JTabbedPane pane = new JTabbedPane();
        names = new ArrayList<String>(Arrays.asList(data));
        for (int i = 0; i < names.size(); i++) {
            pane.addTab(names.get(i), addCheckList(names.get(i), false));
        }
        return pane;
    }
    
    public static checkBoxes addCheckList(String n, Boolean refresh) {
        File name = new File(customPath + n + "Name.TXT");
        File color = new File(customPath + n + "Color.TXT");
        File check = new File(customPath + n + "Check.TXT");
        if (name.exists() || color.exists() || check.exists()) {        
            try {
                name.createNewFile();
                color.createNewFile();
                check.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        names.add(n);
        if (refresh) {
            customCheckList.addCheckList(new checkBoxes(gui.height, gui.length, name, check, color, false), n);
        }
        return new checkBoxes(gui.height, gui.length, name, check, color, false);
    }
    
    public static String[] readData(File file) {
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
    
    public static void writeData(String data, File file) {
        try  {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeData(String[] dataArr, File file) {
        String data = "";
        for (int i = 0; i < dataArr.length; i++) {
            data += (dataArr[i] + "\n");
        }
        writeData(data, file);
    }
}
