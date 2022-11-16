package com.productivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;

import com.productivity.Custom.AddCustomCheckList;
import com.productivity.Custom.CustomCheckList;
import com.productivity.Panels.HomePanel;
import com.productivity.Panels.NotificationPanel;
import com.productivity.Panels.SettingsPanel;
import com.productivity.Panels.TimerPanel;
import com.productivity.Util.ComponentMover;
import com.productivity.Util.CustomTabbedUI;

import net.miginfocom.swing.MigLayout;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkContrastIJTheme;

public class Productivity extends JFrame {
	
	public static final int kWidth = 400; // 400
	public static final int kHeight = 300; // 300
	public static final int kTabHeight = 30;
	public static final boolean kMigDebug = false;
	public static String kPath = "";
	
	private static final JTabbedPane mTabbedPane = new JTabbedPane();
	private static final JLayeredPane mLayeredPane = new JLayeredPane();
	private static CustomCheckList mCustomCheckList;
	private static SettingsPanel mSettingsPanel;
	private static HomePanel mHomePanel;
	
	private static File mNameFile = getSave("Saves/list.TXT");
	private static File mStateFile = getSave("Saves/listCheck.TXT");;
	private static File mColorFile = getSave("Saves/listColor.TXT");;
	private static CheckBoxes mCheckBoxes;
	private static TimerPanel mTimerPanel;

	private static JLabel[] mConfetti = new JLabel[2];
	private static JLabel mCurrentConfetti;
	private static final double kConfettiTime = 1;

