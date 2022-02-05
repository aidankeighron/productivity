package com.productivity;

import java.io.*;
import java.util.*;
import javax.swing.*;
import com.productivity.Custom.addCustomCheckList;
import java.awt.*;
import java.nio.file.Files;
import java.util.Timer;

public class SettingsPanel extends JTabbedPane {
    
    //TODO change look and feel
    private static HashMap<String, String> settings = new HashMap<String, String>();
    private static File settingsFile;
    private static File nameFile;
    private static File stateFile;
    private static File colorFile;
    private static File reminderFile;
    //private static File settingsFile = new File((!gui.debug)?"classes\\settings.TXT":gui.debugPath+"settings.TXT");
    //private static File nameFile = new File((!gui.debug)?"classes\\daily.TXT":gui.debugPath+"daily.TXT");
    //private static File stateFile = new File((!gui.debug)?"classes\\dailyCheck.TXT":gui.debugPath+"dailyCheck.TXT");
    //private static File colorFile = new File((!gui.debug)?"classes\\dailyColor.TXT":gui.debugPath+"dailyColor.TXT");
    //private static File reminderFile = new File((!gui.debug)?"classes\\reminder.TXT":gui.debugPath+"reminder.TXT");
    private JPanel configPanel = new JPanel();
    private Box configBox = Box.createVerticalBox();
    private JPanel reminderPanel = new JPanel();
    private BlockSites BlockSites = new BlockSites();
    
    private JPanel dailyPanel;
    private static String[] timeOptions = {"Seconds", "Minutes", "Hours"};
    private static int timeMuitplyer = 1;
    
    private enum settingTypes {
        text,
        checkbox,
        number
    }
    
    Timer time;
    TimerTask task;
    
    public SettingsPanel() {
        dailyPanel = new CheckBoxes(gui.height, gui.length, nameFile, stateFile, colorFile, true);
        JLabel label = new JLabel("Press enter to confirm");
        configBox.add(label);
        runBoolean allOnTop = (a) -> gui.setOnTop(a);
        addSetting("Always on top", "onTop", "Makes window always on your screen unless you minimize it", settingTypes.checkbox, allOnTop);
        runBoolean reminderActive = (a) -> {
            boolean exists = false;
            Component[] comp = super.getComponents();
            for (Component c : comp) {
                if (c.equals(reminderPanel)) {
                    exists = true;
                    break;
                }
            }
            if (a) {
                if (!exists) {
                    super.addTab("Reminder", reminderPanel);
                    reminder();
                }
            }
            else {
                if (exists) {
                    super.remove(reminderPanel);
                    stopTimer();
                }
            }
        };
        addSetting("Reminder", "reminderActive", "Activates reminder tab", settingTypes.checkbox, reminderActive);
        runBoolean runOnStartup = (a) -> gui.runOnStartup(a);
        addSetting("Run on startup", "runOnStartup", "Runs program when your computer starts", settingTypes.checkbox, runOnStartup);
        runBoolean blockSitesActive = (a) -> {
            boolean exists = false;
            Component[] comp = super.getComponents();
            for (Component c : comp) {
                if (c.equals(BlockSites)) {
                    exists = true;
                    break;
                }
            }
            if (a) {
                if (!exists) {
                    super.addTab("Block Sites", BlockSites);
                }
            }
            else {
                if (exists) {
                    super.remove(BlockSites);
                }
            }
            TimerPanel.setAllowBlock(a);
        };
        addSetting("Block Sites", "blockSites", "Allows you to block sites", settingTypes.checkbox, blockSitesActive);
        configPanel.add(configBox);
        super.addTab("Config", configPanel);
        if (Boolean.parseBoolean(getSetting("blockSites"))) {
            super.addTab("Block Sites", BlockSites);
        }
        if (Boolean.parseBoolean(getSetting("reminderActive"))) {
            super.addTab("Reminder", reminderPanel);
            reminder();
        }
        super.addTab("Custom Checklsits", new addCustomCheckList());
        super.addTab("Daily Checklist", dailyPanel);
    }
    
