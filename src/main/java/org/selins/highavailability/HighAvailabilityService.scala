package org.selins.highavailability

import java.util.Objects

import org.selins.configuration.Configuration

/**
 * @title: HighAvailabilityService
 * @projectName selins
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/2/7 21:34
 */
class HighAvailabilityService(var configuration: Configuration) {
  this.configuration = Objects.requireNonNull(configuration,"the high availability configuration is null.")
  /**
   * Starts a {@link CuratorFramework} instance and connects it to the given ZooKeeper
   * quorum.
   */
  var curator = Curator.startCuratorFramework(configuration)

  /**
   * Start the specified high availability server
   *
   * @param endpoint
   * @param path
   * @tparam C
   * @return Returns the created server
   */
  def startServer[C <: HighAvailability](
    endpoint: C,
    path: String)
  : HighAvailabilityServer = {
    requireNonNull(path)
    new HighAvailabilityServer(curator, endpoint, path)
  }

  /**
   * Start the specified high availability server
   *
   * @param path
   * @tparam C
   * @return Returns the created server
   */
  def startSelector[C <: LeaderSelector](
    path: String)
  : SelectorServer = {
    requireNonNull(path)
    new SelectorServer(curator, null, path)
  }

  /**
   * Start the specified high availability server
   *
   * @param listener
   * @param path
   * @tparam C
   * @return Returns the created server
   */
  def startSelector[C <: LeaderSelector](
    listener: LeaderSelector,
    path: String)
  : SelectorServer = {
    requireNonNull(path)
    new SelectorServer(curator, listener, path)
  }

  /**
   * 校验路径是否为空
   * @param path
   * @return
   */
  def requireNonNull(
    path: String)
  : String ={
    Objects.requireNonNull(path,"the high available zk path is null.")
  }
}