package org.selins.highavailability

import java.io.IOException
import java.util.Objects

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.api.UnhandledErrorListener
import org.apache.curator.framework.recipes.cache.{NodeCache, NodeCacheListener}
import org.apache.logging.log4j.LogManager

/**
 * @title: HighAvailabilityQuerier
 * @projectName selins
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/2/7 21:36
 */
class SelectorServer(
  private var curator: CuratorFramework,
  private val listener: LeaderSelector,
  private var endpointId: String) extends NodeCacheListener with UnhandledErrorListener {

  this.curator =  Objects.requireNonNull(curator)
  this.endpointId =  Objects.requireNonNull(endpointId)
  private val cache = new NodeCache(curator, "/leader/" + endpointId)
  cache.start()
  cache.rebuild()
  var leaderAddress: String = null
  var leaderSessionID: String = null

  notifyLeaderAddress()

  private val LOG = LogManager.getLogger(classOf[SelectorServer])
  LOG.info("Starting QuerierServer {}.", endpointId)

  protected override def nodeChanged(): Unit = {
    this.synchronized {
      try notifyLeaderAddress()
      catch {
        case e: Exception =>
          notifyLeaderAddress(e.getCause)
          throw e
      }
    }
  }

  private def notifyLeaderAddress(): Unit = {
    val childData = cache.getCurrentData
    if (childData != null) {
      val info = new String(childData.getData).split(",");
      leaderAddress = info(0)
      leaderSessionID = info(1)
    }else{
      leaderAddress = null
      leaderSessionID = null
    }
    notifyLeaderAddress(null)
  }

  private  def notifyLeaderAddress(throwable: Throwable): Unit = {
    if (listener != null)
      listener.notifyLeaderAddress(leaderAddress, leaderSessionID, null)
  }


  @throws[Exception]
  def stop(): Unit = {
    LOG.info("Stopping QuerierServer {}.", endpointId)

    curator.getUnhandledErrorListenable.removeListener(this)
    try cache.close()
    catch {
      case e: IOException =>
        throw new Exception("Could not properly stop the QuerierServer.", e)
    }
  }

  protected override def unhandledError(s: String, throwable: Throwable): Unit = {
    notifyLeaderAddress(throwable)
  }
}
