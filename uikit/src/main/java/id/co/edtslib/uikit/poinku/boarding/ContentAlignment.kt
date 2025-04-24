package id.co.edtslib.uikit.poinku.boarding

sealed class ContentAlignment(
    open var verticalBias: Float = 0.5f
) {
    data class Start(
        val horizontalMargin: Float = 0f,
        override var verticalBias: Float = 0f,
    ): ContentAlignment(verticalBias)

    data class Center(
        val startMargin: Float = 0f,
        val endMargin: Float = 0f,
        override var verticalBias: Float = 0.5f,
    ): ContentAlignment()

    data class End(
        val horizontalMargin: Float = 0f,
        override var verticalBias: Float = 1f,
    ): ContentAlignment(verticalBias)

    companion object {
        val entries = listOf(
            Start(),
            Center(),
            End()
        )
    }
}