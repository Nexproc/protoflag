package protoflag.flag.inject

import com.google.protobuf.Message
import dagger.BindsInstance
import dagger.Component
import kotlin.reflect.KClass

@Component
interface FlagsComponent {
    // TODO: create args list and populate builders when parsing args.
    val flags: List<String>

    val protoRegistry: InstanceMap

    @Component.Factory
    interface Builder {
        @BindsInstance
        fun create(
            args: Array<String>,
            builders: List<Message.Builder>,
            namespace: String = "" // top-level namespace for flags in this component
        ): FlagsComponent
    }
}

class InstanceMap {
    private val instanceMap: MutableMap<KClass<*>, Any> = mutableMapOf()

    operator fun <T> get(k: KClass<*>): T? {
        @Suppress("UNCHECKED_CAST")
        return instanceMap[k] as T?
    }

    operator fun set(k: KClass<*>, v: Any) {
        instanceMap[k] = v
    }
}

operator fun <T> FlagsComponent.get(k: KClass<*>): T? {
    @Suppress("UNCHECKED_CAST")
    return protoRegistry[k]
}