package server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    /**
     * Deletes the Node from the database
     * @param id of the node you want to delete
     * @return Optional of an Activity list
     */
    @Query(
            nativeQuery=true,
            value="DELETE FROM Node WHERE id = ?1")
    void deleteNode(long id);

    /**
     * Gets all Nodes currently available
     * Can only be done by an admin
     * @param
     * @return Optional of an Node list
     */
    @Query(
            nativeQuery=true,
            value="SELECT * FROM Node SORT BY faculty")
    Optional<List<Node>> getAllNodes();

    /**
     * Gets all nodes belonging to the specific family
     * @param  faculty you want to get the nodes of
     * @return Optional of an Node list from the specific faculty
     */
    @Query(
            nativeQuery=true,
            value="SELECT * FROM Node WHERE faculty = ?1")
    Optional<List<Node>> getNodesByFaculty(String faculty);


}