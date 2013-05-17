/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.csmobile.persistence;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author indy
 */
@Entity
@Table(name = "collabms_invitation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Invitation.findAll", query = "SELECT i FROM Invitation i"),
    @NamedQuery(name = "Invitation.findByInvitationId", query = "SELECT i FROM Invitation i WHERE i.invitationId = :invitationId"),
    @NamedQuery(name = "Invitation.findByTooken", query = "SELECT i FROM Invitation i WHERE i.tooken = :tooken")})
public class Invitation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "invitation_id")
    private Integer invitationId;
    @Basic(optional = false)
    @Column(name = "tooken")
    private String tooken;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invitation")
    private Collection<Message> messageCollection;

    public Invitation() {
    }

    public Invitation(Integer invitationId) {
        this.invitationId = invitationId;
    }

    public Invitation(Integer invitationId, String tooken) {
        this.invitationId = invitationId;
        this.tooken = tooken;
    }

    public Integer getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Integer invitationId) {
        this.invitationId = invitationId;
    }

    public String getTooken() {
        return tooken;
    }

    public void setTooken(String tooken) {
        this.tooken = tooken;
    }

    @XmlTransient
    public Collection<Message> getMessageCollection() {
        return messageCollection;
    }

    public void setMessageCollection(Collection<Message> messageCollection) {
        this.messageCollection = messageCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invitationId != null ? invitationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invitation)) {
            return false;
        }
        Invitation other = (Invitation) object;
        if ((this.invitationId == null && other.invitationId != null) || (this.invitationId != null && !this.invitationId.equals(other.invitationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.mgn.csmobile.persistence.Invitation[ invitationId=" + invitationId + " ]";
    }
    
}
