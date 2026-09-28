package com.example.data.services

import com.example.R
import com.example.data.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Clean API Service & Repository Layer prepared for Django REST Framework + JWT
 * Base Endpoint Contract: /api/v1/
 */
object GymOwnerRepository {

    // Mock Gym Owner profile
    private var currentOwner = GymOwnerUser(
        id = "own_01",
        name = "Kishore Kumar",
        email = "kishore@ironhousefitness.com",
        phone = "+91 98470 12345",
        role = "Gym Owner",
        profileImage = null
    )

    // Mock Gym details (Iron House Fitness, Kozhikode, Kerala)
    private var currentGym = GymProfile(
        id = "gym_iron_house",
        name = "Iron House Fitness",
        description = "Premium high-performance training facility with modern strength and cardio zones.",
        phone = "+91 495 276 5432",
        email = "contact@ironhousefitness.com",
        website = "www.ironhousefitness.in",
        address = "4th Floor, Emerald Plaza, Mavoor Road",
        city = "Kozhikode",
        district = "Kozhikode",
        state = "Kerala",
        pincode = "673004",
        openingTime = "05:30 AM",
        closingTime = "10:30 PM",
        latitude = 11.2588,
        longitude = 75.7804,
        logoRes = R.drawable.byce_logo
    )

    private val samplePlans = mutableListOf(
        MembershipPlanItem(
            id = "plan_1",
            name = "Monthly",
            description = "Standard 1-month full gym floor access with locker facility",
            duration = 1,
            durationUnit = "Month",
            price = 1499.0,
            discountPrice = null,
            features = listOf("Full gym floor access", "Locker access", "App check-in pass", "Shower facility"),
            isActive = true
        ),
        MembershipPlanItem(
            id = "plan_2",
            name = "Quarterly",
            description = "3-month progressive training pass with complimentary fitness assessment",
            duration = 3,
            durationUnit = "Months",
            price = 3999.0,
            discountPrice = 3799.0,
            features = listOf("All Monthly features", "1 Fitness assessment", "Diet guidance sheet", "Guest pass (1x)"),
            isActive = true
        ),
        MembershipPlanItem(
            id = "plan_3",
            name = "Half Yearly",
            description = "6-month dedicated fitness membership with free steam and recovery sessions",
            duration = 6,
            durationUnit = "Months",
            price = 6999.0,
            discountPrice = 6499.0,
            features = listOf("All Quarterly features", "Steam bath access", "2 Body composition tests", "2 Guest passes"),
            isActive = true
        ),
        MembershipPlanItem(
            id = "plan_4",
            name = "Yearly",
            description = "Ultimate 12-month transformation membership with maximum savings",
            duration = 12,
            durationUnit = "Months",
            price = 11999.0,
            discountPrice = 10999.0,
            features = listOf("All Half Yearly features", "Priority slot booking", "Free BYCE gym kit", "Unlimited guest passes (1/mo)"),
            isActive = true
        )
    )

    private val sampleCustomers = mutableListOf(
        CustomerItem("c1", "Rahul Menon", "rahul.menon@gmail.com", "+91 98471 22334", "Yearly", "10 Jan 2026", "09 Jan 2027", "Active", "10 Jan 2025", "RM"),
        CustomerItem("c2", "Ananya Nair", "ananya.nair@outlook.com", "+91 97455 33445", "Monthly", "01 Sep 2026", "30 Sep 2026", "Active", "15 Jun 2026", "AN"),
        CustomerItem("c3", "Mohammed Faizal", "faizal.m@gmail.com", "+91 99951 44556", "Half Yearly", "15 Jul 2026", "14 Jan 2027", "Active", "15 Jul 2026", "MF"),
        CustomerItem("c4", "Deepak Varma", "deepak.v@yahoo.com", "+91 94471 55667", "Quarterly", "12 Aug 2026", "11 Nov 2026", "Active", "12 Feb 2026", "DV"),
        CustomerItem("c5", "Sneha Joseph", "sneha.j@gmail.com", "+91 96331 66778", "Monthly", "18 Aug 2026", "17 Sep 2026", "Expired", "18 May 2026", "SJ"),
        CustomerItem("c6", "Arjun Prasad", "arjun.p@gmail.com", "+91 98951 77889", "Yearly", "05 Sep 2026", "04 Sep 2027", "Pending", "05 Sep 2026", "AP"),
        CustomerItem("c7", "Pooja Krishnan", "pooja.k@gmail.com", "+91 97461 88990", "Quarterly", "20 Jun 2026", "19 Sep 2026", "Expired", "20 Dec 2025", "PK"),
        CustomerItem("c8", "Vivek Raj", "vivek.raj@gmail.com", "+91 95671 99001", "Half Yearly", "01 Aug 2026", "31 Jan 2027", "Active", "01 Aug 2026", "VR")
    )

