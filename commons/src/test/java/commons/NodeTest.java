package commons;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class NodeTest {

    String name;
    String url;
    String faculty;
    String token;
    int cpu;
    int gpu;
    int memory;
    Node node;

    @BeforeEach
    void setUp() {
        name = "name";
        url = "url";
        faculty = "faculty";
        token = "token";
        cpu = 1;
        gpu = 1;
        memory = 1;

        node = new Node(name, url, faculty, token, cpu, gpu, memory);
    }

    @Test
    void getNameTest() {
        assertThat(node.getName()).isEqualTo("name");
    }

    @Test
    void getUrlTest() {
        assertThat(node.getUrl()).isEqualTo("url");
    }

    @Test
    void getFacultyTest() {
        assertThat(node.getFaculty()).isEqualTo("faculty");
    }

    @Test
    void getTokenTest() {
        assertThat(node.getToken()).isEqualTo("token");
    }

    @Test
    void setTokenTest() {
        node.setToken("newToken");
        assertThat(node.getToken()).isEqualTo("newToken");
    }

    @Test
    void getCpuTest() {
        assertThat(node.getCpu()).isEqualTo(1);
    }

    @Test
    void getGpuTest() {
        assertThat(node.getGpu()).isEqualTo(1);
    }

    @Test
    void getMemoryTest() {
        assertThat(node.getMemory()).isEqualTo(1);
    }
}