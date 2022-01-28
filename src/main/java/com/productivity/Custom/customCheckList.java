package com.productivity.Custom;

import java.util.*;
import javax.swing.*;
import com.productivity.checkBoxes;
import com.productivity.gui;

public class customCheckList extends JTabbedPane {

    static HashMap<String, checkBoxes> boxes = new HashMap<String, checkBoxes>();

    public customCheckList() {

    }

    public void addCheckList(checkBoxes checkBoxes, String name) {
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