    private val sampleMemberships = mutableListOf(
        MembershipItem("m1", "Rahul Menon", "rahul.menon@gmail.com", "+91 98471 22334", "Yearly", "Iron House Fitness", "10 Jan 2026", "09 Jan 2027", 11999.0, "Active"),
        MembershipItem("m2", "Ananya Nair", "ananya.nair@outlook.com", "+91 97455 33445", "Monthly", "Iron House Fitness", "01 Sep 2026", "30 Sep 2026", 1499.0, "Active"),
        MembershipItem("m3", "Mohammed Faizal", "faizal.m@gmail.com", "+91 99951 44556", "Half Yearly", "Iron House Fitness", "15 Jul 2026", "14 Jan 2027", 6999.0, "Active"),
        MembershipItem("m4", "Deepak Varma", "deepak.v@yahoo.com", "+91 94471 55667", "Quarterly", "Iron House Fitness", "12 Aug 2026", "11 Nov 2026", 3999.0, "Active"),
        MembershipItem("m5", "Sneha Joseph", "sneha.j@gmail.com", "+91 96331 66778", "Monthly", "Iron House Fitness", "18 Aug 2026", "17 Sep 2026", 1499.0, "Expired"),
        MembershipItem("m6", "Arjun Prasad", "arjun.p@gmail.com", "+91 98951 77889", "Yearly", "Iron House Fitness", "05 Sep 2026", "04 Sep 2027", 11999.0, "Pending"),
        MembershipItem("m7", "Rohan Pillai", "rohan.p@gmail.com", "+91 94470 11223", "Monthly", "Iron House Fitness", "10 Aug 2026", "09 Sep 2026", 1499.0, "Cancelled")
    )

    private val samplePayments = mutableListOf(
        PaymentItem("TXN-984210", "Rahul Menon", "Yearly", 11999.0, "Razorpay UPI", "Successful", "Today, 10:24 AM", orderId = "ORD_88421", paymentId = "PAY_77341", paidAt = "2026-09-24 10:24:12"),
        PaymentItem("TXN-984209", "Ananya Nair", "Monthly", 1499.0, "Google Pay", "Successful", "Today, 09:15 AM", orderId = "ORD_88420", paymentId = "PAY_77340", paidAt = "2026-09-24 09:15:45"),
        PaymentItem("TXN-984208", "Arjun Prasad", "Yearly", 11999.0, "Card / Razorpay", "Pending", "Yesterday, 07:40 PM", orderId = "ORD_88419", paymentId = "PAY_77339", paidAt = "Pending"),
        PaymentItem("TXN-984207", "Mohammed Faizal", "Half Yearly", 6999.0, "PayTM UPI", "Successful", "22 Sep 2026", orderId = "ORD_88418", paymentId = "PAY_77338", paidAt = "2026-09-22 18:22:04"),
        PaymentItem("TXN-984206", "Karthik Suresh", "Quarterly", 3999.0, "Net Banking", "Failed", "21 Sep 2026", orderId = "ORD_88417", paymentId = "PAY_77337", paidAt = "Failed - Bank Timeout"),
        PaymentItem("TXN-984205", "Vivek Raj", "Half Yearly", 6999.0, "PhonePe", "Successful", "20 Sep 2026", orderId = "ORD_88416", paymentId = "PAY_77336", paidAt = "2026-09-20 14:10:00")
    )

