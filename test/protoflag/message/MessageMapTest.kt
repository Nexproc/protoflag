package protoflag.message

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import protoflag.proto.FlagTestProto

@RunWith(JUnit4::class)
class MessageMapTest {
    @Test
    fun messageIsTheSameToAndFromMap() {
        val defaultFlagTest = FlagTestProto.FlagTest.newBuilder().apply {
            someNumber = 25
            addNames("first")
            addNames("second")
            fooBuilder.moreNumber = 34
        }.build()

        val toMap = defaultFlagTest.toMap()

        val fromMap = FlagTestProto.FlagTest.getDefaultInstance().parseFrom(toMap)

        assertThat(defaultFlagTest).isEqualTo(fromMap)
    }
}