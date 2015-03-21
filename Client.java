//package ikasoft;

//package MiniChat;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JLabel;

import javax.swing.JOptionPane;

import javax.swing.JPanel;

import javax.swing.JPasswordField;

import javax.swing.JScrollPane;

import javax.swing.JTabbedPane;

import javax.swing.JTextField;

import javax.swing.JTextPane;

class Liste_Client implements Runnable {

    private String login = null;
    private JButton b = null;
    private JTextPane p = null;
    private Thread t = null;
    private Socket s = null;
    private int port = 0;
    //private ObjectInputStream in;
    private BufferedReader in = null;
    //private ObjectOutputStream out;
    private PrintWriter out = null;

    JButton getButton() {

        return b;

    }

    JTextPane getTextPane() {

        return p;

    }

    Socket getSocket() {

        return s;

    }

    String getLogin() {

        return login;

    }

    int getPort() {

        return port;

    }

    void setLogin(String s) {

        login = s;

    }

    /* Server. */
    public Liste_Client(int port) {

        try {

            ServerSocket ss = new ServerSocket(port);

            while (true) {

                s = ss.accept();

                System.out.println("Adresse du Client |" + s.getLocalSocketAddress());

                init();

            }

        } catch (Exception e) {

            System.out.println(e);

        }

    }

    /* Client */
    public Liste_Client(String login, int port) {

        this.login = login;

        this.port = port;



        try {

            s = new Socket("127.0.0.1", this.port);

            System.out.println("Adresse du Client |" + s.getLocalSocketAddress());

        } catch (Exception e) {

            e.printStackTrace();

        }



        init();



        this.b = new JButton(port + "|" + login);

        this.p = new JTextPane();

    }

    /* Client. */

