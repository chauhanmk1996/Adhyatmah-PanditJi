package com.app.panditji.data.network

import AddCourt
import AddPaidServices
import AddSlotRequest
import AddVenueModel
import AddWeeklySlotModel
import BookingDetailsRequest
import GetCityModel
import GetStateModel
import LoginRequest
import LoginWithMobileRequest
import RatingBarRequest
import RegistrationModel
import SendOtpModel
import UpdatePaidService
import UpdateProfile
import UserBookingRequest
import android.content.Intent
import android.util.Log
import com.app.adhyatmah.domain.model.delete_account.delete_request.DeleteRequest
import com.app.adhyatmah.domain.model.faq.FAQResponse
import com.app.adhyatmah.domain.model.privacy_policy.TermPrivacyResponse
import com.app.adhyatmah.domain.model.profile.contact_us.ContactUsResponse
import com.app.panditji.core.network.ApiResponse
import com.app.panditji.core.network.GetSportResponse
import com.app.panditji.core.network.VerifyOtpResponse
import com.app.panditji.data.model.UploadMediaResponse
import com.app.panditji.data.model.get_banner.GetBannerResponse
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.data.model.get_profile.GetProfileResponse
import com.app.panditji.data.model.get_services.GetAllServicesResponse
import com.app.panditji.data.model.login.response.LoginResponse
import com.app.panditji.data.model.revenue_list.GetRevenueListResponse
import com.app.panditji.data.model.signup.response.SignupResponse
import com.app.panditji.data.model.update_booking_status.UpdateBookingStatusRequest
import com.app.panditji.data.model.update_profile.UpdateProfileResponse
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.ui.login.SignInActivity
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import splitties.init.appCtx
import java.util.concurrent.TimeUnit
import com.app.panditji.BuildConfig

interface ApiInterface {

    @Multipart
    @POST("createCustomer")
    suspend fun registerUserMultipart(

        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("phone") phone: RequestBody,

        @Part("about") about: RequestBody,
        @Part("address") address: RequestBody,
        @Part("city") city: RequestBody,
        @Part("state") state: RequestBody,
        @Part("country") country: RequestBody,
        @Part("zip") zip: RequestBody,

        @Part("gotra") gotra: RequestBody,
        @Part("pankti") pankti: RequestBody,
        @Part("shakha") shakha: RequestBody,
        @Part("veda") veda: RequestBody,
        @Part("sutra") sutra: RequestBody,
        @Part("pravar") pravar: RequestBody,

        @Part("aadhar") aadhar: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("experience") experience: RequestBody,

        @Part("deviceType") deviceType: RequestBody,
        @Part("deviceToken") deviceToken: RequestBody,

        @Part("image") image: RequestBody,
        @Part("role") role: RequestBody,
        @Part services: List<MultipartBody.Part>,  // services[]
        @Part language: List<MultipartBody.Part>,   // language[]
        @Part("referral_code") referral_code: RequestBody?
    ): Response<SignupResponse>


    @POST("login")
    suspend fun loginUser(
        @Body model: LoginRequest?
    ): Response<LoginResponse>

    @POST("login-mobile")
    suspend fun loginWithMobile(
        @Body model: LoginWithMobileRequest?
    ): Response<LoginResponse>

    @GET("getBanners")
    suspend fun getBanner(): Response<GetBannerResponse>

    @POST("verify-mobile-otp")
    suspend fun verifyOtp(
        @Body model: RegistrationModel?
    ): Response<VerifyOtpResponse>


    @POST("resend-mobile-otp")
    suspend fun reSendOtp(
        @Body model: SendOtpModel?
    ): Response<ApiResponse>


   /* @DELETE("user/deleteAccount")
    suspend fun deleteAccount(
        @Header("auth-token") authToken: String?,
    ): Response<ApiResponse>*/


    @GET("faqs")
    suspend fun getFaqs(
        @Query("type") type: String = "USER"
    ): Response<ApiResponse>

    @GET("aboutUs")
    suspend fun getAboutUs(): Response<ApiResponse>

