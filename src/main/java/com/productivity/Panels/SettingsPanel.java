package com.productivity.Panels;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import java.awt.AWTException;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;

import com.productivity.BlockSites;
import com.productivity.CheckBoxes;
import com.productivity.Productivity;
import com.productivity.Custom.AddCustomCheckList;
import com.productivity.Util.Notification;

public class SettingsPanel extends JTabbedPane {
    
    private static final String[] kTimeOptions = {"Seconds", "Minutes", "Hours"};
    
    private static HashMap<String, String> mSettings = new HashMap<String, String>();
    private static File mSettingsFile;
    private static File mNameFile;
    private static File mStateFile;
    private static File mColorFile;
    private static File mReminderFile;

    private JPanel mConfigPanel = new JPanel();
    private Box mConfigBox = Box.createVerticalBox();
    private JPanel mReminderPanel = new JPanel();
    private BlockSites mBlockSites = new BlockSites();
    
    private static CheckBoxes mDailyPanel;
    private static int mTimeMultiplier = -1;
    private static boolean mStopSound = false;
    
    private enum settingTypes {
        text,
        checkbox,
        number,
        confetti
    }
    
    Timer mTime;
    TimerTask mTask;
    
    public SettingsPanel() {
        super.setFocusable(false);
        mDailyPanel = new CheckBoxes(mNameFile, mStateFile, mColorFile, true, true);
        runBoolean allOnTop = (a) -> Productivity.getInstance().setOnTop(a);
        addSetting("Always on top", "onTop", "Makes window always on your screen unless you minimize it", settingTypes.checkbox, allOnTop, false, null);
        runBoolean reminderActive = (a) -> {
            boolean reminderExists = false;
            boolean blockExists = false;
            Component[] comp = super.getComponents();
            for (Component c : comp) {
                if (c.equals(mReminderPanel)) {
                    reminderExists = true;
                }
                else if (c.equals(mBlockSites)) {
                    blockExists = true;
                }
                else if (reminderExists && blockExists) {
                    break;
                }
            }
            if (a) {
                if (!reminderExists) {
                    if (blockExists) {
                        super.insertTab("Reminder", null, mReminderPanel, null, 2);
                    }
                    else {
                        super.insertTab("Reminder", null, mReminderPanel, null, 1);
                    }
                    mStopSound = false;
                }
            }
            else {
                if (reminderExists) {
                    super.remove(mReminderPanel);
                    mStopSound = true;
                }
            }
        };
        addSetting("Reminder", "reminderActive", "Activates reminder tab", settingTypes.checkbox, reminderActive, false, null);
        runBoolean runOnStartup = (a) -> Productivity.getInstance().runOnStartup(a);
        addSetting("Run on startup", "runOnStartup", "Runs program when your computer starts", settingTypes.checkbox, runOnStartup, true, "Are you sure");
        runBoolean blockSitesActive = (a) -> {
            boolean exists = false;
            Component[] comp = super.getComponents();
            for (Component c : comp) {
                if (c.equals(mBlockSites)) {
                    exists = true;
                    break;
                }
            }
            if (a) {
                if (!exists) {
                    super.insertTab("Block Sites", null, mBlockSites, null, 2);
                }
            }
            else {
                if (exists) {
                    super.remove(mBlockSites);
                }
            }
            TimerPanel.setAllowBlock(a);
        };
        addSetting("Block Sites", "blockSites", "Allows you to block sites", settingTypes.checkbox, blockSitesActive, true, "Are you sure");
        addSetting("Confetti", "wantConfetti", "Enable/Disable Confetti", settingTypes.checkbox, null, false, null);
        addSetting("Selected Confetti: ", "currentConfetti", "", settingTypes.confetti, null, false, null);
        mConfigPanel.add(mConfigBox);
        super.addTab("Config", mConfigPanel);
        if (Boolean.parseBoolean(getSetting("blockSites"))) {
            super.addTab("Block Sites", mBlockSites);
        }
        reminder();
        if (Boolean.parseBoolean(getSetting("reminderActive"))) {
            super.addTab("Reminder", mReminderPanel);
            mStopSound = false;
        }
        else {
            mStopSound = true;
        }
        super.addTab("Custom Setup", new AddCustomCheckList());
        super.addTab("Daily Setup", mDailyPanel);
    }
    
