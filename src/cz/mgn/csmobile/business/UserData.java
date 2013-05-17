/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile.business;

import cz.mgn.csmobile.persistence.HasMessage;
import cz.mgn.csmobile.persistence.Identificator;
import cz.mgn.csmobile.persistence.Message;
import cz.mgn.csmobile.persistence.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Martin Indra <aktive at seznam.cz>
 */
public class UserData {

    public static final int REGISTER_SUCCESSFUL = 0;
    public static final int REGISTER_GENERAL_ERROR = 1;
    public static final int REGISTER_NICK_IS_NOT_FREE = 1;
    //
    protected User user = null;
    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    public UserData() {
        entityManagerFactory = Persistence.createEntityManagerFactory("CollabMobileServerPU");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public boolean isLoggedIn() {
        return user != null;
    }
    
    public List<HasMessage> getMessages() {
        Query query = entityManager.createNamedQuery("HasMessage.findByOwner");
        query.setParameter("owner", user);
        return query.getResultList();
    }

    public void addChatMessage(String message, ArrayList<String> targets) {
        Message msg = new Message();
        msg.setAuthor(user);
        msg.setText(message);
        msg.setExpiration(new Date(System.currentTimeMillis() + 86400000 * 120)); // 120 days
        msg.setTime(new Date());

        entityManager.getTransaction().begin();
        entityManager.persist(msg);
        entityManager.getTransaction().commit();

        Query query = entityManager.createNamedQuery("User.findByNicks");
        query.setParameter("nicks", targets);

        List<User> results = query.getResultList();
        for (User result : results) {
            HasMessage hasMessage = new HasMessage();
            hasMessage.setReaded(false);
            hasMessage.setUserId(result);
            hasMessage.setMessageId(msg);

            entityManager.getTransaction().begin();
            entityManager.persist(hasMessage);
            entityManager.getTransaction().commit();
        }
    }

    public ArrayList<User> searchIdentificator(String type, String value) {
        Query query = entityManager.createNamedQuery("Identificator.findByValueAndType");
        query.setParameter("value", value);
        query.setParameter("type", type);
        List<Identificator> identificators = query.getResultList();

        ArrayList<User> users = new ArrayList<User>();

        for (Identificator id : identificators) {
            users.add(id.getUserId());
        }

        return users;
    }

    public void addIdentificator(String type, String value) {
        Identificator id = new Identificator();
        id.setType(type);
        id.setValue(value);
        id.setUserId(user);

        entityManager.getTransaction().begin();
        entityManager.persist(id);
        entityManager.getTransaction().commit();
    }

    public int register(String nick, String name, byte[] publicKey) {
        Query query = entityManager.createNamedQuery("User.countByNick");
        query.setParameter("nick", nick);

        if ((Long) query.getSingleResult() == 0) {
            User user = new User();
            user.setNick(nick);
            user.setName(name);
            user.setPublicKey(publicKey);

            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();

            return REGISTER_SUCCESSFUL;
        }
        return REGISTER_NICK_IS_NOT_FREE;
    }

    public User loginByNickAndPublicKey(String nick, byte[] publicKey) {
        Query query = entityManager.createNamedQuery("User.findByNick");
        query.setParameter("nick", nick);
        List<User> users = query.getResultList();

        for (User user : users) {
            //FIXME: every user can have many public keys
            byte[] publicKeyOfUser = user.getPublicKey();
            if (publicKeyOfUser == null || publicKey == null || publicKey.length != publicKeyOfUser.length) {
                return null;
            }
            for (int i = 0; i < publicKeyOfUser.length; i++) {
                if (publicKeyOfUser[i] != publicKey[i]) {
                    return null;
                }
            }
            this.user = user;
            break;
        }

        return user;
    }

    public void destroy() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
