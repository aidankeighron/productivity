package com.productivity;

import javax.swing.JPanel;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.nio.file.Files;
import java.awt.BorderLayout;

public class blockSites extends JPanel {
    //TODO fix label spacing
    private static File hostsFile = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
    private static File newHosts = new File((!gui.debug)?"classes\\Newhosts":gui.debugPath+"Newhosts");
    private static File backupFile = new File((!gui.debug)?"classes\\hosts":gui.debugPath+"hosts");
    private static File blockedSites = new File((!gui.debug)?"classes\\blockedSites.TXT":gui.debugPath+"blockedSites.TXT");
    
    public blockSites() {
        JTextArea site = new JTextArea();
        site.setText(load());

        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> blockSite(site));

        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> reset(site));

        JLabel label = new JLabel("You will need to run program as admin for this feature to work");

        Box buttons = Box.createHorizontalBox();
        buttons.add(apply);
        buttons.add(reset);
        buttons.add(label);

        super.setLayout(new BorderLayout());
        super.add(BorderLayout.CENTER, site);
        super.add(BorderLayout.SOUTH, buttons);
        super.setVisible(true);
        gui.repaintFrame();
    }

    public static void reBlockSites() {
        String[] data = readData(newHosts);
        writeData(data, hostsFile);
    }

    public static void unBlockSites() {
        String[] data = readData(backupFile);
        writeData(data, hostsFile);
    }

    private String load() {
        String result = "";
        String[] data = readData(blockedSites);
        if (data.length > 1) {
            for (int i = 0; i < data.length; i++) {
                result += data[i] + "\n";
            }
        }
        return result;
    }

    private void blockSite(JTextArea site) {
        String[] sites = site.getText().split("\\r?\\n");
        String[] host = readData(backupFile);
        String[] data = new String[sites.length + host.length];
        int j = 0;
        for (int i = 0; i < host.length; i++) {
            data[j] = host[i];
            j++;
        }
        for (int i = 0; i < sites.length; i++) {
            if (sites[i].contains("www")) {
                data[j] = "127.0.0.1    " + sites[i];
            }
            else {
                data[j] = "127.0.0.1    www." + sites[i];
            }
            j++;
        }
        writeData(sites, blockedSites);
        writeData(data, newHosts);
    }

    private void reset(JTextArea area) {
        String[] data = readData(backupFile);
        writeData(data, hostsFile);
        writeData(data, newHosts);
        writeData("", blockedSites);
        area.setText("");
    }

    public static String[] readData(File file) {
        String[] result = new String[0];
        try {
            result = new String[(int)Files.lines(file.toPath()).count()];
            Scanner scanner = new Scanner(file);
            int index = 0;
            while (scanner.hasNextLine()) {
                result[index] = scanner.nextLine();
                index++;
            }
            scanner.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void writeData(String data, File file) {
        try  {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeData(String[] dataArr, File file) {
        String data = "";
        for (int i = 0; i < dataArr.length; i++) {
            data += (dataArr[i] + "\n");
        }
        writeData(data, file);
    }
}
