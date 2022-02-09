package com.productivity.Custom;

import com.productivity.CheckBoxes;
import com.productivity.gui;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JTabbedPane;

public class CustomCheckList extends JTabbedPane {
    
    static HashMap<String, CheckBoxes> boxes = new HashMap<String, CheckBoxes>();
    private boolean dragging = false;
    private Image tabImage = null;
    private Point currentMouseLocation = null;
    private int draggedTabIndex = 0;
    
    public CustomCheckList() {
        super();
        super.setFocusable(false);
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                
                if(!dragging) {
                    // Gets the tab index based on the mouse position
                    int tabNumber = getUI().tabForCoordinate(CustomCheckList.this, e.getX(), e.getY());
                    
                    if(tabNumber >= 0) {
                        draggedTabIndex = tabNumber;
                        Rectangle bounds = getUI().getTabBounds(CustomCheckList.this, tabNumber);
                        
                        
                        // Paint the tabbed pane to a buffer
                        Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics totalGraphics = totalImage.getGraphics();
                        totalGraphics.setClip(bounds);
                        // Don't be double buffered when painting to a static image.
                        setDoubleBuffered(false);
                        paintComponent(totalGraphics);
                        
                        // Paint just the dragged tab to the buffer
                        tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
                        Graphics graphics = tabImage.getGraphics();
                        graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y+bounds.height, CustomCheckList.this);
                        
                        dragging = true;
                        repaint();
                    }
                } else {
                    currentMouseLocation = e.getPoint();
                    
                    // Need to repaint
                    repaint();
                }
                super.mouseDragged(e);
            }
        });
        
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                
                if(dragging) {
                    int tabNumber = getUI().tabForCoordinate(CustomCheckList.this, e.getX(), 10);
                    
                    if(tabNumber >= 0) {
                        Component comp = getComponentAt(draggedTabIndex);
                        String title = getTitleAt(draggedTabIndex);
                        removeTabAt(draggedTabIndex);
                        insertTab(title, null, comp, null, tabNumber);
                    }
                }
                
                dragging = false;
                tabImage = null;
            }
        });
    }
    
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
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Are we dragging?
        if(dragging && currentMouseLocation != null && tabImage != null) {
            // Draw the dragged tab
            g.drawImage(tabImage, currentMouseLocation.x, currentMouseLocation.y, this);
        }
    }
}
