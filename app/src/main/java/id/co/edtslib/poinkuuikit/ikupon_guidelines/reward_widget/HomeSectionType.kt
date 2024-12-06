package id.co.edtslib.poinkuuikit.ikupon_guidelines.reward_widget

import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIkuponActivity
import id.co.edtslib.uikit.poinku.overlay.ImageParallaxOverlayLayout

sealed class HomeSectionType(
    val ordinal: Int
) {
    object PromoRecommendationSkeleton : HomeSectionType(PROMO_RECOMMENDATION_SKELETON)
    data class PromoRecommendation(
        val imageUrl: String,
        val title: String,
        val description: String,
        val type: ImageParallaxOverlayLayout.OverlayType,
        val data: List<GuidelinesIkuponActivity.DummyItem>
    ) : HomeSectionType(PROMO_RECOMMENDATION)

    object FavoritePromoSkeleton : HomeSectionType(FAVORITE_PROMO_SKELETON)
    data class FavoritePromo(
        val imageUrl: String,
        val title: String,
        val description: String,
        val type: ImageParallaxOverlayLayout.OverlayType,
        val data: List<GuidelinesIkuponActivity.DummyItem>
    ) : HomeSectionType(FAVORITE_PROMO)

    object NearlyExpirePromoSkeleton : HomeSectionType(NEARLY_EXPIRE_PROMO_SKELETON)
    data class NearlyExpirePromo(
        val imageUrl: String,
        val title: String,
        val description: String,
        val type: ImageParallaxOverlayLayout.OverlayType,
        val data: List<GuidelinesIkuponActivity.DummyItem>
    ) : HomeSectionType(NEARLY_EXPIRE_PROMO)


    companion object {
        const val PROMO_RECOMMENDATION_SKELETON = 0
        const val PROMO_RECOMMENDATION = 1
        const val FAVORITE_PROMO_SKELETON = 2
        const val FAVORITE_PROMO = 3
        const val NEARLY_EXPIRE_PROMO_SKELETON = 4
        const val NEARLY_EXPIRE_PROMO = 5
    }
}