    private val sampleBookings = mutableListOf(
        BookingItem("b1", "Rahul Menon", "+91 98471 22334", "Yearly Plan", "Today", "06:00 PM - 07:30 PM", "Upcoming", arrivalStatus = "Confirmed"),
        BookingItem("b2", "Ananya Nair", "+91 97455 33445", "Monthly Plan", "Today", "07:00 PM - 08:30 PM", "Upcoming", arrivalStatus = "Confirmed"),
        BookingItem("b3", "Mohammed Faizal", "+91 99951 44556", "Half Yearly", "Today", "08:00 AM - 09:30 AM", "Completed", arrivalStatus = "Checked In"),
        BookingItem("b4", "Deepak Varma", "+91 94471 55667", "Quarterly Plan", "Today", "09:30 AM - 11:00 AM", "Completed", arrivalStatus = "Checked In"),
        BookingItem("b5", "Sneha Joseph", "+91 96331 66778", "Monthly Plan", "Tomorrow", "06:30 AM - 08:00 AM", "Upcoming", arrivalStatus = "Confirmed"),
        BookingItem("b6", "Gautham Das", "+91 98950 11447", "Day Pass", "Today", "04:00 PM - 05:30 PM", "Cancelled", arrivalStatus = "Not Arrived")
    )

    private val sampleStaff = mutableListOf(
        StaffMember("st1", "Sanjay Nambiar", "sanjay@ironhousefitness.com", "+91 98472 00112", "Gym Admin", "Active", "12 Jan 2025"),
        StaffMember("st2", "Kavya Sreedharan", "kavya@ironhousefitness.com", "+91 97451 11223", "Staff", "Active", "15 Mar 2025"),
        StaffMember("st3", "Manoj K.V.", "manoj@ironhousefitness.com", "+91 99953 22334", "Staff", "Active", "01 Jun 2025"),
        StaffMember("st4", "Akhil George", "akhil@ironhousefitness.com", "+91 94474 33445", "Staff", "Inactive", "10 Sep 2025")
    )

    private val sampleNotifications = mutableListOf(
        NotificationItem("n1", "Payment Received", "Rahul Menon renewed Yearly Membership for ₹11,999 via Razorpay UPI.", "Payment", "10 mins ago", false),
        NotificationItem("n2", "New Check-in Booking", "Ananya Nair booked a workout slot for 07:00 PM today.", "Booking", "35 mins ago", false),
        NotificationItem("n3", "Membership Expiring Soon", "Sneha Joseph's Monthly plan expires in 2 days.", "Membership", "2 hours ago", false),
        NotificationItem("n4", "System Maintenance", "Scheduled BYCE cloud synchronization at 02:00 AM IST.", "System", "1 day ago", true),
        NotificationItem("n5", "Payment Failed", "Transaction TXN-984206 from Karthik Suresh timed out.", "Payment", "3 days ago", true)
    )

    // GET /api/v1/admin/dashboard/
    fun getDashboardKpis(): DashboardKpis = DashboardKpis(
        totalCustomers = 1248,
        totalCustomersGrowth = 12.0,
        activeMemberships = 986,
        activeMembershipsGrowth = 8.0,
        monthlyRevenue = 482500.0,
        monthlyRevenueGrowth = 15.4,
        upcomingBookings = 42
    )

    fun getRevenueChartData(filter: String = "30 Days"): List<RevenueDataPoint> {
        return when (filter) {
            "7 Days" -> listOf(
                RevenueDataPoint("Mon", 14200.0),
                RevenueDataPoint("Tue", 18500.0),
                RevenueDataPoint("Wed", 16800.0),
                RevenueDataPoint("Thu", 21400.0),
                RevenueDataPoint("Fri", 25900.0),
                RevenueDataPoint("Sat", 32100.0),
                RevenueDataPoint("Sun", 28400.0)
            )
            "3 Months" -> listOf(
                RevenueDataPoint("Jul", 420000.0),
                RevenueDataPoint("Aug", 456000.0),
                RevenueDataPoint("Sep", 482500.0)
            )
            "1 Year" -> listOf(
                RevenueDataPoint("Q1", 1180000.0),
                RevenueDataPoint("Q2", 1340000.0),
                RevenueDataPoint("Q3", 1420000.0),
                RevenueDataPoint("Q4", 1580000.0)
            )
            else -> listOf( // 30 Days
                RevenueDataPoint("Week 1", 108000.0),
                RevenueDataPoint("Week 2", 119500.0),
                RevenueDataPoint("Week 3", 124200.0),
                RevenueDataPoint("Week 4", 130800.0)
            )
        }
    }

