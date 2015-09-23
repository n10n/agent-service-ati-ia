// -*- mode: Scala;-*- 
// Filename:    ConsensusMessage.scala 
// Authors:     luciusmeredith                                                    
// Creation:    Fri Sep 18 10:46:29 2015 
// Copyright:   Not supplied 
// Description: 
// ------------------------------------------------------------------------

package com.synereo.casper.protocol.msgs

import com.synereo.casper._

import com.protegra_ati.agentservices.protocols.msgs._

import com.biosimilarity.evaluator.distribution.PortableAgentCnxn
import com.biosimilarity.lift.model.store.CnxnCtxtLabel
import com.protegra_ati.agentservices.store.extensions.StringExtensions._

abstract class ConsensusMessage[Address,Data,Hash,Signature](
  override val sessionId : String,
  override val correlationId : String
) extends ProtocolMessage with SessionMsgStr


object ConsensusMessage {
  def toLabel(): CnxnCtxtLabel[String, String, String] = {
    "protocolMessage(subchannel(sessionId(_)),_)".toLabel
  }

  def toLabel(sessionId: String): CnxnCtxtLabel[String, String, String] = {
    s"""protocolMessage(subchannel(sessionId(\"$sessionId\")),_)""".toLabel
  }
}
