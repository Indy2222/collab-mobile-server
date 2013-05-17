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
@Table(name = "collabms_server")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Server.findAll", query = "SELECT s FROM Server s"),
    @NamedQuery(name = "Server.findByServerId", query = "SELECT s FROM Server s WHERE s.serverId = :serverId"),
    @NamedQuery(name = "Server.findByAddress", query = "SELECT s FROM Server s WHERE s.address = :address"),
    @NamedQuery(name = "Server.findByPort", query = "SELECT s FROM Server s WHERE s.port = :port"),
    @NamedQuery(name = "Server.findByWeight", query = "SELECT s FROM Server s WHERE s.weight = :weight"),
    @NamedQuery(name = "Server.findByIsUp", query = "SELECT s FROM Server s WHERE s.isUp = :isUp")})
public class Server implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "server_id")
    private Integer serverId;
    @Basic(optional = false)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @Column(name = "port")
    private int port;
    @Basic(optional = false)
    @Column(name = "weight")
    private float weight;
    @Basic(optional = false)
    @Column(name = "is_up")
    private boolean isUp;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "server")
    private Collection<RoomShot> roomShotCollection;

    public Server() {
    }

    public Server(Integer serverId) {
        this.serverId = serverId;
    }

    public Server(Integer serverId, String address, int port, float weight, boolean isUp) {
        this.serverId = serverId;
        this.address = address;
        this.port = port;
        this.weight = weight;
        this.isUp = isUp;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean getIsUp() {
        return isUp;
    }

    public void setIsUp(boolean isUp) {
        this.isUp = isUp;
    }

    @XmlTransient
    public Collection<RoomShot> getRoomShotCollection() {
        return roomShotCollection;
    }

    public void setRoomShotCollection(Collection<RoomShot> roomShotCollection) {
        this.roomShotCollection = roomShotCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serverId != null ? serverId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Server)) {
            return false;
        }
        Server other = (Server) object;
        if ((this.serverId == null && other.serverId != null) || (this.serverId != null && !this.serverId.equals(other.serverId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.mgn.csmobile.persistence.Server[ serverId=" + serverId + " ]";
    }
    
}
