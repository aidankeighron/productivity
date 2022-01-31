package com.productivity.Custom;

import java.util.*;
import javax.swing.*;
import com.productivity.CheckBoxes;
import com.productivity.gui;

public class customCheckList extends JTabbedPane {
    
    static HashMap<String, CheckBoxes> boxes = new HashMap<String, CheckBoxes>();
    
    public customCheckList() {}
    
    public void addCheckList(CheckBoxes checkBoxes, String name) {
        super.addTab(name, checkBoxes);
        boxes.put(name, checkBoxes);
        gui.repaintFrame();
    }
    
    public void removeChecklist(String name) {
        super.remove(boxes.get(name));
        boxes.remove(name);
        gui.repaintFrame();
    }
}
