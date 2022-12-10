package com.productivity.Util;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

import com.productivity.Productivity;

public class Notification {
    
    public static void displayTray(String title, String description) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Productivity.getImage("Images/icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Productivity");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(null);
        tray.add(trayIcon);
        trayIcon.displayMessage(title, description, MessageType.INFO);
        tray.remove(trayIcon);
    }
}