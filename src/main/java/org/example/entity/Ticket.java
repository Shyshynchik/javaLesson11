package org.example.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ticket {

    @Id
    @Column(name = "ticket_no")
    private String number;

    @OneToOne
    @Column(name = "book_ref")
    @JoinColumn(name = "book_ref")
    private Booking book;

    @Column(name = "passenger_id")
    private String passenger;

    @Column(name = "passenger_name")
    private String passengerName;

    @Column(name = "contact_data")
    private String contactData;

}
