/*
package com.example.floater.data.network

import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.example.androidTemplate.utils.AppConstants
import com.example.androidTemplate.utils.extensions.AwsCred
import java.io.File
import java.lang.Exception

class AwsRepo(private val transferUtility: TransferUtility) {

     fun beginUpload(filePaths: ArrayList<String?>) {
         val transferRecordMaps = ArrayList<HashMap<String?, Any?>>()
        val observers = transferUtility.getTransfersWithType(TransferType.UPLOAD)
        val filePath: String?
        if (filePaths.isNotEmpty())
            filePath = filePaths.first()
        else
            return

        if (filePath == null) {
           // appCtx.toast(appCtx.getString(R.string.could_not_find))
            return
        }
        val file = File(filePath)
        val observer: TransferObserver = transferUtility.upload(
            AppConstants.BUCKET_ID, file.getName(),
            file
        )
        observers.add(observer)
        val map = HashMap<String?, Any?>()
        AwsCred.fillMap(map, observer, false)
        transferRecordMaps.add(map)
        observer.setTransferListener(object : TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                println("onProgressChanged : $id $bytesCurrent $bytesTotal")


            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                println("onStateChanged : $state ${TransferState.COMPLETED == state}")
                when (state) {
                    TransferState.FAILED -> { }//hideButton(false)
                    TransferState.COMPLETED -> {

                    }
                    else->{

                    }
                }
            }

            override fun onError(id: Int, ex: Exception?) {
                println("onError   ")
            }

        })
    }
}*/
