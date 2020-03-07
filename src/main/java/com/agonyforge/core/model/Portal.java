package com.agonyforge.core.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "portal")
public class Portal {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Convert(converter = PortalFlag.Converter.class)
    private EnumSet<PortalFlag> flags = EnumSet.noneOf(PortalFlag.class);

    public Portal() {
        // this method intentionally left blank
    }

    public Portal(Room room) {
        setRoom(room);
    }

    public Portal(Room room, EnumSet<PortalFlag> flags) {
        setRoom(room);
        setFlags(flags);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public EnumSet<PortalFlag> getFlags() {
        return flags;
    }

    public void setFlags(EnumSet<PortalFlag> portalFlags) {
        this.flags = portalFlags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Portal)) return false;
        Portal portal = (Portal) o;
        return Objects.equals(getId(), portal.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
