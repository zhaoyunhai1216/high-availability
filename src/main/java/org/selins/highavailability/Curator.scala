package org.selins.highavailability

import java.util

import org.apache.curator.framework.api.ACLProvider
import org.apache.curator.framework.imps.DefaultACLProvider
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.{CreateMode, KeeperException, ZooDefs}
import org.selins.configuration.Configuration

/**
 * @title: Curator
 * @projectName selins
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/2/7 20:26
 */
object Curator {

  /**
   * Starts a {@link CuratorFramework} instance and connects it to the given ZooKeeper
   * quorum.
   *
   * @param configuration { @link Configuration} object containing the configuration values
   * @return { @link CuratorFramework} instance
   */
  def startCuratorFramework(
    configuration: Configuration)
  : CuratorFramework = {

    val zkQuorum = configuration.getValue(
      ConfigOptions.HA_ZOOKEEPER_QUORUM
    )

    val cf = CuratorFrameworkFactory.builder
      .connectString(zkQuorum.split("\\/")(0))
      .sessionTimeoutMs(1000)
      .aclProvider(new DefaultACLProvider)
      .namespace(zkQuorum.split("\\/")(1))
      .retryPolicy(new ExponentialBackoffRetry(5000, 3))
      .build()

    cf.start()
    cf
  }


  def upsert(
    curator: CuratorFramework,
    path: String,
    data: Array[Byte])
  : Boolean = {
    val stat = curator.checkExists.forPath(path)
    if (stat == null) {
      insert(curator, path, data)
    } else {
      update(curator, path, data)
    }
  }

  def upsertIfNotEquals(
    curator: CuratorFramework,
    path: String,
    data: Array[Byte])
  : Boolean = {
    val d = select(curator,path)
    if (d == null) {
      insert(curator, path, data)
    } else if(data == d){
      update(curator, path, data)
    }else{
      true
    }
  }


  def select(
    curator: CuratorFramework,
    path: String)
  : Array[Byte] = {
    try {
      curator.getData.forPath(path)
    } catch {
      case e: KeeperException.NoNodeException => null
    }
  }

  def insert(
    curator: CuratorFramework,
    path: String,
    data: Array[Byte])
  : Boolean = {
    val stat = curator.checkExists.forPath(path)
    if (stat != null)
      false
    else {
      curator.create.creatingParentsIfNeeded.withMode(CreateMode.EPHEMERAL).forPath(path, data)
      true
    }
  }

  def update(
    curator: CuratorFramework,
    path: String,
    data: Array[Byte])
  : Boolean = {
    try {
      curator.setData.forPath(path, data)
      true
    } catch {
      case e: KeeperException.NoNodeException => false
    }
  }

  def delete(
    curator: CuratorFramework,
    path: String)
  : Boolean = {
    try {
      curator.delete.forPath(path)
      true
    } catch {
      case e: KeeperException.NoNodeException => true
    }
  }

  def deleteIfOtherSession(
    curator: CuratorFramework,
    path: String)
  : Unit = {
    val sessionID = curator.getZookeeperClient.getZooKeeper.getSessionId
    val stat = curator.checkExists.forPath(path)
    if (stat != null) {
      val owner = stat.getEphemeralOwner
      if (owner != sessionID) delete(curator, path)
    }
  }
}


/**
 * Secure {@link ACLProvider} implementation.
 */
class SecureAclProvider extends ACLProvider {
  override def getDefaultAcl: util.List[ACL] = ZooDefs.Ids.CREATOR_ALL_ACL

  override def getAclForPath(path: String): util.List[ACL] = ZooDefs.Ids.CREATOR_ALL_ACL
}