    /*public Liste_Client(String address, int port) {

    try {

    s = new Socket(address, port);

    } catch (Exception e) {

    System.out.println(e);

    }

    System.out.println("Adresse du Serveur |" + s.getRemoteSocketAddress());

    init();

    }*/
    private void init() {

        try {

            //this.out = new ObjectOutputStream(s.getOutputStream());
            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

            //this.in = new ObjectInputStream(s.getInputStream());
            this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            this.t = new Thread(this);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    void start() {

        this.t.start();

    }

    public void send(String message) {

        try {

            //out.writeObject(this.port + "|" + message);
            out.print(this.port + "|" + message);

            out.flush();

        } catch (Exception e) {

            //e.printStackTrace();
            System.out.println(e);

        }

    }

    public void close() {

        try {

            s.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void run() {

        String now = "";

        try {

            while (true) {

                //String mess = (String) in.readObject();//size buffer 8192
                String mess = in.readLine();//size buffer 8192



                /* recuperation du client qui envoie le message */

                if (mess == null) {

                    this.close();

                    System.exit(0);

                }

                now = "[" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND) + "]";

                this.getTextPane().getText().concat("\n *" + now + "  :" + mess);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}

public class Client extends JFrame implements ActionListener {

    public final static int MAX = 20;
    Socket Sserveur = null;
    EcouteurIn in = null;
    EcouteurOut out = null;
    Liste_Client moi = null;
    int nbClient = 0;
    Liste_Client LC[] = new Liste_Client[MAX];
    JTextField tf = new JTextField(30);
    JTabbedPane tabp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JScrollPane sp = new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel pc[] = new JPanel[2];
    JTextField tc[] = new JTextField[2];
    JPasswordField tpf[] = new JPasswordField[2];
    JButton bc[] = new JButton[4];

    public static void main(String args[]) {

        try {



            Client c = new Client("127.0.0.1", 12345);



        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    Client(String adresse, int port) throws IOException {

        init();

        this.Sserveur = new Socket(adresse, port);
        toVoid(null, this.Sserveur);



        in = new EcouteurIn(this.Sserveur.getInputStream());

        out = new EcouteurOut(this.Sserveur.getOutputStream());



        /* Ecouteur Out du client est lance */

        in.start();
        out.start();

    }

    public void toVoid(ServerSocket ss, Socket s) {
        String x = "";
        if (ss != null) {
            x += "\t\tSERVEUR :: port :" + ss.getLocalPort() + "\n";
        }
        if (s != null) {
            x += "\t\tCLIENT :: port :" + s.getLocalPort() + "\n";
        }
        System.out.println(x);
    }

    void init() {

        this.setTitle("XChat");

        this.setSize(500, 700);

        this.setVisible(true);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setLayout(new FlowLayout(FlowLayout.CENTER));



        for (int i = 0; i < 2; i++) {

            pc[i] = new JPanel(new FlowLayout(FlowLayout.CENTER));



            if (i == 0) {

                pc[i].setBorder(BorderFactory.createTitledBorder(" Inscription "));



                bc[i] = new JButton(" Inscription ");

                bc[i].setEnabled(true);

            }

            if (i == 1) {

                pc[i].setBorder(BorderFactory.createTitledBorder(" Connexion "));



                bc[i] = new JButton(" Connexion ");

                bc[i].setEnabled(true);
                //bc[i].setEnabled(false);

            }

            pc[i].add(new JLabel("Login"));

            tc[i] = new JTextField("", 10);

            pc[i].add(tc[i]);

            pc[i].add(new JLabel("Mot de passe"));

            tpf[i] = new JPasswordField("", 10);

            pc[i].add(tpf[i]);

            bc[i].addActionListener(this);

            pc[i].add(bc[i]);



            pc[i].setPreferredSize(new Dimension(130, 160));

            this.getContentPane().add(pc[i]);

        }



        this.getContentPane().add(tabp);

        this.getContentPane().add(tf);

        this.getContentPane().add(sp);



        tf.setFont(new Font("Microsoft Sans Serif", Font.ROMAN_BASELINE, 16));



        tabp.setPreferredSize(new Dimension(420, 300));

        sp.setPreferredSize(new Dimension(420, 150));

        //p.setPreferredSize(new Dimension(390, 200));



        tf.addActionListener(this);

    }

    void addClient(Liste_Client c) {

        if (moi == null) {

            moi = new Liste_Client(c.getPort());

            moi.setLogin(c.getLogin());

            moi.start();

        }

        LC[nbClient] = c;

        nbClient++;

        tabp.addTab(c.getLogin(), c.getTextPane());

        LC[nbClient].getButton().addActionListener(this);

        p.add(LC[nbClient].getButton());



        /* Ecouteur Out du client est lance */

        out.start();

    }

    void removeClient(Liste_Client c) {

        for (int i = 0; i < nbClient; i++) {

            if (LC[i] == c) {

                tabp.removeTabAt(i);

            }

            LC[i].getButton().removeActionListener(null);

            p.remove(LC[i].getButton());

        }

    }

    public void actionPerformed(ActionEvent e) {

        /* Envoyer un message a un client */

        if (e.getSource() == this.tf) {

            if (!LC[tabp.getSelectedIndex()].getTextPane().getText().equals("")) {

                LC[tabp.getSelectedIndex()].getTextPane().getText().concat("\n *" + tf.getText());

            } else {

                LC[tabp.getSelectedIndex()].getTextPane().setText(" *" + tf.getText());

            }



            /* Un message envoye */

            LC[tabp.getSelectedIndex()].send(tf.getText());

            tf.setText("");

        }



        /* Selection d'un client */

        if (e.getSource() instanceof JButton && e.getSource() != bc[0] && e.getSource() != bc[1]) {

            for (int i = 0; i < nbClient; i++) {

                if (e.getSource() == LC[i].getButton()) {

                    tabp.setSelectedIndex(i);

                }

            }

        }



        /* Inscription au serveur */

        if (e.getSource() == bc[0]) {
            /*System.out.println("inscription|" + tc[0].getText() + "|" + tpf[0].getPassword());

            out.send("inscription|" + tc[0].getText() + "|" + tpf[0].getPassword());
            System.out.println("inscription|" + tc[0].getText() + "|" + tpf[0].getPassword());*/
            System.out.println("inscription|" + tc[0].getText() + "|" + tpf[0].getText());

            out.send("inscription|" + tc[0].getText() + "|" + tpf[0].getText());
            System.out.println("inscription|" + tc[0].getText() + "|" + tpf[0].getText());

        }



        /* Connexion au serveur */

        if (e.getSource() == bc[1]) {

            out.send("connexion|" + tc[1].getText() + "|" + tpf[1].getPassword());

        }

    }

    class EcouteurIn implements Runnable {

        //private ObjectInputStream in = null;
        private BufferedReader in = null;
        private Thread t = null;

        public EcouteurIn(InputStream s) {

            try {

                //this.in = new ObjectInputStream(s);
                this.in = new BufferedReader(new InputStreamReader(s));

            } catch (Exception e) {

                e.printStackTrace();

            }

            t = new Thread(this);

        }

        /*ObjectInputStream*/
        BufferedReader getIn() {

            return in;

        }

        public void start() {

            t.start();

        }

        public void stop() {

            t.stop();

        }

        public void run() {

            String msg = "";



            while (true) {

                try {

                    //msg = (String) in.readObject();
                    /*msg = in.readLine();*/
					msg = in.readLine(); /* manque de \n dans les message recu  */



                    traiter_requete(msg);

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        }

        public void traiter_requete(String msg) {

            String p[] = msg.split("\\|");

            System.out.println(String.format("msg = [%s] ==> [%s] [%s] [%s]", msg, p[0], p[1], p[2]));



            /* inscription */

            /* msg  inscription|ok|---- or inscription|login_existant|----*/

            if (p[0].equals("inscription")) {

                if (p[1].equals("ok")) {

                    bc[0].setEnabled(false);

                    bc[1].setEnabled(true);



                }

                if (p[1].equals("login_existant")) {

                    JOptionPane.showMessageDialog(null, "login existant", "Erreur", JOptionPane.ERROR_MESSAGE);

                }

            }



            /* connexion */

            /* msg  connexion|ok|---- or connexion|login_pwd_faux|----*/

            if (p[0].equals("connexion")) {

                if (p[0].equals("ok")) {
                    bc[0].setEnabled(false);

                    bc[1].setEnabled(false);

                    addClient(new Liste_Client(p[1], Integer.parseInt(p[2])));

                }

                if (p[1].equals("login_pwd_faux")) {

                    JOptionPane.showMessageDialog(null, "login ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);

                }

            }



            /* update */

            /* msg  update_LCC|debut|----*/

            /* msg  update_LCC|entier|Port|Login*/

            /* msg  update_LCC|fin|----*/

            if (p[0].equals("update_LCC")) {

                String s1[] = null, s2[] = null;

                int x = 0;

                if (p[1].equals("debut")) {

                    s1 = new String[Integer.parseInt(p[2])];

                    s2 = new String[Integer.parseInt(p[2])];

                }



                if (p[1].equals("fin")) {

                    /* verification du liste*/
                } else {

                    s1[x] = p[1];

                    s2[x] = p[2];

                    x++;

                }

            }

        }
    }

    class EcouteurOut implements Runnable {

        //private ObjectOutputStream out = null;
        private PrintWriter out = null;
        private Thread t = null;

        public EcouteurOut(OutputStream s) {

            try {

                //this.out = new ObjectOutputStream(s);
                this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s)), true);

            } catch (Exception e) {

                e.printStackTrace();

            }

            t = new Thread(this);

        }

        /*ObjectOutputStream*/
        PrintWriter getOut() {

            return out;

        }

        void send(String s) {

            try {

                //out.writeObject(s);
                out.print(s);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        public void run() {

            while (true) {

                try {

                    //out.writeObject("update_LCC|ok|ok");
                    out.println("update_LCC|ok|ok");

                    Thread.sleep(30000);

                } catch (Exception e) {

                    e.printStackTrace();

                } /*catch (InterruptedException e) {

                e.printStackTrace();

                }*/



            }

        }

        public void start() {

            t.start();

        }

        public void stop() {

            t.stop();

        }
    }
}
