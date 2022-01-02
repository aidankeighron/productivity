import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.util.Timer;
//TODO add popup for leaving fields blank and make sure settings are saved properly
//TODO when changing reminder timer length make sure the old timer is closed
public class settings extends JTabbedPane {
    
    static HashMap<String, String> settings = new HashMap<String, String>();
    static String currentDir = System.getProperty("user.dir");
	static File settingsFile = new File(currentDir + "\\Saves\\settings.TXT"); //old new File(currentDir + "\\settings.TXT");
    static File checkListFile = new File(currentDir + "\\Saves\\daily.TXT");
    static File reminderFile = new File(currentDir + "\\Saves\\reminder.TXT");
    JPanel configPanel = new JPanel();
    JPanel reminderPanel = new JPanel();
    JPanel dailyPanel = new checkBoxes(gui.numRows, gui.numCollums, checkListFile, true);
    String[] timeOptions = {"Seconds", "Minutes", "Hours"};
    static int timeMuitplyer = 1;

    public settings() {
        configPanel.setLayout(new GridLayout(15, 2));
        addSetting("Checklist Rows:", "checkRows");
        addSetting("Checklist Collums:", "checkCollums");
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
            progressBar.setMaximum(Integer.parseInt(textField.getText()) * timeMuitplyer);
            StartTimer(Integer.parseInt(textField.getText()), progressBar);
            saveTimer(timeList, textField);
        });
        JButton save = new JButton("      Save      ");
        save.addActionListener(e -> {
            progressBar.setMaximum(Integer.parseInt(textField.getText()) * timeMuitplyer);
            StartTimer(Integer.parseInt(textField.getText()), progressBar);
            saveTimer(timeList, textField);
        });
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        Box vertical = Box.createVerticalBox();
        vertical.add(timeList);
        vertical.add(textField);
        vertical.add(save);
        vertical.add(progressBar);
        reminderPanel.setLayout(new BorderLayout());
        reminderPanel.add(BorderLayout.WEST, vertical);
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
        TimerTask task = new TimerTask()
        {
            int seconds = length * timeMuitplyer;
            int i = 0;
            @Override
            public void run()
            {
                if(i == seconds && i != 0) {
                    Toolkit.getDefaultToolkit().beep();
                    bar.setValue(i);
                    i++;
                }
                else {
                    bar.setValue(i);
                }
                if (i < seconds) {
                    i++;
                }
            }
        };
        Timer time = new Timer();
        time.schedule(task, 0, 1000);
    }

    private void addSetting(String name, String key) {
        JLabel label = new JLabel(name);
        JTextField textField = new JTextField();
        textField.addActionListener(e -> {
			settings.put(key, textField.getText());
            saveSettings();
		});
        try {
            textField.setText(settings.get(key));
        }
        catch(Exception e) {
            System.out.println("Setting dosent exist");
        }
        configPanel.add(label);
        configPanel.add(textField);
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
}
