#!/bin/bash

# TODO: print usage

if [[ -z $WALLET_PRIVATE_KEY ]] then
   echo "WALLET_PRIVATE_KEY needs to be set to be able to connect to wallet!"
   exit 1
fi
   

# TODO: more here


# TODO: generate llamas


# TODO: upload images to IPFS, then use those hashes in the metadata, then upload metadata, save hashes, inject those in smart contract

# String-replace the contract so we have the correct IPFS paths

# download openzeppelin contracts directly so we don't have to use npm
rm -rf @openzeppelin
wget -qO- https://github.com/OpenZeppelin/openzeppelin-contracts/archive/refs/tags/v4.3.2.tar.gz | tar zxvf - 
mv openzeppelin-contracts-4.3.2 @openzeppelin

# TODO: compile contract if  
solc CrypticLlamaContract.sol --bin --abi --optimize -o clm

# Generate Web3j stub
web3j generate solidity -a=clm/CrypticLlamaContract.abi -b=clm/CrypticLlamaContract.bin -o=. -p= 

# Deploy contract
./deployContract.kts $WALLET_PRIVATE_KEY $BLOCKCHAIN_URL
