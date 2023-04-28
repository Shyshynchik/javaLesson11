package org.example;

import org.example.entity.*;
import org.example.orm.DefaultRepository;
import org.example.orm.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {

        DefaultRepository<Aircraft> repository = new DefaultRepository<>(Aircraft.class);
        System.out.println(repository.findByPrimaryKey("773"));

        DefaultRepository<Airport> repository1 = new DefaultRepository<>(Airport.class);
        System.out.println(repository1.findByPrimaryKey("123"));

        Repository<Booking> repository2 = new DefaultRepository<>(Booking.class);

        Booking booking = new Booking();
        booking.setDate(LocalDateTime.now());
        booking.setReference("125");
        booking.setAmount(21321.12f);
        repository2.save(booking);
        System.out.println(repository2.findByPrimaryKey("125"));
    }
}