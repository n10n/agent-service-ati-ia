// -*- mode: Scala;-*- 
// Filename:    Validation.scala 
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

case class Validation[Address,Data,Hash,Signature](
  override val sessionId : String,
  override val correlationId : String,
  val validation : ValidationT[Address,Data,Hash,Signature]
) extends ConsensusMessage( sessionId, correlationId ) {
  override def toLabel : CnxnCtxtLabel[String,String,String] = {
    Validation.toLabel( sessionId )
  }
}

object Validation {
  def toLabel(): CnxnCtxtLabel[String, String, String] = {
    "protocolMessage(validation(sessionId(_)))".toLabel
  }

  def toLabel(sessionId: String): CnxnCtxtLabel[String, String, String] = {
    s"""protocolMessage(validation(sessionId(\"$sessionId\")))""".toLabel
  }
}
