package com.productivity.Panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeArea;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class NotificationPanel extends JPanel {
    
    private String[] kRepeatOptions = {"None", "Hour(s)", "Day(s)", "Week(s)", "Month(s)", "Year(s)"};
    private ArrayList<Notification> mNotifications = new ArrayList<Notification>();
    private File mNotificationFile = Productivity.getSave("Saves/notification.TXT");
    private static File mTimeFile = Productivity.getSave("Saves/time.TXT");
    private static DateTimeFormatter mFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss");
    private final String kDelimiter = "|~|"; // I understand this is the worst way to do this but if someone has this in their description they deserve to break the code
    private final String kRegexDelimiter = "\\|~\\|";
    private final JPanel mPanel = new JPanel(new MigLayout(Productivity.kMigDebug?"debug":""));
    private int mNumberOfNotifications = 0;
    private final int kMaxNumberOfNotifications = 15;

    private static TimePickerSettings mTimePickerSettings = new TimePickerSettings();
    static {
        mTimePickerSettings.setColor(TimeArea.TextFieldBackgroundValidTime, UIManager.getColor("TextField.background"));
        mTimePickerSettings.setColor(TimeArea.TextFieldBackgroundInvalidTime, UIManager.getColor("TextField.background"));
        mTimePickerSettings.setColor(TimeArea.TimePickerTextValidTime, UIManager.getColor("textText"));
    }

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
        JScrollPane scroll = new JScrollPane(mPanel);
        super.add(scroll, "grow, push, span");
        super.add(addBtn, "growx");
        loadNotifications();

        Timer time = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                Notification notificationToRemove = null;
                for (Notification notification : mNotifications) {
                    long duration = notification.getStartDate() - (System.currentTimeMillis() / 1000l);
                    
                    if (0 >= duration) {
                        try {
                            if (duration >= -60*60) {
                                com.productivity.Util.Notification.displayTray(notification.mName, notification.mText);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (notification.getNextDate() == -1) {
                            NotificationPanel.super.remove(notification.getPanel());
                            notificationToRemove = notification;
                            Productivity.getInstance().repaint();
                        }
                        else {
                            saveNotifications();
                        }
                    }
                }
                if (notificationToRemove != null) {
                    mNotifications.remove(notificationToRemove);
                    mNumberOfNotifications--;
                    saveNotifications();
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
				LocalDateTime now = LocalDateTime.now();

                DailyChecklist.resetBoxes(!dtf.format(now).equals(readData(mTimeFile)[0]));
				try {
					writeData(dtf.format(now), mTimeFile);
				} catch (Exception e) {
					e.printStackTrace();
					writeData("11/11/2020", mTimeFile);
				}
            }
        };
        time.schedule(task, 0, 1000);
    }
    
    private void notificationPopup() {
        JDialog infoBox = new JDialog(Productivity.getInstance(), "Create Notification");
        
        JLabel nameLbl = new JLabel("Name:");
        JTextField name = new JTextField(10);

        JLabel textLbl = new JLabel("Description");
        JTextArea text = new JTextArea(5, 15);
        text.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(text);

        JLabel timeLbl = new JLabel("Start time (no time will mean now):");
        TimePicker timePicker = new TimePicker(mTimePickerSettings);

        JLabel dateLbl = new JLabel("Date (no date will mean today):");
        DatePicker datePicker = new DatePicker(mDatePickerSettings.copySettings());
        
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
            if (datePicker.getDate() == null) {
                datePicker.setDate(LocalDate.now());
            }
            if (timePicker.getTime() == null) {
                timePicker.setTime(LocalTime.now());
            }
            if (convertDateToLong(LocalDateTime.of(datePicker.getDate(), timePicker.getTime())) - (System.currentTimeMillis() / 1000l) <= -60*60) {
                JOptionPane.showMessageDialog(this, "Date/Time can't be in the past", "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int amount = repeatAmount.getText().equals("") ? 0 : Integer.parseInt(repeatAmount.getText());
            newNotification(name.getText(), text.getText(), repeat.getSelectedIndex(), amount, datePicker.getDate(), timePicker.getTime());
            infoBox.dispose();
        });
        name.addActionListener(e -> {
            if (name.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Name field can not be blank", "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (datePicker.getDate() == null) {
                datePicker.setDate(LocalDate.now());
            }
            if (timePicker.getTime() == null) {
                timePicker.setTime(LocalTime.now());
            }
            if (convertDateToLong(LocalDateTime.of(datePicker.getDate(), timePicker.getTime())) - (System.currentTimeMillis() / 1000l) <= -60*60) {
                JOptionPane.showMessageDialog(this, "Date/Time can't be in the past", "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int amount = repeatAmount.getText().equals("") ? 0 : Integer.parseInt(repeatAmount.getText());
            newNotification(name.getText(), text.getText(), repeat.getSelectedIndex(), amount, datePicker.getDate(), timePicker.getTime());
            infoBox.dispose();
        });
        
        infoBox.setLayout(new MigLayout(((Productivity.kMigDebug)?"debug, ":"")+"ins 5"));
        infoBox.add(nameLbl, "wrap");
        infoBox.add(name, "wrap");
        infoBox.add(textLbl, "wrap");
        infoBox.add(scroll, "wrap, h 100");
        infoBox.add(timeLbl, "wrap");
        infoBox.add(timePicker, "wrap");
        infoBox.add(dateLbl, "wrap");
        infoBox.add(datePicker, "wrap");
        infoBox.add(repeatLbl, "wrap");
        infoBox.add(repeatInfo, "split 3");
        infoBox.add(repeatAmount, "");
        infoBox.add(repeat, "wrap");
        infoBox.add(confirm, "dock south, span, grow, push");
        infoBox.setSize(210, 400);
        infoBox.setLocation(Productivity.getInstance().getLocation());
        infoBox.setVisible(true);
    }

    private void showNotification(String name, String message, Notification notification, int repeat, int amount) {
        if (mNumberOfNotifications >= kMaxNumberOfNotifications) return;
        mNumberOfNotifications++;
        JButton delete = new JButton("Remove");
        LocalDateTime ldt = Instant.ofEpochSecond(notification.getStartDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        JLabel info = new JLabel(name+" | Repeat every: "+amount+" "+kRepeatOptions[repeat]+" | Next notification "+ldt.format(mFormatter));
        JPanel panel = new JPanel(new MigLayout());
        
        panel.add(info);
        panel.add(delete, "align right");
        notification.setPanel(panel);
        delete.addActionListener(e -> {
            mNotifications.remove(notification);
            mPanel.remove(panel);
            mNumberOfNotifications--;
            saveNotifications();
            Productivity.getInstance().repaint();
        });
        mPanel.add(panel, "wrap, growx, pushx, spanx");
        Productivity.getInstance().repaint();
    }

    private void saveNotifications() {
        String[] data = new String[mNotifications.size()];
        for (int j = 0; j < data.length; j++) {
            data[j] = mNotifications.get(j).mName+kDelimiter+mNotifications.get(j).mText+kDelimiter+Integer.toString(mNotifications.get(j).mRepeat)+kDelimiter+Integer.toString(mNotifications.get(j).mAmount)+kDelimiter+Long.toString(mNotifications.get(j).getStartDate());
        }
        writeData(data, mNotificationFile);
    }
    
    private void newNotification(String name, String text, int repeat, int amount, LocalDate date, LocalTime time) {
        LocalDateTime startDate = LocalDateTime.of(date, time);
        Notification notification = new Notification(name, text, repeat, amount, startDate);
        mNotifications.add(notification);
        saveNotifications();

        showNotification(name, text, notification, repeat, amount);
    }

    private void loadNotifications() {
        String[] data = readData(mNotificationFile);

        for (int i = 0; i < data.length; i++) {
            String[] values = data[i].split(kRegexDelimiter);
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(values[4])), TimeZone.getDefault().toZoneId());
            Notification notification = new Notification(values[0], values[1], Integer.parseInt(values[2]), Integer.parseInt(values[3]), date);
            mNotifications.add(notification);

            showNotification(values[0], values[1], notification, Integer.parseInt(values[2]), Integer.parseInt(values[3]));
        }
    }

    public static long convertDateToLong(LocalDateTime date) {
        Instant instant = Instant.now(); //can be LocalDateTime
        ZoneId systemZone = ZoneId.systemDefault(); // my timezone
        return date.toEpochSecond(systemZone.getRules().getOffset(instant));
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
        public String mText;
        public int mRepeat;
        public int mAmount;
        private LocalDateTime mStartDate;
        private JPanel mPanel;
        
        public Notification(String name, String text, int repeat, int amount, LocalDateTime startDate) {
            mName = name;
            mText = text;
            mRepeat = repeat;
            mAmount = amount;
            mStartDate = startDate;
        }

        public long getStartDate() {
            return NotificationPanel.convertDateToLong(mStartDate);
        }

        public void setPanel(JPanel panel) {
            mPanel = panel;
        }

        public JPanel getPanel() {
            return mPanel;
        }
        
        public long getNextDate() {
            switch (mRepeat) {
                case 0: // None
                return -1;
                case 1: // Hours
                mStartDate = mStartDate.plusHours(mAmount);
                break;
                case 2: // Days
                mStartDate = mStartDate.plusDays(mAmount);
                break;
                case 3: // Weeks
                mStartDate = mStartDate.plusWeeks(mAmount);
                break;
                case 4: // Months
                mStartDate = mStartDate.plusMonths(mAmount);
                break;
                case 5: // Years
                mStartDate = mStartDate.plusYears(mAmount);
                break;
                default:
                break;
            }
            if (mAmount <= 0) {
                return -1;
            }
            return 0;
        }
    }
}
