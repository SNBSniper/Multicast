package cl.utfsm.zone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class Zone {

    String name = null;
    InetAddress multicastAddr = null;
    InetAddress petitionAddr = null;

    DatagramSocket serverSocket;
    MulticastSocket multicastSocket;

    String multicastIP;
    int multicastPort;
    String petitionIP;
    int petitionPort;

    LinkedList<String> distribumons;

    public static void main (String args[]) throws IOException {

        Zone zone = new Zone();
        zone.initalServerStart();

    }

    public void initalServerStart() throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        this.distribumons = new LinkedList<String>();
        this.distribumons.add("alpha-centauri:20");
        this.distribumons.add("canis-mayoris:50");
        this.distribumons.add("geminis:60");
        this.distribumons.add("multipacketon:600");
        this.distribumons.add("utpmon:300");

        this.createZoneServer();

        System.out.println("[Servidor Zona] Server Starting on IP: "+this.petitionIP+":"+this.petitionPort);
        System.out.println("[Servidor Zona] Multicast Starting on IP "+this.multicastIP+":"+this.multicastPort);

        this.serverSocket = new DatagramSocket(this.petitionPort);

        String zoneServerName = this.name;
        Thread menu = new Thread()  {
            public void run() {
                while (true) {
                    System.out.print("[Servidor Zona: " + zoneServerName + "] (1) Publicar distribumon\n> ");
                    Scanner scan = new Scanner(System.in);
                    int s = scan.nextInt(); scan.nextLine();
                    if (s == 1) {
                        System.out.println("[Servidor Zona: " + zoneServerName + "] Introducir nombre");
                        System.out.print("> ");
                        String name = scan.nextLine().trim();

                        System.out.println("[Servidor Zona: " + zoneServerName + "] Introducir nivel");
                        System.out.print("> ");
                        String level = scan.next();

                        System.out.println("[Servidor Zona: " + zoneServerName + "] Se ha publicado el Distribumon: " + name);
                        System.out.println("******");
                        System.out.println("id: " + 0);
                        System.out.println("nombre: " + name);
                        System.out.println("nivel: " + level);

                        Zone.this.distribumons.add(name + ":" + level);
                        try {
                            sendMessage(name);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        menu.start();

        while(true){
            byte[] incomingBuffer = new byte[2048];
            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);
            this.serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(datagramPacket.getData());
            String data = outputStream.toString().trim();

            final String parameter = data;
            Thread t = new Thread(new Runnable() {
                String p = parameter;
                public void run() {
                    if (p.equals("capture")) Zone.this.capture(clientAddress, clientPort);
                    if (p.equals("list")) Zone.this.listDistribumons(clientAddress, clientPort);
                }
            });
            t.start();
        }
    }

    private void sendMessageToMulticast(String message){
        InetAddress multicastAddress = null;
        try {
            multicastAddress = InetAddress.getByName(this.multicastIP);
            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, multicastAddress, this.multicastPort);
            datagramSocket.send(msgPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createZoneServer() {
        Scanner scan = new Scanner(System.in);

        System.out.print("[Server Zone] Nombre Servidor\n> ");
        String name = scan.nextLine().trim();
        this.setName(name);

        System.out.print("[Server Zone] IP Multicast\n> ");
        String multicastIP = scan.nextLine().trim();
        this.setMulticastIP(multicastIP);

        System.out.print("[Server Zone] Puerto Multicast\n> ");
        int multicastPort = scan.nextInt(); scan.nextLine();
        this.setMulticastPort(multicastPort);

        System.out.print("[Server Zone] IP Peticiones\n> ");
        String petitionIP = scan.nextLine().trim();
        this.setPetitionIP(petitionIP);

        System.out.print("[Server Zone] Puerto Peticiones\n> ");
        int petitionPort = scan.nextInt(); scan.nextLine();
        this.setPetitionPort(petitionPort);
    }

    public Zone(){
        super();
    }


    public Zone(String name, String multiCastIP, String petitionIP) throws SocketException, UnknownHostException {

        this.serverSocket = new DatagramSocket();
        this.name = name;
        this.multicastAddr = InetAddress.getByName(multiCastIP);
        this.petitionAddr = InetAddress.getByName(petitionIP);

    }

    public void sendMessage(String message) throws UnknownHostException {
        InetAddress multicastAddress = InetAddress.getByName(this.multicastIP);

        DatagramPacket msgPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, multicastAddress, this.multicastPort);

        try {
            serverSocket.send(msgPacket);
            Thread.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void capture(InetAddress clientAddress, Integer clientPort) {
        String response;
        if (this.distribumons.isEmpty()) {
            response = "error;no quedan distribumones";
        }else {
            // TODO add random chance of capture
            response = "capture;" + this.distribumons.remove();
        }
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
        try {
            Zone.this.serverSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listDistribumons(InetAddress clientAddress, Integer clientPort) {
        String response = "list";
        for (Object d: this.distribumons){
            response += ";" + (String)d;
        }
        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
        try {
            Zone.this.serverSocket.send(responsePacket);
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

    public InetAddress getPetitionAddr() {
        return petitionAddr;
    }

    public void setPetitionAddr(InetAddress petitionAddr) {
        this.petitionAddr = petitionAddr;
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
