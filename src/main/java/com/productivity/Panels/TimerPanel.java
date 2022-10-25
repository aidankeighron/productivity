package com.productivity.Panels;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.productivity.BlockSites;
import com.productivity.Productivity;
import com.productivity.Util.Notification;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;

public class TimerPanel extends JPanel {

    private static final String[] kTimeOptions = {"Seconds", "Minutes", "Hours"};
    private static final int kMaxTimers = 20;

    private ArrayList<JProgressBar> mBars = new ArrayList<JProgressBar>();
    private ArrayList<JButton> mButtons = new ArrayList<JButton>();
    private int mTimeMultiplier = 1;
    private int mNumTimers = 0;
    private boolean mAlarm;
    private boolean mIsBlocked = false;
    private boolean mWantSitesBlocked = false;
    private boolean mBlockedTimerActive = false;
    private JCheckBox mBlockBox = new JCheckBox();
    private JLabel mBlockLbl = new JLabel();
    private JPanel mScrollPanel = new JPanel();
    
    public TimerPanel() {
        JLabel timeLbl = new JLabel("Duration:");
        JTextField timeField = new JTextField();
        JLabel nameLbl = new JLabel("Name:");
        JTextField nameFelid = new JTextField();
        nameFelid.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(timeField.getText());
                if (Integer.parseInt(timeField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                notInt = true;
            }
            if (timeField.getText().equals("") || notInt || (!notInt) && Integer.parseInt(timeField.getText()) > 1000 || nameFelid.getText().length() > 10) {
                timeField.setText("");
                JOptionPane.showMessageDialog(this, "Enter valid positive time");
            }
            else {
                if (mNumTimers < kMaxTimers) {
                    addProgressBar(nameFelid.getText(), Integer.parseInt(timeField.getText()));
                    timeField.setText("");
                    nameFelid.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Max number of timers reached");
                }
            }
        });
        timeField.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(timeField.getText());
                if (Integer.parseInt(timeField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                notInt = true;
            }
            if (timeField.getText().equals("") || notInt || (!notInt) && Integer.parseInt(timeField.getText()) > 1000 || nameFelid.getText().length() > 10) {
                timeField.setText("");
                JOptionPane.showMessageDialog(this, "Enter valid positive time");
            }
            else {
                if (mNumTimers < kMaxTimers) {
                    addProgressBar(nameFelid.getText(), Integer.parseInt(timeField.getText()));
                    timeField.setText("");
                    nameFelid.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Max number of timers reached");
                }
            }
        });
        JCheckBox alarmBox = new JCheckBox();
        alarmBox.addActionListener(e -> {
            mAlarm = alarmBox.isSelected();
        });
        mBlockLbl = new JLabel("Block: ");
        mBlockBox.addActionListener(e -> {
            if (!mBlockedTimerActive) {
                mWantSitesBlocked = mBlockBox.isSelected();
            }
            else {
                mBlockBox.setSelected(false);
            }
        });
        JLabel alarmLbl = new JLabel("Alarm: ");
        String alarmInfo = "Causes beep to happen 3 times";
        alarmBox.setToolTipText(alarmInfo);
        alarmLbl.setToolTipText(alarmInfo);
        String blockInfo = "Blocks websites specified in \"Block sites\" for durration of timer";
        mBlockBox.setToolTipText(blockInfo);
        mBlockLbl.setToolTipText(blockInfo);
        JButton addBtn = new JButton("           Add           ");
        addBtn.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(timeField.getText());
                if (Integer.parseInt(timeField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                notInt = true;
            }
            if (timeField.getText().equals("") || notInt) {
                timeField.setText("");
                JOptionPane.showMessageDialog(this, "Enter valid positive time");
            }
            else {
                if (mNumTimers < kMaxTimers) {
                    addProgressBar(nameFelid.getText(), Integer.parseInt(timeField.getText()));
                    timeField.setText("");
                    nameFelid.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Max number of timers reached");
                }
            }
        });
        JComboBox<String> timeList = new JComboBox<>(kTimeOptions);
        timeList.addActionListener(e -> {
            switch(timeList.getSelectedIndex()) {
                case 0:
                mTimeMultiplier = 1;
                break;
                case 1:
                mTimeMultiplier = 60;
                break;
                case 2:
                mTimeMultiplier = 60 * 60;
                break;
                default:
                mTimeMultiplier = 1;
                break;
            }
        });
        timeList.setFocusable(false);

        mScrollPanel.setLayout(new MigLayout());
        JScrollPane scroll = new JScrollPane(mScrollPanel);
        scroll.add(new JTextField());
        scroll.add(new JButton());
        super.setLayout(new MigLayout(((Productivity.kMigDebug)?"debug, ":"")+"flowy"));
        super.add(timeList, "spanx 2, center, aligny bottom");
        super.add(timeLbl, "");
        super.add(nameLbl, "");
        if (Boolean.parseBoolean(SettingsPanel.getSetting("blockSites")))
            super.add(mBlockLbl, "");
        super.add(alarmLbl, "");
        super.add(addBtn, "spanx 2, wrap, pushy, center");
        super.add(timeField, "");
        super.add(nameFelid, "");
        if (Boolean.parseBoolean(SettingsPanel.getSetting("blockSites")))
            super.add(mBlockBox, "");
        super.add(alarmBox, "wrap");
        super.add(scroll, "spany 6, grow, push");
    }
    
    public void setAllowBlock(boolean value) {
        if (value) {
            super.add(mBlockLbl, "cell 0 3");
            super.add(mBlockBox, "cell 1 3");
        }
        else {
            super.remove(mBlockLbl);
            super.remove(mBlockBox);
        }
    }
    
    private void addProgressBar(String name, int length) {
        boolean isBlockedTimer = !mBlockedTimerActive && mWantSitesBlocked && !mIsBlocked;
        mBlockedTimerActive = isBlockedTimer;
        mNumTimers++;
        JProgressBar progressBar = new JProgressBar(0, length * mTimeMultiplier);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        String title = (name.equals("")) ? Integer.toString(length) : name;
        JButton button = new JButton(title);
        if (isBlockedTimer) {
            button.setBackground(Color.RED);
        }

        Timer time = new Timer();
        TimerTask task = new TimerTask()
        {
            int seconds = length * mTimeMultiplier;
            int i = 0;
            int alarmNum = 3;
            @Override
            public void run()
            {
                if(i == seconds && i != 0 && !mAlarm) {
                    progressBar.setValue(i);
                    Toolkit.getDefaultToolkit().beep();
                    i++;
                    time.cancel();
                    time.purge();
                    if (mIsBlocked && isBlockedTimer) {
                        BlockSites.unBlockSites();
                        mIsBlocked = false;
                    }
                    try {
                        Notification.displayTray("Timer Done", title);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (i >= seconds && i != 0 && mAlarm) {
                    alarmNum--;
                    progressBar.setValue(i);
                    Toolkit.getDefaultToolkit().beep();
                    try {
                        Notification.displayTray("Timer Done", title);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                    if (alarmNum <= 0) {
                        time.cancel();
                        time.purge();
                        if (mIsBlocked && isBlockedTimer) {
                            BlockSites.unBlockSites();
                            mIsBlocked = false;
                        }
                    }
                }
                else {
                    progressBar.setValue(i);
                }
                if (i < seconds) {
                    i++;
                }
            }
        };
        time.schedule(task, 0, 1000);
        if (mWantSitesBlocked && !mIsBlocked && isBlockedTimer) {
            BlockSites.reBlockSites();
            mIsBlocked = true;
        }
        button.addActionListener(e -> {
            removeProgressBar(button, progressBar, task, time, isBlockedTimer);
            mNumTimers--;
        });
        
        mBars.add(progressBar);
        mButtons.add(button);

        mScrollPanel.add(progressBar, "grow, pushx");
        mScrollPanel.add(button, "grow, pushx, wrap");
        if (isBlockedTimer) {
            mBlockBox.setSelected(false);
        }
        Productivity.getInstance().repaintFrame();
    }
    
    private void removeProgressBar(JButton button, JProgressBar progressBar, TimerTask task, Timer time, Boolean isBlockedTimer) {
        if (mIsBlocked && mBlockedTimerActive && isBlockedTimer) {
            BlockSites.unBlockSites();
            mBlockedTimerActive = false;
            mIsBlocked = false;
        }
        task.cancel();
        time.cancel();
        time.purge();
        mScrollPanel.remove(progressBar);
        mScrollPanel.remove(button);
        Productivity.getInstance().repaintFrame();
    }
}
