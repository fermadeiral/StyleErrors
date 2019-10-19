package org.educama.flightconnection.businessservice;

import org.educama.flightconnection.repository.ConnectionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionBusinessServiceUnitTest {
    @Mock
    ConnectionRepository connectionRepository;
    @InjectMocks
    ConnectionBusinessService cut;

    @Test
    public void findFlightConnection_returnsEmptyList_whenNoSourceAirport() {
        //Given
        final String destinationAirport = "LAX";
        //Then
        assertThat(cut.findFlightConnection(null, destinationAirport)).isEmpty();
        assertThat(cut.findFlightConnection("", destinationAirport)).isEmpty();
    }

    @Test
    public void findFlightConnection_returnsEmptyList_whenNoSourceAndNoDestinationAirport() {
        //Given
        final String sourceAirport = "LAX";
        //Then
        assertThat(cut.findFlightConnection(sourceAirport, null)).isEmpty();
        assertThat(cut.findFlightConnection(sourceAirport, "")).isEmpty();
    }

    @Test
    public void findFlightConnection_returnsListOfConnections() {
        //Given
        final String sourceAirport = "fra";
        final String destinationAirport = "LAX";
        //When
        cut.findFlightConnection(sourceAirport, destinationAirport);
        //Then
        verify(connectionRepository).findBySourceAirportIataCodeAndDestinationAirportIataCode(sourceAirport.toUpperCase(), destinationAirport);
    }

    @Test
    public void findFlightConnection_returnsConnectionsFromSourceAirport_whenOnlySourceAirportSpecified() {
        //Given
        final String source = "LAX";
        //When
        cut.findAllConnectionsFromSourceToDestionation(source, null);
        //Then
        verify(connectionRepository).findBySourceAirportIataCode(source);


    }

    @Test
    public void findFlightConnection_returnsConnectionsToDestinationAirport_whenOnlyDestinationAirportSpecified() {
        //Given
        final String destination = "LAX";
        //When
        cut.findAllConnectionsFromSourceToDestionation(null, destination);
        //Then
        verify(connectionRepository).findBydestinationAirportIataCode(destination);

    }


}
