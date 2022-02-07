package com.productivity;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import com.productivity.Custom.AddCustomCheckList;

public class HomePanel extends JPanel {

    JPanel checkPanel;
    JPanel dailyPanel;
    JPanel customPanel;

    public HomePanel() {
        reset();
    }

    public void reset() {
        // checkPanel.removeAll();
        // dailyPanel.removeAll();
        // customPanel.removeAll();
        // checkPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        // dailyPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        // customPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        // checkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // dailyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // customPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // JCheckBox[] checkBoxes = gui.getCheckBoxes();
        // JCheckBox[] dailyBoxes = DailyChecklist.getCheckBoxes();
        // JCheckBox[] customBoxes = addCustomCheckList.getRandomCheckBoxes();
        // for (int i = 0; i < checkBoxes.length; i++) {
        //     JCheckBox checkBox = new JCheckBox(checkBoxes[i].getText());
        //     checkBox.setText(checkBox.getText() + String.format("%"+(45 - checkBox.getText().length())+"s", ""));
        //     checkPanel.add(checkBox);
        // }
        // for (int i = 0; i < dailyBoxes.length; i++) {
        //     JCheckBox checkBox = new JCheckBox(dailyBoxes[i].getText());
        //     dailyPanel.add(checkBox);
        // }
        // for (int i = 0; i < customBoxes.length; i++) {
        //     JCheckBox checkBox = new JCheckBox(customBoxes[i].getText());
        //     customPanel.add(checkBox);
        // }
        if (checkPanel != null)
            checkPanel.removeAll();
        if (dailyPanel != null)
            dailyPanel.removeAll();
        if (customPanel != null)
            customPanel.removeAll();
        
        checkPanel = resetPanel(gui.getCheckBoxes(), "Checklist");
        dailyPanel = resetPanel(DailyChecklist.getCheckBoxes(), "Daily");
        customPanel = resetPanel(AddCustomCheckList.getRandomCheckBoxes(), "Custom");

        Box vertical = Box.createVerticalBox();
        vertical.add(dailyPanel);
        vertical.add(customPanel);

        for (Component c : super.getComponents()) {
            if (c.equals(checkPanel)) {
                super.remove(checkPanel);
                super.remove(dailyPanel);
                super.remove(customPanel);
                break;
            }
        }
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.WEST, checkPanel);
        super.add(BorderLayout.CENTER, vertical);
        gui.repaintFrame();
    }

    private JPanel resetPanel(JCheckBox[] boxes, String title) {
        JPanel panel = new JPanel(new GridLayout( /*10 - ((boxes.length<10)?boxes.length:10)*/0, 2));
        //System.out.println(10 - ((boxes.length<10)?boxes.length:10) + " : " + title);
        JLabel label = new JLabel(title);
        panel.add(label);
        //panel.add(Box.createRigidArea(new Dimension(150, 0)));
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        for (int i = 0; i < boxes.length; i++) {
            JCheckBox checkBox = new JCheckBox(boxes[i].getText());
            panel.add(checkBox);
        }
        return panel;
    }
}
