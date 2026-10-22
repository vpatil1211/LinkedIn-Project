package com.linkedInProject.connectionsService.repository;

import com.linkedInProject.connectionsService.entiity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    Optional<Person> findByUserId(Long userId);

    @Query("match (personA:Person) -[:CONNECTED_TO]- (personB:Person) " +
            "where personA.userId = $userId " +
            "return DISTINCT personB")
    List<Person> getFirstDegreeConnections(Long userId);

    @Query("match (personA:Person) -[r:REQUESTED_TO]-> (personB:Person) " +
            "where personA.userId = $senderUserId and personB.userId = $receiverUserId " +
            "return count(r) > 0")
    boolean connectionRequestExists(Long senderUserId, Long receiverUserId);

    @Query("match (personA:Person) -[r:CONNECTED_TO]- (personB:Person) " +
            "where personA.userId = $senderUserId and personB.userId = $receiverUserId " +
            "return count(r) > 0")
    boolean isAlreadyConnected(Long senderUserId, Long receiverUserId);

    @Query("match (personA:Person), (personB:Person) " +
            "where personA.userId = $senderUserId and personB.userId = $receiverUserId " +
            "create (personA) -[:REQUESTED_TO]-> (personB)")
    void addConnection(Long senderUserId, Long receiverUserId);

    @Query("match (personA:Person) -[r:REQUESTED_TO]-> (personB:Person) " +
            "where personA.userId = $senderUserId and personB.userId = $receiverUserId " +
            "delete r " +
            "create (personA) -[:CONNECTED_TO]-> (personB)")
    void acceptConnectionRequest(Long senderUserId, Long receiverUserId);

    @Query("match (personA:Person) -[r:REQUESTED_TO]-> (personB:Person) " +
            "where personA.userId = $senderUserId and personB.userId = $receiverUserId " +
            "delete r ")
    void rejectConnectionRequest(Long senderUserId, Long receiverUserId);

    @Query("match (personA:Person) <-[:REQUESTED_TO]- (personB:Person) " +
            "where personA.userId = $ownerUserId " +
            "return personB ")
    List<Person> getAllPendingRequests(Long ownerUserId);
}
