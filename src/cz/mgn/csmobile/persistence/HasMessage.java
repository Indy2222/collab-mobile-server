/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author indy
 */
@Entity
@Table(name = "collabms_has_message")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HasMessage.findAll", query = "SELECT h FROM HasMessage h"),
    @NamedQuery(name = "HasMessage.findByHasMessageId", query = "SELECT h FROM HasMessage h WHERE h.hasMessageId = :hasMessageId"),
    @NamedQuery(name = "HasMessage.findByOwner", query = "SELECT h FROM HasMessage h WHERE h.userId = :owner"),
    @NamedQuery(name = "HasMessage.findByReaded", query = "SELECT h FROM HasMessage h WHERE h.readed = :readed")})
public class HasMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "has_message_id")
    private Integer hasMessageId;
    @Basic(optional = false)
    @Column(name = "readed")
    private boolean readed;
    @JoinColumn(name = "message_id", referencedColumnName = "message_id")
    @ManyToOne(optional = false)
    private Message messageId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userId;

    public HasMessage() {
    }

    public HasMessage(Integer hasMessageId) {
        this.hasMessageId = hasMessageId;
    }

    public HasMessage(Integer hasMessageId, boolean readed) {
        this.hasMessageId = hasMessageId;
        this.readed = readed;
    }

    public Integer getHasMessageId() {
        return hasMessageId;
    }

    public void setHasMessageId(Integer hasMessageId) {
        this.hasMessageId = hasMessageId;
    }

    public boolean getReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public Message getMessageId() {
        return messageId;
    }

    public void setMessageId(Message messageId) {
        this.messageId = messageId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hasMessageId != null ? hasMessageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HasMessage)) {
            return false;
        }
        HasMessage other = (HasMessage) object;
        if ((this.hasMessageId == null && other.hasMessageId != null) || (this.hasMessageId != null && !this.hasMessageId.equals(other.hasMessageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.mgn.csmobile.persistence.HasMessage[ hasMessageId=" + hasMessageId + " ]";
    }
    
}
