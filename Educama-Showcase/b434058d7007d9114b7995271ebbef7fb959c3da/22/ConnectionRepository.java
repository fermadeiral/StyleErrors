package org.educama.flightconnection.repository;

import org.educama.flightconnection.model.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConnectionRepository extends MongoRepository<Connection, String> {
    public List<Connection> findBySourceAirportIataCodeAndDestinationAirportIataCode(String sourceIataCode, String destinationAirportIataCode);

    public List<Connection> findBySourceAirportIataCode(String sourceIataCode);

    public List<Connection> findBydestinationAirportIataCode(String destinationIataCode);
}
