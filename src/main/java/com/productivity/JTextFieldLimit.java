package com.productivity;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class JTextFieldLimit extends PlainDocument {
  
  private int mLimit;
  
  public JTextFieldLimit(int limit) {
    super();
    this.mLimit = limit;
  }
  
  public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
    if (str == null) return;
    
    if ((getLength() + str.length()) <= mLimit) {
      super.insertString(offset, str, attr);
    }
  }
}
