package com.productivity.Panels;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.productivity.CheckBoxes;
import com.productivity.Productivity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import net.miginfocom.swing.MigLayout;

public class DailyChecklist extends JPanel {
	
	private static File mNameFile = Productivity.getSave("Saves/daily.TXT");
	private static File mStateFile = Productivity.getSave("Saves/dailyCheck.TXT");
	private static File mColorFile = Productivity.getSave("Saves/dailyColor.TXT");
	private static File mTimeFile = Productivity.getSave("Saves/time.TXT");
	private static JPanel mChecklistPanel = new JPanel(new MigLayout("gap 0px 0px, ins 0, flowy")); //new GridLayout(Productivity.kHeight/30, Productivity.kWidth/200));
	private static ArrayList<JCheckBox> mCheckBoxes = new ArrayList<JCheckBox>();
	
	public DailyChecklist() {
		super.setLayout(new MigLayout("gap 5px 5px, ins 5" + ((Productivity.kMigDebug)?",debug":"")));
		super.add(mChecklistPanel, "wmax "+ Productivity.kWidth +", grow, push, span");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
				LocalDateTime now = LocalDateTime.now();
				try {
					writeData(dtf.format(now), mTimeFile);
				} catch (Exception e) {
					e.printStackTrace();
					writeData("11/11/2020", mTimeFile);
				}
				resetBoxes(!dtf.format(now).equals(readData(mTimeFile)[0]));
			}
		}
		);
	}

	public static JCheckBox[] getCheckBoxes() {
		JCheckBox[] data = new JCheckBox[mCheckBoxes.size()];
		data = mCheckBoxes.toArray(data);
		return data;
	}
	
	public static void setCheckBoxes(boolean state, int index) {
		mCheckBoxes.get(index).setSelected(state);
		saveCheckBoxes();
	}
	
	public static void resetBoxes(boolean reset) {
		for (int i = 0; i < mCheckBoxes.size(); i++) {
			mChecklistPanel.remove(mCheckBoxes.get(i));
		}
		try {
			String[] names = readData(mNameFile);
			String[] checked = readData(mStateFile);
			String[] color = readData(mColorFile);
			if (!(names.length == checked.length && checked.length == color.length)) {
				writeData("", mNameFile);
				writeData("", mStateFile);
				writeData("", mColorFile);
				return;
			}
			mCheckBoxes = new ArrayList<JCheckBox>();
			for (int i = 0; i < names.length; i++) {
				if (!reset) {
					addCheckBox(names[i], new Color(Integer.parseInt(color[i])), Boolean.parseBoolean(checked[i]), i);
				}
				else {
					addCheckBox(names[i], new Color(Integer.parseInt(color[i])), false, i);
				}
			}
			HomePanel.getInstance().reset();
		} catch (Exception e) {
			e.printStackTrace();
			writeData("", mNameFile);
			writeData("", mStateFile);
			writeData("", mColorFile);
		}
	}
	
	private static void addCheckBox(String name, Color color, Boolean checked, int index) {
		JCheckBox checkBox = new JCheckBox(name);
		checkBox.setFocusPainted(false);
		checkBox.addActionListener(e -> {
			if (checkBox.isSelected())
				Productivity.showConfetti();
			saveCheckBoxes();
			SettingsPanel.setDailySelected(checkBox.isSelected(), index);
		});
		checkBox.setForeground(color);
		checkBox.setSelected(checked);
		mCheckBoxes.add(checkBox);
		int rows = (int)(mChecklistPanel.getHeight() / (checkBox.getPreferredSize().getHeight()+5));
		if (rows <= 0) rows = 1;
		mChecklistPanel.add(checkBox, "width "+ (int)(Productivity.kWidth/CheckBoxes.kColumns) +", wmax " + (int)(Productivity.kWidth/CheckBoxes.kColumns) + (((mChecklistPanel.getComponentCount()+1) % rows == 0)?", wrap":""));
		Productivity.getInstance().repaintFrame();
	}
	
	private static void saveCheckBoxes() {
		String[] state = new String[mCheckBoxes.size()];
		for (int i = 0; i < mCheckBoxes.size(); i++) {
			state[i] = Boolean.toString(mCheckBoxes.get(i).isSelected());
		}
		try {
			writeData(state, mStateFile);
		} catch (Exception e) {
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
}
