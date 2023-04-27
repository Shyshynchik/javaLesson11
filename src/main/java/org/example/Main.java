package org.example;

import org.example.entity.Aircraft;
import org.example.entity.Ticket;
import org.example.orm.DefaultRepository;

public class Main {
    public static void main(String[] args) {
        DefaultRepository<Ticket, String> repository = new DefaultRepository<>(Ticket.class);

        System.out.println(repository.findByPrimaryKey("0005432000987"));

        DefaultRepository<Aircraft, String> repository1 = new DefaultRepository<>(Aircraft.class);
        System.out.println(repository1.findByPrimaryKey("773"));
    }
}