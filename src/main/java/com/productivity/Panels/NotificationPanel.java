package com.productivity.Panels;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;
import com.productivity.Productivity;
import com.productivity.Util.JTextFieldLimit;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NotificationPanel extends JPanel {
    
    private String[] kRepeatOptions = {"None", "Hour(s)", "Day(s)", "Week(s)", "Month(s)", "Year(s)"};
    private ArrayList<Notification> mNotifications = new ArrayList<Notification>();
    private File mNotificationFile = Productivity.getSave("Saves/notification.TXT");

    private static DatePickerSettings mDatePickerSettings = new DatePickerSettings();
    static {
        mDatePickerSettings.setColor(DateArea.TextFieldBackgroundValidDate, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.BackgroundClearLabel, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.BackgroundMonthAndYearMenuLabels, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.BackgroundTodayLabel, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.CalendarBackgroundNormalDates, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.BackgroundOverallCalendarPanel, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.BackgroundCalendarPanelLabelsOnHover, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.BackgroundTopLeftLabelAboveWeekNumbers, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.TextFieldBackgroundInvalidDate, UIManager.getColor("TextField.background"));
        mDatePickerSettings.setColor(DateArea.CalendarBorderSelectedDate, Color.black);
        
        mDatePickerSettings.setColor(DateArea.CalendarBackgroundSelectedDate, UIManager.getColor("textText"));
        mDatePickerSettings.setColor(DateArea.DatePickerTextValidDate, UIManager.getColor("textText"));
        mDatePickerSettings.setColor(DateArea.CalendarTextWeekdays, Color.black);
        mDatePickerSettings.setColor(DateArea.CalendarTextNormalDates, UIManager.getColor("textText"));
    }
    
    public NotificationPanel() {
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> notificationPopup());
        
        super.setLayout(new MigLayout((Productivity.kMigDebug)?"debug":""));
        super.add(addBtn, "dock south, spanx, grow, push");
        loadNotifications();
    }
    
    private void notificationPopup() {
        JDialog infoBox = new JDialog(Productivity.getInstance(), "Create Notification");
        
        JLabel nameLbl = new JLabel("Name:");
        JTextField name = new JTextField();

        JLabel textLbl = new JLabel("Description");
        JTextArea text = new JTextArea();
        
        JLabel timeLbl = new JLabel("Start time (no time will mean now):");
        TimePicker timePicker = new TimePicker();

        JLabel dateLbl = new JLabel("Date (no date will mean today):");
        DatePicker datePicker = new DatePicker(mDatePickerSettings);
        
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
            newNotification(name.getText(), text.getText(), repeat.getSelectedIndex(), amount, datePicker.getDate(), timePicker.getTime());
            infoBox.dispose();
        });
        
        infoBox.setLayout(new MigLayout((Productivity.kMigDebug)?"debug":""));
        infoBox.add(nameLbl, "wrap");
        infoBox.add(name, "wrap");
        infoBox.add(textLbl, "wrap");
        infoBox.add(text, "wrap");
        infoBox.add(timeLbl, "wrap");
        infoBox.add(timePicker, "wrap");
        infoBox.add(dateLbl, "wrap");
        infoBox.add(datePicker, "wrap");
        infoBox.add(repeatLbl, "wrap");
        infoBox.add(repeatInfo, "split 3");
        infoBox.add(repeatAmount, "");
        infoBox.add(repeat, "wrap");
        infoBox.add(confirm, "dock south, spanx, grow, push");
        infoBox.setSize(230, 300);
        infoBox.setVisible(true);
    }

    private void showNotification(String name, String message, Notification notification) {
        long length = notification.getNextDate() - notification.getStartDate();
        JProgressBar progressBar = new JProgressBar(0, (int)length);
        JButton delete = new JButton("Remove");
        JLabel info = new JLabel(name);
        
        JPanel panel = new JPanel(new MigLayout());
        panel.add(info);
        panel.add(progressBar);
        panel.add(delete);

        Timer time = new Timer();
        TimerTask task = new TimerTask()
        {
            long seconds = length;
            int i = 0;
            @Override
            public void run()
            {
                if (i <= seconds) {
                    try {
                        com.productivity.Util.Notification.displayTray(name, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i = 0;
                }
                i++;
                progressBar.setValue(i);
            }
        };
        time.schedule(task, 0, 1000);

        delete.addActionListener(e -> {
            super.remove(panel);
            mNotifications.remove(notification);
            task.cancel();
        });
        super.add(panel);
        Productivity.getInstance().repaint();
    }
    
    private void newNotification(String name, String text, int repeat, int amount, LocalDate date, LocalTime time) {
        LocalDateTime startDate = LocalDateTime.of(date, time);
        Notification notification = new Notification(name, repeat, amount, startDate);
        mNotifications.add(notification);

        String[] data = new String[mNotifications.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = mNotifications.get(i).mName+","+text+","+Integer.toString(mNotifications.get(i).mRepeat)+","+Integer.toString(mNotifications.get(i).mAmount)+","+Long.toString(mNotifications.get(i).getStartDate());
        }
        writeData(data, mNotificationFile);

        showNotification(name, text, notification);
    }

    private void loadNotifications() {
        String[] data = readData(mNotificationFile);

        for (int i = 0; i < data.length; i++) {
            String[] values = data[i].split(",");
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(values[4])), TimeZone.getDefault().toZoneId());
            Notification notification = new Notification(values[0], Integer.parseInt(values[2]), Integer.parseInt(values[3]), date);
            mNotifications.add(notification);

            showNotification(values[0], values[1], notification);
        }
    }

    public static long convertDateToLong(LocalDateTime date) {
        boolean leapYear = date.toLocalDate().isLeapYear();
        long s = date.getSecond();
        long m = date.getMinute();
        long h = date.getHour();
        long d = date.getDayOfYear();
        long y = date.getYear();
        return s + (m*60) + (h*60*60) + (d*24*60*60) + (y*(leapYear?366:365)*24*60*60);
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
        private LocalDateTime mStartDate;
        
        public Notification(String name, int repeat, int amount, LocalDateTime startDate) {
            mName = name;
            mRepeat = repeat;
            mAmount = amount;
            mStartDate = startDate;
        }

        public long getStartDate() {
            return NotificationPanel.convertDateToLong(mStartDate);
        }
        
        public long getNextDate() {
            switch (mRepeat) {
                case 0: // None
                break;
                case 1: // Hours
                mStartDate.plusHours(mAmount);
                break;
                case 2: // Days
                mStartDate.plusDays(mAmount);
                break;
                case 3: // Weeks
                mStartDate.plusWeeks(mAmount);
                break;
                case 4: // Months
                mStartDate.plusMonths(mAmount);
                break;
                case 5: // Years
                mStartDate.plusYears(mAmount);
                break;
                default:
                break;
            }
            return NotificationPanel.convertDateToLong(mStartDate);
        }
    }
}
