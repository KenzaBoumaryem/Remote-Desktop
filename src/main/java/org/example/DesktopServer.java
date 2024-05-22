package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class DesktopServer extends JFrame implements ActionListener {

    InetAddress privateIP;
    JTextField password;
    public DesktopServer()  {
        try {
            privateIP = InetAddress.getLocalHost();			//Local (Private) IP of your Machine
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        JPanel mainPanel = new JPanel();
        //Creating a GUI which prompts the user to Set up a Password
        JLabel label = new JLabel("Set Password:");
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        password = new JTextField(15);
        password.setToolTipText("Set a Password. Share the password to Connect with your Machine!");
        password.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton submit = new JButton("Submit");
        submit.setFont(new Font("Arial", Font.PLAIN, 13));

        //using JTextField instead of a JLabel to display information because, the text in textField can be selected and copied
        JTextField  IPlabel = new JTextField ();
        IPlabel.setText("Your Machine' IP Address is:  " + privateIP.getHostAddress());			//for intraNet Connection
        IPlabel.setFont(new Font("Arial", Font.PLAIN, 12));
        IPlabel.setEditable(false);
        IPlabel.setBorder(null);

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        westPanel.add(label, BorderLayout.CENTER);
        JPanel eastPanel = new JPanel();
        eastPanel.add(submit);
        JPanel centerPanel = new JPanel();
        centerPanel.add(password);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(westPanel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(eastPanel, BorderLayout.EAST);

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2, 1));
        gridPanel.add(topPanel);  gridPanel.add(IPlabel);


        submit.addActionListener(this);

        setLayout(new BorderLayout());
        add(new JPanel().add(new JLabel(" ")), BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
        add(new JPanel().add(new JLabel(" ")), BorderLayout.SOUTH);

        setVisible(true);
        setSize(400, 130);
        password.requestFocusInWindow();						//Caret focus is on this Component
        setResizable(false);
        setLocation(500, 300);
        setTitle("Set a Password!");
        mainPanel.add(label);
        mainPanel.add(password);
        mainPanel.add(submit);
        mainPanel.add(IPlabel);

        // Définition du panneau principal comme contenu de la JFrame
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();									//when submit is clicked, the GUI is disposed
        try {
            ScreenEvent stub = new ScreenEventImpl(password.getText());
            //RMIRegistry on port 1888
            LocateRegistry.createRegistry(1888);
            Naming.rebind("rmi://" + privateIP.getHostAddress() + ":1888/burr", stub);
            System.out.println("Server Running!!!");

        }
        catch (RemoteException ex) {
            ex.printStackTrace();
        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
}
