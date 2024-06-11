package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ScreenEventImpl extends UnicastRemoteObject implements ScreenEvent {
    Robot robot = null;// Robot pour simuler des actions sur l'écran
    String password;
    private Dimension screenSize;// Taille de l'écran


    protected ScreenEventImpl(String password) throws RemoteException {
        super();
        this.password = password;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();// Récupération de la taille de l'écran
        try {
            // Initialisation du Robot pour interagir avec l'écran
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour vérifier le mot de passe
    @Override
    public boolean checkPassword(String inputPassword) throws RemoteException {
        // Vérifie si le mot de passe entré correspond au mot de passe du serveur
        if(password.equals(inputPassword))
            return true;

        return false;
    }

    // Méthode pour capturer une image de l'écran
    @Override
    public byte[] getScreenImage() throws RemoteException {
        try {
            // Capture d'une image de l'écran
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenImage = robot.createScreenCapture(screenRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return imageBytes;// Retourne l'image capturée sous forme de tableau d'octets
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error capturing screen image: " + e.getMessage());
        }
    }

    // Méthode pour gérer le déplacement de la souris
    @Override
    public void mouseMovedEvent(double x, double y) throws RemoteException {
        try {
            // Déplacement de la souris aux coordonnées spécifiées
            robot.mouseMove((int) (x*screenSize.getWidth()), (int) (y*screenSize.getWidth()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour gérer les événements de clic de souris
    @Override
    public void mousePressedEvent(double x,double y,MouseEvent event) throws RemoteException {
        try {
            int button= event.getButton();// Récupère le bouton de la souris
            if (button == MouseEvent.BUTTON1) {// Si le bouton est le clic gauche
                robot.mouseMove((int) (x*screenSize.getWidth()), (int) (y*screenSize.getHeight()));
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } else if (button == MouseEvent.BUTTON3) {// Si le bouton est le clic droit
                robot.mouseMove((int) (x*screenSize.getWidth()), (int) (y*screenSize.getHeight()));
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour simuler la pression d'une touche du clavier
    @Override
    public void keyPressed(int keyCode) throws RemoteException {
        try {
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour gérer le glissement de la souris
    @Override
    public void mouseDraggedEvent(double x, double y) throws RemoteException {
        // Convertit les coordonnées échelonnées en coordonnées de l'écran
        int screenX = (int) (x*screenSize.getWidth());
        int screenY = (int) (y * screenSize.getHeight());
        robot.mouseMove(screenX, screenY);

        // Simule un clic de souris pour le glissement
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        // Déplace la souris à la position finale du glissement
        robot.mouseMove(screenX + 50, screenY + 50); // Example: Move mouse by 50 pixels

        // Simule le relâchement de la souris pour terminer le glissement
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

    }

    // Méthode pour recevoir un fichier et le sauvegarder localement
    public void receiveFile(byte[] fileData, String fileName) throws RemoteException {
        // Obtenez le chemin vers le répertoire de travail actuel
        String currentDir = System.getProperty("user.dir");

        // Ajoutez le nom du fichier pour créer le chemin complet du fichier dans le même répertoire
        String filePath = currentDir + File.separator + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileData);
            System.out.println("File received and saved to current directory: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw new RemoteException("Error saving file: " + e.getMessage());
        }
    }

    // Méthode pour ouvrir une boîte de dialogue de sélection de fichier
    @Override
    public FileTransfer openFileChooser() throws RemoteException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to send");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                byte[] fileData = fis.readAllBytes();
                String fileName = selectedFile.getName();
                return new FileTransfer(fileData, fileName);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RemoteException("Error reading file: " + e.getMessage());
            }
        }
        return null; // Retourne null si aucun fichier n'est sélectionné
    }
}
