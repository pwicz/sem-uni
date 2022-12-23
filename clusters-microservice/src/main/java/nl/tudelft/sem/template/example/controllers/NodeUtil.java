package nl.tudelft.sem.template.example.controllers;

import commons.Resource;
import java.util.List;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.Node;


@NoArgsConstructor
public class NodeUtil {

    /**
     * Converts a list of nodes int a resource object.
     * Objects CPU, GPU, MEM are sum of themselves from the nodes
     *
     * @param nodes nodes to sum up the resources
     */

    public static Resource resourceCreator(List<Node> nodes) {
        if (nodes == null) {
            return new Resource(0, 0, 0);
        }
        int cpu = 0;
        int gpu = 0;
        int mem = 0;
        for (Node n : nodes) {
            cpu += n.getCpu();
            gpu += n.getGpu();
            mem += n.getMemory();
        }
        return new Resource(cpu, gpu, mem);
    }
}
