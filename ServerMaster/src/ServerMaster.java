import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by danielftapiar on 9/10/16.
 */
public class ServerMaster {

    InetAddress localNetworkIp;

    int serverPort = 4445;
    String serverIP = "192.168.31.241";
    List<ZoneServerOBJ> zoneServers;
    DatagramSocket serverSocket;

    public void start() throws IOException, InterruptedException {
       // BufferedReader bufferedReader = null;
//            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//            String location="[Servidor Central]: ";
//            System.out.println(location + "Agrega Servidor de Zona ");
//
//            System.out.println(location +"Nombre : ");
//            System.out.print("> ");
//            String name = bufferedReader.readLine();
//
//            System.out.println(location +"IP Multicast :");
//            System.out.print("> ");
//            String multicastIP = bufferedReader.readLine();
//
//            System.out.println(location +": IP Peticiones :");
//            System.out.print("> ");
//            String petitionIP = bufferedReader.readLine();
//
//            System.out.println(location +": Puerto Peticiones :");
//            System.out.print("> ");
//            String petitionPort = bufferedReader.readLine();

        String name = "Zona 1";
        String multicastIP = "224.0.0.3";
        String multicastPort = "4449";
        String petitionIP = "192.168.31.241";
        String petitionPort = "4448";

        this.createZoneServer(name,multicastIP, petitionIP, Integer.parseInt(multicastPort), Integer.parseInt(petitionPort));

        this.initalServerStart();


        byte[] incomingBuffer = new byte[2048];


        while(true){ // As of now, Single Threaded, Later Make Multi Thread.


            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);


            serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();

            ZoneServerOBJ serverZone = this.acceptClientConnection(datagramPacket); // CONNECTS AND SEARCHS FOR ZONE SERVER

            if(serverZone == null){
                String response = "404;Zone not found on the server";
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }else{
                String response = "200; Zone found on Server ;"+serverZone.getMulticastIP()+";"+serverZone.getMulticastPort()+";"+serverZone.getPetitionIP()+";"+serverZone.getPetitionPort();
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }

        }



    }

    private ZoneServerOBJ acceptClientConnection(DatagramPacket datagramPacket) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(datagramPacket.getData());
        String data = outputStream.toString().trim();

        System.out.println("Client Connection Accepted");
        System.out.println("Data: "+ data);

        System.out.println("Requesting Zone: "+ data);
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
        if(this.zoneServers == null)
            this.zoneServers = new ArrayList<ZoneServerOBJ>();

        ZoneServerOBJ zone = new ZoneServerOBJ();

        zone.setName(name);
        zone.setMulticastIP(multicastIP);
        zone.setPetitionIP(petitionIP);
        zone.setPetitionPort(petitionPort);
        zone.setMulticastPort(multicastPort);

        zoneServers.add(zone);

        System.out.println("Zone Server Created Succesfully");
    }
}
