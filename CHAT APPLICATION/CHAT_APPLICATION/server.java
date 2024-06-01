import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class MyQueue<T> {
    public void clear() {
    }

    private class Node {
        T data;
        Node next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node front;
    private Node rear;

    MyQueue() {
        this.front = null;
        this.rear = null;
    }

    void enqueue(T data) {
        Node newNode = new Node(data);
        if (rear == null) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
    }

    T dequeue() {
        if (front == null) {
            return null; // Queue is empty
        } else {
            T data = front.data;
            front = front.next;
            if (front == null) {
                rear = null;
            }
            return data;
        }
    }

    boolean isEmpty() {
        return front == null;
    }
}

public class server implements ActionListener {

    JTextField text;
    JPanel body;
    static Box vertical = Box.createVerticalBox();
    static JFrame F = new JFrame();
    static DataOutputStream output;
    private MyQueue<String> messageQueue = new MyQueue<>();
    private MyQueue<String> tempQueue = new MyQueue<>();

    server() {
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
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/1.jpeg"));
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

        JLabel name = new JLabel("TEACHER");
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

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteMessage());
        deleteButton.setBounds(350, 610, 100, 40);
        deleteButton.setBackground(new Color(155, 122, 211));
        deleteButton.setFont(new Font("SAN_SERIF", Font.BOLD, 12));
        deleteButton.setForeground(Color.WHITE);
        F.add(deleteButton);

        F.setSize(450,650);
        F.setLocation(150,50);
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
            messageQueue.enqueue(message);

            text.setText("");

            F.repaint();
            F.invalidate();
            F.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage() {
        String deletedMessage = messageQueue.dequeue();
        if (deletedMessage != null) {
            // Update the UI to reflect the changes
            updateMessageDisplay();

            tempQueue.clear();
            messageQueue.clear();

            F.repaint();
            F.invalidate();
            F.validate();
        }
    }

    private void updateMessageDisplay() {
        // Clear the existing content in the body panel
        body.removeAll();

        // Iterate through the modified message history and add messages to the body panel
        MyQueue<String> tempQueue = new MyQueue<>();
        while (!messageQueue.isEmpty()) {
            String message = messageQueue.dequeue();
            if (message != null) {
                tempQueue.enqueue(message);
                JPanel panel = chatbox(message);
                body.add(panel);
            }
        }

        // Restore the messages back to the original queue
        while (!tempQueue.isEmpty()) {
            messageQueue.enqueue(tempQueue.dequeue());
        }

        // Update the UI
        body.revalidate();
        body.repaint();

        // Scroll to the bottom to show the latest messages
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
        output.setBackground(new Color(176, 157, 208));
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
        new server();
        try {
            ServerSocket skt = new ServerSocket(6001);
            while (true) {
                Socket s = skt.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                output = new DataOutputStream(s.getOutputStream());

                Thread clientThread = new Thread(new ClientHandler(in));
                clientThread.start();

                while (true) {
                    String msg = in.readUTF();
                    JPanel panel = chatbox(msg);

                    JPanel left = new JPanel(new BorderLayout());
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left);
                    F.validate();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private DataInputStream in;

        public ClientHandler(DataInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg = in.readUTF();
                    JPanel panel = chatbox(msg);

                    JPanel left = new JPanel(new BorderLayout());
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left);
                    F.validate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
