package org.selins.highavailability

/**
 * @title: LeaderQuerier
 * @projectName selins
 * @description: TODO
 * @author zhaoyunhai
 * @date 2020/2/7 21:35
 */
trait LeaderSelector {
  /**
   * This method is called by the {@link LeaderRetrievalService} when a new leader is elected.
   *
   * @param leaderAddress   The address of the new leader
   * @param leaderSessionID The new leader session ID
   */
  def notifyLeaderAddress(leaderAddress: String, leaderSessionID: String, throwable: Throwable): Unit
}
