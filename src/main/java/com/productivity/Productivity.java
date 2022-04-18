package com.productivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.productivity.Custom.AddCustomCheckList;
import com.productivity.Custom.CustomCheckList;

public class Productivity extends JFrame {
	
	private static final JFrame kFrame = new JFrame("Productivity");
	public static final int kLength = 400;
	public static final int kHeight = 300;
	private static final Boolean kDebug = true;
	private static final String kDebugPath = "src\\main\\java\\com\\productivity\\";
	private static final String kJarPath = "classes\\com\\productivity\\";
	private static final String kExePath = "target\\classes\\com\\productivity\\";
	private static final String kCustomDebugPath = "src\\main\\java\\com\\productivity\\Custom\\Saves\\";
	private static final String kCustomJarPath = "classes\\com\\productivity\\Custom\\Saves\\";
	private static final String kCustomExePath = "target\\classes\\com\\productivity\\Custom\\Saves\\";
	
	private static final JTabbedPane mTabbedPane = new JTabbedPane();
	private static final CustomCheckList mCustomCheckList = CustomCheckList.getInstance();
	
	private static String mCurrentPath;
	private static String mCurrentCustomPath;
	private static File mNameFile;
	private static File mStateFile;
	private static File mColorFile;
	private static boolean mUsingWindows;
	private static CheckBoxes mCheckBoxes;
	
	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mCurrentPath = (kDebug)?kDebugPath: (args.length>0)?kExePath:kJarPath;
				mCurrentCustomPath = (kDebug)?kCustomDebugPath: (args.length>0)?kCustomExePath:kCustomJarPath;
				createAndShowGUI();
			}
		});
	}
	
	private static void createAndShowGUI() {
		mNameFile = new File(mCurrentPath+"Saves\\list.TXT");
		mStateFile = new File(mCurrentPath+"Saves\\listCheck.TXT");
		mColorFile = new File(mCurrentPath+"Saves\\listColor.TXT");
		String os = System.getProperty("os.name");
		if (os.contains("Windows")) mUsingWindows = true;
		else mUsingWindows = false;
		start();
		if (mUsingWindows) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					BlockSites.unBlockSites();
				}
			}, 
			"Shutdown-thread"
			));
		}
	}
	
	private static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mTabbedPane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SettingsPanel.loadSettings();
		AddCustomCheckList.loadCheckLists();
		mTabbedPane.setFocusable(false);
		mCheckBoxes = new CheckBoxes(kHeight, kLength, mNameFile, mStateFile, mColorFile, false, true);
		mTabbedPane.addTab("Checklist", mCheckBoxes);
		mTabbedPane.addTab("Daily", new DailyChecklist());
		mTabbedPane.addTab("Timers", new TimerPanel());
		mTabbedPane.addTab("Notes", new NotesPanel());
		if (AddCustomCheckList.getNumberOfChecklists() > 0) {
			mTabbedPane.addTab("Custom", mCustomCheckList);
		}
		mTabbedPane.addTab("Settings", new SettingsPanel());
		mTabbedPane.insertTab("Home", null, HomePanel.getInstance(), null, 0);
		ImageIcon img = new ImageIcon("src\\main\\java\\com\\productivity\\icon.png");
		kFrame.setIconImage(img.getImage());
		kFrame.add(mTabbedPane);
		kFrame.setAlwaysOnTop(Boolean.parseBoolean(SettingsPanel.getSetting("onTop")));
		kFrame.setLocationByPlatform(true);
		kFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		kFrame.setSize(kLength, kHeight);
		kFrame.setVisible(true);
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
	
	public static void customCheckListVisibility(boolean value) {
		if (value && mTabbedPane.indexOfComponent(mCustomCheckList) == -1) {
			mTabbedPane.insertTab("Custom", null, mCustomCheckList, null, mTabbedPane.getTabCount()-2);
			repaintFrame();
		}
		else if (mTabbedPane.indexOfComponent(mCustomCheckList) != -1) {
			mTabbedPane.remove(mCustomCheckList);
			repaintFrame();
		}
	}
	
	public static void runOnStartup(Boolean value) {
		String path = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\productivity.bat";
		
		if (value) {
			File startupFile = new File(path);
			try {
				if (!startupFile.exists()) {
					startupFile.createNewFile();
				}
				String currentDir = System.getProperty("user.dir");
				String data = "start " + currentDir + "\\Productivity.exe";
				writeData(data, startupFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			File startupFile = new File(path);
			try {
				if (startupFile.exists()) {
					startupFile.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void setOnTop(boolean top) {
		kFrame.setAlwaysOnTop(top);
	}
	
	public static void repaintFrame() {
		kFrame.repaint();
	}
	
	public static String getCurrentPath() {
		return mCurrentPath;
	}
	
	public static String getCurrentCustomPath() {
		return mCurrentCustomPath;
	}
	
	public static Boolean getUsingWindows() {
		return mUsingWindows;
	}
	
	public static JCheckBox[] getBoxes() {
		return mCheckBoxes.getBoxes();
	}
	
	public static void setSelected(boolean state, int index) {
		mCheckBoxes.setSelected(state, index);
	}
}