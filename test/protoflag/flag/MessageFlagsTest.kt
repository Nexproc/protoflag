package protoflag.flag

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import protoflag.proto.FlagTestProto

@RunWith(JUnit4::class)
class MessageFlagsTest {
    @Test
    fun messageFromArgsIsSameAsMessage() {
        val defaultFlagTest = FlagTestProto.FlagTest.newBuilder().apply {
            someNumber = 25
            addNames("first")
            addNames("second")
            fooBuilder.moreNumber = 34
        }.build()

        val args = listOf("--some_number=25", "--names=first", "--names=second", "--foo.more_number=34")
        val args2 = listOf("--some_number=25", "--names=first,second", "--foo.more_number=34")
        val argProto = FlagTestProto.FlagTest.newBuilder().fromArgs(args, prefix = "--").build()
        val argProto2 = FlagTestProto.FlagTest.newBuilder().fromArgs(args2, prefix = "--").build()

        assertThat(argProto).isEqualTo(defaultFlagTest)
        assertThat(argProto2).isEqualTo(defaultFlagTest)
    }
}