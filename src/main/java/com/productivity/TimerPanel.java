package com.productivity;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimerPanel extends JPanel {

    private final Box progresrBars = Box.createVerticalBox();
    private final Box names = Box.createVerticalBox();
    private final ArrayList<JProgressBar> bars = new ArrayList<JProgressBar>();
    private final ArrayList<JButton> buttons = new ArrayList<JButton>();
    private final String[] timeOptions = {"Seconds", "Minutes", "Hours"};
    private static int timeMuitplyer = 1;
    private static int numTimers = 0;
    private final static int maxTimers = 20;
    private static boolean alarm;
    private static boolean isBlocked = false;
    private static boolean wantSitesBlocked = false;
    private static boolean blockedTimerActive = false;
    private static Box block;
    private JCheckBox blockBox = new JCheckBox();
    private static Box vertical = Box.createVerticalBox();
    
    public TimerPanel() {
        JLabel timeLbl = new JLabel("Length:");
        JTextField timeField = new JTextField();
        JLabel nameLbl = new JLabel("Name:");
        JTextField nameFeild = new JTextField();
        nameFeild.addActionListener(e -> {
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
                JOptionPane.showMessageDialog(this, "Enter vaild positive time");
            }
            else {
                if (numTimers < maxTimers) {
                    addProgressBar(nameFeild.getText(), Integer.parseInt(timeField.getText()));
                    timeField.setText("");
                    nameFeild.setText("");
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
            if (timeField.getText().equals("") || notInt) {
                JOptionPane.showMessageDialog(this, "Enter vaild positive time");
            }
            else {
                if (numTimers < maxTimers) {
                    addProgressBar(nameFeild.getText(), Integer.parseInt(timeField.getText()));
                    timeField.setText("");
                    nameFeild.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Max number of timers reached");
                }
            }
        });
        JCheckBox alarmBox = new JCheckBox();
        alarmBox.addActionListener(e -> {
            alarm = alarmBox.isSelected();
        });
        JLabel blockLbl = new JLabel("Block: ");
        blockBox.addActionListener(e -> {
            if (!blockedTimerActive) {
                wantSitesBlocked = blockBox.isSelected();
            }
            else {
                blockBox.setSelected(false);
            }
        });
        JLabel alarmLbl = new JLabel("Alarm: ");
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
                JOptionPane.showMessageDialog(this, "Enter vaild positive time");
            }
            else {
                if (numTimers < maxTimers) {
                    addProgressBar(nameFeild.getText(), Integer.parseInt(timeField.getText()));
                    timeField.setText("");
                    nameFeild.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Max number of timers reached");
                }
            }
        });
        JComboBox<String> timeList = new JComboBox<>(timeOptions);
        timeList.addActionListener(e -> {
            switch(timeList.getSelectedIndex()) {
                case 0:
                timeMuitplyer = 1;
                break;
                case 1:
                timeMuitplyer = 60;
                break;
                case 2:
                timeMuitplyer = 60 * 60;
                break;
                default:
                timeMuitplyer = 1;
                break;
            }
        });
        
        JPanel config = new JPanel();
        Box time = Box.createHorizontalBox();
        time.add(timeLbl);
        time.add(timeField);
        Box name = Box.createHorizontalBox();
        name.add(nameLbl);
        name.add(nameFeild);
        Box check = Box.createHorizontalBox();
        check.add(alarmLbl);
        check.add(alarmBox);
        block = Box.createHorizontalBox();
        block.add(blockLbl);
        block.add(blockBox);
        Box button = Box.createHorizontalBox();
        button.add(addBtn);
        addBlank(vertical, 2);
        vertical.add(timeList);
        vertical.add(time);
        vertical.add(name);
        vertical.add(check);
        if (Boolean.parseBoolean(SettingsPanel.getSetting("blockSites"))) {
            vertical.add(block);         
        }
        vertical.add(button);
        config.add(vertical);
        
        JPanel progresrBarsPanel = new JPanel();
        progresrBarsPanel.add(progresrBars);
        JPanel namesPanel = new JPanel();
        namesPanel.add(names);
        
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BorderLayout());
        scrollPanel.add(BorderLayout.WEST, progresrBarsPanel);
        scrollPanel.add(BorderLayout.EAST, namesPanel);
        JScrollPane scroll = new JScrollPane(scrollPanel);
        
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.EAST, config);
        super.add(BorderLayout.WEST, scroll);
    }
    
    public static void setAllowBlock(boolean value) {
        if (value) {
            vertical.add(block);
        }
        else {
            vertical.remove(block);
        }
    }
    
    private void addProgressBar(String name, int length) {
        boolean isBlockedTimer = !blockedTimerActive && wantSitesBlocked && !isBlocked;
        blockedTimerActive = isBlockedTimer;
        numTimers++;
        JProgressBar progressBar = new JProgressBar(0, length * timeMuitplyer);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        String title = (name.equals("")) ? Integer.toString(length) : name;
        JButton button = new JButton(title);
        
        Timer time = new Timer();
        TimerTask task = new TimerTask()
        {
            int seconds = length * timeMuitplyer;
            int i = 0;
            int alarmNum = 3;
            @Override
            public void run()
            {
                if(i == seconds && i != 0 && !alarm) {
                    progressBar.setValue(i);
                    Toolkit.getDefaultToolkit().beep();
                    i++;
                    time.cancel();
                    time.purge();
                    if (isBlocked && isBlockedTimer) {
                        BlockSites.unBlockSites();
                        isBlocked = false;
                    }
                }
                else if (i >= seconds && i != 0 && alarm) {
                    alarmNum--;
                    progressBar.setValue(i);
                    Toolkit.getDefaultToolkit().beep();
                    i++;
                    if (alarmNum <= 0) {
                        time.cancel();
                        time.purge();
                        if (isBlocked && isBlockedTimer) {
                            BlockSites.unBlockSites();
                            isBlocked = false;
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
        if (wantSitesBlocked && !isBlocked && isBlockedTimer) {
            BlockSites.reBlockSites();
            isBlocked = true;
        }
        Component progressBox = Box.createRigidArea(new Dimension(0, 8));
        Component nameBox = Box.createRigidArea(new Dimension(0, 2));
        button.addActionListener(e -> {
            removeProgressBar(button, progressBar, task, time, isBlockedTimer, progressBox, nameBox);
            numTimers--;
        });
        
        bars.add(progressBar);
        buttons.add(button);
        Border barBorder = BorderFactory.createEmptyBorder(4, 0, 0, 0);
        progresrBars.setBorder(barBorder);
        progresrBars.add(progressBar);//TODO fix borders
        progresrBars.add(progressBox);
        Border nameBorder = BorderFactory.createEmptyBorder(2, 0, 0, 0);
        names.setBorder(nameBorder);
        names.add(button);
        names.add(nameBox);
        if (isBlockedTimer) {
            blockBox.setSelected(false);
        }
        gui.repaintFrame();
    }
    
    private void removeProgressBar(JButton button, JProgressBar progressBar, TimerTask task, Timer time, Boolean isBlockedTimer, Component progBox, Component nameBox) {
        if (isBlocked && blockedTimerActive && isBlockedTimer) {
            BlockSites.unBlockSites();
            blockedTimerActive = false;
            isBlocked = false;
        }
        task.cancel();
        time.cancel();
        time.purge();
        progresrBars.remove(progressBar);
        progresrBars.remove(progBox);
        names.remove(button);
        names.remove(nameBox);
        bars.remove(progressBar);
        buttons.remove(button);
        gui.repaintFrame();
    }
    
    private void addBlank(Box panel, int ammount) {
        for (int i = 0; i < ammount; i++) {
            JLabel blank = new JLabel();
            panel.add(blank);
        }
    }
}
