package assets.tooltipthingy.font

import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

val char = """{"type":"bitmap","chars":["§"],"file":"tooltipthingy:font/unknown_skill.png","height":7,"ascent":7}"""
val reference = """{"type":"reference","id":"tooltipthingy:§"}"""

val entries = (0..100).joinToString(",") {
    val name = "stats/u" + (0xe800 + it).toString(16)
    Path("$name.json").createParentDirectories().writeText("""{"providers":[${char.replace('§', (Char(0xe800) + it))}]}""".trimIndent())

    reference.replace("§", name)
}

Path("unknown_stats.json").writeText("""{"providers":[${entries}]}""".trimIndent())

