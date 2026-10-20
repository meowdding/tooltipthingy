package assets.iconographic.font

import kotlin.io.path.Path
import kotlin.io.path.writeText

val char = """{"type":"bitmap","chars":["§"],"file":"iconographic:font/unknown_skill.png","height":7,"ascent":7}"""

val entries = (0..100).joinToString(",") {
    char.replace('§', (Char(0xe800) + it))
}

Path("unknown_stats.json").writeText("""{"providers":[${entries}]}""".trimIndent())

