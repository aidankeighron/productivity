package com.productivity.Custom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;


public class CheckableItem implements ActionListener {
    
  private JPopupMenu menu;
  private JButton button;
  
  CheckableItem(JPopupMenu menu, JButton button) {
    this.menu = menu;
    this.button = button;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    menu.show(button, 0, button.getHeight());
  }
}