package com.productivity;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;
import javax.swing.*;
import com.productivity.Custom.addCustomCheckList;
import java.awt.BorderLayout;
import java.awt.BorderLayout;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;

public class HomePanel extends JPanel {

    static JPanel checkPanel = new JPanel(new GridLayout(7, 2));
    static JPanel dailyPanel = new JPanel(new GridLayout(3, 2));
    static JPanel customPanel = new JPanel(new GridLayout(4, 2));

    public HomePanel() {
        reset();
    }

    public void reset() {
        JCheckBox[] checkBoxes = gui.getCheckBoxes();
        JCheckBox[] dailyBoxes = DailyChecklist.getCheckBoxes();
        JCheckBox[] customBoxes = addCustomCheckList.getRandomCheckBoxes();
        for (int i = 0; i < checkBoxes.length; i++) {
            checkPanel.add(checkBoxes[i]);
        }
        for (int i = 0; i < dailyBoxes.length; i++) {
            dailyPanel.add(dailyBoxes[i]);
        }
        for (int i = 0; i < customBoxes.length; i++) {
            customPanel.add(customBoxes[i]);
        }

        Box vertical = Box.createVerticalBox();
        vertical.add(dailyPanel);
        vertical.add(customPanel);

        super.setLayout(new BorderLayout());
        super.add(BorderLayout.WEST, checkPanel);
        super.add(BorderLayout.EAST, vertical);
    }
}
