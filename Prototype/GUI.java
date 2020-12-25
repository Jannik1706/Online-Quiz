import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 18.12.2020
 * @author 
 */

public class GUI extends JFrame {
  // Anfang Attribute
  private JButton jButton1 = new JButton();
  private JButton jButton2 = new JButton();
  private JButton jButton3 = new JButton();
  private JButton jButton4 = new JButton();
  private JLabel jLabel1 = new JLabel();
  private QuizServer s=new QuizServer(5000);
  private QuizClient c=new QuizClient("127.0.0.1", 5000, this);
  private String richtigeAntwort=new String();
  private int richtig=0;
  private int fragen=0;
  
  private JButton jButton5 = new JButton();
  // Ende Attribute
  
  public GUI() { 
    // Frame-Initialisierung
    super();
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    int frameWidth = 300; 
    int frameHeight = 297;
    setSize(frameWidth, frameHeight);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (d.width - getSize().width) / 2;
    int y = (d.height - getSize().height) / 2;
    setLocation(x, y);
    setTitle("GUI");
    setResizable(false);
    Container cp = getContentPane();
    cp.setLayout(null);
    // Anfang Komponenten
    
    jButton1.setBounds(48, 96, 81, 25);
    jButton1.setText("jButton1");
    jButton1.setMargin(new Insets(2, 2, 2, 2));
    jButton1.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        jButton1_ActionPerformed(evt);
      }
    });
    cp.add(jButton1);
    jButton2.setBounds(144, 96, 81, 25);
    jButton2.setText("jButton2");
    jButton2.setMargin(new Insets(2, 2, 2, 2));
    jButton2.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        jButton2_ActionPerformed(evt);
      }
    });
    cp.add(jButton2);
    jButton3.setBounds(48, 128, 81, 25);
    jButton3.setText("jButton3");
    jButton3.setMargin(new Insets(2, 2, 2, 2));
    jButton3.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        jButton3_ActionPerformed(evt);
      }
    });
    cp.add(jButton3);
    jButton4.setBounds(144, 128, 81, 25);
    jButton4.setText("jButton4");
    jButton4.setMargin(new Insets(2, 2, 2, 2));
    jButton4.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        jButton4_ActionPerformed(evt);
      }
    });
    cp.add(jButton4);
    jLabel1.setBounds(48, 64, 177, 25);
    jLabel1.setText("text");
    cp.add(jLabel1);
    jButton5.setBounds(48, 176, 177, 17);
    jButton5.setText("jButton5");
    jButton5.setMargin(new Insets(2, 2, 2, 2));
    jButton5.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        jButton5_ActionPerformed(evt);
      }
    });
    cp.add(jButton5);
    // Ende Komponenten
    
    setVisible(true);
  } // end of public GUI
  // Anfang Methoden
  
  public static void main(String[] args) {
    new GUI();
  } // end of main
  public void frageAnzeigen(String[] pArray){
    if (pArray==null) {
      jLabel1.setText(richtig+"/"+fragen);
      return;
    }
    jButton1.setLabel(pArray[1]);
    jButton2.setLabel(pArray[2]);
    jButton3.setLabel(pArray[3]);
    jButton4.setLabel(pArray[4]);
    jLabel1.setText(pArray[0]);
    richtigeAntwort=pArray[5];
    fragen++;
    System.out.println(richtigeAntwort);
  }
  public void jButton1_ActionPerformed(ActionEvent evt) {
    System.out.println(jButton1.getText());
    if (jButton1.getText().equals(richtigeAntwort)) {
      richtig++;
    }
    c.frageLaden();
  } // end of jButton1_ActionPerformed

  public void jButton2_ActionPerformed(ActionEvent evt) {
    if (jButton2.getText().equals(richtigeAntwort)) {
      richtig++;
    }
    c.frageLaden();
  } // end of jButton2_ActionPerformed

  public void jButton3_ActionPerformed(ActionEvent evt) {
    if (jButton3.getText().equals(richtigeAntwort)) {
      richtig++;
    }
    c.frageLaden();
  } // end of jButton3_ActionPerformed

  public void jButton4_ActionPerformed(ActionEvent evt) {
    if (jButton4.getText().equals(richtigeAntwort)) {
      richtig++;
    }
    c.frageLaden();
  } // end of jButton4_ActionPerformed

  public void jButton5_ActionPerformed(ActionEvent evt) {
    c.frageAnfordern();
    richtig=0;
    fragen=0;
  }
}

