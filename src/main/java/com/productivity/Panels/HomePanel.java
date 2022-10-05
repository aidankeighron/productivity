package com.productivity.Panels;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;

import com.productivity.Productivity;
import com.productivity.Custom.AddCustomCheckList;

public class HomePanel extends JPanel {
    
    private JPanel mCheckPanel;
    private JPanel mDailyPanel;
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
        
        mCheckPanel = makePanel(Productivity.getInstance().getBoxes(), "Checklist", BoxType.check, c);
        mDailyPanel = makePanel(DailyChecklist.getCheckBoxes(), "Daily", BoxType.daily, c);
        
        c.gridx = 0;
        c.gridy = 0;
        super.add(mCheckPanel, c);
        
        c.gridx = 1;
        c.gridy = 0;
        super.add(mDailyPanel, c);
        
        Productivity.getInstance().repaintFrame();
        this.repaint();
    }
    
    private JPanel makePanel(JCheckBox[] boxes, String title, BoxType type, GridBagConstraints c) {
        JPanel panel = new JPanel(new GridBagLayout());
        Box vertical = Box.createVerticalBox();
        Box vertical2 = Box.createVerticalBox();
        Box horizontal = Box.createHorizontalBox();
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        if (type == BoxType.custom) {
            title += AddCustomCheckList.getrandomName();
        }
        JLabel label = new JLabel(title);
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        vertical.add(label);
        if (boxes != null && (boxes.length > 0 || !type.equals(BoxType.custom))) {
            for (int i = 0; i < boxes.length; i++) {
                JCheckBox checkBox = new JCheckBox(boxes[i].getText());
                checkBox.setSelected(boxes[i].isSelected());
                checkBox.setForeground(boxes[i].getForeground());
                checkBox.setFocusPainted(false);
                int index = i;
                checkBox.addActionListener(e -> {
                    if (checkBox.isSelected())
                        Productivity.showConfetti();
                    switch (type) {
                        case check:
                        Productivity.getInstance().setSelected(checkBox.isSelected(), index);
                        break;
                        case daily:
                        DailyChecklist.setCheckBoxes(checkBox.isSelected(), index);
                        SettingsPanel.setDailySelected(checkBox.isSelected(), index);
                        break;
                        case custom:
                        String customTitle = AddCustomCheckList.getrandomName();
                        AddCustomCheckList.setCheckList(checkBox.isSelected(), index, customTitle);
                        break;
                        default:
                        break;
                    }
                });
                if ((boxes.length > 10 && i > boxes.length/2 && type == BoxType.check) || (boxes.length > 5 && i >= 5 && i < 10 && type != BoxType.check))
                    vertical2.add(checkBox);
                else if (i < 10)
                    vertical.add(checkBox);
            }
            horizontal.add(vertical);
            horizontal.add(vertical2);
            panel.add(horizontal, c);
            return panel;
        }
        if (!type.equals(BoxType.custom)) return panel;
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
