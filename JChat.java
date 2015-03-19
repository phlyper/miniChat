//package ikasoft;
//package MiniChat;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.Timer;
class Liste_Client implements ActionListener {
private int x = 0;
private String login = null;
private JButton b = null;
private JTextPane p = null;
private Timer t = null;
public Liste_Client(String s, int x) {
   this.login = (x + 1) + ": " + s;
   b = new JButton(this.login);
   p = new JTextPane();
   t = new Timer(800 + (x * 100), this);
}
JButton getButton() {
   return this.b;
}
JTextPane getTextPane() {
   return this.p;
}
String getLogin() {
   return this.login;
}
Timer getTimer() {
   return this.t;
}
public void start() {
   this.t.start();
}
public void stop() {
   this.t.stop();
}
void setLogin(String s) {
   this.login = s;
}
public void actionPerformed(ActionEvent e) {
   if (e.getSource() == t) {
       this.getTextPane().setText(this.getTextPane().getText() + "\n" + x + "::java");
       x++;
       this.getButton().setForeground(Color.RED);
       this.getButton().setText(this.login + "::" + x);
   }
}
}
public class JChat extends JFrame implements ActionListener {
public final static int MAX = 20;
Liste_Client moi = null;
int nbClient = 0;
Liste_Client LC[] = new Liste_Client[MAX];
JTextField tf = new JTextField(30);
JTabbedPane tabp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
JChat() {
    this.init();
    nbClient = MAX;
    String login[] = {"aaa", "bbb", "ccc", "ddd", "eee", "fff", "ggg", "hhh", "lll", "jjj"};
    for (int i = 0; i < nbClient; i++) {
        LC[i] = new Liste_Client(login[i % login.length], i);
        tabp.addTab(i + "::" + LC[i].getLogin(), new JScrollPane(LC[i].getTextPane(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        LC[i].getButton().addActionListener(this);
        p.add(LC[i].getButton());
        LC[i].start();
    }
}
void init() {
    this.setTitle("JChat");
    this.setSize(500, 700);
    this.setVisible(true);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setLayout(new FlowLayout(FlowLayout.CENTER));

    this.getContentPane().add(tabp);
    this.getContentPane().add(tf);
    this.getContentPane().add(p);

    tf.setFont(new Font("Microsoft Sans Serif", Font.ROMAN_BASELINE, 18));

    tabp.setPreferredSize(new Dimension(420, 300));
    p.setPreferredSize(new Dimension(420, 200));
    p.setBorder(BorderFactory.createTitledBorder(" Les Clients "));

    tf.addActionListener(this);
    tf.requestFocus();
}
public void actionPerformed(ActionEvent e) {
    /** Envoyer un message a un client */
    if (e.getSource() == this.tf) {
        String now = "[" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND) + "]";
        LC[tabp.getSelectedIndex()].getTextPane().setText(LC[tabp.getSelectedIndex()].getTextPane().getText() + "\nPHLYPER a dit " + now + " :\n *" + tf.getText());
        tf.setText("");
    }

    /** Selection d'un client */
    if (e.getSource() instanceof JButton) {
        for (int i = 0; i < nbClient; i++) {
            if (e.getSource() == LC[i].getButton()) {
                tabp.setSelectedIndex(i);
                LC[tabp.getSelectedIndex()].getButton().setForeground(Color.BLUE);
                LC[tabp.getSelectedIndex()].getButton().setBackground(Color.GRAY);
            } else {
                LC[i].getButton().setBackground(null);
            }
        }
    }
}
public static void main(String args[]) {
    JChat c = new JChat();

}
}
