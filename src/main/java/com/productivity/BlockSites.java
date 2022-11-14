package com.productivity;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.productivity.Util.JTextFieldLimit;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.miginfocom.swing.MigLayout;

public class BlockSites extends JPanel {
    
    private static final File kHostsFile = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
    
    private static File mNewHosts = Productivity.getSave("Saves/Newhosts");
    private static File mBackupFile = Productivity.getSave("Saves/hosts");
    private static File mBlockedSites = Productivity.getSave("Saves/blockedSites.TXT");
    
    public BlockSites() {
        JTextArea site = new JTextArea();
        site.setDocument(new JTextFieldLimit(50));
        site.setText(load());
        
        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> {
            blockSite(site);
            site.setText(load());
        });
        
        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> reset(site));
        
        JLabel label = new JLabel("You will need to run program as admin for this feature to work");
        JLabel info = new JLabel("Type websites here then press (EX: youtube.com) \"Apply\"");
        
        super.setLayout(new MigLayout((Productivity.kMigDebug?", debug":"")));
        super.add(info, "wrap");
        super.add(site, "grow, push, span, wrap");
        super.add(label, "align left, wrap");
        super.add(apply, "split 2");
        super.add(reset, "");
    }
    
    public static void reBlockSites() {
        if (Files.isWritable(Paths.get(kHostsFile.getAbsolutePath()))) {
            try {
                Files.copy(mNewHosts.toPath(), kHostsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed blocking sites", "Warning", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                try {
                    Files.copy(mBackupFile.toPath(), mNewHosts.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed resetting local hosts file", "Warning", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }
        else {
            JOptionPane.showMessageDialog(Productivity.getInstance() , "Run as administrator to use blocking");
        }
    }
    
    public static void unBlockSites() {
        if (Files.isWritable(Paths.get(kHostsFile.getAbsolutePath()))) {
            try {
                Files.copy(mBackupFile.toPath(), kHostsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed unblocking sites", "Warning", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private String load() {
        String result = "";
        try {
            String[] data = readData(mBlockedSites);
            for (int i = 0; i < data.length; i++) {
                if (data[i].equals("*")) {
                    continue;
                }
                result += data[i] + "\n";
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed loading blocked sites", "Warning", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed verifying site is valid", "Warning", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    private void blockSite(JTextArea site) {
        String[] sites = site.getText().split("\\r?\\n");
        String[] validSites = new String[sites.length];
        String regex = "^((https?|ftp|smtp):\\/\\/)?(www.)?[a-z0-9]+\\.[a-z]+(\\/[a-zA-Z0-9#]+\\/?|\\/)*$";
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
        try {
            if (Files.isWritable(Paths.get(kHostsFile.getAbsolutePath()))) {
                Files.copy(mBackupFile.toPath(), kHostsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            Files.copy(mBackupFile.toPath(), mNewHosts.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed resetting local hosts file", "Warning", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
            JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed reading data in BlockSites", "Warning", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(Productivity.getInstance(), "Failed writing data in BlockSites", "Warning", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private static void writeData(String[] dataArr, File file) {
        String data = String.join("\n", dataArr);
        writeData(data, file);
    }
}
