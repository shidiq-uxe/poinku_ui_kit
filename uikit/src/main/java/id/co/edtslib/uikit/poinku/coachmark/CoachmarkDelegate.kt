package id.co.edtslib.uikit.poinku.coachmark

interface CoachmarkDelegate {
    fun onNextClickClickListener(index: Int)
    fun onSkipClickListener(index: Int)
    fun onDismissListener()
}