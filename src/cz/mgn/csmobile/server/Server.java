/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author indy
 */
public class Server extends Thread implements ClientConnectionInterface {

    public static final int THREADS = 8;
    public static Server instance = null;
    protected volatile boolean running = false;
    /**
     * listening port of server
     */
    protected int port = 8080;
    private ExecutorService es;
    /**
     * list of currently connected clients
     */
    protected ArrayList<Dispetcher> clients = new ArrayList<Dispetcher>();

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }
    
    public Server() {
        init();
    }

    private void init() {
        es = Executors.newFixedThreadPool(THREADS);
        this.start();
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            running = true;
            server = new ServerSocket(port);
            while (running) {
                Dispetcher client = new Dispetcher(server.accept());
                es.execute(client);
                synchronized (clients) {
                    clients.add(client);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void connectionClosed(Dispetcher eventSource) {
        synchronized (clients) {
            clients.remove(eventSource);
        }
    }
}