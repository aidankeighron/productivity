package com.productivity;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import com.productivity.Custom.AddCustomCheckList;

public class HomePanel extends JPanel {
    
    JPanel checkPanel;
    JPanel dailyPanel;
    JPanel customPanel;
    
    
    enum BoxType {
        check,
        daily,
        custom;
    }
    
    public HomePanel() {
        reset();
    }
    
    public void reset() {
        checkPanel = makePanel(gui.checkBoxPanel.getBoxes(), "Checklist", BoxType.check);
        dailyPanel = makePanel(DailyChecklist.getCheckBoxes(), "Daily", BoxType.daily);
        customPanel = makePanel(AddCustomCheckList.getRandomCheckBoxes(), "Custom", BoxType.custom);
        
        Box vertical = Box.createVerticalBox();
        vertical.add(dailyPanel);
        if (customPanel != null)
            vertical.add(customPanel);
        
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.WEST, checkPanel);
        super.add(BorderLayout.CENTER, vertical);
        gui.repaintFrame();
        this.repaint();
    }
    
    private JPanel makePanel(JCheckBox[] boxes, String title, BoxType type) {
        //JPanel panel = new JPanel(new GridLayout(0, 2));
        JPanel panel = new JPanel(new GridLayout(gui.height/30, gui.length/200));
        JLabel label = new JLabel(title);
        panel.add(label);
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        if (boxes != null) {
            for (int i = 0; i < boxes.length; i++) {
                JCheckBox checkBox = new JCheckBox(boxes[i].getText());
                checkBox.setSelected(boxes[i].isSelected());
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
                panel.add(checkBox);
            }
            return panel;
        }
        return null;
    }
}
