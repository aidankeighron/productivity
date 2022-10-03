package com.productivity.Util;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

public class CustomTabbedUI extends BasicTabbedPaneUI {
    private Color backgroundColor;
    
    public CustomTabbedUI(Color backgroundColor) {
        super();
        this.backgroundColor = backgroundColor;
    }
    
    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
        int width = tabInsets.left + tabInsets.right;// + 3;
        Component tabComponent = tabPane.getTabComponentAt(tabIndex);
        if (tabComponent != null) {
            width += tabComponent.getPreferredSize().width;
        } else {
            Icon icon = getIconForTab(tabIndex);
            if (icon != null) {
                width += icon.getIconWidth() + textIconGap;
            }
            View v = getTextViewForTab(tabIndex);
            if (v != null) {
                // html
                width += (int) v.getPreferredSpan(View.X_AXIS);
            } else {
                // plain text
                AffineTransform affinetransform = new AffineTransform();     
                FontRenderContext frc = new FontRenderContext(affinetransform,true,true);   
                Font font = new Font("Tahoma", Font.PLAIN, 12);
                String title = tabPane.getTitleAt(tabIndex);
                width += (int)(font.getStringBounds(title, frc).getWidth())*.90; //SwingUtilities2.stringWidth(tabPane, metrics, title);
            }
        }
        if (tabPane.getTabCount()-1 == tabIndex) {
            return 60;
        }
        return width;
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return 30;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    @Override
    public void paint(Graphics g, JComponent c) {
        Rectangle bounds = tabPane.getBounds();
        g.setColor(this.backgroundColor);
        g.fillRect(0, 0, bounds.width, bounds.height);
        
        super.paint(g, c); // Call parent...
    }
}