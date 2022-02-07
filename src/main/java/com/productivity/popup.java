package com.productivity;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

public class Popup {

    JMenuItem[] items;

    public Popup(JMenuItem[] items) {
        this.items = items;
    }
    
    class PopUpDemo extends JPopupMenu {
        public PopUpDemo() {
            for (int i = 0; i < items.length; i++) {
                add(items[i]);
            }
        }
    }

    class PopClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger())
                doPop(e);
        }
    
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger())
                doPop(e);
        }
    
        private void doPop(MouseEvent e) {
            PopUpDemo menu = new PopUpDemo();
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
