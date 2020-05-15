package org.selins.highavailability

import java.util.UUID

/**
 * @title: HighAvailability
 * @projectName selins
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/2/7 20:23
 */
trait HighAvailability{
  /**
   * Callback method which is called by the {@link LeaderElectionService} upon selecting this
   * instance as the new leader. The method is called with the new leader session ID.
   *
   * @param leaderSessionID New leader session ID
   */
  def isLeader(leaderSessionID: UUID): Unit

  /**
   * Callback method which is called by the {@link LeaderElectionService} upon revoking the
   * leadership of a former leader. This might happen in case that multiple contenders have
   * been granted leadership.
   */

  def notLeader(): Unit


  /**
   * Returns the address of the {@link LeaderContender} under which other instances can connect
   * to it.
   *
   * @return Address of this contender.
   */
  def getAddress: String

  /**
   * Callback method which is called by {@link LeaderElectionService} in case of an error in the
   * service thread.
   *
   * @param exception Caught exception
   */
  def handleError(exception: Exception): Unit
}