    private void addSetting(String name, String key, String tooltip, settingTypes type, runBoolean rt, boolean conformation, String conformationDio) {
        switch(type) {
            case checkbox:
            JCheckBox checkBox = new JCheckBox(name);
            checkBox.setFocusPainted(false);
            checkBox.addActionListener(e -> {
                if (conformation && checkBox.isSelected()) {
                    int result = JOptionPane.showConfirmDialog(this, conformationDio);
                    if (result != JOptionPane.YES_OPTION) {
                        checkBox.setSelected(false);
                        return;
                    }
                }
                mSettings.put(key, Boolean.toString(checkBox.isSelected()));
                if (rt != null) {
                    runOperation(checkBox.isSelected(), rt);
                }
                saveSettings();
            });
            try {
                checkBox.setSelected(Boolean.parseBoolean(mSettings.get(key)));
            }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("Setting does not exist");
            }
            checkBox.setToolTipText(tooltip);
            Box horizontal = Box.createHorizontalBox();
            horizontal.add(checkBox);
            mConfigBox.add(horizontal);
            break;
            case number:
            JLabel numLabel = new JLabel(name);
            JTextField numField = new JTextField();
            numField.addActionListener(e -> {
                boolean notInt = false;
                try {
                    Integer.parseInt(numField.getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    notInt = true;
                }
                if (notInt || Integer.parseInt(numField.getText()) <= 0) {
                    JOptionPane.showMessageDialog(this, "Enter valid positive number");
                }
                else {
                    mSettings.put(key, numField.getText());
                    saveSettings();
                }
            });
            try {
                numField.setText(mSettings.get(key));
            }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("Setting does not exist");
            }
            numField.setToolTipText(tooltip);
            Box numHorizontal = Box.createHorizontalBox();
            numHorizontal.add(numLabel);
            numHorizontal.add(numField);
            mConfigBox.add(numHorizontal);
            break;
            case text:
            JLabel txtLabel = new JLabel(name);
            JTextField txtField = new JTextField();
            txtField.addActionListener(e -> {
                if (txtField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Enter valid text");
                }
                else {
                    mSettings.put(key, txtField.getText());
                    saveSettings();
                }
            });
            try {
                txtField.setText(mSettings.get(key));
            }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("Setting does not exist");
            }
            txtField.setToolTipText(tooltip);
            Box txtHorizontal = Box.createHorizontalBox();
            txtHorizontal.add(txtLabel);
            txtHorizontal.add(txtField);
            mConfigBox.add(txtHorizontal);
            break;
            case confetti:
            JLabel confettiLabel = new JLabel(name);
            String[] typesOfConfetti = {"High", "Low"};
            JComboBox<String> confettiField = new JComboBox<String>(typesOfConfetti);
            confettiField.setFocusable(false);
            confettiField.addActionListener(e -> {
                mSettings.put(key, Integer.toString(confettiField.getSelectedIndex()));
                saveSettings();
                Productivity.getInstance().setConfetti(confettiField.getSelectedIndex());
            });
            try {
                confettiField.setSelectedIndex(Integer.parseInt(mSettings.get(key)));
            }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("Setting does not exist");
            }
            confettiField.setToolTipText(tooltip);
            Box confettiHorizontal = Box.createHorizontalBox();
            confettiHorizontal.add(confettiLabel);
            confettiHorizontal.add(confettiField);
            mConfigBox.add(confettiHorizontal);
            break;
            default:
            break;
        }
        
        
    }
    
    private static void loadFiles() {
        mSettingsFile = new File(Productivity.getInstance().getCurrentPath()+"Saves\\settings.TXT");
        mNameFile = new File(Productivity.getInstance().getCurrentPath()+"Saves\\daily.TXT");
        mStateFile = new File(Productivity.getInstance().getCurrentPath()+"Saves\\dailyCheck.TXT");
        mColorFile = new File(Productivity.getInstance().getCurrentPath()+"Saves\\dailyColor.TXT");
        mReminderFile = new File(Productivity.getInstance().getCurrentPath()+"Saves\\reminder.TXT");
    }
    
    private void reminder() {
        int[] data = loadTimer();
        JComboBox<String> timeList = new JComboBox<>(kTimeOptions);
        timeList.setFocusable(false);
        timeList.addActionListener(e -> {
            switch(timeList.getSelectedIndex()) {
                case 0:
                mTimeMultiplier = 1;
                break;
                case 1:
                mTimeMultiplier = 1 * 60;
                break;
                case 2:
                mTimeMultiplier = 1 * 60 * 60;
                break;
                default:
                mTimeMultiplier = 1;
                break;
            }
        });
        timeList.setSelectedIndex(data[0]);
        JTextField textField = new JTextField();
        textField.setText(Integer.toString(data[1]));
        JProgressBar progressBar = new JProgressBar(0, Integer.parseInt(textField.getText()) * mTimeMultiplier);
        textField.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(textField.getText());
                if (Integer.parseInt(textField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                notInt = true;
            }
            if (textField.getText().equals("") || notInt) {
                JOptionPane.showMessageDialog(this, "Enter valid positive time");
            }
            else {
                progressBar.setMaximum(Integer.parseInt(textField.getText()) * mTimeMultiplier);
                stopTimer();
                startTimer(Integer.parseInt(textField.getText()), progressBar);
                saveTimer(timeList, textField);
            }
        });
        JButton save = new JButton("      Save      ");
        save.setFocusPainted(false);
        save.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(textField.getText());
                if (Integer.parseInt(textField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                notInt = true;
            }
            if (textField.getText().equals("") || notInt) {
                JOptionPane.showMessageDialog(this, "Enter valid positive time");
            }
            else {
                progressBar.setMaximum(Integer.parseInt(textField.getText()) * mTimeMultiplier);
                stopTimer();
                startTimer(Integer.parseInt(textField.getText()), progressBar);
                saveTimer(timeList, textField);
            }
        });
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JLabel info = new JLabel("Causes a beep to be played every:");
        Box vertical = Box.createVerticalBox();
        vertical.add(info);
        vertical.add(timeList);
        vertical.add(textField);
        vertical.add(save);
        vertical.add(progressBar);
        mReminderPanel.add(vertical);
        startTimer(Integer.parseInt(textField.getText()), progressBar);
    }
    
    private void saveTimer(JComboBox<String> combo, JTextField field) {
        String[] data = {Integer.toString(combo.getSelectedIndex()), field.getText()};
        writeData(data, mReminderFile);
    }
    
    private void startTimer(int length, JProgressBar bar) {
        mTask = new TimerTask()
        {
            int seconds = length * mTimeMultiplier;
            int i = 0;
            @Override
            public void run()
            {
                if(i == seconds && i != 0) {
                    if (!mStopSound)
                        Toolkit.getDefaultToolkit().beep();
                        try {
                            Notification.displayTray("Reminder", "");
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
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
        mTime = new Timer();
        mTime.schedule(mTask, 0, 1000);
    }
    
    private int[] loadTimer() {
        String[] data = readData(mReminderFile);
        int[] results = new int[2];
        switch(Integer.parseInt(data[0])) {
            case 0:
            mTimeMultiplier = 1;
            break;
            case 1:
            mTimeMultiplier = 60;
            break;
            case 2:
            mTimeMultiplier = 60 * 60;
            break;
            default:
            mTimeMultiplier = 1;
            break;
        }
        results[0] = Integer.parseInt(data[0]);
        results[1] = Integer.parseInt(data[1]);
        return results;
    }
    
    private void stopTimer() {
        mTask.cancel();
        mTime.cancel();
        mTime.purge();
    }

    public static void setDailySelected(boolean state, int index) {
        mDailyPanel.setSelected(state, index);
    }
    
    public static String getSetting(String key) {
        return mSettings.get(key);
    }
    
    public static void loadSettings() {
        loadFiles();
        String[] data = readData(mSettingsFile);
        String[] keys = new String[(data.length - 1)/2];
        String[] values = new String[(data.length - 1)/2];
        for (int i = 0; i < (data.length - 1)/2; i++) {
            if (data[i].equals("*")) {
                //System.out.println("End of keys");
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
            mSettings.put(keys[i], values[i]);
        }
    }
    
    private void saveSettings() {
        String[] data = new String[(mSettings.size() * 2) + 1];
        int index = 0;
        for (String setting : mSettings.keySet()) {
            data[index] = setting;
            index++;
        }
        data[index] = "*";
        index++;
        for (String setting : mSettings.values()) {
            data[index] = setting;
            index++;
        }
        writeData(data, mSettingsFile);
    }
    
    public static void checkFile(File f) {
        try {
            if (f.exists()) {
                f.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        