package com.app.panditji.utils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import bookingVm
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.utils.lang.LocalizationApplication
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import networkModules
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import prefModule
import repoBookingModules
import repoModules
import vmModules
import java.util.*
open class MyApplication : LocalizationApplication(), LifecycleObserver {
    private val prefs by inject<PrefsHelper>()
    companion object {
        var deviceToken: String? = ""
        var instance: MyApplication? = null
        var isBackground = true
        var foreGround = false
    }

    override fun onCreate() {
        super.onCreate()
        instance =this
        //FirebaseApp.initializeApp(applicationContext)
        //getFirebaseToken()
      //  getInstallationId()
        configKoin()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    override fun getDefaultLanguage(): Locale {
        return Locale.ENGLISH
    }


    private fun configKoin() {
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    prefModule,
                    //firebaseModules,
                    networkModules,
                    repoModules,
                    vmModules,
                    bookingVm,
                    repoBookingModules
                   // awsModule
                )
            )
        }
    }

    fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            token.let {
                deviceToken = it
                println("HimanshuDeviceToken:$deviceToken")
                println("c:$it")
            }
        })
    }
 /*   fun getInstallationId(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task: Task<InstanceIdResult> ->
                if (task.isSuccessful) {
                    val instanceId = task.result.id
                    println("HimanshuDeviceToken:$instanceId")
                    // Use instanceId as needed.
                } else {
                    // Handle the error.
                }
            }
    }*/
    fun resetKoin() {
        stopKoin()
        configKoin()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        isBackground = true
        println("HimanshuIsBackground:$isBackground")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        isBackground = false
        println("HimanshuIsBackground:$isBackground")

    }
 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        foreGround = false
       // println("praveenIsBackground:$isBackground")

    }

}