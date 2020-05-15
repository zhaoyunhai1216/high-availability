package org.selins.highavailability

import java.util.{Objects, UUID}

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.api.UnhandledErrorListener
import org.apache.curator.framework.recipes.cache.{NodeCache, NodeCacheListener}
import org.apache.curator.framework.recipes.leader.{LeaderLatch, LeaderLatchListener}
import org.apache.logging.log4j.LogManager

/**
 * @title: HighAvailabilityServer
 * @projectName selins
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/2/7 21:41
 */
class HighAvailabilityServer(
  var curator: CuratorFramework,
  contender: HighAvailability,
  var endpointId: String)
  extends LeaderLatchListener with NodeCacheListener with UnhandledErrorListener {

  this.curator =  Objects.requireNonNull(curator)
  endpointId = Objects.requireNonNull(endpointId)

  var issuedLeaderSessionID: UUID = null
  var confirmedLeaderSessionID: UUID = null

  curator.getUnhandledErrorListenable.addListener(this)
  var leaderLatch = new LeaderLatch(curator, "/latch/" + endpointId)
  leaderLatch.addListener(this)
  leaderLatch.start()

  var cache = new NodeCache(curator, "/leader/" + endpointId)
  cache.getListenable.addListener(this)
  cache.start()

  val LOG = LogManager.getLogger(classOf[HighAvailabilityServer])

  LOG.info("Starting HighAvailabilityServer {}.", this.endpointId)

  override def isLeader: Unit = {
    this.synchronized {
      issuedLeaderSessionID = UUID.randomUUID
      confirmedLeaderSessionID = null
      contender.isLeader(issuedLeaderSessionID)
    }
  }


  override def notLeader: Unit = {
    this.synchronized {
      issuedLeaderSessionID = null
      confirmedLeaderSessionID = null
      contender.notLeader()
    }
  }


  def hasLeadership(
    leaderSessionId: UUID)
  : Boolean = {
    leaderLatch.hasLeadership && leaderSessionId == issuedLeaderSessionID
  }


  def confirmLeader(
    leaderSessionID: UUID)
  : Unit = {

    Objects.requireNonNull(leaderSessionID)
    if (hasLeadership(leaderSessionID)) {
      this.synchronized {
        confirmedLeaderSessionID = leaderSessionID
        writeLeader(confirmedLeaderSessionID)
      }
    }
  }

  override def nodeChanged: Unit = {
    if (leaderLatch.hasLeadership)
      writeLeader(confirmedLeaderSessionID)
  }

  /**
   * Writes the current leader's address as well the given leader session ID to ZooKeeper.
   *
   * @param leaderSessionID Leader session ID which is written to ZooKeeper
   */
  def writeLeader(
    leaderSessionID: UUID)
  : Unit = {

    if (leaderLatch.hasLeadership) {
      Curator.deleteIfOtherSession(curator, "/leader/" + endpointId)
      val data = contender.getAddress + "," + leaderSessionID
      Curator.upsertIfNotEquals(curator, "/leader/" + endpointId, data.getBytes)
    }
  }

  override def unhandledError(s: String, throwable: Throwable): Unit = {
    contender.handleError(new RuntimeException("Unhandled error in ZooKeeperLeaderElectionService: " + s, throwable))
  }
}
