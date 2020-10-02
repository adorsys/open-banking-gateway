package mockedsandbox.restrecord
/**
 * This helper shortens WireMock generated filenames
 */

import com.google.common.base.Joiner
import sun.misc.CRC16

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

// YOUR PATH here:
def root = Paths.get("/tmp/open-banking-gateway/core/banking-protocol/src/test/resources/mockedsandbox/restrecord")

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
    def crc = new CRC16()
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
