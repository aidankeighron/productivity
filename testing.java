import javax.swing.*;
import java.awt.*;
import java.util.*;
class testing
{
  public void buildGUI()
  {
    int year = Integer.parseInt(JOptionPane.showInputDialog(null,"Enter year"));
    int month = Integer.parseInt(JOptionPane.showInputDialog(null,"Enter month (1 - 12)"));
    JFrame f = new JFrame();
    f.getContentPane().add(new CalendarPanel(year,month));
    f.pack();
    f.setLocationRelativeTo(null);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }
  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        new testing().buildGUI();
      }
    });
  }
}
class CalendarPanel extends JPanel
{
  public CalendarPanel(int year, int month)
  {
    String[] headers = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    setLayout(new GridLayout(2,7));
    GregorianCalendar gc = new GregorianCalendar(year,month-1,1);
    int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
    for(int x = 0,y = headers.length; x < y; x++) add(new JLabel(headers[x],JLabel.CENTER));
    for(int x = 1; x <= 7; x++)
    {
      if(x == dayOfWeek) add(new JLabel("1",JLabel.CENTER));
      else add(new JLabel(""));
    }
  }
}