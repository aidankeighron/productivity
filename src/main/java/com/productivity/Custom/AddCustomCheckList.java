package com.productivity.Custom;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.productivity.CheckBoxes;
import com.productivity.Productivity;
import com.productivity.Util.JTextFieldLimit;

import net.miginfocom.swing.MigLayout;

public class AddCustomCheckList extends JPanel {
    
    private static final File kCustomNames = Productivity.getSave("Custom/customNames.TXT");
    private static final int kCharLimit = 10;
    private static final int kMaxCustomCheckLists = 8;
    
    private static ArrayList<String> mNames = new ArrayList<String>();
    private static HashMap<String, CheckBoxes> mCheckBoxes = new HashMap<String, CheckBoxes>();
    private static int mCurrentNumCheckLists = 0;
    private static Productivity mProductivity = Productivity.getInstance();
    private static CustomCheckList mCustomCheckList = CustomCheckList.getInstance();
    private static JPanel mCustomPanel = new JPanel(new MigLayout("flowy, gap 5px 5px, ins 5" + (Productivity.kMigDebug?", debug":"")));
    
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
                addCheckList(name.getText());
                mCurrentNumCheckLists++;
                if (getNumberOfChecklists() == 1) {
                    mProductivity.customCheckListVisibility(true);
                }
                name.setText("");
            }
            else {
                JOptionPane.showMessageDialog(this, "Please enter valid name");
            }
        });
        
        JLabel nameLbl = new JLabel("Name Of Custom Checklist:");
        super.setLayout(new MigLayout((Productivity.kMigDebug?"debug":"")));
        super.add(nameLbl, "wrap");
        super.add(name, "split 2, growx, wrap");
        super.add(mCustomPanel, "grow, push, span");

        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadCheckLists();
			}
		}
		);
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
        return mCheckBoxes.size();
    }
    
    public static void loadCheckLists() {
        try {
            String[] data = readData(kCustomNames);
            for (int i = 0; i < data.length; i++) {
                mNames.add(data[i]);
                addCheckList(data[i]);
                mCurrentNumCheckLists++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            File dir = new File(Productivity.kPath);
            purgeDirectory(dir);
            try {
                kCustomNames.createNewFile();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    public static void setCheckList(boolean state, int index, String name) {
        mCheckBoxes.get(name).setSelected(state, index);
    }
    
    private static void addCheckList(String n) {
        File name = new File(Productivity.kPath + n + "Name.TXT");
        File color = new File(Productivity.kPath + n + "Color.TXT");
        File check = new File(Productivity.kPath + n + "Check.TXT");
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
            mCustomPanel.remove(button);
            deleteChecklist(n);
            saveChecklists(false);
        });
        int rows = (int)(mCustomPanel.getHeight() / (button.getPreferredSize().getHeight()+5));
        if (rows <= 0) rows = 1;
        mCustomPanel.add(button, (((mCustomPanel.getComponentCount()+1) % rows == 0)?"wrap":""));
        CheckBoxes checkBox = new CheckBoxes(name, check, color);
        mCheckBoxes.put(n, checkBox);
        saveChecklists(true);
        mCustomCheckList.addCheckList(checkBox, n);
    }
    
    private static void deleteChecklist(String n) {
        File name = new File(Productivity.kPath + n + "Name.TXT");
        File color = new File(Productivity.kPath + n + "Color.TXT");
        File check = new File(Productivity.kPath + n + "Check.TXT");
        name.delete();
        color.delete();
        check.delete();
        mNames.remove(n);
        mCheckBoxes.remove(n);
        mCustomCheckList.removeChecklist(n);
        if (mNames.size() <= 0) {
            mProductivity.customCheckListVisibility(false);
        }
        mCurrentNumCheckLists--;
    }
    
    private static void saveChecklists(boolean append) {
        if (!append) {
            String[] data = new String[mNames.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = mNames.get(i);
            }
            writeData(data, kCustomNames);
        }
        else {
            appendChecklists(mNames.get(mNames.size()-1), kCustomNames);
        }
    }

    private static void appendChecklists(String data, File file) {
        try  {
            FileWriter writer = new FileWriter(file, true);
            writer.write(data+"\n");
            writer.close();
        }
        catch (Exception e) {
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
        String data = String.join("\n", dataArr);
        writeData(data, file);
    }
}
