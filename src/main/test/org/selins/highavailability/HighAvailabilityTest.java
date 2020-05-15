package org.selins.highavailability;

import org.selins.configuration.Configuration;
import scala.reflect.runtime.HasJavaClass;

import java.util.UUID;

/**
 * @author qdyk
 * @title: HighAvailabilityTest
 * @projectName high-availability
 * @description: TODO
 * @date 2020/5/1316:42
 */
public class HighAvailabilityTest implements HighAvailability {
    HighAvailabilityServer server;
    String endpointId;

    public HighAvailabilityTest(HighAvailabilityService service, String endpointId) {
        this.endpointId = endpointId;
        server = service.startServer(this,endpointId);
    }


    public void isLeader(UUID leaderSessionID) {
        System.out.println("<"+getAddress()+"> isLeader");
        server.confirmLeader(leaderSessionID);
    }


    public void notLeader() {
        System.out.println("<"+getAddress()+"> notLesder");
    }


    public String getAddress() {
        return UUID.randomUUID().toString();
    }


    public void handleError(Exception exception) {

    }

    public static void main(String[] args) throws InterruptedException {
        Configuration configuration = new Configuration();
        configuration.setValue(ConfigOptions.HA_ZOOKEEPER_QUORUM,"127.0.0.1:2181/selins");
        HighAvailabilityService service = new HighAvailabilityService(configuration);

        HighAvailabilityTest entry = new HighAvailabilityTest(service,"test");
        Thread.sleep(Integer.MAX_VALUE);
    }
}