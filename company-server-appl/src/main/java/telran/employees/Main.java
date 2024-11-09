package telran.employees;

import telran.net.*;

public class Main {
    public static void main(String[] args) {
        Protocol protocol = new CompanyProtocol();
        int port = 4000;
        TcpServer tcpServer = new TcpServer(protocol, port);
        tcpServer.run();
    }
}