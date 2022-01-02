import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
//import java.time.format.DateTimeFormatter; //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
//import java.time.LocalDateTime;  //LocalDateTime now = LocalDateTime.now(); 

public class dailyChecklist extends JPanel {
    
    static String currentDir = System.getProperty("user.dir");
    static File checkListFile = new File(currentDir + "\\Saves\\daily.TXT");
    static JPanel checkListPanel;

    public dailyChecklist() {
        resetBoxes();
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.WEST, checkListPanel);
        super.setVisible(true);
    }

    public static void resetBoxes() {
        checkListPanel = new JPanel(new GridLayout(gui.numRows, gui.numCollums));
        String[] names = readData(checkListFile);
        for (int i = 0; i < names.length; i++) {
            addCheckBox(names[i]);
        }
    }

    public static void add(JCheckBox box) {
		checkListPanel.add(box);
		gui.repaintFrame();
	}
	
	public static void addCheckBox(String name) {
		JCheckBox checkBox = new JCheckBox(name);
		add(checkBox);
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
	
	public void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(String[] dataArr, File file) {
		String data = "";
		for (int i = 0; i < dataArr.length; i++) {
			data += (dataArr[i] + "\n");
		}
		writeData(data, file);
	}
}
