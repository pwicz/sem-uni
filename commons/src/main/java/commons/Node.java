package commons;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Node implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@Getter @Setter, does this work still?
    private long id;
    private String name; //netId?
    private String url;
    private String faculty;
    private String token; //user auth token?
    private Resource resource; // this should just be cpu, gpu, mem
    private LocalDate released = null;
    private LocalDate releaseEnd = null;

    /**
     * Constructor for the Node class, represent the nodes in the culster.
     *
     * @param name the netId of the user creating the cluster
     * @param faculty the faculty the user belongs to
     * @param token token of access
     * @param cpuUsage the amount of cpu units needed
     * @param gpuUsage the amount of gpu units needed
     * @param memUsage the amount of memory units needed
     */
    public Node(String name, String url, String faculty, String token, int cpuUsage, int gpuUsage, int memUsage) {
        this.name = name;
        this.url = url;
        this.faculty = faculty;
        this.token = token;
        this.resource = resource;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Resource getResource() {
        return resource;
    }

    public LocalDate getReleased() {
        return released;
    }

    public void setReleased(LocalDate released) {
        this.released = released;
    }

    public LocalDate getReleaseEndTime() {
        return releaseEnd;
    }

    public void setReleaseTime(LocalDate releasEnd) {
        this.releaseEnd = releaseEnd;
    }

    /**
     * Comparator for Node.
     *
     * @param otherNode other node you are comparing to
     */
    @Override
    public int compareTo(Object otherNode) {
        if (otherNode instanceof Node) {
            Node o = (Node) otherNode;
            return ((int) (this.id - o.id));
        } else {
            return -1;
        }
    }

}