    private void addSetting(String name, String key, String tooltip, settingTypes type, runBoolean rt) {
        switch(type) {
            case checkbox:
            JCheckBox checkBox = new JCheckBox(name);
            checkBox.addActionListener(e -> {
                settings.put(key, Boolean.toString(checkBox.isSelected()));
                if (rt != null) {
                    runOperation(checkBox.isSelected(), rt);
                }
                saveSettings();
            });
            try {
                checkBox.setSelected(Boolean.parseBoolean(settings.get(key)));
            }
            catch(Exception e) {
                System.out.println("Setting dosent exist");
            }
            checkBox.setToolTipText(tooltip);
            Box horizontal = Box.createHorizontalBox();
            horizontal.add(checkBox);
            configBox.add(horizontal);
            break;
            case number:
            JLabel numLabel = new JLabel(name);
            JTextField numField = new JTextField();
            numField.addActionListener(e -> {
                boolean notInt = false;
                try {
                    Integer.parseInt(numField.getText());
                } catch (Exception ex) {
                    notInt = true;
                }
                if (notInt || Integer.parseInt(numField.getText()) <= 0) {
                    JOptionPane.showMessageDialog(this, "Enter vaild positive number");
                }
                else {
                    settings.put(key, numField.getText());
                    saveSettings();
                }
            });
            try {
                numField.setText(settings.get(key));
            }
            catch(Exception e) {
                System.out.println("Setting dosent exist");
            }
            numField.setToolTipText(tooltip);
            Box numHorizontal = Box.createHorizontalBox();
            numHorizontal.add(numLabel);
            numHorizontal.add(numField);
            configBox.add(numHorizontal);
            break;
            case text:
            JLabel txtLabel = new JLabel(name);
            JTextField txtField = new JTextField();
            txtField.addActionListener(e -> {
                if (txtField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Enter vaild text");
                }
                else {
                    settings.put(key, txtField.getText());
                    saveSettings();
                }
            });
            try {
                txtField.setText(settings.get(key));
            }
            catch(Exception e) {
                System.out.println("Setting dosent exist");
            }
            txtField.setToolTipText(tooltip);
            Box txtHorizontal = Box.createHorizontalBox();
            txtHorizontal.add(txtLabel);
            txtHorizontal.add(txtField);
            configBox.add(txtHorizontal);
            break;
            default:
            break;
        }
        
        
    }
    
    private static void loadFiles() {
        settingsFile = new File(gui.currentPath+"Saves\\settings.TXT");
        nameFile = new File(gui.currentPath+"Saves\\daily.TXT");
        stateFile = new File(gui.currentPath+"Saves\\dailyCheck.TXT");
        colorFile = new File(gui.currentPath+"Saves\\dailyColor.TXT");
        reminderFile = new File(gui.currentPath+"Saves\\reminder.TXT");
    }
    
