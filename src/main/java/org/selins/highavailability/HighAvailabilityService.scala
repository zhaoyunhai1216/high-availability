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
class HighAvailabilityService(private var configuration: Configuration) {
  this.configuration = Objects.requireNonNull(configuration,"the high availability configuration is null.")
  /**
   * Starts a {@link CuratorFramework} instance and connects it to the given ZooKeeper
   * quorum.
   */
  private val curator = Curator.startCuratorFramework(configuration)

  /**
   * Start the specified high availability server
   *
   * @param endpoint
   * @param endpointId
   * @tparam C
   * @return Returns the created server
   */
  def startServer[C <: HighAvailability](
    endpoint: C,
    endpointId: String)
  : HighAvailabilityServer = {
    requireNonNull(endpointId)
    new HighAvailabilityServer(curator, endpoint, endpointId)
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
   * @param endpointId
   * @tparam C
   * @return Returns the created server
   */
  def startSelector[C <: LeaderSelector](
    listener: LeaderSelector,
    endpointId: String)
  : SelectorServer = {
    requireNonNull(endpointId)
    new SelectorServer(curator, listener, endpointId)
  }

  /**
   * 校验路径是否为空
   * @param endpointId
   * @return
   */
  private def requireNonNull(
    endpointId: String)
  : String ={
    Objects.requireNonNull(endpointId,"the high available endpointId is null.")
  }
}
