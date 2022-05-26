package com.productivity;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;


public class TimerPanel extends JPanel {

    private static final String[] kTimeOptions = {"Seconds", "Minutes", "Hours"};
    private static final int kMaxTimers = 20;

    private Box mProgressBars = Box.createVerticalBox();
    private Box mNames = Box.createVerticalBox();
    private ArrayList<JProgressBar> mBars = new ArrayList<JProgressBar>();
    private ArrayList<JButton> mButtons = new ArrayList<JButton>();
    private int mTimeMultiplier = 1;
    private int mNumTimers = 0;
    private boolean mAlarm;
    private boolean mIsBlocked = false;
    private boolean mWantSitesBlocked = false;
    private boolean mBlockedTimerActive = false;
    private JCheckBox mBlockBox = new JCheckBox();
    private static Box mButton = Box.createHorizontalBox();

    private static Box mBlock;
    private static Box mVertical = Box.createVerticalBox();
    
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
                ex.printStackTrace();
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
                ex.printStackTrace();
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
        JLabel blockLbl = new JLabel("Block: ");
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
        blockLbl.setToolTipText(blockInfo);
        JButton addBtn = new JButton("           Add           ");
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(timeField.getText());
                if (Integer.parseInt(timeField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
        JPanel config = new JPanel();
        Box time = Box.createHorizontalBox();
        time.add(timeLbl);
        time.add(timeField);
        Box name = Box.createHorizontalBox();
        name.add(nameLbl);
        name.add(nameFelid);
        Box check = Box.createHorizontalBox();
        check.add(alarmLbl);
        check.add(alarmBox);
        mBlock = Box.createHorizontalBox();
        mBlock.add(blockLbl);
        mBlock.add(mBlockBox);
        mButton = Box.createHorizontalBox();
        mButton.add(addBtn);
        addBlank(mVertical, 2);
        mVertical.add(timeList);
        mVertical.add(time);
        mVertical.add(name);
        mVertical.add(check);
        if (Boolean.parseBoolean(SettingsPanel.getSetting("blockSites"))) {
            mVertical.add(mBlock);         
        }
        mVertical.add(mButton);
        config.add(mVertical);
        
        JPanel progressBarsPanel = new JPanel();
        progressBarsPanel.add(mProgressBars);
        JPanel namesPanel = new JPanel();
        namesPanel.add(mNames);
        
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BorderLayout());
        scrollPanel.add(BorderLayout.WEST, progressBarsPanel);
        scrollPanel.add(BorderLayout.EAST, namesPanel);
        JScrollPane scroll = new JScrollPane(scrollPanel);
        
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.EAST, config);
        super.add(BorderLayout.WEST, scroll);
    }
    
    public static void setAllowBlock(boolean value) {
        if (value) {
            mVertical.remove(mButton);
            mVertical.add(mBlock);
            mVertical.add(mButton);
        }
        else {
            mVertical.remove(mBlock);
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
        button.setFocusPainted(false);
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
                }
                else if (i >= seconds && i != 0 && mAlarm) {
                    alarmNum--;
                    progressBar.setValue(i);
                    Toolkit.getDefaultToolkit().beep();
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
        Component progressBox = Box.createRigidArea(new Dimension(0, 8));
        Component nameBox = Box.createRigidArea(new Dimension(0, 2));
        button.addActionListener(e -> {
            removeProgressBar(button, progressBar, task, time, isBlockedTimer, progressBox, nameBox);
            mNumTimers--;
        });
        
        mBars.add(progressBar);
        mButtons.add(button);
        Border barBorder = BorderFactory.createEmptyBorder(4, 0, 0, 0);
        mProgressBars.setBorder(barBorder);
        mProgressBars.add(progressBar);
        mProgressBars.add(progressBox);
        Border nameBorder = BorderFactory.createEmptyBorder(2, 0, 0, 0);
        mNames.setBorder(nameBorder);
        mNames.add(button);
        mNames.add(nameBox);
        if (isBlockedTimer) {
            mBlockBox.setSelected(false);
        }
        Productivity.getInstance().repaintFrame();
    }
    
    private void removeProgressBar(JButton button, JProgressBar progressBar, TimerTask task, Timer time, Boolean isBlockedTimer, Component progBox, Component nameBox) {
        if (mIsBlocked && mBlockedTimerActive && isBlockedTimer) {
            BlockSites.unBlockSites();
            mBlockedTimerActive = false;
            mIsBlocked = false;
        }
        task.cancel();
        time.cancel();
        time.purge();
        mProgressBars.remove(progressBar);
        mProgressBars.remove(progBox);
        mNames.remove(button);
        mNames.remove(nameBox);
        mBars.remove(progressBar);
        mButtons.remove(button);
        Productivity.getInstance().repaintFrame();
    }
    
    private void addBlank(Box panel, int amount) {
        for (int i = 0; i < amount; i++) {
            JLabel blank = new JLabel();
            panel.add(blank);
        }
    }
}
