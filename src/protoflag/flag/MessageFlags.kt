package protoflag.flag

import com.google.protobuf.Descriptors
import com.google.protobuf.Message
import protoflag.message.parseFrom

fun <T: Message.Builder> T.asFlags(nameSpace: String? = null): List<String> {
    return descriptorForType.asFlags()
}

fun Descriptors.Descriptor.asFlags(nameSpace: String? = null): List<String> {
    val flags = mutableListOf<String>()
    @Suppress("UNCHECKED_CAST")
    (fields as List<Descriptors.FieldDescriptor>).forEach {
        if (it.isMessage()) {
            flags += it.messageType.asFlags(nameSpace.addNamespace(it.name))
        } else {
            flags += nameSpace.addNamespace(it.name)
        }
    }
    return flags.toList()
}

fun String?.addNamespace(other: String) = this?.let { "$it.$other" } ?: other

fun Descriptors.FieldDescriptor.isMessage(): Boolean {
    return javaType == Descriptors.FieldDescriptor.JavaType.MESSAGE
}

fun <T: Message> T.fromArgs(
    args: Collection<String>,
    prefix: String = "--${this.descriptorForType.name}"
): T {
    return parseFrom(args.toNestedMap(prefix = prefix))
}

fun <T: Message.Builder> T.fromArgs(
    args: Collection<String>,
    prefix: String = "--${this.descriptorForType.name}"
): T {
    return parseFrom(args.toNestedMap(prefix = prefix))
}

// TODO: Can't handle nested, repeated messages. There's no way to know which sub-message a
//  repeated field was meant for.
fun Collection<String>.toNestedMap(prefix: String = ""): Map<String, Any> {
    val messageMap = mutableMapOf<String, Any>()
    filter { it.startsWith(prefix) }.map {
        val decomposed = it.removePrefix(prefix).split("=")
        val key = decomposed[0]
        val valueList = decomposed.subList(1, decomposed.size)
        val value = if (valueList.size == 1) valueList[0] else valueList.joinToString { "=" }

        key to value
    }.filter { (_, v) -> v.isNotBlank() }.forEach { (k, v) ->
        val split = k.split(".").filter { it.isNotBlank() } // split namespace into key list
        var currMap: MutableMap<String, Any> = messageMap
        var FlagTesttMap: Any = currMap
        val lastKey = split.last()
        split.forEach { FlagTestt ->
            if (lastKey != FlagTestt) {
                currMap = FlagTesttMap.mapCast()
                FlagTesttMap = FlagTesttMap.mapCast().getOrPut(FlagTestt) { mutableMapOf<String, Any>() }
            } else {
                if (v.contains(",")) v.split(",").map { it.trim() }.forEach {
                    FlagTesttMap.mapCast().insertValue(FlagTestt, it)
                } else {
                    FlagTesttMap.mapCast().insertValue(FlagTestt, v)
                }
            }
        }
    }
    return messageMap
}

internal fun Any.mapCast(): MutableMap<String, Any> {
    @Suppress("UNCHECKED_CAST")
    return this as MutableMap<String, Any>
}

internal fun MutableMap<String, Any>.insertValue(key: String, value: String) {
    val final = this.getOrPut(key) { value }
    when {
        final != value -> {
            @Suppress("UNCHECKED_CAST")
            this[key] = listOf(final) + value
        }
        final is List<*> -> {
            @Suppress("UNCHECKED_CAST")
            this[key] = final + value
        }
        else -> { /* do nothing */ }
    }
}