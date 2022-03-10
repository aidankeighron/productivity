package com.productivity;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class HelpPanel extends JTabbedPane {
    

    public HelpPanel() {
        JPanel warning = new JPanel();
        JLabel warningLabel = new JLabel("Warning");
        warning.add(warningLabel);
        
        super.addTab("Warning", warning);
    }
}
