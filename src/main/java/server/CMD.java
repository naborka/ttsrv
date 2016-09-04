package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tomag on 04.09.2016.
 */
public class CMD extends Thread {

    ServerSocket serverSocket;
    DirectoryObserverServer dos;

    public CMD(DirectoryObserverServer dos) {
        this.dos = dos;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(7059);
        } catch (IOException e) {
            System.err.println("Couldn't open socket");
            e.printStackTrace();
        }

        while (true) {
            try {
                final Socket client = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String cmd;
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                                cmd = reader.readLine();
                                if (cmd.equals("update")) {
                                    dos.sendDirAndFileList();
                                }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
