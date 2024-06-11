package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class DesktopReceiver extends JFrame implements ActionListener {
    private ScreenEvent stub;
    private JTextField serverIP, password;
    private  JMenuBar  menuBar;
    private static JFrame frame;
    private static JLabel label;
    private static int titleBarHeight;
    private static int contentPaneHeight;

        public DesktopReceiver() {

            // Création d'une interface graphique pour entrer l'adresse IP du serveur et le mot de passe
            JLabel IPlabel = new JLabel("Server IP: ");
            IPlabel.setFont(new Font("Arial", Font.PLAIN, 13));
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            serverIP = new JTextField(15);
            serverIP.setToolTipText("Enter IP of Machine you want to Connect with!");
            serverIP.setFont(new Font("Arial", Font.PLAIN, 16));
            password = new JTextField(15);
            password.setToolTipText("Input Password of Machine you want to Connect with!");
            password.setFont(new Font("Arial", Font.PLAIN, 16));
            JButton submit = new JButton("Submit");
            submit.setFont(new Font("Arial", Font.PLAIN, 13));

            // Organisation des éléments de l'interface graphique
            JPanel panel1 = new JPanel();
            panel1.setLayout(new BorderLayout());
            panel1.add(IPlabel, BorderLayout.CENTER);
            JPanel panel2 = new JPanel();
            panel2.add(serverIP);
            JPanel topPanel = new JPanel();
            topPanel.add(panel1);
            topPanel.add(panel2);
            JPanel panel3 = new JPanel();
            panel3.setLayout(new BorderLayout());
            panel3.add(passwordLabel, BorderLayout.CENTER);
            JPanel panel4 = new JPanel();
            panel4.add(password);
            JPanel midPanel = new JPanel();
            midPanel.add(panel3);
            midPanel.add(panel4);
            JPanel bottomPanel = new JPanel();
            bottomPanel.add(submit);
            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new GridLayout(3, 1));
            gridPanel.add(topPanel);
            gridPanel.add(midPanel);
            gridPanel.add(bottomPanel);

            submit.addActionListener(this);

            // Configuration de la fenêtre principale
            setLayout(new BorderLayout());
            add(new JPanel().add(new JLabel(" ")), BorderLayout.NORTH);
            add(gridPanel, BorderLayout.CENTER);
            add(new JPanel().add(new JLabel(" ")), BorderLayout.SOUTH);
            setVisible(true);
            setSize(330, 175);
            setResizable(false);
            setLocation(500, 300);
            setTitle("Enter Password to Connect!");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    // Méthode appelée lorsqu'un bouton est cliqué
    @Override
    public void actionPerformed(ActionEvent e) {
        // Obtention de l'URL du serveur, recherche RMI et démarrage de SwingWorker
        SwingWorker<Void, BufferedImage> worker = new SwingWorker<Void, BufferedImage>() {
            @Override
            protected Void doInBackground() throws Exception {
                stub = (ScreenEvent) Naming.lookup("rmi://" + serverIP.getText() + ":1888/remote");
                if(!(stub.checkPassword(password.getText()))) {
                    System.out.println("Entered Credentials are wrong!");
                    System.exit(0);
                }
                else {
                    dispose();
                    System.out.println("Connection Established with Server!");
                    receiveScreenShot(stub);
                }
                return null;
            }
        };
        worker.execute(); // Démarrer le SwingWorker
    }
    // Méthode pour recevoir et afficher les captures d'écran du serveur
    public  void receiveScreenShot(ScreenEvent server) {
        try {
            createFrame(800, 600); // Créer une fenêtre avec une largeur de 800 et une hauteur de 600
            // Ajout d'un écouteur de souris à la fenêtre
            frame.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
            sendMouseClick(e.getX(), e.getY(),e, server);
                }

            });
            frame.addKeyListener(new KeyAdapter(){
                @Override
                public void keyPressed(KeyEvent e) {
                    sendKeyPress(e.getKeyCode(), server);
                }

            });
            frame.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    sendMouseMove(e.getX(), e.getY(), server);}
                @Override
                public void mouseDragged(MouseEvent e) {
                    sendMouseDrag(e.getX(), e.getY(), server);
                }
            });

            // Boucle pour recevoir et afficher les images
           while (true) {
                byte[] imageData = server.getScreenImage();
                if (imageData != null) {
                    // Convertir les données d'image en BufferedImage
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                    BufferedImage image = ImageIO.read(bais);
                    bais.close();
                    ResizeImage(image);
                }
           }
        } catch (Exception e) {
            System.out.println("Error receiving and displaying images: " + e);
        }
    }
    // Méthode pour créer la fenêtre d'affichage
    private  void createFrame(int width, int height) {
        frame = new JFrame("Remote Desktop Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem sendFileItem = new JMenuItem("Send File");
        JMenuItem receiveFileItem = new JMenuItem("Receive File");

        sendFileItem.addActionListener(e -> sendFile());
        receiveFileItem.addActionListener(e -> {
            try {
                receiveFile();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
        fileMenu.add(sendFileItem);
        fileMenu.add(receiveFileItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        label = new JLabel();
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setSize(width, height);
        frame.setVisible(true);
        Insets insets = frame.getInsets();
        titleBarHeight = insets.top;
        contentPaneHeight = frame.getContentPane().getHeight();
    }
    // Méthode pour redimensionner l'image et l'afficher
    private static void  ResizeImage(BufferedImage image) {
        Image scaledImage = image.getScaledInstance(frame.getContentPane().getWidth(), frame.getContentPane().getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        label.setIcon(scaledIcon);
    }
    // Méthode pour envoyer les événements de clic de souris
    private static void sendMouseClick(int x, int y, MouseEvent event,ScreenEvent server) {
        try {
            int frameWidth = frame.getContentPane().getWidth();
            int frameHeight = frame.getContentPane().getHeight();
            int titleBarHeight = frame.getHeight() - frame.getContentPane().getHeight();
            double scaleX = (double) x / frameWidth;
            double scaleY = (double) (y - titleBarHeight) / frameHeight;

            System.out.println("Scaled X: " + scaleX);
            System.out.println("Scaled Y: " + scaleY);
            server.mousePressedEvent(scaleX, scaleY,event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Méthode pour envoyer les événements de déplacement de souris
    private static void sendMouseMove(int x, int y, ScreenEvent server) {
        try {
            int frameWidth = frame.getContentPane().getWidth();
            int frameHeight = frame.getContentPane().getHeight();
            // Mise à l'échelle des coordonnées de clic de souris pour correspondre aux dimensions du cadre
            double scaleX = (double) x / frameWidth;
            double scaleY = (double) y / frameHeight;
            server. mouseMovedEvent(scaleX, scaleY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Méthode pour envoyer les événements de pression de touche
    private static void sendKeyPress(int keyCode, ScreenEvent server) {
        try {
            server.keyPressed(keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Méthode pour envoyer les événements de glissement de souris
    private static void sendMouseDrag(int x, int y, ScreenEvent server) {
        try {

            int frameWidth = frame.getContentPane().getWidth();
            int frameHeight = frame.getContentPane().getHeight();
            int titleBarHeight = frame.getHeight() - frame.getContentPane().getHeight();
            double scaleX = (double) x / frameWidth;
            double scaleY = (double) (y - titleBarHeight) / frameHeight;

            System.out.println("Scaled X: " + scaleX);
            System.out.println("Scaled Y: " + scaleY);

            server.mouseDraggedEvent(scaleX, scaleY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Méthode pour envoyer un fichier au serveur
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                byte[] fileData = fis.readAllBytes();
                stub.receiveFile(fileData, selectedFile.getName());
                JOptionPane.showMessageDialog(this, "File sent successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to send file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Méthode pour recevoir un fichier du serveur
    public void receiveFile() throws RemoteException {

        SwingUtilities.invokeLater(() -> {
            // Ouvrir un thread pour la logique de traitement de fichier
            new Thread(() -> {
                FileTransfer fileTransfer = null;
                try {
                    fileTransfer = stub.openFileChooser();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                byte[] fileData = fileTransfer.getFileData();
                String fileName = fileTransfer.getFileName();
                //Enregistrez les données du fichier sur le disque local avec le nom de fichier fourni
                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    fos.write(fileData);
                    JOptionPane.showMessageDialog(null, "File received: " + fileName, "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Failed to save file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }).start();
        });
    }
}

