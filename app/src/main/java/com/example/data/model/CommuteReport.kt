package com.example.data.model

enum class CrowdLevel(val label: String) {
    SEATS_AVAILABLE("Seats Available"),
    MODERATE("Moderate Standing"),
    HEAVY("Heavy Rush"),
    PACKED("Severely Packed")
}

enum class ReportStatus(val label: String) {
    CONFIRMED("Confirmed by Commuters"),
    ESTIMATED("Estimated / Expected"),
    UNVERIFIED("Unverified Report")
}

data class CommuteReport(
    val id: String,
    val routeTitle: String, // e.g. "Vadodara to Bharuch JN"
    val origin: String,
    val destination: String,
    val transportName: String, // e.g. "Gujarat Queen Express", "MEMU 09108", "Saurashtra Janata"
    val scheduledTime: String, // "07:45 AM"
    val delayMinutes: Int = 0,
    val crowdLevel: CrowdLevel = CrowdLevel.MODERATE,
    val travelExperience: String,
    val status: ReportStatus = ReportStatus.CONFIRMED,
    val reportedBy: String = "GEC Commuter",
    val reportedAt: Long = System.currentTimeMillis()
)