	private static Productivity mInstance = null;
    public synchronized static Productivity getInstance() {
        if (mInstance == null) {
            mInstance = new Productivity();
        }
        return mInstance;
    }
	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Productivity.getInstance().createAndShowGUI();
				ChangeListener changeListener = new ChangeListener() {
					public void stateChanged(ChangeEvent changeEvent) {
					  JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
					  int index = sourceTabbedPane.getSelectedIndex();
					  if (index == 0) {
						mHomePanel.reset(false, 0);
						mHomePanel.reset(true, Integer.parseInt(readData(getSave("Saves/time.TXT"))[1]));
					  }
					}
				  };
				mTabbedPane.addChangeListener(changeListener);
			}
		});
	}
	
	private void createAndShowGUI() {
		UIManager.put("TabbedPane.selected", Color.BLACK);
		UIManager.put("TabbedPane.focus", UIManager.getColor("TabbedPane.selected"));
		mConfetti[0] = new JLabel(new ImageIcon(getImage("Images/high.gif")));
		mConfetti[0].setBounds(0, 0, kWidth, kHeight);
		mConfetti[1] = new JLabel(new ImageIcon(getImage("Images/low.gif")));
		mConfetti[1].setBounds(0, 0, kWidth, kHeight);
		mTabbedPane.setBounds(0, 0, kWidth, kHeight);
		start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				BlockSites.unBlockSites();
			}
		}, 
		"Shutdown-thread"
		));
	}
	
	private void start() {
		super.setUndecorated(true);
		SettingsPanel.loadSettings();
		try {
			try {
				UIManager.setLookAndFeel(getLaf());
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Failed to load theme using fallback", "Warning", JOptionPane.ERROR_MESSAGE);
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			SwingUtilities.updateComponentTreeUI(mTabbedPane);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Failed to load fallback using default", "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		setConfetti(Integer.parseInt(SettingsPanel.getSetting("currentConfetti")));
		mCustomCheckList = CustomCheckList.getInstance();
		mSettingsPanel = new SettingsPanel();
		mCheckBoxes = new CheckBoxes(mNameFile, mStateFile, mColorFile, false);
		mTimerPanel = new TimerPanel();
		mHomePanel = HomePanel.getInstance();
		mTabbedPane.setUI(new CustomTabbedUI(UIManager.getColor("Panel.background")));
		mTabbedPane.addTab("Checklist", mCheckBoxes);
		mTabbedPane.addTab("Timers", mTimerPanel);
		mTabbedPane.addTab("Notification", new NotificationPanel());
		if (AddCustomCheckList.getNumberOfChecklists() > 0) {
			mTabbedPane.addTab("Custom", mCustomCheckList);
		}
		mTabbedPane.addTab("Settings", mSettingsPanel);
		mTabbedPane.insertTab("Home", null, mHomePanel, null, 0);

		JButton close = new JButton("X");
		close.setFocusPainted(false);
		JButton minimize = new JButton("-");
		minimize.setFocusPainted(false);

		close.addActionListener(e -> close());
		minimize.addActionListener(e -> setState(Frame.ICONIFIED));
		JPanel taskBar = new JPanel(new MigLayout((kMigDebug?"debug, ":"")+"gap 0px 0px, ins 0"));
		taskBar.add(close); taskBar.add(minimize);
		int index = mTabbedPane.getTabCount();
		mTabbedPane.insertTab("", null, null, "", index);
        mTabbedPane.setTabComponentAt(index, taskBar);

		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		String xLast = "xPos";
		String yLast = "yPos";
		int xPos = prefs.getInt(xLast, 0);
		int yPos = prefs.getInt(yLast, 0);

		GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		if (localGraphicsEnvironment.getScreenDevices().length != Integer.parseInt(readData(getSave("Saves/time.TXT"))[2])) {
			xPos = 10;
			yPos = 10;
			prefs.putInt(xLast, xPos);
			prefs.putInt(yLast, yPos);
			String[] fileContents = readData(getSave("Saves/time.TXT"));
			writeData(new String[]{fileContents[0], fileContents[1], Integer.toString(localGraphicsEnvironment.getScreenDevices().length)}, getSave("Saves/time.TXT"));
		}

		super.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				prefs.putInt(xLast, Productivity.super.getX());
				prefs.putInt(yLast, Productivity.super.getY());
			}
		});
		
		ImageIcon img = new ImageIcon(getImage("Images/icon.png"));

		mLayeredPane.add(mTabbedPane, 0);

		ComponentMover cm = new ComponentMover(this, mTabbedPane);
		cm.setChangeCursor(true);

		super.setTitle("Productivity");
		super.setIconImage(img.getImage());
		super.add(mLayeredPane);
		super.setAlwaysOnTop(Boolean.parseBoolean(SettingsPanel.getSetting("onTop")));
		super.setLocationByPlatform(true);
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.setSize(kWidth, kHeight);
		super.setLocation(xPos, yPos);
		super.setResizable(false);
		super.setVisible(true);
	}

	public void updateLaf() {
		try {
			UIManager.setLookAndFeel(getLaf());
		} catch (UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(this, "Failed updating look and feel", "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);
		mTabbedPane.setUI(new CustomTabbedUI(UIManager.getColor("Panel.background")));
		mCustomCheckList.updateLaf();
		if (mSettingsPanel != null) {
			mSettingsPanel.updateLaf();
		}
	}

	private LookAndFeel getLaf() {
		// UIManager.setLookAndFeel(new FlatDarculaLaf());
		// UIManager.setLookAndFeel(new FlatMaterialOceanicContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatMaterialDeepOceanContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatCarbonIJTheme());
		// UIManager.setLookAndFeel(new FlatGruvboxDarkHardIJTheme());				
		// UIManager.setLookAndFeel(new FlatMaterialDesignDarkIJTheme());				
		// UIManager.setLookAndFeel(new FlatMonokaiProContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatOneDarkIJTheme());
		// UIManager.setLookAndFeel(new FlatArcDarkContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatAtomOneDarkContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatDraculaContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatGitHubDarkContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatMaterialDarkerContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatMaterialPalenightContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatMoonlightContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatNightOwlContrastIJTheme());
		// UIManager.setLookAndFeel(new FlatSolarizedDarkContrastIJTheme());
		switch (Integer.parseInt(SettingsPanel.getSetting("laf"))) {
			case 0:
			return new FlatDarculaLaf();
			case 1:
			return new FlatMaterialOceanicContrastIJTheme();
			case 2:
			return new FlatMaterialDeepOceanContrastIJTheme();
			case 3:
			return new FlatCarbonIJTheme();
			case 4:
			return new FlatGruvboxDarkHardIJTheme();
			case 5:
			return new FlatMaterialDesignDarkIJTheme();
			case 6:
			return new FlatMonokaiProContrastIJTheme();
			case 7:
			return new FlatOneDarkIJTheme();
			case 8:
			return new FlatArcDarkContrastIJTheme();
			case 9:
			return new FlatAtomOneDarkContrastIJTheme();
			case 10:
			return new FlatDraculaContrastIJTheme();
			case 11:
			return new FlatGitHubDarkContrastIJTheme();
			case 12:
			return new FlatMaterialDarkerContrastIJTheme();
			case 13:
			return new FlatMaterialPalenightContrastIJTheme();
			case 14:
			return new FlatMoonlightContrastIJTheme();
			case 15:
			return new FlatNightOwlContrastIJTheme();
			case 16:
			return new FlatSolarizedDarkContrastIJTheme();
		}
		return new FlatDarkLaf();
	}

	private void close() {
		String ObjButtons[] = {"Yes","No"};
		int PromptResult = JOptionPane.showOptionDialog(Productivity.this, 
			"Are you sure you want to exit?", "Close", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, 
			ObjButtons,ObjButtons[1]);
		if (PromptResult == 0) {
			System.exit(0);          
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
			JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed reading data in Productivity", "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return result;
	}
	
	private void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Failed writing data in Productivity", "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void writeData(String[] dataArr, File file) {
        String data = String.join("\n", dataArr);
		writeData(data, file);
	}
	
	public void customCheckListVisibility(boolean value) {
		if (value && mTabbedPane.indexOfComponent(mCustomCheckList) == -1) {
			mTabbedPane.insertTab("Custom", null, mCustomCheckList, null, mTabbedPane.getTabCount()-2);
		}
		else if (mTabbedPane.indexOfComponent(mCustomCheckList) != -1) {
			mTabbedPane.remove(mCustomCheckList);
		}
	}
	
	public void runOnStartup(boolean value) {
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
				JOptionPane.showMessageDialog(this, "Failed saving .bat to startup folder", "Warning", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(this, "Failed deleting .bat from startup folder", "Warning", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	public void setOnTop(boolean top) {
		super.setAlwaysOnTop(top);
	}
	
	public void repaintFrame() {
		super.repaint();
	}
	
	public JCheckBox[] getBoxes() {
		if (mCheckBoxes == null) return null;
		return mCheckBoxes.getBoxes();
	}
	
	public void setSelected(boolean state, int index) {
		mCheckBoxes.setSelected(state, index);
	}

	public void setAllowBlock(boolean state) {
		mTimerPanel.setAllowBlock(state);
	}

	public void setConfetti(int index) {
		mCurrentConfetti = mConfetti[index];
	}

	static Timer time;
	static TimerTask task;
	static final int kScale = 10;
	public static void showConfetti() {
		if (!Boolean.parseBoolean(SettingsPanel.getSetting("wantConfetti"))) return;
		if (time != null) {
			time.cancel();
			time.purge();
		}
		if (task != null) task.cancel();
		FadeLabel label = new FadeLabel();
		label.setBounds(0, 0, kWidth, kHeight);
		label.add(mCurrentConfetti);
		time = new Timer();
        task = new TimerTask()
        {
            int i = 0;
			Productivity productivity = Productivity.getInstance();
            @Override
            public void run()
            {
				if (i == 0) {
					mLayeredPane.add(label, 0);
				}
				if (i >= kConfettiTime*kScale) {
					mLayeredPane.remove(label);
					productivity.repaintFrame();
					task.cancel();
					time.cancel();
					time.purge();
				}
				if (i <= kConfettiTime*kScale/2)
					label.setAlpha((float)(i/(kConfettiTime*kScale)));
				else
					label.setAlpha((float)(kConfettiTime-i/(kConfettiTime*kScale)));
				i++;
            }
        };
        time.schedule(task, 0, 1000/kScale);
	}

	public static Image getImage(final String path) {
		final URL url = Thread.currentThread().getContextClassLoader().getResource(path);
		return Toolkit.getDefaultToolkit().getImage(url);
	}

	public static File getSave(final String path) {
		URL url = Productivity.class.getClassLoader().getResource(path);
    	String file = url.getPath();
		String jarName = "";
		try {
			jarName = Productivity.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			jarName = jarName.substring(jarName.lastIndexOf("/") + 1) + "!";
		} catch (URISyntaxException e) {
			JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed getting file:"+path, "Warning", JOptionPane.ERROR_MESSAGE);
			jarName = "Productivity-4.0.0.jar!";
		}
		file = file.split("/", 2)[1].replaceFirst(jarName, "classes");
		kPath = file.substring(0, file.lastIndexOf("/"));
		kPath = kPath.substring(0, kPath.lastIndexOf("/"));
		kPath += "/Custom/";
		return new File(file);
	}

	public SettingsPanel getSettingsPanel() {
		return mSettingsPanel;
	}

	public static class FadeLabel extends JLabel {

        private float alpha;
        private BufferedImage background;

        public FadeLabel() {
            setAlpha(1f);
        }

        public void setAlpha(float value) {
            if (alpha != value) {
                float old = alpha;
                alpha = value;
                firePropertyChange("alpha", old, alpha);
            }
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        public Dimension getPreferredSize() {
            return background == null ? super.getPreferredSize() : new Dimension(background.getWidth(), background.getHeight());
        }

        @Override
        public void paint(Graphics g) {
            // This is one of the few times I would directly override paint
            // This makes sure that the entire paint chain is now using
            // the alpha composite, including borders and child components
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
            super.paint(g2d);
            g2d.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            // This is one of the few times that doing this before the super call
            // will work...
            if (background != null) {
                int x = (getWidth() - background.getWidth()) / 2;
                int y = (getHeight() - background.getHeight()) / 2;
                g.drawImage(background, x, y, this);
            }
            super.paintComponent(g);
        }
    }
}