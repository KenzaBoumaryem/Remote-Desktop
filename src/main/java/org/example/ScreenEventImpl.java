package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ScreenEventImpl extends UnicastRemoteObject implements ScreenEvent  {
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
    public void mouseMovedEvent(int xScale, int yScale) throws RemoteException {

    }

    @Override
    public void mousePressedEvent(double x,double y) throws RemoteException {
        try {
            robot.mouseMove((int) (x*screenSize.getWidth()), (int) (y*screenSize.getHeight()));
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(int keyPressed) throws RemoteException {

    }

}
