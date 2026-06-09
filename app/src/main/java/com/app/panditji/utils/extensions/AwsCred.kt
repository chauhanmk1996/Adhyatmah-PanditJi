/*
package com.example.androidTemplate.utils.extensions

import android.content.Context
import android.net.Uri
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import org.json.JSONException
import splitties.init.appCtx
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.HashMap


object AwsCred {

    private var sS3Client: AmazonS3Client? = null
    private var sMobileClient: AWSCredentialsProvider? = null
    private var sTransferUtility: TransferUtility? = null


    private fun getCredProvider(context: Context): AWSCredentialsProvider? {
        if (sMobileClient == null) {
            val latch = CountDownLatch(1)
            AWSMobileClient.getInstance().initialize(
                context,
                object : Callback<UserStateDetails?> {
                    override fun onResult(result: UserStateDetails?) {
                        latch.countDown()
                    }

                    override fun onError(e: Exception) {
                        latch.countDown()
                        context.toast(e.localizedMessage)
                    }
                })
            try {
                latch.await()
                sMobileClient = AWSMobileClient.getInstance()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return sMobileClient
    }


    fun getS3Client(context: Context): AmazonS3Client? {
        if (sS3Client == null) {
            try {
                val regionString = AWSConfiguration(context)
                    .optJsonObject("S3TransferUtility")
                    .getString("Region")
                val region =
                    Region.getRegion(regionString)
                sS3Client = AmazonS3Client(awsConfig(), region)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return sS3Client
    }


    fun getTransferUtility(context: Context): TransferUtility? {
        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder()
                .context(context)
                .s3Client(getS3Client(context))
                .awsConfiguration(AWSConfiguration(context))
                .build()
        }
        return sTransferUtility
    }


    fun getBytesString(bytes: Long): String {
        val quantifiers = arrayOf(
            "KB", "MB", "GB", "TB"
        )
        var speedNum = bytes.toDouble()
        var i = 0
        while (true) {
            if (i >= quantifiers.size) {
                return ""
            }
            speedNum /= 1024.0
            if (speedNum < 512) {
                return String.format(
                    Locale.US,
                    "%.2f",
                    speedNum
                ) + " " + quantifiers[i]
            }
            i++
        }
    }

    @Throws(IOException::class)
    fun copyContentUriToFile(
        context: Context,
        uri: Uri?
    ): File? {
        val `is` = context.contentResolver.openInputStream(uri!!)
        val copiedData = File(
            context.getDir("SampleImagesDir", Context.MODE_PRIVATE),
            UUID.randomUUID().toString()
        )
        copiedData.createNewFile()
        val fos = FileOutputStream(copiedData)
        val buf = ByteArray(2046)
        var read = -1
        while (`is`!!.read(buf).also { read = it } != -1) {
            fos.write(buf, 0, read)
        }
        fos.flush()
        fos.close()
        return copiedData
    }


    fun fillMap(
        map: HashMap<String?, Any?>,
        observer: TransferObserver,
        isChecked: Boolean
    ) {
        val progress = (observer.bytesTransferred.toDouble() * 100 / observer
            .bytesTotal).toInt()
        map["id"] = observer.id
        map["checked"] = isChecked
        map["fileName"] = observer.absoluteFilePath
        map["progress"] = progress
        map["bytes"] = (getBytesString(observer.bytesTransferred) + "/"
                + getBytesString(observer.bytesTotal))
        map["state"] = observer.state
        map["percentage"] = "$progress%"
    }

    fun awsConfig() = CognitoCachingCredentialsProvider(
            appCtx,
        "ap-southeast-1:cd518d25-b4e4-4f7f-afdf-d93e1a34f6c3"
            */
/*"ap-southeast-1:7eb8463d-9a9a-43aa-85fc-d6c298ab1570"*//*
, // Identity pool ID
            Regions.AP_SOUTHEAST_1 // Region
        )

}*/
