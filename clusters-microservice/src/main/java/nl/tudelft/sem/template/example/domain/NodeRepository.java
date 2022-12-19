package nl.tudelft.sem.template.example.domain;


import commons.Node;
import commons.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    /**
     * Deletes the Node from the database.
     *
     * @param id of the node you want to delete
     */
    @Query(
            nativeQuery = true,
            value = "DELETE FROM Node WHERE id = ?1")
    void deleteNode(long id);

    /**
     * Gets all Nodes currently available.
     * Can only be done by an admin.
     */
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM Node SORT BY faculty")
    Optional<List<Node>> getAllNodes();

    /**
     * Gets all nodes belonging to the specific family.
     *
     * @param  faculty you want to get the nodes of
     * @return Optional of a Node list from the specific faculty
     */
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM Node WHERE faculty = ?1")
    Optional<List<Node>> getNodesByFaculty(String faculty);

    /**
     * Gets all nodes that belong to faculty.
     * And Nodes that are released.
     *
     * @param  faculty you want to get the nodes of
     * @param date date you want to get the resources on
     * @return Optional of a Node list from the specific faculty
     */
    @Query(
            nativeQuery = true,
            value = "SELECT SUM(CPU), SUM(GPU), SUM(MEM) FROM Node "
                    + "WHERE faculty = ?1 OR "
                    + "(released <= ?2 AND releaseEND >= ?2)")
    Optional<Resource> getFreeResources(String faculty, String date);

    /**
     * Meant to return in a FacultyResource model.
     *
     * @param  facultyToUpdate faculty of nodes you want to update
     * @param  newDate date you want to free resouces on
     * @param  newDays how many days to want to free for
     */
    @Query(
            nativeQuery = true,
            value = "UPDATE Node SET date = ?2, days = ?3 WHERE faculty = ?1")
    void updateRelease(String facultyToUpdate, LocalDate newDate, int newDays);

    /**
     * Returns node with the id.
     *
     * @param  id of the node you want to return
     */
    @Query(
            nativeQuery = true,
            value = "SELECT Node WHERE id = ?1")
    Optional<Node> getNodeById(long id);

    /**
     * flag the ndoe with id as deleted from tomorrow.
     *
     * @param  id of the node to delete
     * @param  date deleted from tomorrow
     */
    @Query(
            nativeQuery = true,
            value = "UPDATE Node SET removedDate = ?2 WHERE id = ?1")
    void setAsDeleted(long id, LocalDate date);
}