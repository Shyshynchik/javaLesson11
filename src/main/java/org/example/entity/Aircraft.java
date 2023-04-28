package org.example.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "aircrafts_data")
public class Aircraft {

    @Id
    @Column(name = "aircraft_code")
    private String code;

    @Column
    @Type(type = "json")
    private String model;

    @Column(name = "range")
    private Integer range;

    @OneToMany(targetEntity = Seat.class, mappedBy = "aircraft_code")
    private List<Seat> seats;

}
