import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LoginResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val dev_message:String?,
    val type:String,
    val payload: Payload? = null,
):Parcelable


@Keep
@Parcelize
data class States(
    val id: Int?,
    val state_name: String?
):Parcelable


@Keep
@Parcelize
data class Payload(
    val auth_token: String?=null,
    val created_at: String?=null,
    val device_token: String?=null,
    val device_type: String?=null,
    val email: String?=null,
    val id: Int?=null,
    val name: String?=null,
    val phone: String?=null,
    val profile_img: String?=null,
    val status: Int?=null,
    val user_id: Int?=null,
    val gender: String?=null,

    val apple_id: String?=null,
    val birthday: String?=null,
    val city: String?=null,
    val country_id: String?=null,
    val facebook_id: String?=null,
    val first_name: String?=null,
    val google_id: String?=null,
    val is_phone_verify: Int?=null,
    val last_name: String?=null,
    val nationality_id: String?=null,
    val notification_received: String?=null,
    val social_type: String?=null,


    val statesList:List<StatesList>?=null,
    val citiesList:List<CitiesList>?=null,
    val amenitiesList: List<Amenities>?=null,
    val services: List<ServiceList>? = null,
    val schedule: List<ScheduleItem>?=null,

    val venue: Venue?=null,
    val venues: Venues?=null,
    val faqs: List<Faq>?=null,

    // About Us
    val app_url_android: String?=null,
    val app_url_ios: String?=null,
    val app_version: String?=null,
    val app_version_change_message: String?=null,
    val description: String?=null,
    val logo: String?=null,
    val main_title: String?=null,
    val walkthrough: List<Walkthrough>?=null,

    // Available Slot
    val date: String?=null,
    val day: String?=null,
    val slots: List<Slot>?=null,
    val summary: Summary?=null,

    // Booking List
    val bookings: List<Bookings>?=null,
    val pagination: Pagination?=null,

    //createBooking, bookingDetails
    val booking: Booking?=null,

    //Social Contact
    val social_contacts: SocialContacts?=null,

    //Customer Support
    val address: String?=null,
    val monTofri: String?=null,
    val satTosun: String?=null,
    val title: String?=null,

    //Split Payment
    val booking_summary: BookingSummary?=null,
//    val contributors_list: List<Any>?=null


):Parcelable



// split Payment Response
@Keep
@Parcelize
data class BookingSummary(
    val booking_date: String,
    val booking_id: Int,
    val booking_period: String,
    val booking_reference: String,
    val booking_status: String,
    val court_name: String,
    val end_time: String,
    val final_amount: String,
    val location: String,
    val paid_services_json: List<PaidServicesJson>,
    val payment_method: String,
    val payment_status: String,
    val remaining_amount: Int,
    val sport_name: String,
    val start_time: String,
    val total_paid: Int,
    val vendor_image: String,
    val venue_location: String,
    val vendor_name: String

): Parcelable

@Keep
@Parcelize
data class SocialContacts(
    val facebook_url: String,
    val instagram_url: String,
    val twitter_url: String,
    val website_url: String,
    val whatsapp_url: String
): Parcelable


@Keep
@Parcelize
data class ScheduleItem(
    val date: String,
    val day: String,
    val slots: List<SlotItem>
):Parcelable

@Keep
@Parcelize
data class ServiceList(
    val id: Int? = null,
    val vendor_id: Int? = null,
    val court_id: Int? = null,
    val sport_id: Int? = null,
    val service_name: String? = null,
    val description: String? = null,
    val price: String?=null,
    val status: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    var quantity: Int = 1,

   /* val description: String,
    val id: Int,
    val price: String,
    val service_name: String
*/
):Parcelable

@Keep
@Parcelize
data class Venues(
    val current_page: Int?,
    val `data`: List<VenueData>?,
    val first_page_url: String?,
    val from: Int?,
    val last_page: Int?,
    val last_page_url: String?,
    val links: List<Link>?,
    val next_page_url: String?,
    val path: String?,
    val per_page: Int?,
    val prev_page_url: String?,
    val to: Int?,
    val total: Int?
):Parcelable




@Keep
@Parcelize
data class Faq(
    val answer: String,
    val question: String
): Parcelable

@Keep
@Parcelize
data class Walkthrough(
    val id: Int,
    val image_url: String,
    val text: String,
    val title: String
): Parcelable


@Keep
@Parcelize
data class Slot(
    val booking_reference: String?=null,
    val booking_type: String?=null,
    val end_time: String,
    val price: String,
    val slot_id: Int,
    val start_time: String,
    val status: String,
    val type: String
): Parcelable



