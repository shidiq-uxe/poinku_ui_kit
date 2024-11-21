package id.co.edtslib.poinkuuikit

import java.util.Locale

fun generateLoremIpsum(wordCount: Int): String {
    val loremWords = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing",
        "elit", "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et",
        "dolore", "magna", "aliqua", "ut", "enim", "ad", "minim", "veniam", "quis",
        "nostrud", "exercitation", "ullamco", "laboris", "nisi", "ut", "aliquip", "ex",
        "ea", "commodo", "consequat"
    )

    return (1..wordCount).joinToString(" ") { loremWords.random() }
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + "."
}
