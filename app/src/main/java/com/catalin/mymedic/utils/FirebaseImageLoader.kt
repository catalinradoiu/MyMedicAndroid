package com.catalin.mymedic.utils

import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StreamDownloadTask

import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.security.MessageDigest


/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class FirebaseImageLoader : ModelLoader<StorageReference, InputStream> {


    /**
     * Factory to create [FirebaseImageLoader].
     */
    class Factory : ModelLoaderFactory<StorageReference, InputStream> {

        override fun build(factory: MultiModelLoaderFactory): ModelLoader<StorageReference, InputStream> {
            return FirebaseImageLoader()
        }

        override fun teardown() {
            // No-op
        }
    }

    @Nullable
    override fun buildLoadData(reference: StorageReference, height: Int, width: Int, options: Options): ModelLoader.LoadData<InputStream> =
        ModelLoader.LoadData<InputStream>(FirebaseStorageKey(reference), FirebaseStorageFetcher(reference))


    override fun handles(reference: StorageReference): Boolean {
        return true
    }

    private class FirebaseStorageKey(private val storageReference: StorageReference) : Key {

        override fun updateDiskCacheKey(digest: MessageDigest) {
            digest.update(storageReference.path.toByteArray(Charset.defaultCharset()))
        }
    }

    private class FirebaseStorageFetcher(private val mRef: StorageReference) :
        DataFetcher<InputStream> {
        private var streamTask: StreamDownloadTask? = null
        private var inputStream: InputStream? = null

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            streamTask = mRef.stream
            streamTask?.addOnSuccessListener { snapshot ->
                inputStream = snapshot.stream
                callback.onDataReady(inputStream)
            }?.addOnFailureListener { e -> callback.onLoadFailed(e) }
        }

        override fun cleanup() {
            // Close stream if possible
            if (inputStream != null) {
                try {
                    inputStream?.close()
                    inputStream = null
                } catch (e: IOException) {
                    Log.w(TAG, "Could not close stream", e)
                }

            }
        }

        override fun cancel() {
            // Cancel task if possible
            if (streamTask != null && streamTask?.isInProgress == true) {
                streamTask?.cancel()
            }
        }

        @NonNull
        override fun getDataClass(): Class<InputStream> {
            return InputStream::class.java
        }

        @NonNull
        override fun getDataSource(): DataSource {
            return DataSource.REMOTE
        }
    }

    companion object {

        private val TAG = "FirebaseImageLoader"
    }
}