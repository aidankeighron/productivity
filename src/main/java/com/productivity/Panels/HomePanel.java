package com.productivity.Panels;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;

import com.productivity.Productivity;

import net.miginfocom.swing.MigLayout;

public class HomePanel extends JPanel {
    
    private JPanel mCheckPanel = new JPanel(new MigLayout("flowy, gap 0px 0px, ins 0" + (Productivity.kMigDebug?", debug":"")));
    private JPanel mDailyPanel = new JPanel(new MigLayout("flowy, gap 0px 0px, ins 0" + (Productivity.kMigDebug?", debug":"")));
    
    private enum BoxType {
        check,
        daily,
    }
    
    public HomePanel() {
        super.setLayout(new MigLayout("gap 5px 5px, ins 5" + (Productivity.kMigDebug?", debug":"")));
        super.add(mCheckPanel, "split 2, grow, push, span, hmax " + (Productivity.kHeight - Productivity.kTabHeight - 20) + ", wmax " + (Productivity.kWidth-15)/2);
        super.add(mDailyPanel, "grow, push, span, hmax " + (Productivity.kHeight - Productivity.kTabHeight - 20) + ", wmax " + (Productivity.kWidth-15)/2);
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
                reset();
			}
		}
		);
    }
    
    public void reset() {
        mCheckPanel.removeAll();
        mDailyPanel.removeAll();
        
        makePanel(mCheckPanel, Productivity.getInstance().getBoxes(), "Checklist", BoxType.check);
        makePanel(mDailyPanel, DailyChecklist.getCheckBoxes(), "Daily", BoxType.daily);
        
        Productivity.getInstance().repaintFrame(); // reset() is called in multiple places where repainting is needed + home needs to repaint
    }
    
    private JPanel makePanel(JPanel panel, JCheckBox[] boxes, String title, BoxType type) {
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel label = new JLabel(title);
        panel.add(label, "spanx 2, center, pushx");
        if (boxes != null && boxes.length > 0) {
            for (int i = 0; i < boxes.length; i++) {
                JCheckBox checkBox = new JCheckBox(boxes[i].getText());
                checkBox.setSelected(boxes[i].isSelected());
                checkBox.setForeground(boxes[i].getForeground());
                int index = i;
                checkBox.addActionListener(e -> {
                    if (checkBox.isSelected())
                        Productivity.showConfetti();
                    switch (type) {
                        case check:
                        Productivity.getInstance().setSelected(checkBox.isSelected(), index);
                        break;
                        case daily:
                        DailyChecklist.setCheckBoxes(checkBox.isSelected(), index);
                        SettingsPanel.setDailySelected(checkBox.isSelected(), index);
                        break;
                        default:
                        break;
                    }
                });
                int rows = (int)(panel.getHeight() / checkBox.getPreferredSize().getHeight());
                if (rows <= 0) rows = 1;
                panel.add(checkBox, "width "+ panel.getWidth()/2 + ", wmax "+ panel.getWidth()/2 +(((panel.getComponentCount()+1) % rows == 0)?", wrap":""));
            }
        }
        return panel;
    }
    
    private static HomePanel mInstance = null;
    public synchronized static HomePanel getInstance() {
        if (mInstance == null) {
            mInstance = new HomePanel();
        }
        return mInstance;
    }
}
