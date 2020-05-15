package org.selins.highavailability;

import org.selins.configuration.Option;
import static org.selins.configuration.Options.key;
/**
 * @author qdyk
 * @title: ConfigOptions
 * @projectName high-availability
 * @description: TODO
 * @date 2020/5/1316:40
 */
public class ConfigOptions {
    /**
     * The ZooKeeper quorum to use, when running Flink in a high-availability mode with ZooKeeper.
     */
    public static final Option<String> HA_ZOOKEEPER_QUORUM =
            key("high-availability.zookeeper.quorum")
                    .noDefaultValue()
                    .withDeprecatedKeys("recovery.zookeeper.quorum")
                    .withDescription("The ZooKeeper quorum to use, when running Flink in a high-availability mode with ZooKeeper.");
}
