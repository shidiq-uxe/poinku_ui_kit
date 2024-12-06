package id.co.edtslib.poinkuuikit.ikupon_guidelines.reward_widget

import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.DiffUtil
import id.co.edtslib.poinkuuikit.databinding.ItemSectionIKuponCarouselBinding
import id.co.edtslib.poinkuuikit.databinding.ItemSkeletonSectionIKuponCarouselBinding
import id.co.edtslib.uikit.poinku.adapter.multiTypeAdapter
import id.co.edtslib.uikit.poinku.adapter.skeletonAdapter
import id.co.edtslib.uikit.poinku.databinding.ItemGridSkeletonPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.utils.MarginItem
import id.co.edtslib.uikit.poinku.utils.deviceWidth
import id.co.edtslib.uikit.poinku.utils.dp

object HomeSectionAdapter {
    private val skeletonAdapter = skeletonAdapter(
        skeletonCount = 5,
        onBindHolder = { position, binding ->
            binding.root.updateLayoutParams<ViewGroup.LayoutParams> {
                width = (binding.root.context.deviceWidth * 0.33).toInt()
            }
        }
    )

    val homeAdapter = multiTypeAdapter(
        diffCallback = HomeDiffCallback(),
        viewTypeConfig = { item -> item.ordinal },
        bindingConfig = {
            registerViewType(
                viewType = HomeSectionType.PROMO_RECOMMENDATION_SKELETON,
                bindingInflater = { inflater, parent, attachToParent ->
                    ItemSkeletonSectionIKuponCarouselBinding.inflate(inflater, parent, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemSkeletonSectionIKuponCarouselBinding)
                        .rvPromoSkeleton.adapter = skeletonAdapter
                }
            )

            registerViewType(
                viewType = HomeSectionType.PROMO_RECOMMENDATION,
                bindingInflater = { inflater, parent, attachToParent ->
                    ItemSectionIKuponCarouselBinding.inflate(inflater, parent, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    setPromoRecommendation(position, itemBinding as ItemSectionIKuponCarouselBinding, item as HomeSectionType.PromoRecommendation)
                }
            )

            //  Another Skeleton

            registerViewType(
                viewType = HomeSectionType.FAVORITE_PROMO,
                bindingInflater = { inflater, parent, attachToParent ->
                    ItemSectionIKuponCarouselBinding.inflate(inflater, parent, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    setFavoritePromo(position, itemBinding as ItemSectionIKuponCarouselBinding, item as HomeSectionType.FavoritePromo)
                }
            )

            // Another Skeleton Here

            registerViewType(
                viewType = HomeSectionType.NEARLY_EXPIRE_PROMO,
                bindingInflater = { inflater, parent, attachToParent ->
                    ItemSectionIKuponCarouselBinding.inflate(inflater, parent, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    setNearlyExpire(position, itemBinding as ItemSectionIKuponCarouselBinding, item as HomeSectionType.NearlyExpirePromo)
                }
            )
        }

    )

    // Since it's only dummy Impl you could replace this with your own implementation for each ViewType
    private fun setPromoRecommendation(
        position: Int,
        binding: ItemSectionIKuponCarouselBinding,
        item: HomeSectionType.PromoRecommendation
    ) {
        binding.tvTitle.text = item.title
        binding.tvDescription.text = item.description
        binding.carouselOverlay.overlayType = item.type
        binding.carouselOverlay.imageUrl = item.imageUrl
        binding.carouselOverlay.drawableHorizontalMargin = 12.dp

        binding.horizontalCarousel.items = item.data
    }

    private fun setFavoritePromo(
        position: Int,
        binding: ItemSectionIKuponCarouselBinding,
        item: HomeSectionType.FavoritePromo
    ) {
        binding.tvTitle.text = item.title
        binding.tvDescription.text = item.description
        binding.carouselOverlay.overlayType = item.type
        binding.carouselOverlay.imageUrl = item.imageUrl
        binding.carouselOverlay.drawableHorizontalMargin = 12.dp

        binding.horizontalCarousel.items = item.data
    }

    private fun setNearlyExpire(
        position: Int,
        binding: ItemSectionIKuponCarouselBinding,
        item: HomeSectionType.NearlyExpirePromo
    ) {
        binding.tvTitle.text = item.title
        binding.tvDescription.text = item.description
        binding.carouselOverlay.overlayType = item.type
        binding.carouselOverlay.imageUrl = item.imageUrl
        binding.carouselOverlay.drawableHorizontalMargin = 0.dp

        binding.carouselOverlay.updateLayoutParams<MarginLayoutParams> {
            updateMargins(top = 8.dp.toInt())
        }

        binding.horizontalCarousel.updateLayoutParams<MarginLayoutParams> {
            updateMargins(top = 8.dp.toInt(), bottom = 8.dp.toInt())
        }

        binding.horizontalCarousel.items = item.data
    }

    private class HomeDiffCallback : DiffUtil.ItemCallback<HomeSectionType>() {
        override fun areItemsTheSame(oldItem: HomeSectionType, newItem: HomeSectionType) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: HomeSectionType,
            newItem: HomeSectionType
        ) = oldItem.ordinal == newItem.ordinal
    }
}