package com.productivity;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.productivity.Util.JTextFieldLimit;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;

public class BlockSites extends JPanel {
    
    private static final File kHostsFile = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
    
    private static File mNewHosts;
    private static File mBackupFile;
    private static File mBlockedSites;
    
    public BlockSites() {
        mNewHosts = new File(Productivity.getInstance().getCurrentPath()+"Saves\\Newhosts");
        mBackupFile = new File(Productivity.getInstance().getCurrentPath()+"Saves\\hosts");
        mBlockedSites = new File(Productivity.getInstance().getCurrentPath()+"Saves\\blockedSites.TXT");
        JTextArea site = new JTextArea();
        site.setDocument(new JTextFieldLimit(50));
        site.setText(load());
        
        JButton apply = new JButton("Apply");
        apply.setFocusPainted(false);
        apply.addActionListener(e -> {
            blockSite(site);
            site.setText(load());
            Productivity.getInstance().repaintFrame();
        });
        
        JButton reset = new JButton("Reset");
        reset.setFocusPainted(false);
        reset.addActionListener(e -> reset(site));
        
        JLabel label = new JLabel("You will need to run program as admin for this feature to work");
        JLabel info = new JLabel("Type websites here then press \"Apply\"");
        
        Box buttons = Box.createHorizontalBox();
        buttons.add(apply);
        buttons.add(reset);
        Box vertical = Box.createVerticalBox();
        vertical.add(label);
        vertical.add(buttons);
        
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.NORTH, info);
        super.add(BorderLayout.CENTER, site);
        super.add(BorderLayout.SOUTH, vertical);
    }
    
    public static void reBlockSites() {
        if (Files.isWritable(Paths.get(kHostsFile.getAbsolutePath()))) {
            try {
                String[] data = readData(mNewHosts);
                writeData(data, kHostsFile);
            } catch (Exception e) {
                e.printStackTrace();
                String[] data = readData(mBackupFile);
                writeData(data, mNewHosts);
            }
        }
    }
    
    public static void unBlockSites() {
        if (Files.isWritable(Paths.get(kHostsFile.getAbsolutePath()))) {
            try {
                String[] data = readData(mBackupFile);
                writeData(data, kHostsFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
    
    private String load() {
        String result = "";
        try {
            String[] data = readData(mBlockedSites);
            if (data.length >= 1) {
                for (int i = 0; i < data.length; i++) {
                    if (data[i].equals("*")) {
                        continue;
                    }
                    result += data[i] + "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeData("", mBlockedSites);
        }
        
        return result;
    }
    
    private static boolean isMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void blockSite(JTextArea site) {
        String[] sites = site.getText().split("\\r?\\n");
        String[] validSites = new String[sites.length];
        String regex = "^((https?|ftp|smtp):\\/\\/)?(www.)?[a-z0-9]+\\.[a-z]+(\\/[a-zA-Z0-9#]+\\/?)*$";
        int numValidSites = 0;
        for (int i = 0; i < sites.length; i++) {
            if (isMatch(sites[i], regex)) {
                validSites[i] = sites[i];
                numValidSites++;
            }
            else {
                validSites[i] = "*";
            }
        }
        String[] host = readData(mBackupFile);
        String[] data = new String[numValidSites + host.length];
        String[] filteredSites = new String[numValidSites];
        int j = 0;
        for (int i = 0; i < host.length; i++) {
            data[j] = host[i];
            j++;
        }
        int k = 0;
        for (int i = 0; i < validSites.length; i++) {
            if (validSites[i].equals("*")) {
                continue;
            }
            filteredSites[k] = validSites[i];
            k++;
            if (validSites[i].contains("www.")) {
                data[j] = "127.0.0.1    " + validSites[i];
            }
            else {
                data[j] = "127.0.0.1    www." + validSites[i];
            }
            j++;
        }
        writeData(filteredSites, mBlockedSites);
        writeData(data, mNewHosts);
    }
    
    private void reset(JTextArea area) {
        String[] data = readData(mBackupFile);
        if (Files.isWritable(Paths.get(kHostsFile.getAbsolutePath()))) {
            writeData(data, kHostsFile);
        }
        writeData(data, mNewHosts);
        writeData("", mBlockedSites);
        area.setText("");
    }
    
    private static String[] readData(File file) {
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
    
    private static void writeData(String data, File file) {
        try  {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void writeData(String[] dataArr, File file) {
        String data = "";
        for (int i = 0; i < dataArr.length; i++) {
            data += (dataArr[i] + "\n");
        }
        writeData(data, file);
    }
}
