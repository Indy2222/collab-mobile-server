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
import javax.persistence.Lob;
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
@Table(name = "collabms_room_shot")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoomShot.findAll", query = "SELECT r FROM RoomShot r"),
    @NamedQuery(name = "RoomShot.findByRoomShotId", query = "SELECT r FROM RoomShot r WHERE r.roomShotId = :roomShotId"),
    @NamedQuery(name = "RoomShot.findByTitle", query = "SELECT r FROM RoomShot r WHERE r.title = :title")})
public class RoomShot implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "room_shot_id")
    private Integer roomShotId;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @Lob
    @Column(name = "data")
    private byte[] data;
    @JoinColumn(name = "server", referencedColumnName = "server_id")
    @ManyToOne(optional = false)
    private Server server;

    public RoomShot() {
    }

    public RoomShot(Integer roomShotId) {
        this.roomShotId = roomShotId;
    }

    public RoomShot(Integer roomShotId, String title, byte[] data) {
        this.roomShotId = roomShotId;
        this.title = title;
        this.data = data;
    }

    public Integer getRoomShotId() {
        return roomShotId;
    }

    public void setRoomShotId(Integer roomShotId) {
        this.roomShotId = roomShotId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomShotId != null ? roomShotId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoomShot)) {
            return false;
        }
        RoomShot other = (RoomShot) object;
        if ((this.roomShotId == null && other.roomShotId != null) || (this.roomShotId != null && !this.roomShotId.equals(other.roomShotId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cz.mgn.csmobile.persistence.RoomShot[ roomShotId=" + roomShotId + " ]";
    }
    
}
