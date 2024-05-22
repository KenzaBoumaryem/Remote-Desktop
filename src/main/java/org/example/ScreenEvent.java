package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ScreenEvent extends Remote {
    boolean checkPassword(String inputPassword) throws RemoteException;
    byte[] getScreenImage() throws RemoteException;
    //for receiveing events
     void mouseMovedEvent(int xScale, int yScale) throws RemoteException;
    void mousePressedEvent(double x,double y) throws RemoteException;
    void keyPressed(int keyPressed) throws RemoteException;


}
