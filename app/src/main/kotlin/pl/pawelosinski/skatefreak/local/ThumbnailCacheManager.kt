package pl.pawelosinski.skatefreak.local


import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import pl.pawelosinski.skatefreak.model.TrickRecord

class ThumbnailCacheManager() {

    companion object{
        private val cache: LruCache<String, Bitmap> = LruCache(50 * 1024 * 1024) // 50 MB

        fun preloadTrickRecordImages(trickRecords: List<TrickRecord>, context: Context, onSuccess: () -> Unit = {}) {
            val mainHandler = Handler(Looper.getMainLooper())

            trickRecords.forEach { trickRecord ->
                val trickId = trickRecord.id
                val trickUrl = trickRecord.videoUrl

                Glide.with(context)
                    .asBitmap()
                    .load(trickUrl)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            cache.put(trickId, resource)
                        }
                    })

                // delay between requests to avoid overwhelming the server
                Thread.sleep(100)
            }
            onSuccess()
        }

        fun getVideoThumbnail(trickId: String): Bitmap? {
            return cache.get(trickId)
        }
    }
}