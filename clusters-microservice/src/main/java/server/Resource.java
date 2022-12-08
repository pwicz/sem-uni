package server;

public class Resource {
    private int CPU;
    private int GPU;
    private int MEM;

    public Resource(int CPU, int GPU, int MEM){
        this.CPU = CPU;
        this.GPU = GPU;
        this.MEM = MEM;
    }

    public int getCPU() {
        return this.CPU;
    }

    public int getGPU() {
        return this.GPU;
    }

    public int getMEM() {
        return this.MEM;
    }

}
