package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class FeedFilter(val label: String) {
    FOR_YOU("For You"),
    FOLLOWING("Joined"),
    LATEST("Latest"),
    POPULAR("Popular"),
    UNANSWERED("Questions")
}

class CampusViewModel : ViewModel() {

    val currentUser: StateFlow<User> = CampusRepository.currentUser
    val isDarkTheme: StateFlow<Boolean> = CampusRepository.isDarkTheme
    val communities: StateFlow<List<Community>> = CampusRepository.communities
    val allPosts: StateFlow<List<Post>> = CampusRepository.posts
    val commentsMap: StateFlow<Map<String, List<Comment>>> = CampusRepository.comments
    val commuteReports: StateFlow<List<CommuteReport>> = CampusRepository.commuteReports
    val localListings: StateFlow<List<LocalListing>> = CampusRepository.localListings
    val reports: StateFlow<List<ReportItem>> = CampusRepository.reports
    val pendingVerifications: StateFlow<List<User>> = CampusRepository.pendingVerifications
    val notifications: StateFlow<List<AppNotification>> = CampusRepository.notifications

    private val _feedFilter = MutableStateFlow(FeedFilter.FOR_YOU)
    val feedFilter: StateFlow<FeedFilter> = _feedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCommuteOrigin = MutableStateFlow("All")
    val selectedCommuteOrigin: StateFlow<String> = _selectedCommuteOrigin.asStateFlow()

    private val _selectedListingCategory = MutableStateFlow<ListingCategory?>(null)
    val selectedListingCategory: StateFlow<ListingCategory?> = _selectedListingCategory.asStateFlow()

    // Filtered Feed
    val feedPosts: StateFlow<List<Post>> = combine(
        allPosts,
        currentUser,
        feedFilter,
        searchQuery
    ) { posts, user, filter, query ->
        var list = posts

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.content.lowercase().contains(q) ||
                it.communityName.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }

        when (filter) {
            FeedFilter.FOR_YOU -> list.sortedWith(compareByDescending<Post> { it.isPinned }.thenByDescending { it.netScore + it.commentCount })
            FeedFilter.FOLLOWING -> list.filter { user.joinedCommunities.contains(it.communityId) }.sortedByDescending { it.createdAt }
            FeedFilter.LATEST -> list.sortedByDescending { it.createdAt }
            FeedFilter.POPULAR -> list.sortedByDescending { it.netScore }
            FeedFilter.UNANSWERED -> list.filter { it.postType == PostType.QUESTION && it.acceptedCommentId == null }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Commute Hub Filtered
    val filteredCommuteReports: StateFlow<List<CommuteReport>> = combine(
        commuteReports,
        selectedCommuteOrigin
    ) { reports, origin ->
        if (origin == "All") reports else reports.filter { it.origin.equals(origin, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Local Guide Filtered
    val filteredListings: StateFlow<List<LocalListing>> = combine(
        localListings,
        selectedListingCategory
    ) { listings, category ->
        if (category == null) listings else listings.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setFeedFilter(filter: FeedFilter) {
        _feedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCommuteOrigin(origin: String) {
        _selectedCommuteOrigin.value = origin
    }

    fun setListingCategory(category: ListingCategory?) {
        _selectedListingCategory.value = category
    }

    fun votePost(postId: String, direction: Int) {
        CampusRepository.votePost(postId, direction)
    }

    fun voteComment(postId: String, commentId: String, direction: Int) {
        CampusRepository.voteComment(postId, commentId, direction)
    }

    fun toggleSavePost(postId: String) {
        CampusRepository.toggleSavePost(postId)
    }

    fun votePoll(postId: String, optionId: String) {
        CampusRepository.votePoll(postId, optionId)
    }

    fun toggleJoinCommunity(communityId: String) {
        CampusRepository.toggleJoinCommunity(communityId)
    }

    fun createPost(
        communityId: String,
        title: String,
        content: String,
        isAnonymous: Boolean,
        postType: PostType,
        tags: List<String>,
        pollOptions: List<String> = emptyList()
    ): Post {
        return CampusRepository.createPost(
            communityId = communityId,
            title = title,
            content = content,
            isAnonymous = isAnonymous,
            postType = postType,
            tags = tags,
            pollOptions = pollOptions
        )
    }

    fun addComment(postId: String, parentId: String?, content: String, isAnonymous: Boolean): Comment {
        return CampusRepository.addComment(postId, parentId, content, isAnonymous)
    }

    fun markAcceptedAnswer(postId: String, commentId: String) {
        CampusRepository.markAcceptedAnswer(postId, commentId)
    }

    fun addCommuteReport(
        origin: String,
        destination: String,
        transportName: String,
        scheduledTime: String,
        delayMinutes: Int,
        crowdLevel: CrowdLevel,
        travelExperience: String
    ) {
        CampusRepository.addCommuteReport(
            origin, destination, transportName, scheduledTime, delayMinutes, crowdLevel, travelExperience
        )
    }

    fun addLocalListing(
        name: String,
        category: ListingCategory,
        neighborhood: String,
        approximateLocation: String,
        priceRange: String,
        amenities: List<String>,
        contactInfo: String,
        description: String
    ) {
        CampusRepository.addLocalListing(
            name, category, neighborhood, approximateLocation, priceRange, amenities, contactInfo, description
        )
    }

    fun submitReport(
        targetType: ReportTargetType,
        targetId: String,
        summary: String,
        reason: ReportReason,
        details: String
    ) {
        CampusRepository.submitReport(targetType, targetId, summary, reason, details)
    }

    fun resolveReport(reportId: String, action: ModerationStatus) {
        CampusRepository.resolveReport(reportId, action)
    }

    fun reviewVerification(userId: String, approve: Boolean) {
        CampusRepository.reviewVerification(userId, approve)
    }

    fun updateUserProfile(
        displayName: String,
        department: String,
        semester: Int,
        commuteCity: String,
        bio: String
    ) {
        CampusRepository.updateUserProfile(displayName, department, semester, commuteCity, bio)
    }

    fun markNotificationsRead() {
        CampusRepository.markNotificationsRead()
    }

    fun toggleTheme() {
        CampusRepository.toggleTheme()
    }

    fun setTheme(isDark: Boolean) {
        CampusRepository.setTheme(isDark)
    }
}
