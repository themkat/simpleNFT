// SPDX-License-Identifier: MIT
pragma solidity ^0.8.2;

import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";

contract CrypticLlamaContract is ERC721URIStorage, Ownable {
    constructor() ERC721("Cryptic Llama", "CLM") {
        #MINTING_GOING_ON#
	}

	function mintToken(uint256 tokenId, string memory tokenUri) private {
		address owner = msg.sender;
		_safeMint(owner, tokenId);
		_setTokenURI(tokenId, tokenUri);
	}
}