@Keep
@Parcelize
data class Bookings(
    val booking_date: String,
    val booking_period: String,
    val booking_reference: String,
    val booking_status: String,
    val booking_type: String,
    val court_name: String,
    val end_time: String,
    val final_amount: String,
    val id: Int,
    val slot_price: String,
    val sport_icon: String,
    val sport_name: String,
    val start_time: String,
    val tax_amount: String,
    val total_amount: String,
    val vendor_image: String,
    val vendor_name: String,
    val venue_id: Int,
    val venue_location: String,
    val paid_services_json: List<PaidServicesJson>?=null,

): Parcelable

@Keep
@Parcelize
data class PaidServicesJson(
        val paid_service_id: Int,
        val paid_service_name: String,
        val price: Int,
        val quantity: Int,

): Parcelable


@Keep
@Parcelize
data class Pagination(
    val current_page: String,
    val last_page: Int,
    val per_page: String,
    val total: Int
): Parcelable


// create Booking Response

@Keep
@Parcelize
data class Booking(
    val booking_date: String,
    val booking_notes: String,
    val booking_reference: String,
    val booking_status: String,
    val booking_type: String,
    val cancellation_reason: String?=null,
    val cancelled_at: String?=null,
    val cancelled_by: String?=null,
    val checked_in_at: String?=null,
    val checked_out_at: String?=null,
    val court_id: Int,
    val court_name: String,
    val created_at: String,
    val discount_amount: String,
    val end_time: String,
    val final_amount: String,
    val id: Int,
    val is_recurring: Int,
    val is_vendor_approve: Int,
    val is_vendor_reject: String?=null,
    val parent_booking_id: String?=null,
    val participants: String?=null,
    val payment_method: String?=null,
    val payment_status: String,
    val recurring_pattern: String?=null,
    val recurring_until: String?=null,
    val slot_price: String,
    val sport_id: String?=null,
    val sport_name: String,
    val start_time: String,
    val tax_amount: String,
    val total_amount: String,
    val transaction_id: String?=null,
    val updated_at: String,
    val user_id: Int,
    val vendor_id: Int,


    // Booking Details
    val booking_period: String,
    val has_review: Int,
    val paid_services_json: List<PaidServicesJson>,
//    val paid_services_json: String,
    val vendor_name: String,
    val vendor_phone: String,
    val venue_location: String,
    val venue_name: String



): Parcelable



@Keep
@Parcelize
data class Summary(
    val cancelled: Int,
    val completed: Int,
    val confirmed: Int,
    val hold: Int,
    val pending: Int,
    val total_slots: Int
): Parcelable

@Keep
@Parcelize
data class VenueData(
    val amenities: String?,
    val city: String?,
    val created_at: String?,
    val description: String?,
    val id: Int?,
    val image_urls: String?,
    val latitude: String?,
    val location: String?,
    val longitude: String?,
    val name: String?,
    val pin_code: String?,
    val state_id: String?,
    val updated_at: String?,

    val available_slots: Int,
    val avg_rating: String,
    val booking_count: Int,
    val distance: Double,
    val vendor_id: Int,
    val venue_id: Int,
    val venue_name: String

):Parcelable

@Keep
@Parcelize
data class Link(
    val active: Boolean?,
    val label: String?,
    val url: String?
):Parcelable


@Keep
@Parcelize
data class VenueResponse(
    val data: List<Venue>?=null,
    val current_page: Int?=null,
    val total: Int?=null
):Parcelable


@Keep
@Parcelize
data class Amenities(
    val icon: String?=null,
    val id: Int?=null,
    val name: String?=null,
    var isSelected: Boolean = false
):Parcelable

@Keep
@Parcelize
data class S3Details(
    val ACCESS_KEY: String?,
    val SECRET_KEY: String?,
    val BUCKET_NAME: String?,
    val REGION: String?
):Parcelable

@Keep
@Parcelize
data class IntroSlideData(
    val title: String,
    val description: String,
    val imageResId: Int,
    var isSelected:Boolean = false
):Parcelable

@Keep
@Parcelize
data class SlotItem(
    var start_time: String = "",
    var end_time: String = "",
    var price: String = ""
):Parcelable

data class PaidService(
    var serviceName: String,
    var description: String,
    var price: String
)


data class SendOtpModel(
    val mobile: String?=null,
)

// RatingBar Request Model
data class RatingBarRequest(
    val booking_id: Int,
    val cleanliness_rating: Int,
    val rating: Int,
    val review_text: String
)

// Booking details req
data class BookingDetailsRequest(
    val booking_id: Int?=null,
)

//Create Booking RequestModel
data class UserBookingRequest(
    val vendor_id: Int? = null,
    val court_id: Int? = null,
    val booking_date: String,       // Format: "YYYY-MM-DD"
    val start_time: String?,         // Format: "HH:mm:ss"
    val end_time: String?,           // Format: "HH:mm:ss"
    val booking_notes: String?,
    val final_amount1: Float,
    val final_amount: Double,
    val paid_services_json: List<PaidServices>? = null,
)

