package com.protegra_ati.agentservices.core.events

import com.protegra_ati.agentservices.core.messages.verifier._

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

class GetClaimResponseReceivedEvent(source:GetClaimResponse) extends MessageEvent[GetClaimResponse](source) {

  override def triggerEvent(adapter: MessageEventAdapter) = {
    adapter.getClaimResponseReceived(this)
  }
  
}
