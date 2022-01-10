import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.util.Timer;

public class settings extends JTabbedPane {
    //TODO tooltips
    static HashMap<String, String> settings = new HashMap<String, String>();
    static File settingsFile = new File("Saves\\settings.TXT");
    static File checkListFile = new File("Saves\\daily.TXT");
    static File checkListStateFile = new File("Saves\\dailyCheck.TXT");
    static File reminderFile = new File("Saves\\reminder.TXT");
    JPanel configPanel = new JPanel();
    Box configBox = Box.createVerticalBox();
    JPanel reminderPanel = new JPanel();
    
    JPanel dailyPanel = new checkBoxes(gui.height, gui.length, checkListFile, checkListStateFile, true);
    String[] timeOptions = {"Seconds", "Minutes", "Hours"};
    static int timeMuitplyer = 1;
    
    private enum settingTypes {
        text,
        checkbox,
        number
    }
    
    Timer time;
    TimerTask task;
    
    public settings() {
        JLabel label = new JLabel("Press enter to confirm");
        configBox.add(label);
        runBoolean allOnTop = (a) -> gui.setOnTop(a);
        addSetting("Always on top", "onTop", "Makes window always on your screen unless you minimize it", settingTypes.checkbox, allOnTop);
        configPanel.add(configBox);
        configPanel.setVisible(true);
        super.addTab("Config", configPanel);
        reminder();
        super.addTab("Reminder", reminderPanel);
        super.addTab("Daily Checklist", dailyPanel);
        super.setVisible(true);
    }
    
    void reminder() {
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
                StopTimer();
                StartTimer(Integer.parseInt(textField.getText()), progressBar);
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
                StopTimer();
                StartTimer(Integer.parseInt(textField.getText()), progressBar);
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
        StartTimer(Integer.parseInt(textField.getText()), progressBar);
    }
    
    public void saveTimer(JComboBox<String> combo, JTextField field) {
        String[] data = {Integer.toString(combo.getSelectedIndex()), field.getText()};
        writeData(data, reminderFile);
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
    
    private void StartTimer(int length, JProgressBar bar) {
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
    
    private void StopTimer() {
        task.cancel();
        time.cancel();
        time.purge();
    }
    
    private void addSetting(String name, String key, String tooltip, settingTypes type, runBoolean rt) {
        switch(type) {
            case checkbox:
            JCheckBox checkBox = new JCheckBox(name);
            checkBox.addActionListener(e -> {
                settings.put(key, Boolean.toString(checkBox.isSelected()));
                runOperation(checkBox.isSelected(), rt);
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
    
    public static String getSetting(String key) {
        return settings.get(key);
    }
    
    public static void loadSettings() {
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
    
    public void saveSettings() {
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
    
    interface runBoolean {
        void operation(Boolean a);
    }
    
    public static void runOperation(Boolean a, runBoolean rt) {
        rt.operation(a);
    }
    
    interface runString {
        void operation(String a);
    }
    
    public static void runOperation(String a, runString rt) {
        rt.operation(a);
    }
    
    interface runInt {
        void operation(int a);
    }
    
    public static void runOperation(int a, runInt rt) {
        rt.operation(a);
    }
}
