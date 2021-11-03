#!/usr/bin/env kscript

import java.io.File
import java.awt.image.BufferedImage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.nio.PngReader
import com.sksamuel.scrimage.composite.AlphaComposite
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.annotation.JsonInclude

//DEPS com.sksamuel.scrimage:scrimage-core:4.0.22
//DEPS com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0

/*
 * Expects a directory structure like this:
 *  - components
 *     - body
 *     - neck_accessories
 *     - face_accessories
 *     - head_accessories
 * 
 * Usage:
 * ./llamaGenerator.kts component_directory  ...
 * TODO: describe usage 
 * Results will be found in the new directory called generated
 */

// helpers to make below code cleaner
fun getFileNamesWithPathInDirectory(directory : String) = File(directory)
	.walk()
	.iterator()
	.asSequence()
	.filter {
		it.isFile()
	}
	.map {
		it.absolutePath
	}
	.toList();


// TODO: so much cool stuff we can do with traits :O any cool things we can do with these llamas? personality type?
// https://docs.opensea.io/docs/metadata-standards
data class NftTrait(val trait_type : String, val value : String, val display_type : String? = null)

// TODO: should the descriptions be generated in any way?
// TODO: any cool algorithm for generating cool names? and descriptions? based upon attributes maybe? 
data class NftMetadata(val name : String,
					   val attributes : List<NftTrait>,
					   val description : String = "Llama...",
					   val image : String = "#SETLATER#")


// Componentsystem to load the component file paths only once.
class ComponentSystem(val bodyFiles : List<String>,
					  val headAccessoryFiles : List<String>,
					  val neckAccessoryFiles : List<String>,
					  val faceAccessoryFiles : List<String>) {
	init {
		if(bodyFiles.isEmpty() || headAccessoryFiles.isEmpty() || neckAccessoryFiles.isEmpty() || faceAccessoryFiles.isEmpty()) {
			println("All component folders need to have data!")
			System.exit(1)
		}
	}
}

fun generateLlama(components : ComponentSystem, num : Int) {
	// TODO: maybe extract parts of this to some helper function to make it more readable.
	
	// TODO: any logic for blank on certain places? Or maybe only for the cigarette place if I decide to use it?

	// TODO: how should we generate the backgrounds?

	val bodyFile = components.bodyFiles.random()
	val headAccessoryFile = components.headAccessoryFiles.random()
	val neckAccessoryFile = components.neckAccessoryFiles.random()
	val faceAccessoryFile = components.faceAccessoryFiles.random()
	
	// load random components
	// copy is due to converting to int representation, because of overlay not being pleased with float representation
	val body = ImmutableImage.loader().fromFile(bodyFile).copy(BufferedImage.TYPE_INT_ARGB)
	val headAccessory = ImmutableImage.loader().fromFile(headAccessoryFile).copy(BufferedImage.TYPE_INT_ARGB)
	val neckAccessory = ImmutableImage.loader().fromFile(neckAccessoryFile).copy(BufferedImage.TYPE_INT_ARGB)
	val faceAccessory = ImmutableImage.loader().fromFile(faceAccessoryFile).copy(BufferedImage.TYPE_INT_ARGB)
	
	// actually creating the final image
	val llama = body.overlay(headAccessory).overlay(neckAccessory).overlay(faceAccessory)
	
	// make the directory for this particular result
	val basePath = "generated/$num" 
	File(basePath).mkdirs()
	llama.output(PngWriter.NoCompression, File("$basePath/llama.png"))

	
	// TODO: generate metadata from a class? is there a minimal way of doing that? just use jackson fasterxml kotlin? Seems easy enough...
	val bodyType = bodyFile.substring(bodyFile.lastIndexOf("/")+1, bodyFile.lastIndexOf("."))
	val headAccessoryType = headAccessoryFile.substring(headAccessoryFile.lastIndexOf("/")+1, headAccessoryFile.lastIndexOf(".")).replace('_', ' ')
	val neckAccessoryType = neckAccessoryFile.substring(neckAccessoryFile.lastIndexOf("/")+1, neckAccessoryFile.lastIndexOf(".")).replace('_', ' ')
	val faceAccessoryType = faceAccessoryFile.substring(faceAccessoryFile.lastIndexOf("/")+1, faceAccessoryFile.lastIndexOf(".")).replace('_', ' ')
	
	// description: ... llama with a nice $bodyColor coat!
	val bodyTrait = NftTrait("Coat", bodyType)
	val headAccessoryTrait = NftTrait("Head accessory", headAccessoryType)
	val neckAccessoryTrait = NftTrait("Neck accessory", neckAccessoryType)
	val faceAccessoryTrait = NftTrait("Face accessory", faceAccessoryType)

	// TODO: maybe create a description based upon these? and maybe one other? Then it would be cool and dynamic
	val personality = listOf("Laid back", "Cool", "Lame", "Pervert", "Polite", "Rude").random()
	val personalityTrait = NftTrait("Personality", personality)

	// TODO: have stamina trait?

	val metadata = NftMetadata("Llama McLlama", listOf(bodyTrait, headAccessoryTrait, neckAccessoryTrait, faceAccessoryTrait, personalityTrait))
	
	// write the metadata to file
	// couldnt get the property to work for some reason (errors when running), so using setter instead...
	val objectMapper = jacksonObjectMapper()
	objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
	objectMapper.writeValue(File("$basePath/metadata.json"), metadata)
}


// TODO: generate the component system paths. traverse the desired folder. 
val componentDirectory = args[0]
// TODO: generate these inside the component system instead? 
val bodyFiles = getFileNamesWithPathInDirectory("$componentDirectory/body")
val headAccessoryFiles = getFileNamesWithPathInDirectory("$componentDirectory/head_accessories")
val neckAccessoryFiles = getFileNamesWithPathInDirectory("$componentDirectory/neck_accessories")
val faceAccessoryFiles = getFileNamesWithPathInDirectory("$componentDirectory/face_accessories")
val componentSystem = ComponentSystem(bodyFiles, headAccessoryFiles, neckAccessoryFiles, faceAccessoryFiles)

// TODO: make the directory generated
generateLlama(componentSystem, 1)

