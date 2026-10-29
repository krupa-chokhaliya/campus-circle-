package com.example.data.model

enum class CommunityCategory(val displayName: String) {
    GENERAL("General"),
    COMMUTE("Commute & Travel"),
    ACADEMICS("Academics & Study"),
    HOUSING("PGs & Living"),
    CAMPUS_LIFE("Campus Life"),
    CAREER("Placements & Internships"),
    CONFESSIONS("Anonymous")
}

data class Community(
    val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val category: CommunityCategory,
    val memberCount: Int,
    val iconEmoji: String,
    val rules: List<String>,
    val moderators: List<String>,
    val isOfficial: Boolean = false,
    val isJoined: Boolean = false,
    val accentColorHex: String = "#F72585"
)
