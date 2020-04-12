package dev.olog.scrollhelper.example.model

data class Model(
    val image: String
) {

    companion object {
        val IMAGES = listOf(
            Model("https://homepages.cae.wisc.edu/~ece533/images/airplane.png"),
            Model("https://homepages.cae.wisc.edu/~ece533/images/arctichare.png"),
            Model("https://homepages.cae.wisc.edu/~ece533/images/boat.png"),
            Model("https://homepages.cae.wisc.edu/~ece533/images/cat.png"),
            Model("https://homepages.cae.wisc.edu/~ece533/images/fruits.png")
        )
    }

}

operator fun <T> List<T>.times(times: Int): List<T> {
    val result = mutableListOf<T>()

    repeat(times) {
        result.addAll(this)
    }

    return result
}