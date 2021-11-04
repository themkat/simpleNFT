#!/bin/bash

# could probably have created a makefile, but a script seemed suitable here :P 

if [[ -z $WALLET_PRIVATE_KEY ]]
then
   echo "WALLET_PRIVATE_KEY needs to be set to be able to connect to wallet!"
   exit 1
fi

if [[ -z $PINATA_JWT ]]
then
	echo "PINATA_JWT token needs to be set to be able to upload files"
	exit 1
fi 
   

# generate llamas
./llamaGenerator.kts components

# Upload images to IPFS, then use those hashes in the metadata, then upload metadata, save hashes, inject those in smart contract
# String-replace the contract so we have the correct IPFS paths
cp __CrypticLlamaContract.sol CrypticLlamaContract.sol
for llamaDir in generated/* ; do
	num=$(echo $llamaDir | sed 's/generated\///')
	
	# upload the image
	imageHash=$(curl -X POST -H "Authorization: Bearer $PINATA_JWT" -F "file=@$llamaDir/llama.png" https://api.pinata.cloud/pinning/pinFileToIPFS | jq -r '.IpfsHash')
	
	# fix the metadata with the link to the uploaded image
	cat $llamaDir/metadataWIP.json | sed -e "s/#SETLATER#/ipfs\:\/\/$imageHash/" > $llamaDir/metadata.json

	# upload the metadata
	jsonHash=$(curl -X POST -H "Authorization: Bearer $PINATA_JWT" -H "Content-Type: application/json" --data-binary "@$llamaDir/metadata.json" https://api.pinata.cloud/pinning/pinJSONToIPFS | jq -r '.IpfsHash')
	
	# put a call to mint this NFT in the contract
	cat CrypticLlamaContract.sol | sed -e "s/#MINTING_GOING_ON#/mintToken($num, \"ipfs\:\/\/$jsonHash\");\\n        #MINTING_GOING_ON#/" > CrypticLlamaContract.sol_new
	mv CrypticLlamaContract.sol_new CrypticLlamaContract.sol
	
	# sleep for 5 seconds. Only 180 requests to Pinata allowed per minute, so stay far below that
	sleep 5
done
cat CrypticLlamaContract.sol | sed -e "s/#MINTING_GOING_ON#/\/\/ Minting done.../" > CrypticLlamaContract.sol_new
mv CrypticLlamaContract.sol_new CrypticLlamaContract.sol 

# download openzeppelin contracts directly so we don't have to use npm
rm -rf @openzeppelin
wget -qO- https://github.com/OpenZeppelin/openzeppelin-contracts/archive/refs/tags/v4.3.2.tar.gz | tar zxvf - 
mv openzeppelin-contracts-4.3.2 @openzeppelin

# compile contract
solc CrypticLlamaContract.sol --bin --abi --optimize -o clm

# Generate Web3j stub
web3j generate solidity -a=clm/CrypticLlamaContract.abi -b=clm/CrypticLlamaContract.bin -o=. -p= 

# Warning: Extremely hacky!! Build a Maven dependency of the contract to be able to use it in KScript (Did not know KScript could not have Java deps using Include to include file!!!)
jbang export mavenrepo -Dgroup=net.themkat.simplenft -Dartifact=cryptic-llama-contract -Dversion=1.0 --deps=org.web3j:core:4.8.7 CrypticLlamaContract.java

# Deploy contract
./deployContract.kts $WALLET_PRIVATE_KEY $BLOCKCHAIN_URL
