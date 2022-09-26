package com.productivity.Custom;

import com.productivity.CheckBoxes;
import com.productivity.Productivity;

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
    
    private static HashMap<String, CheckBoxes> mBoxes = new HashMap<String, CheckBoxes>();
    private boolean mDragging = false;
    private Image mTabImage = null;
    private Point mCurrentMouseLocation = null;
    private int mDraggedTabIndex = 0;
    
    public CustomCheckList() {
        super();
        super.setFocusable(false);
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                
                if(!mDragging) {
                    // Gets the tab index based on the mouse position
                    int tabNumber = getUI().tabForCoordinate(CustomCheckList.this, e.getX(), e.getY());
                    
                    if(tabNumber >= 0) {
                        mDraggedTabIndex = tabNumber;
                        Rectangle bounds = getUI().getTabBounds(CustomCheckList.this, tabNumber);
                        
                        
                        // Paint the tabbed pane to a buffer
                        Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics totalGraphics = totalImage.getGraphics();
                        totalGraphics.setClip(bounds);
                        // Don't be double buffered when painting to a static image.
                        setDoubleBuffered(false);
                        paintComponent(totalGraphics);
                        
                        // Paint just the dragged tab to the buffer
                        mTabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
                        Graphics graphics = mTabImage.getGraphics();
                        graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y+bounds.height, CustomCheckList.this);
                        
                        mDragging = true;
                        repaint();
                    }
                } else {
                    mCurrentMouseLocation = e.getPoint();
                    
                    // Need to repaint
                    repaint();
                }
                super.mouseDragged(e);
            }
        });
        
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                
                if(mDragging) {
                    int tabNumber = getUI().tabForCoordinate(CustomCheckList.this, e.getX(), 10);
                    
                    if(tabNumber >= 0) {
                        Component comp = getComponentAt(mDraggedTabIndex);
                        String title = getTitleAt(mDraggedTabIndex);
                        removeTabAt(mDraggedTabIndex);
                        insertTab(title, null, comp, null, tabNumber);
                    }
                }
                
                mDragging = false;
                mTabImage = null;
            }
        });
    }
    
    public void addCheckList(CheckBoxes checkBoxes, String name) {
        super.addTab(name, checkBoxes);
        mBoxes.put(name, checkBoxes);
        Productivity.getInstance().repaintFrame();
    }
    
    public void removeChecklist(String name) {
        super.remove(mBoxes.get(name));
        mBoxes.remove(name);
        Productivity.getInstance().repaintFrame();
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Are we dragging?
        if(mDragging && mCurrentMouseLocation != null && mTabImage != null) {
            // Draw the dragged tab
            g.drawImage(mTabImage, mCurrentMouseLocation.x, mCurrentMouseLocation.y, this);
        }
    }
    
    private static CustomCheckList mInstance = null;
    public synchronized static CustomCheckList getInstance() {
        if (mInstance == null) {
            mInstance = new CustomCheckList();
        }
        return mInstance;
    }
}
