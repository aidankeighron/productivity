import java.awt.BorderLayout;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Files;

public class gui extends JFrame {
	
	static ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	static JPanel checkListPannel = new JPanel();
	static String currentDir = System.getProperty("user.dir");
	static File checkListFile = new File(currentDir + "\\list.TXT");
	static JFrame frame = new JFrame("Check List");
	static GridBagConstraints gc = new GridBagConstraints();
	public static void main(String[] args) {
		start();
		checkBoxes();
	}
	
	public static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
	}
	
	public static void checkBoxes() {
		JTextField input = new JTextField();
		input.addActionListener(e -> {
			addCheckBox(input.getText());
			input.setText("");
			saveCheckBoxes();
		});
		
		JButton reset = new JButton("Reset");
		reset.addActionListener( e -> removeCheckBoxes());
		
		JButton clear = new JButton("Clear Selected");
		clear.addActionListener(e -> clearSelected());

		JPanel buttonPannel = new JPanel();
		buttonPannel.add(reset);
		buttonPannel.add(clear);

		frame.setLayout(new BorderLayout());
        checkListPannel.setLayout(new GridBagLayout());

		input.setBorder(new EmptyBorder(10, 10, 10, 10));
		checkListPannel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(checkListPannel, BorderLayout.WEST);
		frame.add(input, BorderLayout.NORTH);
		frame.add(buttonPannel, BorderLayout.SOUTH);
		//frame.add(checkListPannel, BorderLayout.CENTER);
		loadCheckBoxes();
		checkListPannel.setVisible(true);
		frame.setVisible(true);
	}
	static int count = 0;
	public static void add(JCheckBox box) { //Good
		//checkListPannel.add(box);
        gc.gridx = checkBoxes.size()/8;
		gc.gridy = checkBoxes.size() - (((checkBoxes.size()/8) * 8)+1);
		//gc.insets = new Insets(2, 0, 0, 2);
        gc.anchor = GridBagConstraints.NORTHWEST;
        //gc.weightx = 1;
        //gc.weighty = 1;
        checkListPannel.add(box, gc);
		saveCheckBoxes();
		frame.repaint();
		frame.setVisible(true);
	}
	
	public static void addCheckBox(String name) { //Good
		JCheckBox checkBox = new JCheckBox(name);
		checkBoxes.add(checkBox);
		add(checkBox);
	}

	public static void clearSelected() { //Good
		for (int i = checkBoxes.size() - 1; i >= 0; i--) {
			if (checkBoxes.get(i).isSelected()) {
				checkListPannel.remove(checkBoxes.get(i));
				checkBoxes.remove(checkBoxes.get(i));
			}
		}
		saveCheckBoxes();
		frame.repaint();
		frame.setVisible(true);
	}
	
	public static void removeCheckBoxes() { //Good
		for (JCheckBox checkBox : checkBoxes) {
			checkListPannel.remove(checkBox);
		}
		checkBoxes = new ArrayList<JCheckBox>();
		try {
			clearTheFile(checkListFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		checkListPannel.repaint();
		frame.repaint();
	}
	
	public static void clearTheFile(File file) throws IOException { //Good
		FileWriter fwOb = new FileWriter(file); 
		PrintWriter pwOb = new PrintWriter(fwOb, false);
		pwOb.flush();
		pwOb.close();
		fwOb.close();
	}
	
	public static void saveCheckBoxes() { //Good
		try {
			clearTheFile(checkListFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] name = new String[checkBoxes.size()];
		for (int i = 0; i < checkBoxes.size(); i++) {
			name[i] = checkBoxes.get(i).getText();
		}
		writeData(name, checkListFile);
	}
	
	public static void loadCheckBoxes() { //Good
		String[] data = readData(checkListFile);
		for (String s : data) {
			JCheckBox checkBox = new JCheckBox(s);
			add(checkBox);
			checkBoxes.add(checkBox);
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
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