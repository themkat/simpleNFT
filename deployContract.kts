#!/usr/bin/env kscript

import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.crypto.Credentials
import org.web3j.tx.gas.DefaultGasProvider

//DEPS org.web3j:core:4.8.7
//DEPS net.themkat.simplenft:cryptic-llama-contract:1.0
// DURING DEV INCLUDE CrypticLlamaContract.java
// uncomment the above DEPS line for it in that case...

// web3j stub CrypticLlamaContract.java must have been generated for this class to run

if(1 > args.size) {
	println("Usage:")
	println("./deployContact.kts PRIVATE_KEY [BLOCKCHAIN_URL]")
	println("(BLOCKCHAIN_URL is optional)")
	System.exit(1)
}

val privateKey = args[0]
val blockchainUrl = args.getOrNull(1)

val web3j = Web3j.build(if(null == blockchainUrl)
						HttpService()
						else
						HttpService(blockchainUrl))
val credentials = Credentials.create(privateKey)
CrypticLlamaContract.deploy(web3j, credentials, DefaultGasProvider()).send()
web3j.shutdown()
