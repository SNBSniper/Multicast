import com.sun.corba.se.spi.activation.Server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerZone {

    String name = null;
    InetAddress multicastAddr = null;
    InetAddress petitionAddr = null;

    DatagramSocket serverSocket;
    MulticastSocket multicastSocket;

    String serverIP = "10.6.43.79";
    String serverIPMulticast = "224.0.0.3";
    int port = 4448;
    LinkedList<String> distribumons;

    public void initalServerStart() throws IOException {
        this.distribumons = new LinkedList<String>();
        this.distribumons.add("alpha-centauri:20");
        this.distribumons.add("canis-mayoris:50");

        System.out.println("Server Starting on IP: "+this.serverIP+":"+this.port);

        this.serverSocket = new DatagramSocket();


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

                        ServerZone.this.distribumons.add(name + ":" + level);
                        // TODO and send multicast message
                    }
                }
            }
        };
        menu.start();

        while(true){
            byte[] incomingBuffer = new byte[2048];
            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);
            System.out.println("Waiting for Connections....");
            this.serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();
            String hostname = clientAddress.getHostAddress();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(datagramPacket.getData());
            String data = outputStream.toString().trim();

            System.out.println("Client Connection Accepted");
            System.out.println("Data: "+ data);

            System.out.println("Starting thread");
            final String parameter = data;
            Thread t = new Thread(new Runnable() {
                String p = parameter;
                public void run() {
                    System.out.println("Request: " + p);
                    if (p.equals("capture")) ServerZone.this.capture(clientAddress, clientPort);
                    if (p.equals("list")) ServerZone.this.listDistribumons(clientAddress, clientPort);
                }
            });
            t.start();
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

    private void capture(InetAddress clientAddress, Integer clientPort) {
        String response;
        if (this.distribumons.isEmpty()) {
            response = "error;no quedan distribumones";
        }else {
            // TODO add random chance of capture
            response = "capture;" + this.distribumons.remove();
            // response = "miss;aweonao";
        }
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
        try {
            ServerZone.this.serverSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listDistribumons(InetAddress clientAddress, Integer clientPort) {
        String response = "list";
        for (Object d: this.distribumons){
            response += ";" + (String)d;
        }
        System.out.println(response);
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
        try {
            ServerZone.this.serverSocket.send(responsePacket);
        } catch (IOException e) {
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
