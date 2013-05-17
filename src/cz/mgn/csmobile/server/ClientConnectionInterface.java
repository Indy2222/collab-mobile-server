/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile.server;

/**
 *
 * @author indy
 */
public interface ClientConnectionInterface {

    /** called after end of connection with client */
    public void connectionClosed(Dispetcher eventSource);
}