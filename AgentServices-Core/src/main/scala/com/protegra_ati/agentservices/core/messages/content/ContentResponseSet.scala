package com.protegra_ati.agentservices.core.messages.content

/* User: jklassen
*/

import com.protegra_ati.agentservices.core.platformagents._
import com.protegra.agentservicesstore.usage.AgentKVDBScope.acT._
import com.protegra_ati.agentservices.core.schema._
import com.protegra_ati.agentservices.core.messages._
import com.protegra.agentservicesstore.util._

trait ContentResponseSet {
  self:AgentHostStorePlatformAgent =>

  def listenPublicContentResponse(cnxn: AgentCnxnProxy) =
  {
    listen(_publicQ, cnxn, Channel.Content, ChannelType.Response, ChannelLevel.Public, sendPrivate(_: AgentCnxnProxy, _: Message))
  }
}