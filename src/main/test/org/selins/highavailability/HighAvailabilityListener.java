package org.selins.highavailability;

import org.selins.configuration.Configuration;

/**
 * @author qdyk
 * @title: HighAvailabilityListener
 * @projectName high-availability
 * @description: TODO
 * @date 2020/5/1514:46
 */
public class HighAvailabilityListener implements LeaderSelector {
    @Override
    public void notifyLeaderAddress(
            String leaderAddress, //leader连接地址
            String leaderSessionID, // leader session ID
            Throwable throwable) {
        //todo
        System.out.println(leaderAddress);
    }

    public static void main(String[] args) throws InterruptedException {
        Configuration configuration = new Configuration();
        configuration.setValue(ConfigOptions.HA_ZOOKEEPER_QUORUM,"127.0.0.1:2181/selins");
        HighAvailabilityService service = new HighAvailabilityService(configuration);
        SelectorServer selectorServer = service.startSelector(new HighAvailabilityListener(),"test");
        System.out.println(selectorServer.lastLeaderAddress());
    }
}
