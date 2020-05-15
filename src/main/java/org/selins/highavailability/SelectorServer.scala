package org.selins.highavailability

import java.io.IOException
import java.util.Objects

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.api.UnhandledErrorListener
import org.apache.curator.framework.recipes.cache.{NodeCache, NodeCacheListener}
import org.apache.logging.log4j.LogManager
import org.slf4j.LoggerFactory

/**
 * @title: HighAvailabilityQuerier
 * @projectName selins
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/2/7 21:36
 */
class SelectorServer(
  var curator: CuratorFramework,
  listener: LeaderSelector,
  var path: String) extends NodeCacheListener with UnhandledErrorListener {

  this.curator =  Objects.requireNonNull(curator)
  this.path =  Objects.requireNonNull(path)
  var cache = new NodeCache(curator, "/leader/" + path)
  cache.start()
  cache.rebuild()
  var lastLeaderAddress: String = null
  var lastLeaderSessionID: String = null

  notifyLeaderAddress()

  val LOG = LogManager.getLogger(classOf[SelectorServer])
  LOG.info("Starting QuerierServer {}.", path)

  override def nodeChanged(): Unit = {
    this.synchronized {
      try notifyLeaderAddress()
      catch {
        case e: Exception =>
          notifyLeaderAddress(e.getCause)
          throw e
      }
    }
  }

  def notifyLeaderAddress(): Unit = {
    val childData = cache.getCurrentData
    if (childData != null) {
      val info = new String(childData.getData).split(",");
      lastLeaderAddress = info(0)
      lastLeaderSessionID = info(1)
    }else{
      lastLeaderAddress = null
      lastLeaderSessionID = null
    }
    notifyLeaderAddress(null)
  }

  def notifyLeaderAddress(throwable: Throwable): Unit = {
    if (listener != null)
      listener.notifyLeaderAddress(lastLeaderAddress, lastLeaderSessionID, null)
  }


  @throws[Exception]
  def stop(): Unit = {
    LOG.info("Stopping QuerierServer {}.", path)

    curator.getUnhandledErrorListenable.removeListener(this)
    try cache.close()
    catch {
      case e: IOException =>
        throw new Exception("Could not properly stop the QuerierServer.", e)
    }
  }

  override def unhandledError(s: String, throwable: Throwable): Unit = {
    notifyLeaderAddress(throwable)
  }
}
