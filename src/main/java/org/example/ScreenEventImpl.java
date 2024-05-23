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
    Robot robot = null;
    String password;
    private Dimension screenSize;


    protected ScreenEventImpl(String password) throws RemoteException {
        super();
        this.password = password;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            // Initialize the Robot
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkPassword(String inputPassword) throws RemoteException {			//checks if the entered password matches to that of the Server's Password
        if(password.equals(inputPassword))
            return true;

        return false;
    }
    @Override
    public byte[] getScreenImage() throws RemoteException {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenImage = robot.createScreenCapture(screenRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return imageBytes;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error capturing screen image: " + e.getMessage());
        }
    }

    @Override
    public void mouseMovedEvent(double x, double y) throws RemoteException {
        try {
            robot.mouseMove((int) (x*screenSize.getWidth()), (int) (y*screenSize.getWidth()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mousePressedEvent(double x,double y,MouseEvent event) throws RemoteException {
        try {
            int button= event.getButton();
            if (button == MouseEvent.BUTTON1) {
                robot.mouseMove((int) (x*screenSize.getWidth()), (int) (y*screenSize.getHeight()));
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } else if (button == MouseEvent.BUTTON3) {
                robot.mouseMove((int) (x*screenSize.getWidth()), (int) (y*screenSize.getHeight()));
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            }

           // robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
           // robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(int keyCode) throws RemoteException {
        try {
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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

        return null; // Return null if no file is selected
    }
    @Override
    public void mouseDraggedEvent(double x, double y) throws RemoteException {
        // Convert scaled coordinates back to screen coordinates
        int screenX = (int) (x*screenSize.getWidth());
        int screenY = (int) (y * screenSize.getHeight());
        robot.mouseMove(screenX, screenY);

        // Simulate mouse press for the drag
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        // Move the mouse to the end position of the drag
        robot.mouseMove(screenX + 50, screenY + 50); // Example: Move mouse by 50 pixels

        // Simulate mouse release to complete the drag
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

    }


}
