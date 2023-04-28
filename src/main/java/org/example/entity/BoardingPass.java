package org.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "boarding_passes")
public class BoardingPass {

    @Id
    @Column(name = "boarding_no")
    private Integer number;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "ticket_no")
    private Ticket ticket;

    @Column(name = "seat_no")
    private String seatNumber;


}
