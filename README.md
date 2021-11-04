# simpleNFT - Cryptic Llamas
Simple NFT project from scratch. Using a basic setup and an excuse to play with Web3j, and not big JavaScript project structures. Planning on using KScript, bash and super simple stuff instead (not recommended for big projects, done as a fun challenge).

Feel free to experiment with the scripts if you can get any use from them :) I created this for fun late in the evenings, so the code is far from perfect. My goal was more having fun with a lot of different libraries and technologies I think are cool.


## Goals
My goals for this project are as following:
- Simplest possible project setup. Scripts doing most of the work (NOT GOOD FOR BIGGER PROJECTS, use real tools like Truffle and various NPM tools instead)
- Hopefully the steps might be educational for someone reading this... A simple small project will show how easy and minimal it is to create your own NFTs.
- Experiment a bit with Ethereum smart contracts (even though they are super simple in this case). Looking into OpenZeppelin and other libraries that might be useful.
- Actually release the project on something like OpenSea
- Very simple pixel art graphics, complete NFT generated programatically from components
- Use IPFS (possibly Pinata) to host the images, instead of a regular website (which can be mutated/changed)


## Tools needed
### Software
TODO: describe which is only applicable for local and not
- Ganache (local only)
- KScript
- Web3j cli
- jbang (to export generated Java file from web3j as a Maven dependency that is usable in KScript)
- curl and wget
- jq (or just use sed?)

## Services
You also need a Pinata JWT to be able to upload the file to IPFS. You can tweak the scripts to use other IPFS pinning services if you want to.

If you want to deploy to testnet or mainnet, a gateway service (to get a valid HTTP endpoint) is required. I use [Alchemy](https://www.alchemy.com/) for this purpose. 


## How to run locally
TODO: describe steps to run locally
