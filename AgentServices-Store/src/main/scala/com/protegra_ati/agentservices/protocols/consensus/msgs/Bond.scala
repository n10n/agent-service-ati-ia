// -*- mode: Scala;-*- 
// Filename:    Bond.scala 
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

case class Bond[Address,Data,Hash,Signature](
  override val sessionId : String,
  override val correlationId : String,
  val bond : BondT[Address,Data,Hash,Signature]
) extends ConsensusMessage( sessionId, correlationId ) {
  override def toLabel : CnxnCtxtLabel[String,String,String] = {
    Bond.toLabel( sessionId )
  }
}

object Bond {
  def toLabel(): CnxnCtxtLabel[String, String, String] = {
    "protocolMessage(bond(sessionId(_)))".toLabel
  }

  def toLabel(sessionId: String): CnxnCtxtLabel[String, String, String] = {
    s"""protocolMessage(bond(sessionId(\"$sessionId\")))""".toLabel
  }
}
