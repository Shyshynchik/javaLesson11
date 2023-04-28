package org.example.entity;

import lombok.*;
import org.hibernate.annotations.Type;

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
@Table(name = "airports_data")
public class Airport {

    @Id
    @Column(name = "airport_code")
    private String code;

    @Column(name = "timezone")
    private String timezone;

    @Type(type = "json")
    @Column(name = "airport_name")
    private String name;

    @Type(type = "json")
    @Column(name = "city")
    private String city;

    @Type(type = "point")
    @Column
    private String coordinates;

}
