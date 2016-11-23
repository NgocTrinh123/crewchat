package com.dazone.crewchat.socket.Interfaces;

/**
 * Created by david on 12/23/15.
 */
public interface SocketListener {
    public void onConnect();
    public void onMessage(String message);
    public void onMessage(byte[] data);
    public void onDisconnect(int code, String reason);
    public void onError(Exception error);
}
