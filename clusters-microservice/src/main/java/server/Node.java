package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Node implements Comparable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@Getter @Setter, does this work still?
    private long id;
    private String name; //netId?
    private String url;
    private String faculty;
    private String token; //user auth token?
    private int CPUaddtion;
    private int GPUaddition;
    private int memoryAddition;

    public Node(String name, String url, String faculty, String token, int CPUaddtion, int GPUaddition, int memoryAddition) {
        this.name = name;
        this.url = url;
        this.faculty = faculty;
        this.token = token;
        this.CPUaddtion = CPUaddtion;
        this.GPUaddition = GPUaddition;
        this.memoryAddition = memoryAddition;
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

    public int getCPUaddtion() {
        return CPUaddtion;
    }

    public int getGPUaddition() {
        return GPUaddition;
    }

    public int getMemoryAddition() {
        return memoryAddition;
    }

    /**
     * Comparator for Node
     * @param otherNode
     */
    @Override
    public int compareTo(Object otherNode) {
        if (otherNode instanceof Node){
            Node o = (Node) otherNode;
            return ((int) this.id-o.id;
        } else {
            return -1;
        }
    }

}