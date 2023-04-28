package org.example.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(name = "book_ref")
    private String reference;

    @Column(name = "book_date")
    @Type(type = "datetime")
    private LocalDateTime date;

    @Column(name = "total_amount")
    private Float amount;
}
