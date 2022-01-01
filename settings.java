import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
//TODO add popup for leaving fields blank
public class settings extends JPanel {
    
    static HashMap<String, String> settings = new HashMap<String, String>();
    static String currentDir = System.getProperty("user.dir");
	static File settingsFile = new File(currentDir + "\\Saves\\settings.TXT"); //old new File(currentDir + "\\settings.TXT");

    public settings() {
        super.setLayout(new GridLayout(15, 2));
        addSetting("Checklist Rows:", "checkRows");
        addSetting("Checklist Collums:", "checkCollums");
        super.setVisible(true);
    }

    void addSetting(String name, String key) {
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
        super.add(label);
        super.add(textField);
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
