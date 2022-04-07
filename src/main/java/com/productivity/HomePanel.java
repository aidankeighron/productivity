package com.productivity;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;

import com.productivity.Custom.AddCustomCheckList;

public class HomePanel extends JPanel {
    
    private JPanel mCheckPanel;
    private JPanel mDailyPanel;
    private JPanel mCustomPanel;
    private GridBagConstraints c = new GridBagConstraints();
    
    private enum BoxType {
        check,
        daily,
        custom;
    }
    
    public HomePanel() {
        super.setLayout(new GridBagLayout());
        reset();
    }
    
    public void reset() {
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.ipady = Productivity.kHeight;
        
        if (mCheckPanel != null) {
            mCheckPanel.removeAll();
            super.remove(mCheckPanel);
        }
        if (mDailyPanel != null) {
            mDailyPanel.removeAll();
            super.remove(mDailyPanel);
        }
        if (mCustomPanel != null) {
            mCustomPanel.removeAll();
            super.remove(mCustomPanel);
        }
        
        mCheckPanel = makePanel(Productivity.getBoxes(), "Checklist", BoxType.check, c);
        mDailyPanel = makePanel(DailyChecklist.getCheckBoxes(), "Daily", BoxType.daily, c);
        mCustomPanel = makePanel(AddCustomCheckList.getRandomCheckBoxes(), "Custom", BoxType.custom, c);
        
        if (mCustomPanel != null) {
            c.gridx = 0;
            c.gridy = 0;
            c.gridheight = 2;
            super.add(mCheckPanel, c);
            
            c.gridheight = 1;
            c.gridx = 1;
            c.gridy = 0;
            super.add(mDailyPanel, c);
            
            c.gridx = 1;
            c.gridy = 1;
            super.add(mCustomPanel, c);
        }
        else {
            c.gridx = 0;
            c.gridy = 0;
            super.add(mCheckPanel, c);
            
            c.gridx = 1;
            c.gridy = 0;
            super.add(mDailyPanel, c);
        }
        
        Productivity.repaintFrame();
        this.repaint();
    }
    
    private JPanel makePanel(JCheckBox[] boxes, String title, BoxType type, GridBagConstraints c) {
        JPanel panel = new JPanel(new GridBagLayout());
        Box vertical = Box.createVerticalBox();
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        if (type == BoxType.custom) {
            title += AddCustomCheckList.getrandomName();
        }
        JLabel label = new JLabel(title);
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        vertical.add(label);
        if (boxes != null) {
            for (int i = 0; i < boxes.length; i++) {
                JCheckBox checkBox = new JCheckBox(boxes[i].getText());
                checkBox.setSelected(boxes[i].isSelected());
                checkBox.setForeground(boxes[i].getForeground());
                checkBox.setFocusPainted(false);
                int index = i;
                checkBox.addActionListener(e -> {
                    switch (type) {
                        case check:
                        Productivity.setSelected(checkBox.isSelected(), index);
                        break;
                        case daily:
                        DailyChecklist.setCheckBoxes(checkBox.isSelected(), index);
                        SettingsPanel.setDailySelected(checkBox.isSelected(), index);
                        break;
                        case custom:
                        AddCustomCheckList.setCheckList(checkBox.isSelected(), index, checkBox.getText());
                        break;
                        default:
                        break;
                    }
                });
                vertical.add(checkBox);
            }
            panel.add(vertical, c);
            return panel;
        }
        return null;
    }
    
    private static HomePanel mInstance = null;
    public synchronized static HomePanel getInstance() {
        if (mInstance == null) {
            mInstance = new HomePanel();
        }
        return mInstance;
    }
}
