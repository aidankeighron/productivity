import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class timer extends JPanel {
    
    public final static int ONE_SECOND = 1000;
    JPanel progresrBarsPanel = new JPanel();
    Box progresrBars = Box.createVerticalBox();
    JPanel namesPanel = new JPanel();
    Box names = Box.createVerticalBox();
    ArrayList<JProgressBar> bars = new ArrayList<JProgressBar>();
    ArrayList<JButton> buttons = new ArrayList<JButton>();
    String[] timeOptions = {"Seconds", "Minutes", "Hours"};
    static int timeMuitplyer = 1;
    
    public timer() {
        JLabel timeLbl = new JLabel("Length:");
        JTextField timeField = new JTextField();
        JLabel nameLbl = new JLabel("Name:");
        JTextField nameFeild = new JTextField();
        nameFeild.addActionListener(e -> {
            addProgressBar(nameFeild.getText(), Integer.parseInt(timeField.getText()));
            timeField.setText("");
            nameFeild.setText("");
        });
        timeField.addActionListener(e -> {
            addProgressBar(nameFeild.getText(), Integer.parseInt(timeField.getText()));
            timeField.setText("");
            nameFeild.setText("");
        });
        JButton addBtn = new JButton("           Add           ");
        addBtn.addActionListener(e -> {
            boolean notInt = false;
            try {
                Integer.parseInt(timeField.getText());
                if (Integer.parseInt(timeField.getText()) <= 0) {
                    notInt = true;
                }
            } catch (Exception ex) {
                notInt = true;
            }
            if (timeField.getText().equals("") || notInt) {
                JOptionPane.showMessageDialog(this, "Enter vaild positive time");
            }
            else {
                addProgressBar(nameFeild.getText(), Integer.parseInt(timeField.getText()));
                timeField.setText("");
                nameFeild.setText("");
            }
        });
        JComboBox<String> timeList = new JComboBox<>(timeOptions);
        timeList.addActionListener(e -> {
            switch(timeList.getSelectedIndex()) {
                case 0:
                timeMuitplyer = 1;
                break;
                case 1:
                timeMuitplyer = 60;
                break;
                case 2:
                timeMuitplyer = 60 * 60;
                break;
                default:
                timeMuitplyer = 1;
                break;
            }
        });
        
        JPanel config = new JPanel();
        Box vertical = Box.createVerticalBox();
        Box time = Box.createHorizontalBox();
        time.add(timeLbl);
        time.add(timeField);
        Box name = Box.createHorizontalBox();
        name.add(nameLbl);
        name.add(nameFeild);
        Box button = Box.createHorizontalBox();
        button.add(addBtn);
        addBlank(vertical, 2);
        vertical.add(timeList);
        vertical.add(time);
        vertical.add(name);
        vertical.add(button);
        config.add(vertical);
        
        progresrBarsPanel.add(progresrBars);
        namesPanel.add(names);
        
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.EAST, config);
        super.add(BorderLayout.CENTER, namesPanel);
        super.add(BorderLayout.WEST, progresrBarsPanel);
        super.setVisible(true);
    }
    
    public void addProgressBar(String name, int length) {
        JProgressBar progressBar = new JProgressBar(0, length * timeMuitplyer);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        String title = (name.equals("")) ? Integer.toString(length) : name;
        JButton button = new JButton(title);
        
        Timer time = new Timer();
        TimerTask task = new TimerTask()
        {
            int seconds = length * timeMuitplyer;
            int i = 0;
            @Override
            public void run()
            {
                if(i == seconds && i != 0) {
                    progressBar.setValue(i);
                    Toolkit.getDefaultToolkit().beep();
                    i++;
                    time.cancel();
                    time.purge();
                }
                else {
                    progressBar.setValue(i);
                }
                if (i < seconds) {
                    i++;
                }
            }
        };
        time.schedule(task, 0, 1000);
        
        button.addActionListener(e -> {
            removeProgressBar(button, progressBar, task, time);
        });
        
        bars.add(progressBar);
        buttons.add(button);
        names.add(button);
        Border border = BorderFactory.createEmptyBorder(4, 0, 0, 0);
        progresrBars.setBorder(border);
        progresrBars.add(progressBar);
        progresrBars.add(Box.createRigidArea(new Dimension(0, 4)));
        gui.repaintFrame();
    }
    
    public void removeProgressBar(JButton button, JProgressBar progressBar, TimerTask task, Timer time) {
        task.cancel();
        time.cancel();
        time.purge();
        progresrBars.remove(progressBar);
        names.remove(button);
        bars.remove(progressBar);
        buttons.remove(button);
        gui.repaintFrame();
    }
    
    public void addBlank(JPanel panel, int ammount) {
        for (int i = 0; i < ammount; i++) {
            JLabel blank = new JLabel();
            panel.add(blank);
        }
    }
    
    public void addBlank(Box panel, int ammount) {
        for (int i = 0; i < ammount; i++) {
            JLabel blank = new JLabel();
            panel.add(blank);
        }
    }
}
