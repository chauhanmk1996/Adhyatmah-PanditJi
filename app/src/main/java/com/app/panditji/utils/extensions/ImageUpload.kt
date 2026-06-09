/*
package com.example.delightapp.utils.extensions

import android.content.Context
import android.graphics.Region
import android.icu.util.TimeZone.getRegion
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.RegionUtils.getRegion
import com.amazonaws.services.s3.AmazonS3Client
import com.example.delightapp.utils.AppConstants
import com.google.android.gms.common.api.Api
import org.json.JSONException
import splitties.init.appCtx
import java.io.File
import java.util.concurrent.CountDownLatch


fun uploadPicture(path: String) {
    beginUploadInBackground(path)

}

private fun beginUploadInBackground(filePath: String) {

    if (filePath == null) {
        return
    }
    if (!filePath.isNullOrEmpty()) {
        val file = File(filePath)
        if (file != null) {
            val image_name = System.currentTimeMillis()
            //  const MEDIA_TYPE_VIDEO = 1;
            // const MEDIA_TYPE_PHOTO = 2;
            uploadWithTransferUtility("$image_name.png", file)


        }
    }
}

@Synchronized
fun uploadWithTransferUtility(name: String, file: File) {
    val transferUtility = TransferUtility.builder()
        .context(applilcation)
        .awsConfiguration(AWSMobileClient.getInstance().configuration)
        .s3Client(getS3Client(applilcation))
        .build()
    val uploadObserver =
        transferUtility.upload(
            AppConstant.BUCKET_ID,
            AppConstant.PROFILE_PATH + name,
            file
        )

    uploadObserver.setTransferListener(object : TransferListener {

        override fun onStateChanged(id: Int, state: TransferState) {
            if (TransferState.COMPLETED == state) {
                println("amit...image_url.....")
                addUrlToList(uploadObserver.key, name)
            } else if (TransferState.FAILED == state) {
                //  val firstUpdate = workDataOf(CoroutineUploadMediaWorker.IS_DOWNLOADED to "0")
                // setProgressAsync(firstUpdate)
                progress.value = 0
                isDownload.value = "0"
                println("gautamji11")


            }
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
            val percentDone = percentDonef.toInt()
            //  val firstUpdate = workDataOf(CoroutineUploadMediaWorker.Progress to percentDone)
            // setProgressAsync(firstUpdate)
            progress.value = percentDone
            println(
                "YourActivity ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%"
            )
            // updateNotification(percentDone)
        }

        override fun onError(id: Int, ex: Exception) {

        }

    })
}

fun getS3Client(context: Context): AmazonS3Client {
    if (sS3Client == null) {
        sS3Client = AmazonS3Client(getCredProvider(context))
        try {
            val regionString = AWSConfiguration(context)
                .optJsonObject("S3TransferUtility")
                .getString("Region")
            sS3Client!!.setRegion(Region.getRegion(regionString))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
    return sS3Client!!
}

fun addUrlToList(key: String?, name: String) {
    if (!name.isNullOrEmpty()) {
        val service = Api.client
        val model = ChangeImageModel()
        val userId = AppUtils.getPref(AppConstant.USER_PREF.USER_ID, applilcation)
        model.imageName = name
        model.userId = userId
        model.imageType = imageType.value
        val call = service.chng_image(model)
        call.enqueue(object : Callback<ChangeImageResponse> {
            override fun onFailure(call: Call<ChangeImageResponse>, t: Throwable) {
                //  val firstUpdate = workDataOf(CoroutineUploadMediaWorker.IS_DOWNLOADED to "0")
                // setProgressAsync(firstUpdate)
                isDownload.value = "0"
                progressLiveData.value = true

            }

            override fun onResponse(
                call: Call<ChangeImageResponse>,
                response: Response<ChangeImageResponse>
            ) {
                progressLiveData.value = true

                if (response.isSuccessful) {

                    if (response.body() != null) {
                        if (response.body()!!.commandResult != null) {
                            if (response.body()!!.commandResult!!.status == AppConstant.SUCCESS_CODE) {
                                if (imageType.value.equals("1")) {
                                    AppUtils.putPref(
                                        AppConstant.USER_PREF.PROFILE_IMAGE,
                                        AppConstant.AWS_PROFILE_URL + name,
                                        applilcation
                                    )
                                } else if (imageType.value.equals("2")) {
                                    AppUtils.putPref(
                                        AppConstant.USER_PREF.ANNOTATION_IMAGE,
                                        AppConstant.AWS_ANNOTATION_URL + name,
                                        applilcation
                                    )
                                }
                                isDownload.value = "1"
                            } else {
                                isDownload.value = "0"
                            }
                        } else {
                            isDownload.value = "0"
                        }
                    } else {
                        isDownload.value = "0"
                    }
                } else {
                    isDownload.value = "0"
                }
            }
        })

    }
}

private fun getCredProvider(context: Context): AWSCredentialsProvider {
    if (sMobileClient == null) {
        val latch = CountDownLatch(1)
        AWSMobileClient.getInstance().initialize(context, object :
            com.amazonaws.mobile.client.Callback<UserStateDetails> {
            override fun onResult(result: UserStateDetails) {
                latch.countDown()
            }

            override fun onError(e: Exception) {
                latch.countDown()
            }
        })
        try {
            latch.await()
            sMobileClient = AWSMobileClient.getInstance()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
    return sMobileClient!!
}*/
