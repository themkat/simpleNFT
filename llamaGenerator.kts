#!/usr/bin/env kscript

import java.io.File
import java.awt.image.BufferedImage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.nio.PngReader
import com.sksamuel.scrimage.composite.AlphaComposite

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


// TODO: how to set attributes? Just a map for simplicity?
// TODO: should the descriptions be generated in any way?
data class NftMetadata(val name : String,
					   val attributes : Map<String, String>,
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


// TODO: probably ineffective to read components from file system each time here. Any good structures we can save them in? Map of Maps seem messy, and a simple object with lists seem weird.. just send in everything? would prefer not to have global variables...
// some other class structure or similar to work with? 
fun generateLlama(components : ComponentSystem, num : Int) {
	// TODO: any logic for blank on certain places? Or maybe only for the cigarette place if I decide to use it?

	// TODO: how should we generate the backgrounds?

	// load random components
	// copy is due to converting to int representation, because of overlay not being pleased with float representation
	val body = ImmutableImage.loader().fromFile(components.bodyFiles.random()).copy(BufferedImage.TYPE_INT_ARGB)
	val headAccessory = ImmutableImage.loader().fromFile(components.headAccessoryFiles.random()).copy(BufferedImage.TYPE_INT_ARGB)
	val neckAccessory = ImmutableImage.loader().fromFile(components.neckAccessoryFiles.random()).copy(BufferedImage.TYPE_INT_ARGB)
	val faceAccessory = ImmutableImage.loader().fromFile(components.faceAccessoryFiles.random()).copy(BufferedImage.TYPE_INT_ARGB)
	
	// actually creating the final image
	val llama = body.overlay(headAccessory).overlay(neckAccessory).overlay(faceAccessory)
	
	// make the directory for this particular result
	val basePath = "generated/$num" 
	File(basePath).mkdirs()
	llama.output(PngWriter.NoCompression, File("$basePath/llama.png"))
	
	// TODO: generate metadata from a class? is there a minimal way of doing that? just use jackson fasterxml kotlin? Seems easy enough...
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

