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
@Table(name = "collabms_identificator")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Identificator.findAll", query = "SELECT i FROM Identificator i"),
    @NamedQuery(name = "Identificator.findByIdentificatorId", query = "SELECT i FROM Identificator i WHERE i.identificatorId = :identificatorId"),
    @NamedQuery(name = "Identificator.findByValue", query = "SELECT i FROM Identificator i WHERE i.value = :value"),
    @NamedQuery(name = "Identificator.findByValueAndType", query = "SELECT i FROM Identificator i WHERE i.value = :value AND i.type = :type"),
    @NamedQuery(name = "Identificator.findByType", query = "SELECT i FROM Identificator i WHERE i.type = :type")})
public class Identificator implements Serializable {
    public static final String TYPE_PHONE_NUMBER = "PHONE_NUMBER";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "identificator_id")
    private Integer identificatorId;
    @Basic(optional = false)
    @Column(name = "value")
    private String value;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userId;

    public Identificator() {
    }

    public Identificator(Integer identificatorId) {
        this.identificatorId = identificatorId;
    }

    public Identificator(Integer identificatorId, String value, String type) {
        this.identificatorId = identificatorId;
        this.value = value;
        this.type = type;
    }

    public Integer getIdentificatorId() {
        return identificatorId;
    }

    public void setIdentificatorId(Integer identificatorId) {
        this.identificatorId = identificatorId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        hash += (identificatorId != null ? identificatorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Identificator)) {
            return false;
        }
        Identificator other = (Identificator) object;
        if ((this.identificatorId == null && other.identificatorId != null) || (this.identificatorId != null && !this.identificatorId.equals(other.identificatorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.mgn.csmobile.persistence.Identificator[ identificatorId=" + identificatorId + " ]";
    }
    
}
