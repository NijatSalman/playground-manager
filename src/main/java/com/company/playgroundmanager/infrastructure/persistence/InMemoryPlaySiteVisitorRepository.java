package com.company.playgroundmanager.infrastructure.persistence;

import com.company.playgroundmanager.playground.api.model.VisitorRecord;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPlaySiteVisitorRepository {
    private final Map<String, VisitorRecord> visitorsByTicket = new ConcurrentHashMap<>();

    public boolean exists(String ticketNumber) {
        return visitorsByTicket.containsKey(ticketNumber);
    }

    public void save(VisitorRecord record) {
        visitorsByTicket.put(record.getTicketNumber(), record);
    }

    public Optional<VisitorRecord> find(String ticketNumber) {
        return Optional.ofNullable(visitorsByTicket.get(ticketNumber));
    }

    public void delete(String ticketNumber) {
        visitorsByTicket.remove(ticketNumber);
    }

    public Map<String, VisitorRecord> findAll() {
        return visitorsByTicket;
    }
}
