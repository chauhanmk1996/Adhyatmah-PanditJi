package com.app.panditji.data.apiVm
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.adhyatmah.domain.model.delete_account.delete_request.DeleteRequest
import com.app.panditji.data.apiRepo.apiRepo
import com.app.panditji.data.model.update_booking_status.UpdateBookingStatusRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part


class apiVm (private val apiRepo: apiRepo): ViewModel() {

    fun registerUserMultipart(
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
    ) = liveData {
        emit(
            apiRepo.registerUserMultipart(
                firstName, lastName, email, password, gender, phone,
                about, address, city, state, country, zip,
                gotra, pankti, shakha, veda, sutra, pravar,
                aadhar, dateOfBirth, experience,
                deviceType, deviceToken,
                image,role,  services, language,referralCode
            ).value
        )
    }


    // Helper to convert string to RequestBody
    private fun String.toRequestBody(): RequestBody =
        RequestBody.create("text/plain".toMediaTypeOrNull(), this)

    fun loginUser(model : LoginRequest?) =
        liveData { emit(apiRepo.loginUser(model).value) }

    fun loginWithMobile(model : LoginWithMobileRequest?) =
        liveData { emit(apiRepo.loginWithMobile(model).value) }

    fun getBanner() =
        liveData { emit(apiRepo.getBanner().value) }

    fun verifyOtp(model : RegistrationModel?) =
        liveData {
            emit(apiRepo.verifyOtp(model).value) }

    fun reSendOtp(model : SendOtpModel?) =
        liveData {
            emit(apiRepo.reSendOtp(model).value) }

    fun getDeleteAccount(model: DeleteRequest) =
        liveData {
            emit(apiRepo.getDeleteAccount(model).value) }


    fun getAboutsUs() =
        liveData { emit(apiRepo.getAboutsUs().value) }

    fun getVenuesList(authToken: String, perPage: Int, page: Int, search: String?, lat: Double, long: Double) =
        liveData { emit(apiRepo.getVenuesList(authToken,perPage,page,search,lat,long).value) }

    fun getVenueDetailById(token:String?,venueId:String?) =
        liveData { emit(apiRepo.getVenueDetailById(token,venueId).value) }

    fun userLogout(authToken: String) =
        liveData { emit(apiRepo.userLogout(authToken).value) }

    fun userProfile(userId: String) =
        liveData { emit(apiRepo.userProfile( userId).value) }

    fun getAllServices() =
        liveData { emit(apiRepo.getAllServices().value) }

    fun uploadImage(@Part file: MultipartBody.Part,
                    @Part ("customerId") customerId: RequestBody) =
        liveData { emit(apiRepo.uploadImage(file, customerId).value) }
    fun userUpdateProfile(authToken: String, model:UpdateProfile?) =
        liveData { emit(apiRepo.userUpdateProfile(authToken, model).value) }


    fun getAvailableSlots(vendorId: Int?, courtId:Int?, sportId: String?,fromDate:String) =
        liveData { emit(apiRepo.getAvailableSlots(vendorId,courtId,sportId,fromDate).value) }

    fun getBookings(type:String, authToken: String) =
        liveData { emit(apiRepo.getBookings(type, authToken,).value) }

    fun updateBookingStatus(authToken: String, request: UpdateBookingStatusRequest) =
        liveData { emit(apiRepo.updateBookingStatus(authToken, request,).value) }


    fun createUserBooking(request:UserBookingRequest, authToken: String) =
        liveData { emit(apiRepo.createUserBooking(request,authToken).value) }

    fun userBookingDetails(request:BookingDetailsRequest, authToken: String) =
        liveData { emit(apiRepo.userBookingDetails(request,authToken).value) }

    fun userSplitPaymentDetails(authToken: String, request:BookingDetailsRequest,) =
        liveData { emit(apiRepo.userSplitPaymentDetails(authToken,request).value) }

    fun addRateReview(authToken: String, request: RatingBarRequest) =
        liveData { emit(apiRepo.addRateReview(authToken,request).value) }

    fun socialContacts() =
        liveData { emit(apiRepo.socialContacts().value) }

    fun customerSupport() =
        liveData { emit(apiRepo.customerSupport().value) }


    fun sendOtp(model : SendOtpModel?) =
        liveData { emit(apiRepo.sendOtp(model).value) }


    fun getStateList(model : GetStateModel?) =
        liveData { emit(apiRepo.getStateList(model).value) }

    fun getCityList(model : GetCityModel?) =
        liveData { emit(apiRepo.getCityList(model).value) }


    fun getAmmenties() =
        liveData { emit(apiRepo.getAmmenties().value) }

    fun addVenue(model : AddVenueModel?) =
        liveData { emit(apiRepo.addVenue(model).value) }



    fun getSportsList() =
        liveData { emit(apiRepo.getSportList().value) }

    fun addCourt(model : AddCourt?) =
        liveData { emit(apiRepo.addCourt(model).value) }

    fun addWeeklySlot(token:String,model : AddWeeklySlotModel?) =
        liveData { emit(apiRepo.addWeeklySlot(token,model).value) }

    fun getVenueList(token:String?,perPage: Int,page: Int,search: String?) =
        liveData { emit(apiRepo.getVenueList(token,perPage,page,search).value) }


    fun addPaidServices(model : AddPaidServices?) =
        liveData { emit(apiRepo.addPaidServices(model).value) }

    fun getPaidServices(token:String,courtId:Int?) =
        liveData { emit(apiRepo.getPaidServices(token,courtId).value) }


    fun updatePaidServices(model:UpdatePaidService?) =
        liveData { emit(apiRepo.updatePaidServices(model).value) }

    fun deletePaidServices(token:String?,id:String?) =
        liveData { emit(apiRepo.deletePaidServices(token,id).value) }

    fun getUserDetail(token:String?) =
        liveData { emit(apiRepo.getUserProfile(token).value) }

    fun getSlots(token:String?,courtId:Int?,fromDate:String?,toDate:String?) =
        liveData { emit(apiRepo.getSlots(token,courtId,fromDate,toDate).value) }

    fun addNewSlot(model:AddSlotRequest?) =
        liveData { emit(apiRepo.addNewSlot(model).value) }

    fun updateProfile(token:String?,model:UpdateProfile?) =
        liveData { emit(apiRepo.updateProfile(token,model).value) }

    fun getContactUs() =
        liveData { emit(apiRepo.getContactUs().value) }
    fun getPolicies() =
        liveData { emit(apiRepo.getPolicies().value) }

    fun getFaqs(role: String) =
        liveData { emit(apiRepo.getFaqs(role).value) }

    fun getPanditRevenue(vendorId: String) =
        liveData { emit(apiRepo.getPanditRevenue(vendorId ).value) }
}