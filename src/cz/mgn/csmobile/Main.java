/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile;

import cz.mgn.csmobile.server.Server;

/**
 *
 * @author indy
 */
public class Main {

    public static void main(String[] args) {
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("CollabMobileServerPU");
//        EntityManager em = entityManagerFactory.createEntityManager();
//
//        Query q = em.createNamedQuery("User.findAll");
//
//        List<User> users = q.getResultList();
//        System.out.println("a = " + users.get(0).getLastLogin());
//
//        em.close();
//        entityManagerFactory.close();

        Server.getInstance();
    }
}
