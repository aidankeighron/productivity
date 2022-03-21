package com.productivity.Custom;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
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
    private static int maxCustomCheckLists = 8;
    private static int currentNumCheckLists = 0;
    private static boolean wantHome = false;
    private static int randomIndex = -1;
    
    public AddCustomCheckList() {
        JTextField name = new JTextField();
        name.setDocument(new JTextFieldLimit(charLimit));
        name.addActionListener(e -> {
            if (!testValidFileName(name.getText())) {
                JOptionPane.showMessageDialog(this, "Please enter valid name");
                return;
            }
            if (currentNumCheckLists > maxCustomCheckLists) {
                JOptionPane.showMessageDialog(this, "Maximum custom checklists reached");
                return;
            }
            if (!names.contains(name.getText()) && !name.getText().equals("")) {
                addCheckList(name.getText(), wantHome);
                currentNumCheckLists++;
                if (getNumberOfChecklists() == 1) {
                    gui.customCheckListVisibility(true);
                    gui.repaintFrame();
                }
                name.setText("");
                gui.homeReset();
            }
            else {
                JOptionPane.showMessageDialog(this, "Please enter valid name");
            }
        });
        JCheckBox home = new JCheckBox("Home");
        home.addActionListener(e -> {
            wantHome = home.isSelected();
        });
        
        JLabel nameLbl = new JLabel("Name Of Custom Checklist:");
        Box nameBox = Box.createVerticalBox();
        Box inputBox = Box.createHorizontalBox();
        inputBox.add(name);
        inputBox.add(home);
        nameBox.add(nameLbl);
        nameBox.add(inputBox);
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.NORTH, nameBox);
        super.add(BorderLayout.CENTER, vertical);
    }
    
    private boolean testValidFileName(String text) {
        return text.matches("^[a-zA-Z0-9._ ]+$");
    }
    
    public static int getNumberOfChecklists() {
        return readData(customNames).length/2;
    }
    
    public static void loadCheckLists() {
        try {
            String[] data = readData(customNames);
            for (int i = 0; i < data.length; i += 2) {
                names.add(data[i]);
                addCheckList(data[i], Boolean.parseBoolean(data[i + 1]));
                currentNumCheckLists++;
            }

        } catch (Exception e) {
            File dir = new File(gui.currentCustomPath);
            purgeDirectory(dir);
            try {
                customNames.createNewFile();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    private static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
            purgeDirectory(file);
            file.delete();
        }
    }
    
    public static JCheckBox[] getRandomCheckBoxes() {
        if (names.size() <= 0) {
            randomIndex = -1;
            return null;
        }
        int index = (int)(Math.random() * names.size());
        try {
            if (!checkBoxes.get(names.get(index)).getHome()) {
                if (index < checkBoxes.size()) {
                    for (int i = index; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(names.get(i)).getHome()) {
                            index = i;
                            break;
                        }
                    }
                }
                if (!checkBoxes.get(names.get(index)).getHome()) {
                    for (int i = index; i >= 0; i--) {
                        if (checkBoxes.get(names.get(i)).getHome()) {
                            index = i;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting random check box");
            e.printStackTrace();
        }
        CheckBoxes checkBox = checkBoxes.get(names.get(index));
        if (checkBox != null && checkBoxes.get(names.get(index)).getHome()) {
            randomIndex = index;
            return checkBox.getBoxes();
        }
        randomIndex = -1;
        return null;
    }

    public static String getrandomName() {
        return (randomIndex != -1) ? names.get(randomIndex) : "";
    }
    
    public static void setCheckList(boolean state, int index, String name) {
        checkBoxes.get(name).setSelected(state, index);
    }
    
    private static void addCheckList(String n, boolean home) {
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
        JButton button = new JButton(n);
        button.addActionListener(e -> {
            vertical.remove(button);
            gui.repaintFrame();
            deleteChecklist(n);
            saveChecklists();
            gui.homeReset();
        });
        button.setFocusPainted(false);
        vertical.add(button);
        CheckBoxes checkBox = new CheckBoxes(gui.height, gui.length, name, check, color, false, home);
        checkBoxes.put(n, checkBox);
        saveChecklists();
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
        currentNumCheckLists--;
    }
    
    private static void saveChecklists() {
        String[] data = new String[names.size() * 2];
        for (int i = 0; i < data.length; i += 2) {
            data[i] = names.get(i/2);
            data[i + 1] = Boolean.toString(checkBoxes.get(names.get(i/2)).getHome());
        }
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
