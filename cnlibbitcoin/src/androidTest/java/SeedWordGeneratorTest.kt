import app.coinninja.cn.libbitcoin.SeedWordGenerator
import org.junit.Test

import com.google.common.truth.Truth.assertThat

class SeedWordGeneratorTest {

    @Test
    fun generates_12_words() {
        assertThat(SeedWordGenerator().generate().size).isEqualTo(12)
    }


}