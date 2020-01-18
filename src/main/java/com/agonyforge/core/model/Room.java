package com.agonyforge.core.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Room {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    private Zone zone;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "exit_type")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Direction, Portal> exits = new HashMap<>();

    @OneToMany(mappedBy = "room")
    private List<Creature> creatures = new ArrayList<>();

    public Room() {
        // this method intentionally left blank
    }

    public Room(Zone zone, int sequence) {
        setZone(zone);
        setSequence(sequence);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Map<Direction, Portal> getExits() {
        return exits;
    }

    public void setExits(Map<Direction, Portal> exits) {
        this.exits = exits;
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public void setCreatures(List<Creature> creatures) {
        this.creatures = creatures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(getId(), room.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
