package telran.employees;

import java.lang.reflect.InvocationTargetException;

import telran.net.*;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
        Protocol protocol = new CompanyProtocol();
        int port = 4000;
        TcpServer tcpServer = new TcpServer(protocol, port);
        tcpServer.run();
    }
}