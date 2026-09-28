package com.example.data.models

/**
 * Standard Django REST Framework Response Envelope
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: Map<String, List<String>>? = null
)

/**
 * UI State container for asynchronous backend calls
 */
sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Success<out T>(val data: T) : ResourceState<T>()
    data class Empty(val message: String = "No data available") : ResourceState<Nothing>()
    data class Error(val message: String) : ResourceState<Nothing>()
}

/**
 * Gym Owner User Profile
 */
data class GymOwnerUser(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "Owner",
    val profileImage: String? = null
)

/**
 * Gym Details & Location Model
 */
data class GymProfile(
    val id: String,
    val name: String,
    val description: String,
    val phone: String,
    val email: String,
    val website: String,
    val address: String,
    val city: String,
    val district: String,
    val state: String,
    val pincode: String,
    val openingTime: String,
    val closingTime: String,
    val latitude: Double,
    val longitude: Double,
    val logoRes: Int? = null
)

/**
 * Dashboard KPIs & Summary
 */
data class DashboardKpis(
    val totalCustomers: Int,
    val totalCustomersGrowth: Double,
    val activeMemberships: Int,
    val activeMembershipsGrowth: Double,
    val monthlyRevenue: Double,
    val monthlyRevenueGrowth: Double,
    val upcomingBookings: Int
)

data class RevenueDataPoint(
    val label: String,
    val amount: Double
)

data class MembershipOverview(
    val active: Int,
    val pending: Int,
    val expired: Int,
    val cancelled: Int
)

/**
 * Customer / Member Entity
 */
data class CustomerItem(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val membershipPlan: String,
    val startDate: String,
    val endDate: String,
    val status: String, // Active, Expired, Pending
    val joinedDate: String,
    val avatarInitials: String = ""
)

/**
 * Membership Entity
 */
data class MembershipItem(
    val id: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String = "",
    val planName: String,
    val gymName: String,
    val startDate: String,
    val endDate: String,
    val amount: Double,
    val status: String // Active, Pending, Expired, Cancelled
)

/**
 * Membership Plan Tier
 */
data class MembershipPlanItem(
    val id: String,
    val name: String,
    val description: String,
    val duration: Int,
    val durationUnit: String, // Months, Days, Year
    val price: Double,
    val discountPrice: Double? = null,
    val features: List<String>,
    val isActive: Boolean = true
)

/**
 * Payment Transaction Record
 */
data class PaymentItem(
    val transactionId: String,
    val customerName: String,
    val planName: String,
    val amount: Double,
    val provider: String, // Razorpay, Stripe, UPI, Cash
    val status: String, // Successful, Pending, Failed
    val date: String,
    val currency: String = "INR",
    val orderId: String = "",
    val paymentId: String = "",
    val paidAt: String = ""
)

/**
 * Booking Record
 */
data class BookingItem(
    val id: String,
    val customerName: String,
    val customerPhone: String = "",
    val membershipPlan: String,
    val date: String,
    val time: String,
    val status: String, // Upcoming, Today, Completed, Cancelled
    val arrivalStatus: String = "Checked In" // Feature 10: Checked In, Not Arrived, Pending
)

/**
 * Gym Staff Member
 */
data class StaffMember(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // Gym Admin, Staff
    val status: String, // Active, Inactive
    val createdDate: String
)

/**
 * Notification Item
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // Payment, Membership, Booking, System
    val timestamp: String,
    val isRead: Boolean
)

/**
 * Feature 1: Today's Gym Operations Summary
 */
data class TodayOperationsSummary(
    val todayCheckins: Int = 86,
    val todayBookings: Int = 42,
    val activeMembersInside: Int = 28,
    val newMembershipsToday: Int = 7,
    val expiringSoonCount: Int = 18,
    val todayRevenue: Double = 24500.0,
    val pendingActionsCount: Int = 5
)

/**
 * Feature 2: Attendance / Check-in Entity
 */
data class AttendanceItem(
    val id: String,
    val customerId: String,
    val customerName: String,
    val checkInTime: String,
    val checkOutTime: String? = null,
    val membershipPlan: String,
    val status: String = "Checked In", // Checked In, Completed
    val date: String = "Today",
    val bookedSlot: String? = null
)

data class AttendanceSummary(
    val todayCheckins: Int = 86,
    val currentlyInside: Int = 28,
    val completedVisits: Int = 58,
    val avgDailyVisits: Int = 112
)

/**
 * Feature 4: Membership Expiry Monitor
 */
data class ExpiringMembershipItem(
    val customerId: String,
    val customerName: String,
    val membershipPlan: String,
    val expiryDate: String,
    val daysRemaining: Int,
    val status: String = "Expiring Soon"
)

/**
 * Feature 5: Absent / Inactive Customer Entity
 */
data class InactiveCustomerItem(
    val customerId: String,
    val customerName: String,
    val lastCheckIn: String,
    val daysSinceVisit: Int,
    val membershipPlan: String,
    val membershipExpiry: String,
    val status: String = "Inactive"
)

/**
 * Feature 6: Customer CRM Notes
 */
data class CustomerCrmNote(
    val id: String,
    val customerId: String,
    val note: String,
    val createdBy: String,
    val createdAt: String
)

/**
 * Feature 7: Customer Activity Timeline Event
 */
data class CustomerActivityEvent(
    val id: String,
    val customerId: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val type: String // CheckIn, Membership, Payment, Booking, Note
)

/**
 * Feature 9: Outstanding / Pending Payment Item
 */
data class PendingPaymentItem(
    val id: String,
    val customerName: String,
    val membershipPlan: String,
    val amount: Double,
    val dueDate: String,
    val status: String = "Pending",
    val phoneNumber: String = "+91 98765 43210"
)

/**
 * Feature 11: Gym Capacity & Live Occupancy
 */
data class GymOccupancy(
    val currentInside: Int = 42,
    val capacity: Int = 100,
    val percentage: Int = 42,
    val status: String = "Normal", // Normal, High, Max Capacity
    val peakPeriod: String = "06:00 PM - 08:30 PM",
    val dailyVisits: Int = 148
)

/**
 * Feature 12 & 15: Gym Operating Status & Listing Visibility
 */
data class GymOperatingStatus(
    val status: String = "OPEN", // OPEN, CLOSED, TEMPORARILY CLOSED
    val openingTime: String = "05:30 AM",
    val closingTime: String = "10:30 PM",
    val visibilityStatus: String = "ACTIVE" // ACTIVE, INACTIVE, PENDING REVIEW, SUSPENDED
)

/**
 * Feature 13: Gym Profile Completeness
 */
data class GymProfileCompleteness(
    val percentage: Int = 85,
    val missingFields: List<String> = listOf("Cover image", "Website URL")
)

/**
 * Feature 16: Operational Alert
 */
data class OperationalAlert(
    val id: String,
    val message: String,
    val category: String, // Membership, Payment, Booking, Profile
    val urgency: String = "Medium" // High, Medium, Info
)

/**
 * Feature 20: Admin Audit / Activity Log Entity
 */
data class AdminActivityLogItem(
    val id: String,
    val adminName: String,
    val action: String,
    val module: String,
    val timestamp: String
)