data class PaidServices(
    val paid_service_id: Int?,
    val paid_service_name: String,
    val price: Float?,
    val quantity: Int
)




data class RegistrationModel(
    val firstName: String?=null,
    val lastName: String?=null,
    val email: String?=null,
    val mobile: String?=null,
    val otp: String?=null,
    val password:String?=null,
    val acceptsMarketing: Boolean?=null,
    val gender: String?=null,
    val role: String? = null,
    val commission: Int? = null,
    val about: String? = null,
    val gotra: String? = null,
    val deviceType: String? = null,
    val deviceToken: String? = null,
    var aadhar: String? = null,
    val language: List<String>? = null,

    )

@Keep
@Parcelize
data class StatesList(
    val id:Int?=null,
    val state_name: String?=null
):Parcelable


@Keep
@Parcelize
data class CitiesList(
    val city_id:Int?=null,
    val name: String?=null
):Parcelable

data class GetStateModel(
    val country_id:String?=null
)

data class GetCityModel(
    val country_id:String?=null,
    val state_id:Int?=null
)




data class AddVenueModel(
    val name:String?=null,
    val description: String?=null,
    val location:String?=null,
    val city_id:String?=null,
    val state_id:String?=null,
    val pin_code:String?=null,
    val amenities:List<Int>?=null,
    val status:String?=null,
    val latitude:String?=null,
    val longititude:String?=null
)

@Keep
@Parcelize
data class Venue(
    val amenities: List<Amenities>?=null,
    val city: String?=null,
    val city_name: String?=null,
    val country_id: Int?=null,
    val created_at: String?=null,
    val description: String?=null,
    val id: Int?=null,
    val image_urls: String?=null,
    val is_blocked: Int?=null,
    val latitude: String?=null,
    val location: String?=null,
    val longitude: String?=null,
    val name: String?=null,
    val pin_code: String?=null,
    val price_per_hour: String?=null,
    val state_id: String?=null,
    val state_name: String?=null,
    val status: Int?=null,
    val terms: String?=null,
    val updated_at: String?=null,
    val vendor_id: Int?=null,
    val vendor_name: String?=null,
    val courts:List<CourtList>?=null
):Parcelable

@Keep
@Parcelize
data class CourtList(
    val id:Int?=null,
    val court_name: String?=null,
    val icon: String?=null,
    val sport_name: String?=null
):Parcelable



data class AddCourt(
    val court_name:String?=null,
    val sport_id:Int?=null,
    val venue_id:Int?=null
)


data class AddWeeklySlotModel(
    val court_id: Int?,
    val sport_id: Int?,
    val valid_from: String?,
    val valid_to: String?,
    val week_slots: WeekSlots?
)

data class WeekSlots(
    val Friday: List<SlotItem>?,
    val Monday: List<SlotItem>?,
    val Saturday: List<SlotItem>?,
    val Sunday: List<SlotItem>?,
    val Thursday: List<SlotItem>?,
    val Tuesday: List<SlotItem>?,
    val Wednesday: List<SlotItem>?
)

data class AddPaidServices(
    val vendor_id:Int?=null,
    val court_id:Int?=null,
    val service_name:String?=null,
    val description: String?=null,
    val price: String
)

data class UpdatePaidService(
    val id:Int?=null,
    val service_name:String?=null,
    val description: String?=null,
    val price: String
)


@Keep
@Parcelize
data class UpdateProfile(
    val firstName: String?=null,
    val lastName: String?=null,
    val gender: String?=null,
    val email: String?=null,
    val phone: String?=null,
    var image: String?=null,
    val about: String?=null,
    val gotra: String?=null,
    val pravar: String?=null,
    val dateOfBirth: String?=null,
    val veda: String?=null,
    val pankti: String?=null,
    val shakha: String?=null,
    val sutra: String?=null,
    val address: String?=null,
    val state: String?=null,
    val city: String?=null,
    val zip: String?=null,
    val country: String?=null,
    var aadhar: String?=null,
    var accessToken: String?=null,
    val language: List<String>?=null,
    val services: List<String>?=null
):Parcelable


data class AddSlotRequest(
    val vendor_id: Int?=null,
    val court_id: Int?=null,
    val sport_id: Int?=null,
    val date_slots: Map<String, List<SlotItem>>?=null
)

// login Request

data class LoginRequest(
    val email: String?=null,
    val password: String?=null,
    val deviceType: String? = null,
    val deviceToken: String? = null,
)
data class LoginWithMobileRequest(
    val mobile: String?=null,
)






