import com.sun.corba.se.spi.activation.Server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerZone {


    String name = null;
    InetAddress multicastAddr = null;
    InetAddress petitionAddr = null;

    DatagramSocket serverSocket;
    MulticastSocket multicastSocket;

    String serverIP = "192.168.31.241";
    String serverIPMulticast = "224.0.0.3";
    int port = 4448;



    public void initalServerStart() throws IOException {

        System.out.println("Server Starting on IP: "+this.serverIP+":"+this.port);

        this.serverSocket = new DatagramSocket();

        byte[] incomingBuffer = new byte[2048];

        this.serverSocket = new DatagramSocket(this.port);

        DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);
        System.out.println("Waiting for Connections....");
        this.serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT

        int clientPort = datagramPacket.getPort();
        InetAddress clientAddress = datagramPacket.getAddress();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(datagramPacket.getData());
        String data = outputStream.toString().trim();

        System.out.println("Client Connection Accepted");
        System.out.println("Data: "+ data);


    }

    public ServerZone(){
        super();
    }


    public ServerZone(String name, String multiCastIP, String petitionIP) throws SocketException, UnknownHostException {

        this.serverSocket = new DatagramSocket();
        this.name = name;
        this.multicastAddr = InetAddress.getByName(multiCastIP);
        this.petitionAddr = InetAddress.getByName(petitionIP);

    }

    public void start() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String location = "[SERVIDOR ZONA "+this.getName()+"]: ";

        System.out.println(location + " Publicar Distribumon ");

        System.out.println(location + " Introducir nombre ");
        System.out.print("> ");
        String distribumonName = bufferedReader.readLine();

        System.out.println(location + " Introducir level ");
        System.out.print("> ");
        String distribumonLevel = bufferedReader.readLine();

        Distribumon distribumon = new Distribumon(Math.random(), distribumonName, distribumonLevel);
        DatagramSocket zoneSocket = new DatagramSocket();

        String message = "New Distribumon Created"+" "+distribumon.getName()+distribumon.getLevel();

        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.multicastAddr, this.port);
        this.serverSocket.send(msgPacket);
        System.out.println("Pokemon Sent");
//
//            System.out.println(location + " Ingresar IP Servidor Central : ");

//            centralServerIP = bufferedReader.readLine();
//
//            System.out.println(location + "Introducir Nombre de Zona a explorar :");
//            System.out.print("> ");
//            zone = bufferedReader.readLine();



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

    public void SendPokemonToBroadCast() throws IOException {
        String msg = "NEW POKEMON FOUND: PIKACHU?";
        DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, this.multicastAddr, this.port);
        DatagramSocket zoneSocket = new DatagramSocket();
        zoneSocket.send(msgPacket);
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
