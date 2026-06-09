package com.app.panditji.data.model.revenue_list

data class GetRevenueListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val dateRange: String,
        val pagination: Pagination,
        val period: String,
        val recentBookings: List<RecentBooking>,
        val summary: Summary,
        val vendor: Vendor
    ) {
        data class Pagination(
            val currentPage: Int,
            val hasNextPage: Boolean,
            val hasPrevPage: Boolean,
            val totalBookings: Int,
            val totalPages: Int
        )

        data class RecentBooking(
            val bookingID: String,
            val customerImage: String,
            val customerName: String,
            val date: String,
            val formattedDate: String,
            val id: String,
            val `package`: String,
            val poojaType: String,
            val revenue: Int,
            val status: String
        )

        data class Summary(
            val averageRevenuePerBooking: Int,
            val totalBookings: Int,
            val totalRevenue: Int
        )

        data class Vendor(
            val email: String,
            val id: String,
            val name: String
        )
    }
}