    fun getMembershipOverview(): MembershipOverview = MembershipOverview(
        active = 986,
        pending = 45,
        expired = 172,
        cancelled = 45
    )

    // GET /api/v1/auth/me/
    fun getOwnerProfile(): GymOwnerUser = currentOwner

    fun updateOwnerProfile(name: String, email: String, phone: String) {
        currentOwner = currentOwner.copy(name = name, email = email, phone = phone)
    }

    // GET /api/v1/gyms/{id}/
    fun getGymProfile(): GymProfile = currentGym

    fun updateGymProfile(updated: GymProfile) {
        currentGym = updated
    }

    // GET /api/v1/admin/customers/
    fun getCustomers(): List<CustomerItem> = sampleCustomers

    fun addCustomer(customer: CustomerItem) {
        sampleCustomers.add(0, customer)
    }

    // GET /api/v1/admin/memberships/
    fun getMemberships(): List<MembershipItem> = sampleMemberships

    // GET /api/v1/gyms/{gym_id}/membership-plans/
    fun getMembershipPlans(): List<MembershipPlanItem> = samplePlans

    fun addPlan(plan: MembershipPlanItem) {
        samplePlans.add(plan)
    }

    fun togglePlanStatus(planId: String) {
        val index = samplePlans.indexOfFirst { it.id == planId }
        if (index != -1) {
            val p = samplePlans[index]
            samplePlans[index] = p.copy(isActive = !p.isActive)
        }
    }

    // GET /api/v1/admin/payments/
    fun getPayments(): List<PaymentItem> = samplePayments

    // GET /api/v1/admin/bookings/
    fun getBookings(): List<BookingItem> = sampleBookings

