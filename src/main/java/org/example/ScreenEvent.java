package org.example;

import java.awt.event.MouseEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ScreenEvent extends Remote {
    boolean checkPassword(String inputPassword) throws RemoteException;
    byte[] getScreenImage() throws RemoteException;
    //for receiveing events
    void mouseMovedEvent(double xScale, double yScale) throws RemoteException;
    void mousePressedEvent(double x,double y,MouseEvent event) throws RemoteException;
    void keyPressed(int keyPressed) throws RemoteException;

    void mouseDraggedEvent(double x,double y) throws RemoteException;
    FileTransfer openFileChooser() throws RemoteException;
    void receiveFile(byte[] fileData, String fileName) throws RemoteException;
}