    private void reminder() {
        int[] data = loadTimer();
        JComboBox<String> timeList = new JComboBox<>(timeOptions);
        timeList.addActionListener(e -> {
            switch(timeList.getSelectedIndex()) {
                case 0:
                timeMuitplyer = 1;
                break;
                case 1:
                timeMuitplyer = 60;
                break;
                case 2:
                timeMuitplyer = 60 * 60;
                break;
                default:
                timeMuitplyer = 1;
                break;
            }
        });
        timeList.setSelectedIndex(data[0]);
        JTextField textField = new JTextField();
        textField.setText(Integer.toString(data[1]));
        JProgressBar progressBar = new JProgressBar(0, Integer.parseInt(textField.getText()) * timeMuitplyer);
        textField.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(textField.getText());
                if (Integer.parseInt(textField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                notInt = true;
            }
            if (textField.getText().equals("") || notInt) {
                JOptionPane.showMessageDialog(this, "Enter vaild positive time");
            }
            else {
                progressBar.setMaximum(Integer.parseInt(textField.getText()) * timeMuitplyer);
                stopTimer();
                startTimer(Integer.parseInt(textField.getText()), progressBar);
                saveTimer(timeList, textField);
            }
        });
        JButton save = new JButton("      Save      ");
        save.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(textField.getText());
                if (Integer.parseInt(textField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                notInt = true;
            }
            if (textField.getText().equals("") || notInt) {
                JOptionPane.showMessageDialog(this, "Enter vaild positive time");
            }
            else {
                progressBar.setMaximum(Integer.parseInt(textField.getText()) * timeMuitplyer);
                stopTimer();
                startTimer(Integer.parseInt(textField.getText()), progressBar);
                saveTimer(timeList, textField);
            }
        });
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        Box vertical = Box.createVerticalBox();
        vertical.add(timeList);
        vertical.add(textField);
        vertical.add(save);
        vertical.add(progressBar);
        reminderPanel.add(vertical);
        startTimer(Integer.parseInt(textField.getText()), progressBar);
    }
    
    private void saveTimer(JComboBox<String> combo, JTextField field) {
        String[] data = {Integer.toString(combo.getSelectedIndex()), field.getText()};
        writeData(data, reminderFile);
    }
    
    private void startTimer(int length, JProgressBar bar) {
        task = new TimerTask()
        {
            int seconds = length * timeMuitplyer;
            int i = 0;
            @Override
            public void run()
            {
                if(i == seconds && i != 0) {
                    Toolkit.getDefaultToolkit().beep();
                    bar.setValue(i);
                    i = -1;
                }
                else {
                    bar.setValue(i);
                }
                if (i < seconds) {
                    i++;
                }
            }
        };
        time = new Timer();
        time.schedule(task, 0, 1000);
    }
    
    private int[] loadTimer() {
        String[] data = readData(reminderFile);
        int[] results = new int[2];
        switch(Integer.parseInt(data[0])) {
            case 0:
            timeMuitplyer = 1;
            break;
            case 1:
            timeMuitplyer = 60;
            break;
            case 2:
            timeMuitplyer = 60 * 60;
            break;
            default:
            timeMuitplyer = 1;
            break;
        }
        results[0] = Integer.parseInt(data[0]);
        results[1] = Integer.parseInt(data[1]);
        return results;
    }
    
    private void stopTimer() {
        task.cancel();
        time.cancel();
        time.purge();
    }
    
    public static String getSetting(String key) {
        return settings.get(key);
    }
    
    public static void loadSettings() {
        loadFiles();
        String[] data = readData(settingsFile);
        String[] keys = new String[(data.length - 1)/2];
        String[] values = new String[(data.length - 1)/2];
        for (int i = 0; i < (data.length - 1)/2; i++) {
            if (data[i].equals("*")) {
                System.out.println("End of keys");
                break;
            }
            keys[i] = data[i];
        }
        int j = 0;
        for (int i = (data.length - 1)/2 + 1; i < data.length; i++) {
            values[j] = data[i];
            j++;
        }
        for (int i = 0; i < keys.length; i++) {
            settings.put(keys[i], values[i]);
        }
    }
    
    private void saveSettings() {
        String[] data = new String[(settings.size() * 2) + 1];
        int index = 0;
        for (String setting : settings.keySet()) {
            data[index] = setting;
            index++;
        }
        data[index] = "*";
        index++;
        for (String setting : settings.values()) {
            data[index] = setting;
            index++;
        }
        writeData(data, settingsFile);
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
    
    interface runBoolean {
        void operation(Boolean a);
    }
    
    private static void runOperation(Boolean a, runBoolean rt) {
        rt.operation(a);
    }
    
    interface runString {
        void operation(String a);
    }
    
    // private static void runOperation(String a, runString rt) {
        //     rt.operation(a);
        // }
        
        interface runInt {
            void operation(int a);
        }
        
        // private static void runOperation(int a, runInt rt) {
            //     rt.operation(a);
            // }
        }
        