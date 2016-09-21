import com.sun.corba.se.spi.activation.Server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Scanner;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerZone implements Runnable {


    String name = null;
    InetAddress multicastAddr = null;
    InetAddress petitionAddr = null;

    DatagramSocket serverSocket;
    MulticastSocket multicastSocket;

    String serverIP = "192.168.8.101";
    String serverIPMulticast = "224.0.0.3";
    int port = 4448;

    Thread runner;

    public void initalServerStart() throws IOException {

        System.out.println("Server Starting on IP: "+this.serverIP+":"+this.port);

        this.serverSocket = new DatagramSocket();

        byte[] incomingBuffer = new byte[2048];

        this.serverSocket = new DatagramSocket(this.port);

        String zoneServerName = this.name;
        Thread menu = new Thread()  {
            public void run() {
                while (true) {
                    System.out.print("[SERVIDOR ZONA: " + zoneServerName + "] ");
                    Scanner scan = new Scanner(System.in);
                    String s = scan.nextLine();
                    System.out.println(s.trim());
                    if (s.equals("Publicar distribumon")) {
                        System.out.println("[SERVIDOR ZONA: " + zoneServerName + "] Introducir nombre");
                        System.out.print("> ");
                        String name = scan.next();

                        System.out.println("[SERVIDOR ZONA: " + zoneServerName + "] Introducir nivel");
                        System.out.print("> ");
                        String level = scan.next();

                        System.out.println("[SERVIDOR ZONA: " + zoneServerName + "] Se ha publicado al Distribumon: " + name);
                        System.out.println("******");
                        System.out.println("id: " + 0);
                        System.out.println("nombre: " + name);
                        System.out.println("nivel: " + level);

                        // TODO add distribumon to List of pokemons, and send multicast message
                    }
                }
            }
        };
        menu.start();

        while(true){
            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);
            System.out.println("Waiting for Connections....");
            this.serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();
            String hostname = clientAddress.getHostAddress();
            this.runner = new Thread(this, "Thread Host: "+hostname);
            System.out.println("Starting thread : "+ this.runner.getName());
            this.runner.start();


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(datagramPacket.getData());
            String data = outputStream.toString().trim();

            System.out.println("Client Connection Accepted");
            System.out.println("Data: "+ data);
        }



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
//
//    public void start() throws IOException {
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        String location = "[SERVIDOR ZONA "+this.getName()+"]: ";
//
//        System.out.println(location + " Publicar Distribumon ");
//
//        System.out.println(location + " Introducir nombre ");
//        System.out.print("> ");
//        String distribumonName = bufferedReader.readLine();
//
//        System.out.println(location + " Introducir level ");
//        System.out.print("> ");
//        String distribumonLevel = bufferedReader.readLine();
//
//        Distribumon distribumon = new Distribumon(Math.random(), distribumonName, distribumonLevel);
//        DatagramSocket zoneSocket = new DatagramSocket();
//
//        String message = "New Distribumon Created"+" "+distribumon.getName()+distribumon.getLevel();
//
//        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.multicastAddr, this.port);
//        this.serverSocket.send(msgPacket);
//        System.out.println("Pokemon Sent");
////
////            System.out.println(location + " Ingresar IP Servidor Central : ");
//
////            centralServerIP = bufferedReader.readLine();
////
////            System.out.println(location + "Introducir Nombre de Zona a explorar :");
////            System.out.print("> ");
////            zone = bufferedReader.readLine();
//
//
//
//    }
//
//    public void sendMessage(String message){
//
//
//        // Create a packet that will contain the data
//        // (in the form of bytes) and send it.
//        // ACA EMPIEZA JUAN PABLO
//        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.multicastAddr, this.port);
//        try {
//            serverSocket.send(msgPacket);
//            System.out.println("Server sent packet with msg: " + message);
//            Thread.sleep(500);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public void SendPokemonToBroadCast() throws IOException {
//        String msg = "NEW POKEMON FOUND: PIKACHU?";
//        DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, this.multicastAddr, this.port);
//        DatagramSocket zoneSocket = new DatagramSocket();
//        zoneSocket.send(msgPacket);
//    }

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


    @Override
    public void run() {
        System.out.println("Yea madie it into a thread");
    }
}
