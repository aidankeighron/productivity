package com.productivity;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

public class HelpPanel extends JTabbedPane {
    
    public HelpPanel() {
        JPanel warning = new JPanel();
        JLabel warningLabel =  new JLabel("<html>Two<br>lines</html>");
        warningLabel.setText("<html>WARNING both \"Run on startup\" and \"Block sites\" modify system files. Run on startup adds a .bat file to your startup folder (there is little to no risk in using this feature).<br>Block sites edits your host file in /System32, redirecting any website back to yourself (causing it not to load). There are fail-safes in place to prevent anything from going wrong. The worst thing that can happen is the program crashing before it can revert the changes to the host file, causing the website to stay blocked. The fix is simple, all you need to do is create a \"block\" timer and end it or press the reset button in the block sites setting. If both of these fixes don't work then navigate to C:\\Windows\\System32\\drivers\\etc\\hosts and remove anything below the last line with a # before it</html>");
        warning.setLayout(new BorderLayout());
        warning.add(BorderLayout.NORTH, warningLabel);
        super.addTab("Warning", warning);
    }
}
