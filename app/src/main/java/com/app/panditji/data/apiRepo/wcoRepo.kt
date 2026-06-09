package com.app.panditji.data.apiRepo

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
import androidx.lifecycle.liveData
import com.app.adhyatmah.domain.model.delete_account.delete_request.DeleteRequest
import com.app.panditji.core.network.BaseRepo
import com.app.panditji.data.model.signup.response.SignupResponse
import com.app.panditji.data.model.update_booking_status.UpdateBookingStatusRequest
import com.app.panditji.data.network.ApiInterface
import com.app.panditji.data.sharedPrefs.PrefsHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

class apiRepo(private val apiInterface: ApiInterface, private val prefsHelper: PrefsHelper):
    BaseRepo() {

    private fun getBearerToken() = """Bearer ${prefsHelper.authToken}"""

    suspend fun registerUserMultipart(
        firstName: RequestBody,
        lastName: RequestBody,
        email: RequestBody,
        password: RequestBody,
        gender: RequestBody,
        phone: RequestBody,
        about: RequestBody,
        address: RequestBody,
        city: RequestBody,
        state: RequestBody,
        country: RequestBody,
        zip: RequestBody,
        gotra: RequestBody,
        pankti: RequestBody,
        shakha: RequestBody,
        veda: RequestBody,
        sutra: RequestBody,
        pravar: RequestBody,
        aadhar: RequestBody,
        dateOfBirth: RequestBody,
        experience: RequestBody,
        deviceType: RequestBody,
        deviceToken: RequestBody,
        image: RequestBody,
        role: RequestBody,
        services: List<MultipartBody.Part>,
        language: List<MultipartBody.Part>,
        referralCode:RequestBody?
    ) = loadData {
        apiInterface.registerUserMultipart(
            firstName, lastName, email, password, gender, phone,
            about, address, city, state, country, zip,
            gotra, pankti, shakha, veda, sutra, pravar,
            aadhar, dateOfBirth, experience,
            deviceType, deviceToken,
            image,role,  services, language,referralCode
        )
    }


    suspend fun loginUser(model: LoginRequest?) =
        loadData { apiInterface.loginUser(model) }
    suspend fun loginWithMobile(model: LoginWithMobileRequest?) =
        loadData { apiInterface.loginWithMobile(model) }

    suspend fun getBanner() =
        loadData { apiInterface.getBanner() }

    suspend fun verifyOtp(model: RegistrationModel?) =
        loadData { apiInterface.verifyOtp(model) }

    suspend fun reSendOtp(model: SendOtpModel?) =
        loadData { apiInterface.reSendOtp(model) }


    suspend fun getDeleteAccount(model: DeleteRequest) =
        loadData { apiInterface.deleteAccount(model) }


    suspend fun getAboutsUs() =
        loadData { apiInterface.getAboutUs() }

    suspend fun getVenuesList(authToken: String, perPage: Int, page: Int, search: String?, latitude: Double, longitude: Double) =
        loadData { apiInterface.getVenueList(authToken,perPage,page,search,latitude,longitude) }

    suspend fun getVenueDetailById(token:String?,venueId:String?) =
        loadData { apiInterface.getVenueDetailsById(token,venueId) }

    suspend fun userLogout(authToken: String) =
        loadData { apiInterface.userLogout(authToken) }

    suspend fun deleteAccount(model: DeleteRequest) =
        loadData { apiInterface.deleteAccount(model) }

    suspend fun userProfile(userId: String) =
        loadData { apiInterface.userProfile( userId) }

    suspend fun getAllServices() =
        loadData { apiInterface.getAllServices( ) }

    suspend fun uploadImage(@Part file: MultipartBody.Part,
                            @Part ("customerId") customerId: RequestBody) =
        loadData { apiInterface.upLoadImg(file,customerId) }
    suspend fun userUpdateProfile(authToken: String,model:UpdateProfile?) =
        loadData { apiInterface.userUpdateProfile(model) }

    suspend fun getAvailableSlots(vendorId:Int?,courtId:Int?,sportId:String?, fromDate:String) =
        loadData { apiInterface.getAvailableSlots(vendorId, courtId,sportId,fromDate) }

//    suspend fun getBookingList(token:String?,filter: String,perPage: Int,page: Int) =
//        loadData { apiInterface.getBookingList(token,filter,perPage,page) }

    suspend fun getBookings(type:String, token:String?) =
        loadData { apiInterface.getBookings(token, type) }

    suspend fun updateBookingStatus(token:String?, request: UpdateBookingStatusRequest) =
        loadData { apiInterface.updateBookingStatus(token, request) }

    suspend fun createUserBooking(model:UserBookingRequest, token:String?) =
        loadData { apiInterface.createUserBooking(model,token) }

    suspend fun userBookingDetails(request: BookingDetailsRequest, token:String?) =
        loadData { apiInterface.userBookingDetails(request,token) }

    suspend fun userSplitPaymentDetails(token:String?, request: BookingDetailsRequest) =
        loadData { apiInterface.userSplitPaymentDetails(token,request) }

    suspend fun addRateReview(token:String?,request: RatingBarRequest) =
        loadData { apiInterface.addRateReview(token,request) }

    suspend fun socialContacts() =
        loadData { apiInterface.socialContacts()
        }
    suspend fun customerSupport() =
        loadData { apiInterface.customerSupport()
        }



    suspend fun sendOtp(model: SendOtpModel?) =
        loadData { apiInterface.sendOtp(model) }

    suspend fun getStateList(model: GetStateModel?) =
        loadData { apiInterface.getStateList(model) }


    suspend fun getCityList(model: GetCityModel?) =
        loadData { apiInterface.getCityList(model) }

    suspend fun getAmmenties() =
        loadData { apiInterface.getAmmenities() }

    suspend fun addVenue(model: AddVenueModel?) =
        loadData { apiInterface.addVenue(model) }


    suspend fun getSportList() =
        loadData { apiInterface.getSportList() }

    suspend fun addCourt(model: AddCourt?) =
        loadData { apiInterface.addCourt(model) }

    suspend fun addWeeklySlot(token:String,model: AddWeeklySlotModel?) =
        loadData { apiInterface.addWeeklySlot(token,model) }


    suspend fun getVenueList(token:String?,perPage: Int,page: Int,search: String?) =
        loadData { apiInterface.getVenues(token,perPage,page,search) }

    suspend fun addPaidServices(model: AddPaidServices?) =
        loadData { apiInterface.addPaidServices(model) }

    suspend fun getPaidServices(token:String?,court_id:Int?) =
        loadData { apiInterface.getPaidServices(token,court_id) }


    suspend fun updatePaidServices(model:UpdatePaidService?) =
        loadData { apiInterface.updatePaidServices(model) }

    suspend fun deletePaidServices(token:String?,id:String?) =
        loadData { apiInterface.deletePaidServices(token,id) }

    suspend fun getUserProfile(token:String?) =
        loadData { apiInterface.getUserProfile(token) }

    suspend fun getSlots(token:String?,courtId:Int?,fromDate:String?,toDate:String?) =
        loadData { apiInterface.getSlots(token, courtId, fromDate, toDate) }

    suspend fun addNewSlot(model:AddSlotRequest?) =
        loadData { apiInterface.addNewSlot(model) }


    suspend fun updateProfile(token:String?,model:UpdateProfile?) =
        loadData { apiInterface.updateProfile(token,model) }

    suspend fun getContactUs() =
        loadData { apiInterface.getContactUs() }

    suspend fun getPolicies() =
        loadData { apiInterface.getPolicies() }

    suspend fun getFaqs(role: String) =
        loadData { apiInterface.faq(role) }

    suspend fun getPanditRevenue(vendorId: String) =
        loadData { apiInterface.getPanditRevenue(vendorId) }

}
