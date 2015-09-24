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
import scala.collection.mutable.Map
import scala.util.continuations._
import java.util.Date
import java.util.UUID

trait ValidatorBehaviorT[Address,Data,PrimHash,Hash <: Tuple2[PrimHash,PrimHash],Signature,AppState,Timer] extends ProtocolBehaviorT
with SignatureOpsT[Address,Data,Hash,Signature] with Serializable {
  import com.biosimilarity.evaluator.distribution.utilities.DieselValueTrampoline._
  import com.protegra_ati.agentservices.store.extensions.StringExtensions._

  def validator[Session](
    sessionId : Session
  ) : ValidatorT[Address,Data,PrimHash,Hash,Signature,AppState,Timer]
    = sessionMap( sessionId )._1
  def cmgtState[Session](
    sessionId : Session
  ) : ConsensusManagerStateT[Address,Data,Hash,Signature] = 
    sessionMap( sessionId )._2

  def sessionMap[Session] : Map[Session,(ValidatorT[Address,Data,PrimHash,Hash,Signature,AppState,Timer],ConsensusManagerStateT[Address,Data,Hash,Signature])] 

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
	    rsrc2V[ConsensusMessage[Address,Data,Hash,Signature]]( eValidationTxn ) match {
	      case Left( vTxn ) => {
		vTxn match {
		  case BlockMsg( sid, _, blk : BlockT[Address,Data,Hash,Signature] ) => {
		    val vldtr = validator[String]( sid )
		    val cmgtS : ConsensusManagerStateT[Address,Data,Hash,Signature] =
		      cmgtState[String]( sid )
		    val blkHash =
		      vldtr.hash[UnsignedBlockT[Address,Data,Hash,Signature]](
			blk.unsignedBlock
		      )
		    if (
		      isValidSignature[Address]( blkHash, blk.signature, blk.proposer )
		    ) {
		      cmgtS.blockHashMap.get( blkHash ) match {
			case Some( _ ) => {
			  // do nothing
			}
			case None => {
			  val blkDeps =
			    vldtr.haveBlockDependencies( cmgtS, blk )
			  if ( blkDeps ) {
			    val blkVld = vldtr.valid( blk )
			    if ( blkVld ) {
			      val blkStat = 
				BlockStatus( blk, Some( true ), Some( true ), new Date() )
			      val nBlkHMap : BlockHashMapT[Address,Data,Hash,Signature] =
				(
				  cmgtS.blockHashMap
				  + ( blkHash -> blkStat )
				).asInstanceOf[BlockHashMapT[Address,Data,Hash,Signature]]
			      val nCmgtS =
				ConsensusManagerState[Address,Data,Hash,Signature](
				  cmgtS, 
				  nBlkHMap
				)
			      sessionMap += ( sid -> ( vldtr, nCmgtS ) );
			      ()
			    }
			    else {
			      // package as evidence, publish, evict validator
			    }
			  }
			  else {
			    // wait, or ask other validators for dependencies, or timeout
			    // on receipt of a dependency, say depBlk, we need to
			    // remember that !valid( depBlk ) counts as evidence
			    // against the validator that sent
			    // the depending block
			  }
			}
		      }
		    }
		    else {
		    }
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
	    rsrc2V[ConsensusMessage[Address,Data,Hash,Signature]]( eClientTxn ) match {
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

