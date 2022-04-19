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
import com.productivity.HomePanel;
import com.productivity.JTextFieldLimit;
import com.productivity.Productivity;

public class AddCustomCheckList extends JPanel {
    
    private static final String kCustomPath = Productivity.getCurrentCustomPath();
    private static final File kCustomNames = new File(Productivity.getCurrentCustomPath()+"customNames.TXT");
    private static final int kCharLimit = 10;
    private static final int kMaxCustomCheckLists = 8;
    
    private static Box mVertical = Box.createVerticalBox();
    private static ArrayList<String> mNames = new ArrayList<String>();
    private static HashMap<String, CheckBoxes> mCheckBoxes = new HashMap<String, CheckBoxes>();
    private static int mCurrentNumCheckLists = 0;
    private static boolean mWantHome = true;
    private static int mRandomIndex = -1;
    
    public AddCustomCheckList() {
        JTextField name = new JTextField();
        name.setDocument(new JTextFieldLimit(kCharLimit));
        name.addActionListener(e -> {
            if (!testValidFileName(name.getText())) {
                JOptionPane.showMessageDialog(this, "Please enter valid name");
                return;
            }
            if (mCurrentNumCheckLists > kMaxCustomCheckLists) {
                JOptionPane.showMessageDialog(this, "Maximum custom checklists reached");
                return;
            }
            if (!mNames.contains(name.getText()) && !name.getText().equals("")) {
                addCheckList(name.getText(), mWantHome);
                mCurrentNumCheckLists++;
                if (getNumberOfChecklists() == 1) {
                    Productivity.customCheckListVisibility(true);
                    Productivity.repaintFrame();
                }
                name.setText("");
                HomePanel.getInstance().reset();
            }
            else {
                JOptionPane.showMessageDialog(this, "Please enter valid name");
            }
        });
        JCheckBox home = new JCheckBox("Home");
        home.setSelected(true);
        home.addActionListener(e -> {
            mWantHome = home.isSelected();
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
        super.add(BorderLayout.CENTER, mVertical);
    }
    
    private boolean testValidFileName(String text) {
        return text.matches("^[a-zA-Z0-9._ ]+$");
    }

    private static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
            purgeDirectory(file);
            file.delete();
        }
    }
    
    public static int getNumberOfChecklists() {
        return readData(kCustomNames).length/2;
    }
    
    public static void loadCheckLists() {
        try {
            String[] data = readData(kCustomNames);
            for (int i = 0; i < data.length; i += 2) {
                mNames.add(data[i]);
                addCheckList(data[i], Boolean.parseBoolean(data[i + 1]));
                mCurrentNumCheckLists++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            File dir = new File(Productivity.getCurrentCustomPath());
            purgeDirectory(dir);
            try {
                kCustomNames.createNewFile();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    public static JCheckBox[] getRandomCheckBoxes() {
        if (mNames.size() <= 0) {
            mRandomIndex = -1;
            return null;
        }
        int index = (int)(Math.random() * mNames.size());
        try {
            if (!mCheckBoxes.get(mNames.get(index)).getHome() || mCheckBoxes.get(mNames.get(index)).getBoxes().length <= 0) {
                if (index < mCheckBoxes.size()) {
                    for (int i = index; i < mCheckBoxes.size(); i++) {
                        if (mCheckBoxes.get(mNames.get(i)).getHome() && mCheckBoxes.get(mNames.get(index)).getBoxes().length > 0) {
                            index = i;
                            break;
                        }
                    }
                }
                if (!mCheckBoxes.get(mNames.get(index)).getHome() || mCheckBoxes.get(mNames.get(index)).getBoxes().length <= 0) {
                    for (int i = index; i >= 0; i--) {
                        if (mCheckBoxes.get(mNames.get(i)).getHome() && mCheckBoxes.get(mNames.get(index)).getBoxes().length > 0) {
                            index = i;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting random check box");
        }
        CheckBoxes checkBox = mCheckBoxes.get(mNames.get(index));
        if (checkBox != null && mCheckBoxes.get(mNames.get(index)).getHome() && mCheckBoxes.get(mNames.get(index)).getBoxes().length > 0) {
            mRandomIndex = index;
            return checkBox.getBoxes();
        }
        mRandomIndex = -1;
        return null;
    }

    public static String getrandomName() {
        return (mRandomIndex != -1) ? mNames.get(mRandomIndex) : "";
    }
    
    public static void setCheckList(boolean state, int index, String name) {
        mCheckBoxes.get(name).setSelected(state, index);
    }
    
    private static void addCheckList(String n, boolean home) {
        File name = new File(kCustomPath + n + "Name.TXT");
        File color = new File(kCustomPath + n + "Color.TXT");
        File check = new File(kCustomPath + n + "Check.TXT");
        if (!name.exists() && !color.exists() && !check.exists()) {        
            try {
                name.createNewFile();
                color.createNewFile();
                check.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!mNames.contains(n)) {
            mNames.add(n);
        }
        JButton button = new JButton(n);
        button.addActionListener(e -> {
            mVertical.remove(button);
            Productivity.repaintFrame();
            deleteChecklist(n);
            saveChecklists();
            HomePanel.getInstance().reset();
        });
        button.setFocusPainted(false);
        mVertical.add(button);
        CheckBoxes checkBox = new CheckBoxes(Productivity.kHeight, Productivity.kLength, name, check, color, false, home);
        mCheckBoxes.put(n, checkBox);
        saveChecklists();
        CustomCheckList.getInstance().addCheckList(checkBox, n);
    }
    
    private static void deleteChecklist(String n) {
        File name = new File(kCustomPath + n + "Name.TXT");
        File color = new File(kCustomPath + n + "Color.TXT");
        File check = new File(kCustomPath + n + "Check.TXT");
        name.delete();
        color.delete();
        check.delete();
        mNames.remove(n);
        mCheckBoxes.remove(n);
        CustomCheckList.getInstance().removeChecklist(n);
        if (mNames.size() <= 0) {
            Productivity.customCheckListVisibility(false);
        }
        mCurrentNumCheckLists--;
    }
    
    private static void saveChecklists() {
        String[] data = new String[mNames.size() * 2];
        for (int i = 0; i < data.length; i += 2) {
            data[i] = mNames.get(i/2);
            data[i + 1] = Boolean.toString(mCheckBoxes.get(mNames.get(i/2)).getHome());
        }
        writeData(data, kCustomNames);
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