    fun cancelBooking(bookingId: String) {
        val index = sampleBookings.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            sampleBookings[index] = sampleBookings[index].copy(status = "Cancelled", arrivalStatus = "Not Arrived")
            logAdminAction("Kishore Kumar", "Cancelled booking $bookingId", "Bookings")
        }
    }

    fun markBookingArrived(bookingId: String, adminName: String = "Kishore Kumar") {
        val index = sampleBookings.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val booking = sampleBookings[index]
            sampleBookings[index] = booking.copy(arrivalStatus = "Checked In", status = "Completed")
            recordCheckIn(
                customerId = booking.id,
                name = booking.customerName,
                plan = booking.membershipPlan
            )
            logAdminAction(adminName, "Marked ${booking.customerName} as arrived / checked in for slot ${booking.time}", "Bookings")
        }
    }

    // Staff
    fun getStaff(): List<StaffMember> = sampleStaff

    fun addStaff(staff: StaffMember) {
        sampleStaff.add(0, staff)
    }

    fun toggleStaffStatus(staffId: String) {
        val index = sampleStaff.indexOfFirst { it.id == staffId }
        if (index != -1) {
            val s = sampleStaff[index]
            val newStatus = if (s.status == "Active") "Inactive" else "Active"
            sampleStaff[index] = s.copy(status = newStatus)
        }
    }

    // Notifications
    fun getNotifications(): List<NotificationItem> = sampleNotifications

    fun markNotificationAsRead(id: String) {
        val index = sampleNotifications.indexOfFirst { it.id == id }
        if (index != -1) {
            sampleNotifications[index] = sampleNotifications[index].copy(isRead = true)
        }
    }

    fun markAllNotificationsAsRead() {
        for (i in sampleNotifications.indices) {
            sampleNotifications[i] = sampleNotifications[i].copy(isRead = true)
        }
    }

    // ----------------------------------------------------
    // FEATURE 1: TODAY'S GYM OPERATIONS
    // ----------------------------------------------------
    fun getTodayOperations(): TodayOperationsSummary = TodayOperationsSummary(
        todayCheckins = 86,
        todayBookings = 42,
        activeMembersInside = 28,
        newMembershipsToday = 7,
        expiringSoonCount = 18,
        todayRevenue = 24500.0,
        pendingActionsCount = 5
    )

    // ----------------------------------------------------
    // FEATURE 2: ATTENDANCE / CHECK-IN RECORDS
    // ----------------------------------------------------
    private val sampleAttendance = mutableListOf(
        AttendanceItem("att_1", "c1", "Rahul Menon", "10:04 AM", "11:35 AM", "Yearly", "Completed", "Today", "06:00 PM Slot"),
        AttendanceItem("att_2", "c2", "Ananya Nair", "09:12 AM", null, "Monthly", "Checked In", "Today", "07:00 PM Slot"),
        AttendanceItem("att_3", "c3", "Mohammed Faizal", "08:15 AM", "09:45 AM", "Half Yearly", "Completed", "Today", "08:00 AM Slot"),
        AttendanceItem("att_4", "c4", "Deepak Varma", "09:32 AM", null, "Quarterly", "Checked In", "Today", "09:30 AM Slot"),
        AttendanceItem("att_5", "c8", "Vivek Raj", "07:05 AM", "08:30 AM", "Half Yearly", "Completed", "Today", null),
        AttendanceItem("att_6", "c6", "Arjun Prasad", "06:40 AM", "08:10 AM", "Yearly", "Completed", "Today", null),
        AttendanceItem("att_7", "c7", "Pooja Krishnan", "05:15 PM", "06:45 PM", "Quarterly", "Completed", "Yesterday", null),
        AttendanceItem("att_8", "c5", "Sneha Joseph", "06:20 PM", "07:35 PM", "Monthly", "Completed", "Yesterday", null),
        AttendanceItem("att_9", "c9", "Karthik Suresh", "10:15 AM", null, "Quarterly Pass", "Checked In", "Today", "10:00 AM Slot"),
        AttendanceItem("att_10", "c10", "Meera Nambiar", "10:28 AM", null, "Monthly Pass", "Checked In", "Today", null)
    )

    fun getAttendanceRecords(): List<AttendanceItem> = sampleAttendance

    fun getAttendanceSummary(): AttendanceSummary = AttendanceSummary(
        todayCheckins = 86,
        currentlyInside = 28,
        completedVisits = 58,
        avgDailyVisits = 112
    )

    fun recordCheckIn(customerId: String, name: String, plan: String = "Active Plan"): AttendanceItem {
        val newRecord = AttendanceItem(
            id = "att_${System.currentTimeMillis()}",
            customerId = customerId,
            customerName = name,
            checkInTime = "Just now",
            checkOutTime = null,
            membershipPlan = plan,
            status = "Checked In",
            date = "Today"
        )
        sampleAttendance.add(0, newRecord)
        logAdminAction("Kishore Kumar", "Recorded manual check-in for $name", "Attendance")
        return newRecord
    }

    fun recordCheckOut(attendanceId: String) {
        val index = sampleAttendance.indexOfFirst { it.id == attendanceId }
        if (index != -1) {
            val record = sampleAttendance[index]
            sampleAttendance[index] = record.copy(checkOutTime = "Just now", status = "Completed")
            logAdminAction("Kishore Kumar", "Recorded check-out for ${record.customerName}", "Attendance")
        }
    }

    // ----------------------------------------------------
    // FEATURE 3: GYM QR CHECK-IN MANAGEMENT
    // ----------------------------------------------------
    private var gymQrPayload = "BYCE-GYM-IRON-HOUSE-KOZHIKODE-673004-AUTHENTICATED"

    fun getGymQrPayload(): String = gymQrPayload

    fun regenerateGymQrPayload(): String {
        gymQrPayload = "BYCE-GYM-IRON-HOUSE-KOZHIKODE-${System.currentTimeMillis()}"
        logAdminAction("Kishore Kumar", "Regenerated Gym Check-in QR Payload", "Gym QR")
        return gymQrPayload
    }

    // ----------------------------------------------------
    // FEATURE 4: MEMBERSHIPS EXPIRING SOON
    // ----------------------------------------------------
    private val sampleExpiringMemberships = listOf(
        ExpiringMembershipItem("c5", "Sneha Joseph", "Monthly Plan", "17 Sep 2026", 2, "Expires in 2 days"),
        ExpiringMembershipItem("c2", "Ananya Nair", "Monthly Plan", "30 Sep 2026", 6, "Expires in 6 days"),
        ExpiringMembershipItem("c7", "Pooja Krishnan", "Quarterly Plan", "08 Oct 2026", 14, "Expires in 14 days"),
        ExpiringMembershipItem("c4", "Deepak Varma", "Quarterly Plan", "11 Nov 2026", 28, "Expires in 28 days")
    )

    fun getExpiringMemberships(filterDays: Int = 30): List<ExpiringMembershipItem> {
        return sampleExpiringMemberships.filter { it.daysRemaining <= filterDays }
    }

    // ----------------------------------------------------
    // FEATURE 5: ABSENT / INACTIVE CUSTOMERS
    // ----------------------------------------------------
    private val sampleInactiveCustomers = listOf(
        InactiveCustomerItem("c5", "Sneha Joseph", "17 Sep 2026", 7, "Monthly Plan", "17 Sep 2026", "Inactive"),
        InactiveCustomerItem("c7", "Pooja Krishnan", "10 Sep 2026", 14, "Quarterly Plan", "19 Sep 2026", "Expired"),
        InactiveCustomerItem("c9", "Rohan Pillai", "24 Aug 2026", 31, "Monthly Plan", "09 Sep 2026", "Cancelled"),
        InactiveCustomerItem("c10", "Fahad Fazil", "12 Aug 2026", 43, "Half Yearly", "15 Feb 2027", "At Risk")
    )

    fun getInactiveCustomers(filterDays: Int = 30): List<InactiveCustomerItem> {
        return sampleInactiveCustomers.filter { it.daysSinceVisit >= filterDays }
    }

    // ----------------------------------------------------
    // FEATURE 6: CUSTOMER CRM NOTES
    // ----------------------------------------------------
    private val sampleCustomerNotes = mutableMapOf(
        "c1" to mutableListOf(
            CustomerCrmNote("n1", "c1", "Customer requested membership renewal information.", "Kishore Kumar", "21 Sep 2026, 11:30 AM"),
            CustomerCrmNote("n2", "c1", "Prefers early morning slots (06:00 AM). Provided locker key #42.", "Sanjay Nambiar", "10 Jan 2026, 08:15 AM")
        ),
        "c2" to mutableListOf(
            CustomerCrmNote("n3", "c2", "Customer visited reception regarding payment settlement.", "Kavya Sreedharan", "Today, 09:30 AM")
        ),
        "c4" to mutableListOf(
            CustomerCrmNote("n4", "c4", "Requested receipt copy for company reimbursement.", "Sanjay Nambiar", "18 Aug 2026, 04:00 PM")
        )
    )

    fun getCustomerNotes(customerId: String): List<CustomerCrmNote> {
        return sampleCustomerNotes[customerId] ?: emptyList()
    }

    fun addCustomerNote(customerId: String, note: String, author: String = "Kishore Kumar") {
        val list = sampleCustomerNotes.getOrPut(customerId) { mutableListOf() }
        list.add(0, CustomerCrmNote(
            id = "note_${System.currentTimeMillis()}",
            customerId = customerId,
            note = note,
            createdBy = author,
            createdAt = "Today, just now"
        ))
        logAdminAction(author, "Added CRM note for customer ID $customerId", "CRM Notes")
    }

    fun deleteCustomerNote(customerId: String, noteId: String) {
        sampleCustomerNotes[customerId]?.removeAll { it.id == noteId }
        logAdminAction("Kishore Kumar", "Removed CRM note $noteId", "CRM Notes")
    }

    // ----------------------------------------------------
    // FEATURE 7: CUSTOMER ACTIVITY TIMELINE
    // ----------------------------------------------------
    private val sampleCustomerActivities = mapOf(
        "c1" to listOf(
            CustomerActivityEvent("act1", "c1", "Gym Check-in", "Checked in at reception scanner", "24 Sep", "10:04 AM", "CheckIn"),
            CustomerActivityEvent("act2", "c1", "Booking Confirmed", "Reserved workout slot 06:00 PM", "24 Sep", "08:30 AM", "Booking"),
            CustomerActivityEvent("act3", "c1", "Admin Note Added", "Customer requested membership renewal info", "21 Sep", "11:30 AM", "Note"),
            CustomerActivityEvent("act4", "c1", "Payment Successful", "Renewed Yearly Plan - ₹11,999", "18 Sep", "10:24 AM", "Payment"),
            CustomerActivityEvent("act5", "c1", "Membership Purchased", "Annual VIP Membership activated", "10 Jan", "09:00 AM", "Membership")
        ),
        "c2" to listOf(
            CustomerActivityEvent("act6", "c2", "Gym Check-in", "Checked in at front desk scanner", "24 Sep", "09:12 AM", "CheckIn"),
            CustomerActivityEvent("act7", "c2", "Payment Successful", "Monthly Plan Renewed - ₹1,499", "24 Sep", "09:15 AM", "Payment"),
            CustomerActivityEvent("act8", "c2", "Booking Created", "Workout slot booked 07:00 PM", "23 Sep", "07:30 PM", "Booking"),
            CustomerActivityEvent("act9", "c2", "Membership Activated", "Monthly Membership started", "01 Sep", "10:00 AM", "Membership")
        )
    )

    fun getCustomerActivityTimeline(customerId: String): List<CustomerActivityEvent> {
        return sampleCustomerActivities[customerId] ?: listOf(
            CustomerActivityEvent("def1", customerId, "Gym Check-in", "Attendance logged via BYCE check-in", "22 Sep", "11:00 AM", "CheckIn"),
            CustomerActivityEvent("def2", customerId, "Payment Received", "Membership transaction settled", "15 Sep", "02:15 PM", "Payment"),
            CustomerActivityEvent("def3", customerId, "Membership Joined", "Registered on BYCE platform", "01 Aug", "10:00 AM", "Membership")
        )
    }

    // ----------------------------------------------------
    // FEATURE 9: OUTSTANDING / PENDING PAYMENTS
    // ----------------------------------------------------
    private val samplePendingPayments = mutableListOf(
        PendingPaymentItem("p1", "Arjun Prasad", "Yearly Plan", 11999.0, "Due in 2 days", "Pending", "+91 98471 22345"),
        PendingPaymentItem("p2", "Sneha Joseph", "Monthly Plan Renewal", 1499.0, "Due Yesterday", "Overdue", "+91 94462 88123"),
        PendingPaymentItem("p3", "Karthik Suresh", "Quarterly Pass", 3999.0, "Due 21 Sep 2026", "Failed / Due", "+91 97455 19283"),
        PendingPaymentItem("p4", "Rohit Nambiar", "Gym Locker Fee", 500.0, "Due in 5 days", "Pending", "+91 96330 45678"),
        PendingPaymentItem("p5", "Meera Menon", "Monthly Pass", 1499.0, "Due in 3 days", "Pending", "+91 99951 84729")
    )

    fun getPendingPayments(): List<PendingPaymentItem> = samplePendingPayments

    fun recordPendingPaymentPaid(paymentId: String, recordedBy: String = "Kishore Kumar") {
        val index = samplePendingPayments.indexOfFirst { it.id == paymentId }
        if (index != -1) {
            val item = samplePendingPayments.removeAt(index)
            logAdminAction(recordedBy, "Recorded manual payment for ${item.customerName} (₹${item.amount.toInt()})", "Payments")
        }
    }

    fun sendPaymentReminder(paymentId: String, sender: String = "Kishore Kumar") {
        val item = samplePendingPayments.find { it.id == paymentId }
        if (item != null) {
            logAdminAction(sender, "Sent payment reminder to ${item.customerName} (${item.phoneNumber})", "Payments")
        }
    }

    // ----------------------------------------------------
    // FEATURE 11: GYM CAPACITY & LIVE OCCUPANCY
    // ----------------------------------------------------
    private var gymOccupancy = GymOccupancy(
        currentInside = 42,
        capacity = 100,
        percentage = 42,
        status = "Normal",
        peakPeriod = "06:00 PM - 08:30 PM",
        dailyVisits = 148
    )

    fun getGymOccupancy(): GymOccupancy = gymOccupancy

    // ----------------------------------------------------
    // FEATURE 12 & 15: GYM OPERATING STATUS & VISIBILITY
    // ----------------------------------------------------
    private var gymOperatingStatus = GymOperatingStatus(
        status = "OPEN",
        openingTime = "05:30 AM",
        closingTime = "10:30 PM",
        visibilityStatus = "ACTIVE"
    )

    fun getGymOperatingStatus(): GymOperatingStatus = gymOperatingStatus

    fun updateGymOperatingStatus(newStatus: String) {
        gymOperatingStatus = gymOperatingStatus.copy(status = newStatus)
        logAdminAction("Kishore Kumar", "Changed gym operating status to $newStatus", "Gym Operations")
    }

    // ----------------------------------------------------
    // FEATURE 13: GYM PROFILE COMPLETENESS
    // ----------------------------------------------------
    fun getGymProfileCompleteness(): GymProfileCompleteness = GymProfileCompleteness(
        percentage = 85,
        missingFields = listOf("Gym Cover Photo", "Alternate Support Phone", "Instagram URL")
    )

    // ----------------------------------------------------
    // FEATURE 16: OPERATIONAL ALERTS
    // ----------------------------------------------------
    fun getOperationalAlerts(): List<OperationalAlert> = listOf(
        OperationalAlert("al_1", "18 memberships expire within the next 7 days.", "Membership", "High"),
        OperationalAlert("al_2", "5 pending payments require follow-up settlement.", "Payment", "High"),
        OperationalAlert("al_3", "3 workout reservations cancelled today.", "Booking", "Medium"),
        OperationalAlert("al_4", "Gym profile is 85% complete. 2 fields require attention.", "Profile", "Info")
    )

    // ----------------------------------------------------
    // FEATURE 20: ADMIN AUDIT / ACTIVITY LOG
    // ----------------------------------------------------
    private val sampleActivityLogs = mutableListOf(
        AdminActivityLogItem("log_1", "Kishore Kumar", "Regenerated Gym Check-in QR Payload", "Gym QR", "Today, 10:14 AM"),
        AdminActivityLogItem("log_2", "Sanjay Nambiar", "Recorded manual check-in for Rahul Menon", "Attendance", "Today, 10:04 AM"),
        AdminActivityLogItem("log_3", "Kishore Kumar", "Updated Quarterly Membership Plan pricing to ₹3,999", "Plans", "Today, 09:30 AM"),
        AdminActivityLogItem("log_4", "Kavya Sreedharan", "Added CRM note for Ananya Nair", "Customers", "Today, 09:16 AM"),
        AdminActivityLogItem("log_5", "Kishore Kumar", "Verified bank settlement for TXN-984210", "Payments", "Yesterday, 06:45 PM"),
        AdminActivityLogItem("log_6", "Sanjay Nambiar", "Changed operating status to OPEN", "Gym Status", "Yesterday, 05:30 AM"),
        AdminActivityLogItem("log_7", "Kishore Kumar", "Exported monthly revenue report to CSV", "Reports", "22 Sep 2026, 04:20 PM")
    )

    fun getAdminActivityLogs(): List<AdminActivityLogItem> = sampleActivityLogs

    fun logAdminAction(adminName: String, action: String, module: String) {
        sampleActivityLogs.add(0, AdminActivityLogItem(
            id = "log_${System.currentTimeMillis()}",
            adminName = adminName,
            action = action,
            module = module,
            timestamp = "Today, just now"
        ))
    }
}
