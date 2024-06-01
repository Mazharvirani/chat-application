import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class node {
    String data;
    node next;

    public node(String data) {
        this.data = data;
        this.next = null;
    }
}

class customLinkedList {
    node head;

    public void add(String data) {
        node newnode = new node(data);
        if (head == null) {
            head = newnode;
        } else {
            node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newnode;
        }
    }

    public void removeLast() {
        if (head == null || head.next == null) {
            head = null;
        } else {
            node temp = head;
            while (temp.next.next != null) {
                temp = temp.next;
            }
            temp.next = null;
        }
    }

    public boolean isEmpty() {
        return head == null;
    }
}

class customStack {
    node top;

    public void push(String data) {
        node newnode = new node(data);
        if (top == null) {
            top = newnode;
        } else {
            newnode.next = top;
            top = newnode;
        }
    }

    public String pop() {
        if (top == null) {
            return null;
        }
        String data = top.data;
        top = top.next;
        return data;
    }

    public boolean isEmpty() {
        return top == null;
    }
}

public class client implements ActionListener {

    static JFrame F = new JFrame();
    JTextField text;
    static JPanel body;
    static Box vertical = Box.createVerticalBox();
    static DataOutputStream output;
    private customLinkedList messageHistory = new customLinkedList();
    private customStack messageStack = new customStack();

    JTextArea chatArea;

    client()
    {
        F.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(155, 122, 211));
        p1.setBounds(0,0,450,65);
        p1.setLayout(null);
        F.add(p1);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(20,20,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5,20,20,20);
        p1.add(back);
        back.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/2.png"));
        Image i5 = i4.getImage().getScaledInstance(50,50,Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(35,10,50,50);
        p1.add(profile);

        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("icons/video.png"));
        Image i8 = i7.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel video = new JLabel(i9);
        video.setBounds(300,20,30,30);
        p1.add(video);

        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("icons/phone.png"));
        Image i11 = i10.getImage().getScaledInstance(35,30,Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel phone = new JLabel(i12);
        phone.setBounds(350,20,35 ,30);
        p1.add(phone);

        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("icons/3icon.png"));
        Image i14 = i13.getImage().getScaledInstance(10,25,Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel icon = new JLabel(i15);
        icon.setBounds(410,20,10,28);
        p1.add(icon);

        JLabel name = new JLabel("STUDENT");
        name.setBounds(100,18,100,18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("ARIAL",Font.BOLD, 18));
        p1.add(name);

        JLabel status = new JLabel("online");
        status.setBounds(100,40,100,18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF",Font.BOLD, 12));
        p1.add(status);

        body = new JPanel();
        body.setBounds(5,65,440,540);
        F.setUndecorated(true);
        F.add(body);

        chatArea = new JTextArea(); // Added chatArea initialization
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(5, 65, 440, 540);
        F.add(scrollPane);

        text = new JTextField();
        text.setBounds(5,610,250,40);
        F.add(text);

        JButton send = new JButton("send");
        send.addActionListener(this);
        send.setBounds(260,610,100,40);
        send.setBackground(new Color(155, 122, 211));
        send.setFont(new Font("SAN_SERIF",Font.BOLD, 12));
        send.setForeground(Color.WHITE);
        F.add(send);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMessage());
        undoButton.setBounds(350, 610, 100, 40);
        undoButton.setBackground(new Color(155, 122, 211));
        undoButton.setFont(new Font("SAN_SERIF", Font.BOLD, 12));
        undoButton.setForeground(Color.WHITE);
        F.add(undoButton);


        F.setSize(450,650);
        F.setLocation(800,50);
        F.getContentPane().setBackground(Color.WHITE);

        F.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String message = text.getText();
            JPanel p2 = chatbox(message);

            body.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            body.add(vertical, BorderLayout.PAGE_START);

            output.writeUTF(message);

            messageHistory.add(message);
            messageStack.push(message);

            text.setText("");

            F.repaint();
            F.invalidate();
            F.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearHistory() {
        messageHistory = new customLinkedList();
        messageStack = new customStack();
    }

    private void undoMessage() {
        if (!messageStack.isEmpty()) {
            messageStack.pop();
            messageHistory.removeLast();
            updateMessageDisplay();
            F.repaint();
            F.invalidate();
            F.validate();
        } else {
            clearDisplay();
        }
    }

    private void clearDisplay() {
        body.removeAll();
        body.revalidate();
        body.repaint();
        F.repaint();
        F.invalidate();
        F.validate();
    }

    private void updateMessageDisplay() {
        body.removeAll();
        node temp = messageHistory.head;
        while (temp != null) {
            JPanel panel = chatbox(temp.data);
            body.add(panel);
            temp = temp.next;
        }
        body.revalidate();
        body.repaint();

        JScrollPane scrollPane = (JScrollPane) body.getParent().getParent();
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());

        F.repaint();
        F.invalidate();
        F.validate();
    }

    public static JPanel chatbox(String message) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel output = new JLabel(message);
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(109, 92, 134));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        box.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("HH:mm");
        JLabel time = new JLabel();
        time.setText(date.format(cal.getTime()));
        box.add(time);

        return box;
    }

    public static void main(String[] args) {
        new client();
        try {
            Socket s = new Socket("127.0.0.1", 6001);
            DataInputStream in = new DataInputStream(s.getInputStream());
            output = new DataOutputStream(s.getOutputStream());

            while (true) {
                body.setLayout(new BorderLayout());
                String msg = in.readUTF();
                JPanel panel = chatbox(msg);

                JPanel left = new JPanel(new BorderLayout());
                left.add(panel, BorderLayout.LINE_START);
                vertical.add(left);

                vertical.add(Box.createVerticalStrut(15));
                body.add(vertical, BorderLayout.PAGE_START);

                F.validate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
