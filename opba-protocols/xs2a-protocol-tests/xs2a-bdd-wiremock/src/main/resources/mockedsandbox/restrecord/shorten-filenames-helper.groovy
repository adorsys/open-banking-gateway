package mockedsandbox.restrecord

import com.google.common.base.Joiner

import java.nio.charset.StandardCharsets
import java.nio.file.Files

/**
 * This helper shortens WireMock generated filenames
 */

import java.nio.file.Paths
import java.util.zip.CRC32

// YOUR PATH here:
def root = Paths.get("/home/valb3r/IdeaProjects/open-banking-gateway/opba-protocols/xs2a-protocol-tests/xs2a-bdd-wiremock/src/main/resources/mockedsandbox/restrecord/decoupled-sca/embedded-mode-decoupled-sca/payments")

def targetFiles = Files.walk(root)
        .filter {Files.isRegularFile(it)}
        .filter {it -> it.toString().endsWith("json") || it.toString().endsWith("txt")}
        .collect {it}

def renames = [:]

String hashValue(String nameWithoutExt) {
    def split = nameWithoutExt.split("-")
    def result
    def segments = [
            "body", "v1", "accounts", "consents", "parameters", "provide", "more", "psu", "password", "sca", "result",
            "LIST_TRANSACTIONS", "sagas", "server", "token"
    ]

    for (int i = 0; i < split.length; i++) {
        if (split[i] in segments) {
            result = i + 1
        }
    }

    if (null == result) {
        println("ERR: $nameWithoutExt")
        throw new IllegalAccessError("")
    }

    return Joiner.on("-").join(split.toList().stream().skip(result).toArray());
}

for (def file : targetFiles) {
    def origName = file.getFileName().toString()
    def hash = hashValue(origName.split("\\.")[0])
    println(hash)
    def crc = new CRC32()
    hash.getBytes(StandardCharsets.UTF_8).each {crc.update(it)}
    def newName = origName.replaceAll("-" + hash, '-' + crc.value)
    renames[origName] = newName
}

for (def file : targetFiles) {
    String text = file.toFile().text
    renames.each {key, value ->
        println("$key : $value")
        text = text.replace(key, value)
    }
    file.toFile().text = text
}

for (def file : targetFiles) {
    def fileRoot = file.getParent()
    if (renames.containsKey(file.getFileName().toString())) {
        file.toFile().renameTo(fileRoot.resolve(renames[file.getFileName().toString()]).toAbsolutePath().toFile())
    }
}
