package org.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "seats")
public class Seat {

    @Id
    @Column(name = "seat_no")
    private String number;

    @Column(name = "aircraft_code")
    private String aircraft;

    @Column(name = "fare_conditions")
    private String fareConditions;

}
