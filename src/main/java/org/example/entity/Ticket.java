package org.example.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "tickets")
public class Ticket {

    @Id
    @Column(name = "ticket_no")
    private String number;

    @Column(name = "book_ref")
    private String book;

    @Column(name = "passenger_id")
    private String passenger;

    @Column(name = "passenger_name")
    private String passengerName;

    @Column(name = "contact_data")
    private String contactData;

}
