import java.io.IOException;
import java.net.*;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerZone {

    DatagramSocket serverSocket;
    String name = null;
    InetAddress multicastAddr = null;
    InetAddress petitionAddr = null;

    int port = 4446;

    public ServerZone(String name, String multiCastIP, String petitionIP) throws SocketException, UnknownHostException {

        this.serverSocket = new DatagramSocket();
        this.name = name;
        this.multicastAddr = InetAddress.getByName(multiCastIP);
        this.petitionAddr = InetAddress.getByName(petitionIP);
        this.start();

    }

    public void start(){



        try(DatagramSocket zoneSocket = new DatagramSocket()){
            for (int i =0 ; i< 5 ; i++){
                String msg = "Sent msg NO: "+ i;
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, this.multicastAddr, this.port);
                zoneSocket.send(msgPacket);
                System.out.println("Server sent packet with msg: " + msg);
                Thread.sleep(500);

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){


        // Create a packet that will contain the data
        // (in the form of bytes) and send it.
        // ACA EMPIEZA JUAN PABLO
        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.multicastAddr, this.port);
        try {
            serverSocket.send(msgPacket);
            System.out.println("Server sent packet with msg: " + message);
            Thread.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getMulticastAddr() {
        return multicastAddr;
    }

    public void setMulticastAddr(InetAddress multicastAddr) {
        this.multicastAddr = multicastAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getPetitionAddr() {
        return petitionAddr;
    }

    public void setPetitionAddr(InetAddress petitionAddr) {
        this.petitionAddr = petitionAddr;
    }
}
