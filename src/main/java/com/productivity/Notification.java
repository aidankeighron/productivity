package com.productivity;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class Notification {
    
    public static void displayTray(String title, String description) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
    
        Image image = Toolkit.getDefaultToolkit().createImage("C:/Users/aidan/OneDrive/Documents/Programms/Productivity/src/main/java/com/productivity/icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Productivity");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(null);
        tray.add(trayIcon);
        trayIcon.displayMessage(title, description, MessageType.INFO);
    }
}