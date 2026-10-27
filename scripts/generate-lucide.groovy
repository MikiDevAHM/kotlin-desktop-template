#!/usr/bin/env groovy
// Installs the Lucide icon font into the project and generates the Kotlin mapping (Lucide.kt).
//
// Source of the font: the "lucide-static" npm package (https://www.npmjs.com/package/lucide-static),
// which ships font/lucide.ttf, font/codepoints.json (icon name -> code point) and font/info.json (icons present in the font).
//
// Usage (from the project root, needs Groovy, plus npm and tar when downloading):
//   groovy scripts/generate-lucide.groovy                                  # latest lucide-static from npm
//   groovy scripts/generate-lucide.groovy --version 1.62.0                 # a specific release
//   groovy scripts/generate-lucide.groovy --lucide-dir path/to/package     # an already unpacked lucide-static
//   groovy scripts/generate-lucide.groovy --package com.acme.app.ui.icons  # override the Kotlin package
//
// Files written:
//   src/main/resources/fonts/lucide.ttf
//   src/main/resources/fonts/lucide-codepoints.json   (name -> code point, includes renamed/alias names, only icons that exist in the font)
//   src/main/resources/licenses/lucide.txt            (ISC + MIT notices, must ship with the font)
//   src/main/kotlin/<package>/Lucide.kt               (one typed constant per icon)

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.nio.file.Files
import java.nio.file.StandardCopyOption

def options = [:]
for (int i = 0; i < args.length; i += 2) {
    if (!args[i].startsWith('--') || i + 1 >= args.length) {
        System.err.println("Unexpected argument: ${args[i]}")
        System.exit(2)
    }
    options[args[i]] = args[i + 1]
}

def projectDir = new File('.').canonicalFile
def gradleProperties = new File(projectDir, 'gradle.properties')
if (!gradleProperties.exists()) {
    System.err.println('Run this script from the project root (gradle.properties not found).')
    System.exit(2)
}
def properties = new Properties()
gradleProperties.withInputStream { properties.load(it) }
def kotlinPackage = options['--package'] ?: "${properties.getProperty('app.group')}.ui.icons"

def windows = System.getProperty('os.name').toLowerCase().contains('win')
def shell = windows ? ['cmd', '/c'] : []
def run = { List<String> command, File directory ->
    def process = new ProcessBuilder(command).directory(directory).redirectErrorStream(true).start()
    def output = process.inputStream.text
    if (process.waitFor() != 0) {
        throw new IllegalStateException("${command.join(' ')} failed:\n${output}")
    }
    output
}

File packageDir
if (options['--lucide-dir']) {
    packageDir = new File(options['--lucide-dir']).canonicalFile
} else {
    def temp = Files.createTempDirectory('lucide-static').toFile()
    def spec = "lucide-static@${options['--version'] ?: 'latest'}"
    println "Downloading ${spec} from npm..."
    def packed = run(shell + ['npm', 'pack', spec, '--silent'], temp)
    def tarball = packed.readLines().findAll { it.trim().endsWith('.tgz') }.last().trim()
    run(shell + ['tar', '-xzf', tarball], temp)
    packageDir = new File(temp, 'package')
}

def fontDir = new File(packageDir, 'font')
def required = [new File(fontDir, 'lucide.ttf'), new File(fontDir, 'codepoints.json'), new File(fontDir, 'info.json'),
                new File(packageDir, 'package.json'), new File(packageDir, 'LICENSE')]
required.findAll { !it.exists() }.each { throw new IllegalStateException("Missing ${it} in the lucide-static package") }

def json = new JsonSlurper()
Map<String, Integer> allCodePoints = json.parse(new File(fontDir, 'codepoints.json')) as Map<String, Integer>
Set<String> inFont = (json.parse(new File(fontDir, 'info.json')) as Map<String, Object>).keySet()
def codePoints = new TreeMap<String, Integer>()
inFont.each { name ->
    if (!allCodePoints.containsKey(name)) throw new IllegalStateException("info.json lists '${name}' but codepoints.json does not")
    codePoints[name] = allCodePoints[name]
}
def skipped = allCodePoints.keySet().findAll { !inFont.contains(it) }.sort()
def version = json.parse(new File(packageDir, 'package.json')).version

def pascal = { String name -> name.split('-').collect { it.capitalize() }.join('') }
def constants = new TreeMap<String, Integer>()
codePoints.keySet().sort().each { name ->
    def codePoint = codePoints[name]
    def constant = pascal(name)
    if (!Character.isLetter(constant.charAt(0))) throw new IllegalStateException("Kotlin name ${constant} does not start with a letter")
    if (constants.containsKey(constant)) {
        if (constants[constant] != codePoint) throw new IllegalStateException("'${name}' and another icon map to ${constant} with different glyphs")
        return
    }
    constants[constant] = codePoint
}

def resources = new File(projectDir, 'src/main/resources')
new File(resources, 'fonts').mkdirs()
new File(resources, 'licenses').mkdirs()
Files.copy(new File(fontDir, 'lucide.ttf').toPath(), new File(resources, 'fonts/lucide.ttf').toPath(), StandardCopyOption.REPLACE_EXISTING)
new File(resources, 'fonts/lucide-codepoints.json').text = JsonOutput.toJson(codePoints) + '\n'
Files.copy(new File(packageDir, 'LICENSE').toPath(), new File(resources, 'licenses/lucide.txt').toPath(), StandardCopyOption.REPLACE_EXISTING)

def kotlinDir = new File(projectDir, "src/main/kotlin/${kotlinPackage.replace('.', '/')}")
kotlinDir.mkdirs()
def kotlin = new StringBuilder()
kotlin << "package ${kotlinPackage}\n\n"
kotlin << "object Lucide {\n"
kotlin << "    const val VERSION = \"${version}\"\n\n"
constants.each { constant, codePoint ->
    kotlin << "    val ${constant} = LucideGlyph(${String.format('0x%04X', codePoint)})\n"
}
kotlin << "}\n"
new File(kotlinDir, 'Lucide.kt').text = kotlin.toString()

println "Lucide ${version}: ${constants.size()} Kotlin constants from ${codePoints.size()} names (${new HashSet(codePoints.values()).size()} distinct glyphs)"
println "Kotlin package: ${kotlinPackage}"
if (skipped) println "Skipped ${skipped.size()} names listed in codepoints.json but missing from the font: ${skipped.join(', ')}"
