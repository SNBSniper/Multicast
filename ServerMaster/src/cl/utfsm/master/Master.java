package cl.utfsm.master;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class Master {

    InetAddress localNetworkIp;

    final static int serverPort = 4445;
    final static String serverIP = "127.0.0.1";
    List<ZoneServerOBJ> zoneServers;
    DatagramSocket serverSocket;

    public static void main(String[] args) throws InterruptedException {
        Master masterServer = new Master();

        try {
            masterServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException, InterruptedException {
        this.zoneServers = new ArrayList<ZoneServerOBJ>();

        Thread menu = new Thread()  {
            public void run() {
                while (true) {
                    System.out.println("[Servidor Central] (1) Agregar servidor de zona");
                    System.out.print("> ");
                    Scanner scan = new Scanner(System.in);
                    int s = scan.nextInt(); scan.nextLine();
                    if (s == 1) {
                        System.out.println("[Servidor Central] Nombre");
                        System.out.print("> ");
                        String name = scan.nextLine();

                        System.out.println("[SERVIDOR ZONA] IP Multicast");
                        System.out.print("> ");
                        String multicastIP = scan.nextLine();

                        System.out.println("[SERVIDOR ZONA] Puerto Multicast");
                        System.out.print("> ");
                        String multicastPort = scan.nextLine();

                        System.out.println("[SERVIDOR ZONA] IP Peticiones");
                        System.out.print("> ");
                        String petitionIP = scan.nextLine();

                        System.out.println("[SERVIDOR ZONA] Puerto Peticiones");
                        System.out.print("> ");
                        String petitionPort = scan.nextLine();

                        Master.this.createZoneServer(name,multicastIP, petitionIP, Integer.parseInt(multicastPort), Integer.parseInt(petitionPort));
                    }
                }
            }
        };
        menu.start();

        this.initalServerStart();

        byte[] incomingBuffer = new byte[2048];
        while(true){ // As of now, Single Threaded, Later Make Multi Thread.
            java.util.Arrays.fill(incomingBuffer, (byte) 0);
            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);

            serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();

            ZoneServerOBJ serverZone = this.acceptClientConnection(datagramPacket, clientAddress); // CONNECTS AND SEARCHS FOR ZONE SERVER

            if(serverZone == null){
                System.out.println("[Servidor central] Servidor de zona no encontrado");
                String response = "404;Zone not found on the server";
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }else{
                System.out.println("[Servidor central] Nombre: " + serverZone.getName() +
                                   ", IP Multicast: "            + serverZone.getMulticastIP() +
                                   ", Puerto Multicast: "        + serverZone.getMulticastPort() +
                                   ", IP Peticiones: "           + serverZone.getPetitionIP() +
                                   ", Puerto Peticiones: "       + serverZone.getPetitionPort());

                String response = "200;Zone found on Server;"+serverZone.getMulticastIP()+";"+serverZone.getMulticastPort()+";"+serverZone.getPetitionIP()+";"+serverZone.getPetitionPort();
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }

        }



    }

    private ZoneServerOBJ acceptClientConnection(DatagramPacket datagramPacket, InetAddress clientAddress) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(datagramPacket.getData());
        String data = outputStream.toString().trim();

        System.out.println("[Servidor Central] Respuesta a " + clientAddress + " por " + data);

        ZoneServerOBJ lookup = null;
        for (ZoneServerOBJ i  : this.zoneServers){
            if(i.getName().equals(data)  )
                lookup = i;
        }

        return lookup;
    }

    private void initalServerStart() throws UnknownHostException, SocketException {
        this.localNetworkIp = InetAddress.getByName(this.serverIP);

        System.out.println("Starting Master Server");
        System.out.println("Server Starting on IP: "+this.serverIP+":"+this.serverPort);
        System.out.println("Waiting for Connections....");
        this.serverSocket = new DatagramSocket(this.serverPort);
    }

    private void createZoneServer(String name, String multicastIP, String petitionIP, Integer multicastPort, Integer petitionPort) {
        ZoneServerOBJ zone = new ZoneServerOBJ();

        zone.setName(name);
        zone.setMulticastIP(multicastIP);
        zone.setPetitionIP(petitionIP);
        zone.setPetitionPort(petitionPort);
        zone.setMulticastPort(multicastPort);

        zoneServers.add(zone);

        System.out.println("[Servidor Central]: Servidor de zona " + name + " creado exitosamente");
    }
}
