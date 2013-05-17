/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile.server;

import cz.mgn.csmobile.business.processor.SimpleProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author indy
 */
public class Dispetcher implements Runnable, Answerer {

    /**
     * is client currently connected?
     */
    protected volatile boolean connected = false;
    /**
     * client socket
     */
    protected Socket socket = null;
    protected OutputStream out = null;
    protected InputStream in = null;
    /**
     * command reader
     */
    protected SimpleProcessor processor = new SimpleProcessor(this);
    /**
     * observers of this connection
     */
    protected ArrayList<ClientConnectionInterface> listenners = new ArrayList<ClientConnectionInterface>();

    public Dispetcher(Socket socket) {
        this.socket = socket;
        addConnectionListener(processor);
    }

    @Override
    public void run() {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            connected = true;

            BufferedReader r = new BufferedReader(new InputStreamReader(in));

            String line = "";
            ArrayList<String> lines = new ArrayList<String>();
            while ((line = r.readLine()) != null) {
                processor.parse(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Dispetcher.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connected = false;
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Dispetcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            synchronized (listenners) {
                for (ClientConnectionInterface listener : listenners) {
                    listener.connectionClosed(this);
                }
            }
        }
    }

    /**
     * returns if client is currently connected to server
     */
    public boolean isConnected() {
        return connected;
    }

    public void addConnectionListener(ClientConnectionInterface listener) {
        synchronized (listenners) {
            listenners.add(listener);
        }
    }

    public boolean removeConnectionListener(ClientConnectionInterface listener) {
        synchronized (listenners) {
            return listenners.remove(listener);
        }
    }

    @Override
    public void answer(byte[] data) {
        try {
            out.write(data);
        } catch (IOException ex) {
            Logger.getLogger(Dispetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}