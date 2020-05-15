# high-availability

这是一个通用的实现高可用的工具, 可以通过此工具非常简便的实现高可用, 来源于flink源码, 并进行简化处理。

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
##调用方法
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
