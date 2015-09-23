// -*- mode: Scala;-*- 
// Filename:    Unbond.scala 
// Authors:     luciusmeredith                                                    
// Creation:    Fri Sep 18 10:44:29 2015 
// Copyright:   Not supplied 
// Description: 
// ------------------------------------------------------------------------

package com.synereo.casper.protocol.msgs

import com.synereo.casper._

import com.biosimilarity.evaluator.distribution.PortableAgentCnxn
import com.biosimilarity.lift.model.store.CnxnCtxtLabel
import com.protegra_ati.agentservices.store.extensions.StringExtensions._

case class UnbondMsg[Address,Data,Hash,Signature](
  override val sessionId : String,
  override val correlationId : String,
  val unbond : UnbondT[Address,Data,Hash,Signature]
) extends ConsensusMessage[Address,Data,Hash,Signature](
  sessionId, correlationId
) {
  override def toLabel : CnxnCtxtLabel[String,String,String] = {
    UnbondMsg.toLabel( sessionId )
  }
}

object UnbondMsg {
  def toLabel(): CnxnCtxtLabel[String, String, String] = {
    "protocolMessage(subchannel(sessionId(_)),unbond(_))".toLabel
  }

  def toLabel(sessionId: String): CnxnCtxtLabel[String, String, String] = {
    s"""protocolMessage(subchannel(sessionId(\"$sessionId\")),unbond(_))""".toLabel
  }
}
