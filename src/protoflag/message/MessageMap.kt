package protoflag.message

import com.google.gson.Gson
import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat

fun Message.toMap() : Map<String, *> {
    val json = JsonFormat.printer().preservingProtoFieldNames().print(this)
    return Gson().fromJson<Map<String, *>>(json, Map::class.java)
}

fun <T : Message> T.parseFrom(map: Map<String, *>) : T {
    @Suppress("UNCHECKED_CAST")
    return newBuilderForType().parseFrom(map).build() as T
}

fun <T : Message.Builder> T.parseFrom(map: Map<String, *>) : T {
    @Suppress("UNCHECKED_CAST")
    return apply { JsonFormat.parser().merge(Gson().toJson(map), this) }
}