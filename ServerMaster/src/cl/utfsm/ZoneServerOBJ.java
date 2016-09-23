package cl.utfsm;
/**
 * Created by danielftapiar on 9/22/16.
 */
public class ZoneServerOBJ {

    String multicastIP;
    int multicastPort;
    String petitionIP;
    int petitionPort;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMulticastIP() {
        return multicastIP;
    }

    public void setMulticastIP(String multicastIP) {
        this.multicastIP = multicastIP;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
    }

    public String getPetitionIP() {
        return petitionIP;
    }

    public void setPetitionIP(String petitionIP) {
        this.petitionIP = petitionIP;
    }

    public int getPetitionPort() {
        return petitionPort;
    }

    public void setPetitionPort(int petitionPort) {
        this.petitionPort = petitionPort;
    }
}
