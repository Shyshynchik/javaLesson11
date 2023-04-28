package org.example;

import org.example.entity.Airport;
import org.example.entity.Ticket;
import org.example.orm.DefaultRepository;

public class Main {
    public static void main(String[] args) {

        DefaultRepository<Ticket> repository = new DefaultRepository<>(Ticket.class);
        System.out.println(repository.findByPrimaryKey("0005432000987"));

        DefaultRepository<Airport> repository1 = new DefaultRepository<>(Airport.class);


        Airport airport = new Airport();
        airport.setCode("123");
        airport.setCity("{\"en\": \"Yakutsk\", \"ru\": \"Якутск\"}");
        airport.setName("{\"en\": \"Yakutsk Airport\", \"ru\": \"Якутск\"}");
        airport.setCoordinates("129.77099609375,62.093299865722656");
        airport.setTimezone("Asia/Yakutsk");

        repository1.save(airport);

        System.out.println(repository1.findByPrimaryKey("123"));
    }
}