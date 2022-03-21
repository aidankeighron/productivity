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
    
    JPanel checkPanel;
    JPanel dailyPanel;
    JPanel customPanel;
    GridBagConstraints c = new GridBagConstraints();
    
    enum BoxType {
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
        c.ipady = gui.height;

        if (checkPanel != null) 
            super.remove(checkPanel);
        if (dailyPanel != null) 
            super.remove(dailyPanel);
        if (customPanel != null) 
            super.remove(customPanel);
        

        checkPanel = makePanel(gui.checkBoxPanel.getBoxes(), "Checklist", BoxType.check, c);
        dailyPanel = makePanel(DailyChecklist.getCheckBoxes(), "Daily", BoxType.daily, c);
        customPanel = makePanel(AddCustomCheckList.getRandomCheckBoxes(), "Custom", BoxType.custom, c);

        if (customPanel != null) {
            c.gridx = 0;
            c.gridy = 0;
            c.gridheight = 2;
            super.add(checkPanel, c);

            c.gridheight = 1;
            c.gridx = 1;
            c.gridy = 0;
            super.add(dailyPanel, c);

            c.gridx = 1;
            c.gridy = 1;
            super.add(customPanel, c);
        }
        else {
            c.gridx = 0;
            c.gridy = 0;
            super.add(checkPanel, c);

            c.gridx = 1;
            c.gridy = 0;
            super.add(dailyPanel, c);
        }

        gui.repaintFrame();
        this.repaint();
    }
    
    private JPanel makePanel(JCheckBox[] boxes, String title, BoxType type, GridBagConstraints c) {
        //JPanel panel = new JPanel(new GridLayout(gui.height/30, gui.length/200));
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
                        gui.checkBoxPanel.setSelected(checkBox.isSelected(), index);
                        break;
                        case daily:
                        DailyChecklist.setCheckBoxes(checkBox.isSelected(), index);
                        SettingsPanel.dailyPanel.setSelected(checkBox.isSelected(), index);
                        break;
                        case custom:
                        //AddCustomCheckList.setCheckList(checkBox.isSelected(), index, name); TODO
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
}
