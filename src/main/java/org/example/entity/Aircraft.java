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


}
