#!/usr/bin/env kscript

import java.io.File
import java.awt.image.BufferedImage
import java.util.Date
import java.time.LocalDate
import java.time.ZoneId
import java.time.LocalTime
import java.time.ZoneOffset
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
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

fun printUsage() {
	println("Usage: ")
	println("./llamaGenerator.kts components_directory\n")
	println("The components directory needs to have the following sub-directories with png-files:")
	println(" - body")
	println(" - neck_accessories")
	println(" - face_accessories")
	println(" - head_accessories")
	println("as well as a file called cigarette.png that is conditionally added (can be omitted)")
}

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

// NFT related data we will convert to JSON
data class NftTrait(val trait_type : String, val value : Any, val display_type : String? = null)

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

fun getAttributeNameFromComponentFileName(componentFileName : String ) = componentFileName.substring(componentFileName.lastIndexOf("/")+1, componentFileName.lastIndexOf(".")).replace('_', ' ')

fun generateRandomBirthdayTimestamp() : Long {
	val birthday = LocalDate.of((1980..2020).toList().random(),
								 (1..12).toList().random(),
								 (1..28).toList().random())
	return birthday.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC)
}

fun generateLlamaNft(components : ComponentSystem, num : Int) {
	// TODO: maybe extract parts of this to some helper function to make it more readable.
	// TODO: find a background or background color that can work?

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
	// TODO: load cigratte conditionally
	
	// actually creating the final image
	val llama = body.overlay(headAccessory).overlay(neckAccessory).overlay(faceAccessory)
	
	// make the directory for this particular result
	val basePath = "generated/$num" 
	File(basePath).mkdirs()
	llama.output(PngWriter.NoCompression, File("$basePath/llama.png"))

	
	// Get attributes and create traits 
	val bodyType = getAttributeNameFromComponentFileName(bodyFile)
	val headAccessoryType = getAttributeNameFromComponentFileName(headAccessoryFile)
	val neckAccessoryType = getAttributeNameFromComponentFileName(neckAccessoryFile)
	val faceAccessoryType = getAttributeNameFromComponentFileName(faceAccessoryFile)
	
	val bodyTrait = NftTrait("Coat", bodyType)
	val headAccessoryTrait = NftTrait("Head accessory", headAccessoryType)
	val neckAccessoryTrait = NftTrait("Neck accessory", neckAccessoryType)
	val faceAccessoryTrait = NftTrait("Face accessory", faceAccessoryType)

	// TODO: maybe create a description based upon these? and maybe one other? Then it would be cool and dynamic
	val personality = listOf("Laid back", "Cool", "Lame", "Pervert", "Polite", "Rude").random()
	val personalityTrait = NftTrait("Personality", personality)

	// generate a fun little birthday for the llama that users can have fun with in OpenSea
	val birthdayTrait = NftTrait("birthday", generateRandomBirthdayTimestamp(), display_type = "date")
	
	// Maybe name should include the number?
	// description: ... llama with a nice $bodyColor coat!
	// Maybe cigarette or not can do something with the description as well?
	// TODO: could we use year the llama was born in the description as well?
	// This llama is getting old, and have seen some shit!
	val metadata = NftMetadata(name = "Cryptic Llama #$num",
							   description = "Llama with a nice $bodyType coat. ",
							   attributes = listOf(bodyTrait, headAccessoryTrait, neckAccessoryTrait, faceAccessoryTrait, personalityTrait, birthdayTrait))
	
	// write the metadata to file
	// couldnt get the property to work for some reason (errors when running), so using setter instead...
	val objectMapper = jacksonObjectMapper()
	objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
	objectMapper.writeValue(File("$basePath/metadataWIP.json"), metadata)
}


// TODO: maybe some error handling? just to make sure the user have given arguments. printUsage
if(1 != args.size ) {
	printUsage()
	System.exit(1)
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
generateLlamaNft(componentSystem, 1)
generateLlamaNft(componentSystem, 2)
