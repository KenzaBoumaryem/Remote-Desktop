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
    InetAddress privateIP;// Adresse IP privée de la machine
    JTextField password;// Champ de texte pour le mot de passe
    public DesktopServer()  {
        try {
            privateIP = InetAddress.getLocalHost();	// Obtention de l'adresse IP locale de la machine
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        JPanel mainPanel = new JPanel();
        // Création de l'interface utilisateur qui invite l'utilisateur à configurer un mot de passe
        JLabel label = new JLabel("Set Password:");
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        password = new JTextField(15);// Champ de texte pour entrer le mot de passe
        password.setToolTipText("Set a Password. Share the password to Connect with your Machine!");
        password.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton submit = new JButton("Submit");// Bouton de soumission
        submit.setFont(new Font("Arial", Font.PLAIN, 13));

        // Utilisation de JTextField au lieu de JLabel pour afficher les informations car le texte peut être sélectionné et copié copied
        JTextField  IPlabel = new JTextField ();
        IPlabel.setText("Your Machine' IP Address is:  " + privateIP.getHostAddress());
        IPlabel.setFont(new Font("Arial", Font.PLAIN, 12));
        IPlabel.setEditable(false);
        IPlabel.setBorder(null);
       // Configuration des panneaux pour l'interface utilisateur
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

        submit.addActionListener(this);// Ajout d'un écouteur d'événements au bouton de soumission

        // Configuration de la fenêtre principale
        setLayout(new BorderLayout());
        add(new JPanel().add(new JLabel(" ")), BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
        add(new JPanel().add(new JLabel(" ")), BorderLayout.SOUTH);
        setVisible(true);
        setSize(400, 130);// Taille de la fenêtre
        password.requestFocusInWindow();// Mise en focus du champ de mot de passe
        setResizable(false);
        setLocation(500, 300);// Position de la fenêtre sur l'écran
        setTitle("Set a Password!");// Titre de la fenêtre
        mainPanel.add(label);
        mainPanel.add(password);
        mainPanel.add(submit);
        mainPanel.add(IPlabel);
        // Définition du panneau principal comme contenu de la JFrame
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Fermeture de l'application à la fermeture de la fenêtre
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();		// Lorsque le bouton de soumission est cliqué, la GUI est fermée
        try {
            ScreenEvent stub = new ScreenEventImpl(password.getText());// Création d'une implémentation de l'interface ScreenEvent avec le mot de passe entré
            // Création d'un registre RMI sur le port 1888
            LocateRegistry.createRegistry(1888);
            Naming.rebind("rmi://" + privateIP.getHostAddress() + ":1888/remote", stub);// Enregistrement de l'objet distant avec le nom "remote"
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