    @GET("venues")
    suspend fun getVenueList(
        @Header("auth-token") token: String?,
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
        @Query("search") search: String?,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<ApiResponse>


    @GET("venue-details/{id}")
    suspend fun getVenueDetailsById(
        @Header("auth-token") authToken: String?,
        @Path("id") venueId: String?
    ): Response<ApiResponse>

    @POST("logout")
    suspend fun userLogout(
        @Header("auth-token") authToken: String?,
    ): Response<ApiResponse>

    @POST("deleteCustomer")
    suspend fun deleteAccount(
        @Body model: DeleteRequest?
    ): Response<ApiResponse>

    @GET("panditProfile")
    suspend fun userProfile(
        @Query("vendorId") vendorId: String? = null,
    ): Response<GetProfileResponse>

    @GET("getAllPanditServices")
    suspend fun getAllServices(
    ): Response<GetAllServicesResponse>

    @POST("updateCustomerProfile")
    suspend fun userUpdateProfile(
        @Body model: UpdateProfile? = null
    ): Response<UpdateProfileResponse>

    @Multipart
    @POST("upload")
    suspend fun upLoadImg(
        @Part file: MultipartBody.Part,
        @Part ("customerId") customerId: RequestBody
    ): Response<UploadMediaResponse>

    @GET("available-slots")
    suspend fun getAvailableSlots(
        @Query("vendor_id") vendorId: Int?,
        @Query("court_id") courtId: Int?,
        @Query("sport_id") sportId: String?,  // nullable if it's empty
        @Query("date") date: String?
    ): Response<ApiResponse>

    @GET("user/paid-services")
    suspend fun getPaidServices(
        @Header("auth-token") token: String?,
        @Query("court_id") courtId: Int? = null,
    ): Response<ApiResponse>


    //    @GET("user/booking/list")
//    suspend fun getBookingList(
//        @Header("auth-token") token: String?,
//        @Query("filter") filter: String,
//        @Query("page") page: Int,
//        @Query("per_page") perPage: Int
//    ): Response<ApiResponse>
    @GET("bookingHistory")
    suspend fun getBookings(
        @Header("auth-token") token: String? = null,
        @Query("type") type: String? = null,
    ): Response<GetBookingResponse>
    @GET("bookingHistory")
    suspend fun getBookingsInner(
        @Header("auth-token") token: String? = null,
        @Query("type") type: String? = null,
    ): GetBookingResponse



    @POST("updateBookingStatus")
    suspend fun updateBookingStatus(
        @Header("auth-token") token: String? = null,
        @Body userBookingRequest: UpdateBookingStatusRequest,
    ): Response<GetBookingResponse>

    @POST("updateBookingStatus")
    suspend fun updateBookingStatusInner(
        @Header("auth-token") token: String? = null,
        @Body userBookingRequest: UpdateBookingStatusRequest,
    ): GetBookingResponse






    @POST("user/booking/create")
    suspend fun createUserBooking(
        @Body userBookingRequest: UserBookingRequest,
        @Header("auth-token") token: String?,
    ): Response<ApiResponse>

    @POST("user/booking/getBookingDetails")
    suspend fun userBookingDetails(
        @Body request: BookingDetailsRequest,
        @Header("auth-token") token: String?,
    ): Response<ApiResponse>

    @POST("user/booking/split-screen")
    suspend fun userSplitPaymentDetails(
        @Header("auth-token") token: String?,
        @Body request: BookingDetailsRequest,
    ): Response<ApiResponse>


    @POST("user/booking/review")
    suspend fun addRateReview(
        @Header("auth-token") token: String?,
        @Body request: RatingBarRequest
    ): Response<ApiResponse>


    @GET("get-social-contact")
    suspend fun socialContacts(
    ): Response<ApiResponse>

    @GET("customerSupport")
    suspend fun customerSupport(): Response<ApiResponse>


    @POST("get-state")
    suspend fun getStateList(
        @Body model: GetStateModel?
    ): Response<ApiResponse>

    @POST("get-city")
    suspend fun getCityList(
        @Body model: GetCityModel?
    ): Response<ApiResponse>

    @GET("amenities")
    suspend fun getAmmenities(
    ): Response<ApiResponse>


    @POST("vendor/send-otp")
    suspend fun sendOtp(
        @Body model: SendOtpModel?
    ): Response<ApiResponse>


    @POST("vendor/venue")
    suspend fun addVenue(
        @Body model: AddVenueModel?
    ): Response<ApiResponse>


    /*@GET("vendor/venue-details/{id}")
    suspend fun getVenueDetailsById(
        @Header("auth-token") authToken: String?,
        @Path("id") venueId: String?
    ): Response<ApiResponse>
*/
    @GET("sports-list")
    suspend fun getSportList(
    ): Response<GetSportResponse>

    @POST("vendor/add-court")
    suspend fun addCourt(
        @Body model: AddCourt?
    ): Response<ApiResponse>

    @POST("vendor-slots/weekly")
    suspend fun addWeeklySlot(
        @Header("auth-token") authToken: String?,
        @Body model: AddWeeklySlotModel?
    ): Response<ApiResponse>


    @GET("vendor/venues")
    suspend fun getVenues(
        @Header("auth-token") token: String?,
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
        @Query("search") search: String?
    ): Response<ApiResponse>


    @POST("vendor/paid-services")
    suspend fun addPaidServices(
        @Body model: AddPaidServices?
    ): Response<ApiResponse>


    /* @GET("vendor/paid-services")
     suspend fun getPaidServices(
         @Header("auth-token") token: String?,
         @Query("court_id") courtId: Int? = null,
     ): Response<ApiResponse>
 */


    @POST("vendor/update-paid-services")
    suspend fun updatePaidServices(
        @Body model: UpdatePaidService? = null
    ): Response<ApiResponse>


    @DELETE("vendor/paid-services/{id}")
    suspend fun deletePaidServices(
        @Header("auth-token") authToken: String?,
        @Path("id") venueId: String?
    ): Response<ApiResponse>

    @GET("vendor/myProfile")
    suspend fun getUserProfile(
        @Header("auth-token") authToken: String?,
    ): Response<ApiResponse>

    @GET("vendor-slots/schedule")
    suspend fun getSlots(
        @Header("auth-token") token: String? = null,
        @Query("court_id") courtId: Int? = null,
        @Query("from") fromDate: String? = null,
        @Query("to") toDate: String? = null
    ): Response<ApiResponse>


    @POST("vendor-slots/overrides")
    suspend fun addNewSlot(
        @Body model: AddSlotRequest? = null
    ): Response<ApiResponse>


    @POST("vendor/updateProfile")
    suspend fun updateProfile(
        @Header("auth-token") authToken: String?,
        @Body model: UpdateProfile? = null
    ): Response<ApiResponse>

    @GET("getContactInfo")
    suspend fun getContactUs(): Response<ContactUsResponse>

    @GET("getPolicies")
    suspend fun getPolicies(): Response<TermPrivacyResponse>

    @GET("getFAQs")
    suspend fun faq(@Query("role") role: String): Response<FAQResponse>

    @GET("getPanditRevenue")
    suspend fun getPanditRevenue(@Query("vendorId") vendorId: String): Response<GetRevenueListResponse>

    companion object {
        private const val BASE_URL = "https://api.adhyatmah.com/api/"

        private const val AUTH = "Authorization"

        fun create(prefsHelper: PrefsHelper): ApiInterface {
            val okHttpClient = OkHttpClient.Builder().apply {
                this.connectTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES) // write timeout
                    .readTimeout(2, TimeUnit.MINUTES) // read timeout
                    .retryOnConnectionFailure(true)
                addInterceptor {
                    val request: Request.Builder = it.request().newBuilder()
                    request.header("Content-Type", "application/json")
                    request.header("Accept", "application/json")
                    request.addHeader("lang", prefsHelper.selectedLanguageCode)

                    if (prefsHelper.authToken.isNotEmpty()) {
                        request.header(AUTH, "Bearer " + prefsHelper.authToken)
                    }
                    val response = it.proceed(request.build())
                    if (response.code == 401) {
                        val intent = Intent(appCtx, SignInActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        prefsHelper.sharedPref.edit().apply {
                            clear()
                            apply()
                        }
                        appCtx.startActivity(intent)
                    }
                    response
                }

                addNetworkInterceptor(
                    HttpLoggingInterceptor { logMessage ->
                        if (BuildConfig.DEBUG) {
                            Log.d("PanditJi:: okhhtp", logMessage)
                        }
                    }.apply { level = HttpLoggingInterceptor.Level.BODY }
                )

            }.build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }
}