package com.example.data.model

enum class ListingCategory(val label: String, val icon: String) {
    FOOD("Food & Canteens", "🍔"),
    TIFFIN("Tiffin Services", "🍱"),
    PG_ROOM("PGs & Rooms", "🏠"),
    STUDY("Libraries & Study", "📚"),
    ESSENTIALS("Stationery & Essentials", "✏️")
}

data class LocalListing(
    val id: String,
    val name: String,
    val category: ListingCategory,
    val neighborhood: String, // e.g. "Near GEC Campus", "Zadeshwar Road", "Station Area", "Old NH8"
    val approximateLocation: String,
    val priceRange: String, // e.g. "₹50 - ₹120 / meal", "₹3,500 - ₹5,000 / month"
    val amenities: List<String>,
    val contactInfo: String,
    val rating: Float = 4.5f,
    val reviewCount: Int = 18,
    val isVerified: Boolean = true,
    val submittedBy: String = "Student Council",
    val description: String
)
