/*
import com.example.androidtemplate.data.network.ApiInterface
import com.example.sampleproject.data.network.FirebaseRepo
import com.example.androidtemplate.data.network.GoogleBuilder
import com.example.androidtemplate.data.sharedPrefs.PrefsHelper
import com.example.sampleproject.ui.post.PostRepo
import com.example.sampleproject.ui.post.PostVM
import com.example.sampleproject.ui.login.LoginRepo
import com.example.sampleproject.ui.login.LoginVM
import com.example.sampleproject.ui.home.explore.ExploreRepo
import com.example.sampleproject.ui.home.explore.ExploreVM
import com.example.sampleproject.ui.home.follow.FollowRepo
import com.example.sampleproject.ui.home.follow.FollowVM
import com.example.sampleproject.ui.home.notification.NotificationRepo
import com.example.sampleproject.ui.home.notification.NotificationVM
import com.example.sampleproject.ui.home.order.OrderRepo
import com.example.sampleproject.ui.home.order.OrderVM
import com.example.sampleproject.ui.home.payment.PaymentRepo
import com.example.sampleproject.ui.home.payment.PaymentVM
import com.example.sampleproject.ui.home.profile.ProfileRepo
import com.example.sampleproject.ui.home.profile.ProfileVM
import com.example.sampleproject.ui.home.resturant.RestaurantRepo
import com.example.sampleproject.ui.home.resturant.RestaurantVM
import com.example.sampleproject.ui.home.search.SearchRepo
import com.example.sampleproject.ui.home.search.SearchVM
import com.example.androidtemplate.utils.extensions.AwsCred
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

*/

/*
val vmModules = module {
    single { ProfileVM(get()) }
    single { SearchVM(get()) }
    single { ExploreVM(get()) }
    single { PostVM(get()) }
    single { LoginVM(get(),get(),get()) }
    single { FollowVM(get()) }
    single { OrderVM(get()) }
    single { RestaurantVM(get()) }
    single { NotificationVM(get()) }
    single { PaymentVM(get()) }
}

val repoModules = module {
    single { ProfileRepo(get()) }
    single { SearchRepo(get()) }
    single { ExploreRepo(get()) }
    single { PostRepo(get()) }
    single { LoginRepo(get()) }
    single { FollowRepo(get()) }
    single { OrderRepo(get()) }
    single { RestaurantRepo(get()) }
    single { NotificationRepo(get()) }
    single { PaymentRepo(get()) }
}


val networkModules = module { single { ApiInterface.create(get()) } }

val firebaseModules = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseRepo(get()) }
    single { GoogleBuilder(get()) }
}

val prefModule= module {
    single { PrefsHelper(get()) }
}

val awsModule= module {
    single { AwsCred.getTransferUtility(get()) }
    single { AwsCred.getS3Client(get()) }

}


*/

import com.app.panditji.data.apiRepo.RepoBooking
import com.app.panditji.data.apiRepo.apiRepo
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.network.ApiInterface
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.ui.dashboard.BookingViewModel
import org.koin.dsl.module

val vmModules = module {
    single { apiVm(get()) }
    //single { ProfileVM(get()) }
}

val bookingVm = module {
    single { BookingViewModel( get()) }
    //single { ProfileVM(get()) }
}

/*
val localDbm = module {
    single { AppDatabase.getDatabase(get()).appDao() }
    single { LocalDbRepo(get()) }
    viewModel { LocalDbVm(get()) }
}
*/

val repoModules = module {
    single { apiRepo(get(), get()) }
    //single { ProfileRepo(get(), get()) }
}

val repoBookingModules = module {
    single { RepoBooking(get(), get()) }
    //single { ProfileRepo(get(), get()) }
}


val networkModules = module { single { ApiInterface.create(get()) } }

/*val firebaseModules = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseRepo(get()) }
    single { GoogleBuilder(get()) }
}*/

val prefModule = module {
    single { PrefsHelper(get()) }
}

/*
val awsModule = module {
    single { AwsCred.getTransferUtility(get()) }
    single { AwsCred.getS3Client(get()) }

}*/
