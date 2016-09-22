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
    String serverIP = "10.6.43.79";
    List<ServerZone> zoneServers;
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
        String petitionIP = "10.6.43.79";
        String petitionPort = "4448";

        createZoneServer(name,multicastIP, petitionIP);

        this.initalServerStart();


        byte[] incomingBuffer = new byte[2048];


        while(true){ // As of now, Single Threaded, Later Make Multi Thread.


            DatagramPacket datagramPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);


            serverSocket.receive(datagramPacket); //HERE IT STOPS AND WAITS FOR CLIENT
            int clientPort = datagramPacket.getPort();
            InetAddress clientAddress = datagramPacket.getAddress();

            ServerZone serverZone = this.acceptClientConnection(datagramPacket); // CONNECTS AND SEARCHS FOR ZONE SERVER

            if(serverZone == null){
                String response = "404;Zone not found on the server";
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }else{
                String response = "200; Zone found on Server ;"+serverZone.getMulticastAddr().getHostAddress()+";"+serverZone.getPetitionAddr().getHostAddress()+";"+serverZone.getPetitionPort();
                System.out.println(response);
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, clientAddress, clientPort);
                this.serverSocket.send(responsePacket);
            }

        }



    }

    private ServerZone acceptClientConnection(DatagramPacket datagramPacket) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(datagramPacket.getData());
        String data = outputStream.toString().trim();

        System.out.println("Client Connection Accepted");
        System.out.println("Data: "+ data);

        System.out.println("Requesting Zone: "+ data);
        ServerZone lookup = null;
        for (ServerZone i  : this.zoneServers){
            if(i.getName().equals(data)  )
                lookup = i;
        }

        return lookup;
    }

    private void initalServerStart() throws UnknownHostException, SocketException {
        this.localNetworkIp = InetAddress.getByName(this.serverIP);

        System.out.println("Starting Master Server");
        System.out.println("Server Starting on IP: "+this.localNetworkIp+":"+this.serverPort);
        System.out.println("Waiting for Connections....");
        this.serverSocket = new DatagramSocket(this.serverPort);
    }

    private void createZoneServer(String name, String multicastIP, String petitionIP) {
        if(this.zoneServers == null)
            this.zoneServers = new ArrayList<ServerZone>();
        try {
            ServerZone zone = new ServerZone(name,multicastIP, petitionIP);

            zoneServers.add(zone);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Zone Server Created Succesfully");
    }
}
