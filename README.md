# high-availability

这是一个通用的实现高可用的工具, 可以通过此工具非常简便的实现高可用, 只要实现一个接口接可以, 此工具主要源于flink高可用模块，参考并稍做改动而成。
其中主要技术为利用zookeeper/Curator 进行实现高可用.

# 使用方法
##引用依赖

    <repository>
        <id>mvn-repo</id>
        <url>https://github.com/zhaoyunhai1216/mvn-repo/master</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
    
    <dependency>
        <groupId>org.selins</groupId>
        <artifactId>high-availability</artifactId>
        <version>0.1.0</version>
    </dependency>

    #如果以上引用不好用,可以自行下载编译
##实现方法

    //接口实现
    public class HighAvailabilityTest implements HighAvailability {
        HighAvailabilityServer server;
        String endpointId;
    
        public HighAvailabilityTest(HighAvailabilityService service, String endpointId) {
            this.endpointId = endpointId;
            server = service.startServer(this,endpointId);
        }
    
    
        public void isLeader(UUID leaderSessionID) {
            //TODO
            server.confirmLeader(leaderSessionID);
        }
    
    
        public void notLeader() {
            //TODO
        }
    
    
        public String getAddress() {
            return UUID.randomUUID().toString();
        }
    
    
        public void handleError(Exception exception) {
            //TODO
        }
    }
    
    //调用样例
    Configuration configuration = new Configuration();
    configuration.setValue(ConfigOptions.HA_ZOOKEEPER_QUORUM,"127.0.0.1:2181/selins");
    HighAvailabilityService service = new HighAvailabilityService(configuration);
    HighAvailabilityTest entry = new HighAvailabilityTest(service,"test"); 
##查询leader方法

    //查询监听
    public class HighAvailabilityListener implements LeaderSelector {
        @Override
        public void notifyLeaderAddress(
                String leaderAddress, //leader连接地址
                String leaderSessionID, // leader session ID
                Throwable throwable) {
            //todo
            System.out.println(leaderAddress);
        }
    }
    
    //调用样例
    Configuration configuration = new Configuration();
    configuration.setValue(ConfigOptions.HA_ZOOKEEPER_QUORUM,"127.0.0.1:2181/selins");
    HighAvailabilityService service = new HighAvailabilityService(configuration);
    SelectorServer selectorServer = service.startSelector(new HighAvailabilityListener(),"test");
    System.out.println(selectorServer.lastLeaderAddress());