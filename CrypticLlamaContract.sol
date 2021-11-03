// SPDX-License-Identifier: MIT
pragma solidity ^0.8.2;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";

contract CrypticLlamaContract is ERC721URIStorage {
    constructor() ERC721("Cryptic Llama", "CLM") {
		// TODO: wildcard that a script can use to insert minting lines
		// #MINTING_GOING_ON#
		mintToken(1, "someuri");
	}

	function mintToken(uint256 tokenId, string memory tokenUri) private {
		address owner = msg.sender;
		_safeMint(owner, tokenId);
		_setTokenURI(tokenId, tokenUri);
	}
}
