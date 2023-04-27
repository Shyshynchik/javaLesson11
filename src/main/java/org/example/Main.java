package org.example;

import org.example.entity.Aircraft;
import org.example.entity.Ticket;
import org.example.orm.DefaultRepository;

public class Main {
    public static void main(String[] args) {

        DefaultRepository<Ticket, String> repository = new DefaultRepository<>(Ticket.class);
        System.out.println(repository.findByPrimaryKey("0005432000987"));

        DefaultRepository<Aircraft, String> repository1 = new DefaultRepository<>(Aircraft.class);


        Aircraft aircraft = new Aircraft();
        aircraft.setCode("123");
        aircraft.setRange(123);
        aircraft.setModel("{\"en\": \"Boeing 777-300\", \"ru\": \"Боинг 777-300\"}");

        repository1.save(aircraft);

        System.out.println(repository1.findByPrimaryKey("123"));
    }
}