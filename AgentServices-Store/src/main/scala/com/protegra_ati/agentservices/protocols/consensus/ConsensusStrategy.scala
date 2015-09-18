// -*- mode: Scala;-*- 
// Filename:    consensus-strategy.scala 
// Authors:     luciusmeredith                                                    
// Creation:    Fri Sep 18 10:31:49 2015 
// Copyright:   Not supplied 
// Description: 
// ------------------------------------------------------------------------

package com.synereo.casper.protocol

import com.synereo.casper.protocol.msgs._
import com.synereo.casper._
import com.protegra_ati.agentservices.protocols._

import com.biosimilarity.evaluator.distribution.{PortableAgentCnxn, PortableAgentBiCnxn}
import com.biosimilarity.evaluator.distribution.diesel.DieselEngineScope._
import com.biosimilarity.evaluator.distribution.ConcreteHL.PostedExpr
import com.protegra_ati.agentservices.protocols.msgs._
import com.biosimilarity.lift.model.store.CnxnCtxtLabel
import com.biosimilarity.lift.lib._
import scala.util.continuations._
import java.util.UUID

trait ValidatorT extends ProtocolBehaviorT with Serializable {
  import com.biosimilarity.evaluator.distribution.utilities.DieselValueTrampoline._
  import com.protegra_ati.agentservices.store.extensions.StringExtensions._

  def run(
    node : Being.AgentKVDBNode[PersistedKVDBNodeRequest, PersistedKVDBNodeResponse],
    cnxns : Seq[PortableAgentCnxn],
    filters : Seq[CnxnCtxtLabel[String, String, String]]
  ): Unit = {
    BasicLogService.tweet(
      (
        "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
        + "\nvalidator -- behavior instantiated and run method invoked " 
        + "\nnode: " + node
        + "\ncnxns: " + cnxns
        + "\nfilters: " + filters
        + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
      )
    )
    println(
      (
        "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
        + "\nclaimant -- behavior instantiated and run method invoked " 
        + "\nnode: " + node
        + "\ncnxns: " + cnxns
        + "\nfilters: " + filters
        + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
      )
    )
    doValidation( node, cnxns )
  }

  def doValidation(
    node: Being.AgentKVDBNode[PersistedKVDBNodeRequest, PersistedKVDBNodeResponse],
    cnxns: Seq[PortableAgentCnxn]
  ): Unit = {
    cnxns match {
      case validatorCnxn :: clientCnxn :: Nil => {
	val validatorCnxnRd =
          acT.AgentCnxn(
	    validatorCnxn.src, validatorCnxn.label, validatorCnxn.trgt
	  )
        val validatorCnxnWr =
          acT.AgentCnxn(
	    validatorCnxn.trgt, validatorCnxn.label, validatorCnxn.src
	  )
	val clientCnxnRd =
          acT.AgentCnxn(
	    clientCnxn.src, clientCnxn.label, clientCnxn.trgt
	  )
        val clientCnxnWr =
          acT.AgentCnxn(
	    clientCnxn.trgt, clientCnxn.label, clientCnxn.src
	  )

	reset {
	  for( eValidationTxn <- node.subscribe( validatorCnxnRd )( ConsensusMessage.toLabel ) ){
	    rsrc2V[ConsensusMessage]( eValidationTxn ) match {
	      case Left( vTxn ) => {
		vTxn match {
		  case BlockMsg( _, _, _ ) => {
		  }
		  case BondMsg( _, _, _ ) => {
		  }
		  case EvidenceMsg( _, _, _ ) => {
		  }
		  case TxnMsg( _, _, _ ) => {
		  }
		  case UnbondMsg( _, _, _ ) => {
		  }
		  case ValidationMsg( _, _, _ ) => {
		  }
		}
	      }
	      case Right( true ) => {
	      }
	      case _ => {
	      }
	    }
	  }
	}
	
	reset {
	  for( eClientTxn <- node.subscribe( clientCnxnRd )( ConsensusMessage.toLabel ) ){
	    rsrc2V[ConsensusMessage]( eClientTxn ) match {
	      case Left( vTxn ) => {
		vTxn match {		  
		  case BondMsg( _, _, _ ) => {
		  }
		  case EvidenceMsg( _, _, _ ) => {
		  }
		  case TxnMsg( _, _, _ ) => {
		  }
		}
	      }
	      case Right( true ) => {
	      }
	      case _ => {
	      }
	    }
	  }
	}
      }
      case _ => {
	throw new Exception( "two cnxns expected : " + cnxns )
      }
    }    
  }    
}

