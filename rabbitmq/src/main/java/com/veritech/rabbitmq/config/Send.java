package com.veritech.rabbitmq.config;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;


public class Send {
    private final static String QUEUE_NAME = "teste_java";
    public static void main(String[] argv) throws Exception {

        char[] trustPassphrase = "changeit".toCharArray();
        KeyStore tks = KeyStore.getInstance("JKS");
        tks.load(new FileInputStream("C:\\Users\\igor.ferreira\\git\\rabbitmq\\rabbitmq\\target\\classes\\truststore.jks"), trustPassphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(tks);

        ConnectionFactory factory = new ConnectionFactory();
        
        factory.setHost("rabbitmqfila.hm.ans.gov.br");
        factory.setPort(443);
        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(null, tmf.getTrustManagers(), null);
        SSLParameters sslParameters = new SSLParameters();
        URL url = new URL("https://rabbitmqfila.hm.ans.gov.br");
        List sniHostNames = new ArrayList(1);
        sniHostNames.add(new SNIHostName(url.getHost()));
        sslParameters.setServerNames(sniHostNames);
        SSLSocketFactory SSLSocketFactory = new SSLSocketFactoryWrapper(c.getSocketFactory(), sslParameters);
        factory.useSslProtocol(c);
        factory.setSocketFactory(SSLSocketFactory);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
             channel.queueDeclare(QUEUE_NAME, false, false, false, null);
             String message = "Hello World!";
             channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
             System.out.println(" [x] Sent '" + message + "'");
             GetResponse chResponse = channel.basicGet(QUEUE_NAME, false);
             if (chResponse == null) {
                 System.out.println("No message retrieved");
             } else {
                 byte[] body = chResponse.getBody();
                 System.out.println("Received: " + new String(body));
             }

            channel.close();
            connection.close();
        }

    }
}