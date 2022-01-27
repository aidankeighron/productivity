package com.productivity.Custom;

import java.io.*;
import java.util.*;
import javax.swing.*;

import com.productivity.checkBoxes;

import java.nio.file.Files;

public class customCheckList extends JPanel {

    static JTabbedPane pane = new JTabbedPane();

    public customCheckList() {
        pane = addCustomCheckList.loadCheckLists();
        super.add(pane);
    }

    public static void addCheckList(checkBoxes checkBoxes, String name) {
        pane.addTab(name, checkBoxes);
    }
}
