package id.co.edtslib.uikit.poinku.chip

data class BucketData(
    var id: String? = null,
    var bucketName: String? = null,
    var bucketImage: Any? = null,
    var bucketStampCount: Int? = null,
    var bucketList: List<String> = emptyList()
)
