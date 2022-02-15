package com.productivity.Custom;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.productivity.CheckBoxes;
import com.productivity.JTextFieldLimit;
import com.productivity.gui;

public class AddCustomCheckList extends JPanel {

    private static ArrayList<String> names = new ArrayList<String>();
    //private static String customPath = (!gui.debug)?"classes\\com\\productivity\\Custom\\Saves\\":"src\\main\\java\\com\\productivity\\Custom\\Saves\\";
    private static String customPath = gui.currentCustomPath;
    private static File customNames = new File(gui.currentCustomPath + "customNames.TXT");
    private static Box vertical = Box.createVerticalBox();
    private static HashMap<String, CheckBoxes> checkBoxes = new HashMap<String, CheckBoxes>();
    private static int charLimit = 10;
    
    public AddCustomCheckList() {
        JTextField name = new JTextField();
        name.setDocument(new JTextFieldLimit(charLimit));
        name.addActionListener(e -> {
            if (!names.contains(name.getText()) && !name.getText().equals("") && !(isEmoji(name.getText()))) {
                addCheckList(name.getText());
                if (getNumberOfChecklists() == 1) {
                    gui.customCheckListVisibility(true);
                    gui.repaintFrame();
                }
                name.setText("");
            }
            else {
                JOptionPane.showMessageDialog(this, "Please enter vaild name");
            }
        });
        JLabel nameLbl = new JLabel("Name Of Custom Checklist:");
        Box nameBox = Box.createVerticalBox();
        nameBox.add(nameLbl);
        nameBox.add(name);
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.NORTH, nameBox);
        super.add(BorderLayout.CENTER, vertical);
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
    
    public static int getNumberOfChecklists() {
        return readData(customNames).length;
    }
    
    public static void loadCheckLists() {
        try {
            String[] data = readData(customNames);
            names = new ArrayList<String>(Arrays.asList(data));
            for (int i = 0; i < names.size(); i++) {
                addCheckList(names.get(i));
            }
        } catch (Exception e) {
            writeData("", customNames);
        }
    }

    public static JCheckBox[] getRandomCheckBoxes() {
        if (names.size() <= 0) {
            return null;
        }
        int index = (int)(Math.random() * names.size());
        CheckBoxes checkBox = checkBoxes.get(names.get(index));
        return checkBox.getBoxes();
    }
    
    private static void addCheckList(String n) {
        File name = new File(customPath + n + "Name.TXT");
        File color = new File(customPath + n + "Color.TXT");
        File check = new File(customPath + n + "Check.TXT");
        if (!name.exists() && !color.exists() && !check.exists()) {        
            try {
                name.createNewFile();
                color.createNewFile();
                check.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!names.contains(n)) {
            names.add(n);
        }
        saveChecklists();
        JButton button = new JButton(n);
        button.addActionListener(e -> {
            vertical.remove(button);
            gui.repaintFrame();
            deleteChecklist(n);
            saveChecklists();
        });
        button.setFocusPainted(false);
        vertical.add(button);
        CheckBoxes checkBox = new CheckBoxes(gui.height, gui.length, name, check, color, false);
        checkBoxes.put(n, checkBox);
        gui.customCheckList.addCheckList(checkBox, n);
    }
    
    private static void deleteChecklist(String n) {
        File name = new File(customPath + n + "Name.TXT");
        File color = new File(customPath + n + "Color.TXT");
        File check = new File(customPath + n + "Check.TXT");
        name.delete();
        color.delete();
        check.delete();
        names.remove(n);
        checkBoxes.remove(n);
        gui.customCheckList.removeChecklist(n);
        if (names.size() <= 0) {
            gui.customCheckListVisibility(false);
        }
    }
    
    private static void saveChecklists() {
        String[] data = new String[names.size()];
        data = names.toArray(data);
        writeData(data, customNames);
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
