package com.productivity.Panels;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.productivity.Productivity;
import com.productivity.Util.JTextFieldLimit;
import com.productivity.Util.Notification;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

public class NotificationPanel extends JPanel {
    
    String[] kRepeatOptions = {"None", "Hour(s)", "Day(s)", "Week(s)", "Month(s)", "Year(s)"};
    ArrayList<Notification> mNotifications = new ArrayList<Notification>();
    File mNotificationFile = Productivity.getSave("Saves/notification.TXT");
    
    public NotificationPanel() {
        JButton addBtn = new JButton("Add");
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> notificationPopup());
        
        super.setLayout(new MigLayout((Productivity.kMigDebug)?"debug":""));
        super.add(addBtn, "dock south, spanx, grow, push");
        loadNotifications();
    }
    
    private void notificationPopup() {
        JDialog infoBox = new JDialog(Productivity.getInstance(), "Create Notification");
        
        JLabel nameLbl = new JLabel("Name:");
        JTextField name = new JTextField();
        
        JLabel startTimeLbl = new JLabel("Start time (no time will mean now):");
        
        JLabel dateLbl = new JLabel("Date (no date will mean today):");
        
        JLabel repeatLbl = new JLabel("Repeat:");
        JLabel repeatInfo = new JLabel("Every:");
        JTextField repeatAmount = new JTextField(3);
        repeatAmount.setDocument(new JTextFieldLimit(3));
        JComboBox<String> repeat = new JComboBox<String>(kRepeatOptions);
        
        JButton confirm = new JButton("confirm");
        confirm.addActionListener(e -> {
            if (name.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Name field can not be blank", "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int amount = repeatAmount.getText().equals("") ? 0 : Integer.parseInt(repeatAmount.getText());
            newNotification(name.getText(), repeat.getSelectedIndex(), amount);
            infoBox.dispose();
        });
        
        infoBox.setLayout(new MigLayout((Productivity.kMigDebug)?"debug":""));
        infoBox.add(nameLbl, "wrap");
        infoBox.add(name, "wrap");
        infoBox.add(repeatLbl, "wrap");
        infoBox.add(repeatInfo, "split 3");
        infoBox.add(repeatAmount, "");
        infoBox.add(repeat, "wrap");
        infoBox.add(confirm, "dock south, spanx, grow, push");
        infoBox.setSize(300, 200);
        infoBox.setVisible(true);
    }

    private void showNotification(String name, long lastTime, long nextTime) {
        JProgressBar progressBar = new JProgressBar(0, (int)(nextTime-lastTime));
        JButton delete = new JButton("Remove");
        JLabel info = new JLabel(name);

        JPanel panel = new JPanel(new MigLayout());
        panel.add(info);
        panel.add(progressBar);
        panel.add(delete);

        super.add(panel);
        Productivity.getInstance().repaint();
    }
    
    private void newNotification(String name, int repeat, int amount) {
        Notification notification = new Notification(name, repeat, amount);
        mNotifications.add(notification);

        String[] data = new String[mNotifications.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = mNotifications.get(i).mName + "," + Integer.toString(mNotifications.get(i).mRepeat) + "," + Integer.toString(mNotifications.get(i).mAmount);
        }
        writeData(data, mNotificationFile);

        notification.setLastDateTime(LocalDateTime.now());
        long lastTime = notification.getLastTime();
        long nextTime = notification.getNextTime();
        showNotification(name, lastTime, nextTime);
    }

    private void loadNotifications() {
        String[] data = readData(mNotificationFile);

        for (int i = 0; i < data.length; i++) {
            String[] values = data[i].split(",");
            Notification notification = new Notification(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]));
            mNotifications.add(notification);

            notification.setLastDateTime(LocalDateTime.now());
            long lastTime = notification.getLastTime();
            long nextTime = notification.getNextTime();
            showNotification(values[0], lastTime, nextTime);
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
    
    private static class Notification {
        
        public String mName;
        public int mRepeat;
        public int mAmount;
        
        private LocalDateTime mLastDateTime;
        
        public Notification(String name, int repeat, int amount) {
            mName = name;
            mRepeat = repeat;
            mAmount = amount;
        }
        
        public void setLastDateTime(LocalDateTime time) {
            mLastDateTime = time;
        }

        public long getLastTime() {
            long s = mLastDateTime.getSecond();
            long m = mLastDateTime.getMinute();
            long h = mLastDateTime.getHour();
            long d = mLastDateTime.getDayOfYear();
            long y = mLastDateTime.getYear();
            return s + (m*60) + (h*60*60) + (d*24*60*60) + (y*365*24*60*60);
        }
        
        public long getNextTime() {
            LocalDateTime nextDateTime = mLastDateTime;
            switch (mRepeat) {
                case 0: // None
                break;
                case 1: // Hours
                nextDateTime.plusHours(mAmount);
                break;
                case 2: // Days
                nextDateTime.plusDays(mAmount);
                break;
                case 3: // Weeks
                nextDateTime.plusWeeks(mAmount);
                break;
                case 4: // Months
                nextDateTime.plusMonths(mAmount);
                break;
                case 5: // Years
                nextDateTime.plusYears(mAmount);
                break;
                default:
                break;
            }
            long s = nextDateTime.getSecond();
            long m = nextDateTime.getMinute();
            long h = nextDateTime.getHour();
            long d = nextDateTime.getDayOfYear();
            long y = nextDateTime.getYear();
            return s + (m*60) + (h*60*60) + (d*24*60*60) + (y*365*24*60*60);
        }
    }
}
