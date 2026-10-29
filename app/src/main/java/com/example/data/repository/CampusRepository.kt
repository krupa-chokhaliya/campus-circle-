package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

object CampusRepository {

    private val _currentUser = MutableStateFlow(
        User(
            id = "user_krups_gec",
            username = "krups_gec",
            displayName = "Krupali P.",
            email = "krups20202121@gmail.com",
            college = "Government Engineering College, Bharuch",
            department = "Computer Engineering",
            semester = 6,
            commuteCity = "Vadodara",
            verificationStatus = VerificationStatus.VERIFIED,
            role = UserRole.ADMIN,
            karma = 412,
            bio = "CE @ GEC Bharuch | Vadodara <-> Bharuch daily train commuter | CampusCircle lead",
            joinedCommunities = listOf("general", "commute", "food", "study", "career", "events")
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Communities
    private val initialCommunities = listOf(
        Community(
            id = "general",
            name = "General Campus",
            slug = "gec-general",
            description = "The central hub for all GEC Bharuch announcements, queries, campus vibes, and everyday discussions.",
            category = CommunityCategory.GENERAL,
            memberCount = 1420,
            iconEmoji = "🏛️",
            rules = listOf(
                "Respect fellow students, faculties, and staff.",
                "No promotional spam or unsolicited advertising.",
                "Keep discussions relevant to GEC Bharuch and college life."
            ),
            moderators = listOf("krups_gec", "rahul_patel", "faculty_coord"),
            isOfficial = true,
            isJoined = true,
            accentColorHex = "#F72585"
        ),
        Community(
            id = "commute",
            name = "Up-Down and Trains",
            slug = "up-down-trains",
            description = "Dedicated community for students commuting daily from Vadodara, Surat, Ankleshwar, and nearby stations. Train delays, crowd updates, and commuter advice.",
            category = CommunityCategory.COMMUTE,
            memberCount = 1180,
            iconEmoji = "🚆",
            rules = listOf(
                "Share verified train and bus information.",
                "Do not post unverified panic delays.",
                "Respect fellow commuters in discussion threads."
            ),
            moderators = listOf("krups_gec", "train_guru_surat"),
            isOfficial = true,
            isJoined = true,
            accentColorHex = "#4CC9F0"
        ),
        Community(
            id = "food",
            name = "Bharuch Food & Canteens",
            slug = "bharuch-food",
            description = "Affordable student eats, campus canteens, best tiffins, street food near Zadeshwar road and Old NH8.",
            category = CommunityCategory.CAMPUS_LIFE,
            memberCount = 780,
            iconEmoji = "🍲",
            rules = listOf(
                "Only genuine student reviews.",
                "No paid promotional shilling without clear disclosure."
            ),
            moderators = listOf("foodie_gec"),
            isOfficial = false,
            isJoined = true,
            accentColorHex = "#FF80B5"
        ),
        Community(
            id = "housing",
            name = "PGs and Rooms",
            slug = "pgs-rooms",
            description = "Find verified flats, flatmates, PGs, and rental rooms in Bharuch and Zadeshwar near college.",
            category = CommunityCategory.HOUSING,
            memberCount = 650,
            iconEmoji = "🏡",
            rules = listOf(
                "State exact price and location clearly.",
                "Do not post broker spam without student rates."
            ),
            moderators = listOf("hostel_sec"),
            isOfficial = false,
            isJoined = false,
            accentColorHex = "#9B5DE5"
        ),
        Community(
            id = "confessions",
            name = "Anonymous Confessions",
            slug = "confessions",
            description = "Safe, anonymous venting and campus thoughts for GEC Bharuch students. Real identity is kept strictly separate.",
            category = CommunityCategory.CONFESSIONS,
            memberCount = 1890,
            iconEmoji = "🎭",
            rules = listOf(
                "Zero tolerance for bullying, personal attacks, or doxxing.",
                "No naming students or faculties maliciously.",
                "Anonymous posts are moderated strictly."
            ),
            moderators = listOf("krups_gec", "safe_mod"),
            isOfficial = true,
            isJoined = false,
            accentColorHex = "#F72585"
        ),
        Community(
            id = "study",
            name = "Study Help & PYQs",
            slug = "study-help",
            description = "GTU past papers, assignment discussions, notes exchange across Computer, Chemical, Mechanical, Electrical, and EC branches.",
            category = CommunityCategory.ACADEMICS,
            memberCount = 1350,
            iconEmoji = "📚",
            rules = listOf(
                "Mention branch and semester tag in title.",
                "Mark helpful answers with accepted checkmark."
            ),
            moderators = listOf("krups_gec", "academics_lead"),
            isOfficial = true,
            isJoined = true,
            accentColorHex = "#06D6A0"
        ),
        Community(
            id = "career",
            name = "Placements & Internships",
            slug = "placements-internships",
            description = "On-campus recruitment updates, resume reviews, referral leads, and interview preparation for GEC Bharuch students.",
            category = CommunityCategory.CAREER,
            memberCount = 920,
            iconEmoji = "💼",
            rules = listOf(
                "No fake job offers or paid certificate schemes.",
                "Share honest interview experiences."
            ),
            moderators = listOf("tpo_rep", "krups_gec"),
            isOfficial = true,
            isJoined = true,
            accentColorHex = "#FFD166"
        ),
        Community(
            id = "events",
            name = "College Events and Memes",
            slug = "events-memes",
            description = "Tech fests, cultural nights, sports tournaments, and relatable GEC Bharuch engineering memes.",
            category = CommunityCategory.CAMPUS_LIFE,
            memberCount = 1260,
            iconEmoji = "🎉",
            rules = listOf(
                "Keep memes fun and non-offensive.",
                "Official event notices will be pinned."
            ),
            moderators = listOf("cultural_sec"),
            isOfficial = false,
            isJoined = true,
            accentColorHex = "#9B5DE5"
        )
    )

    private val _communities = MutableStateFlow(initialCommunities)
    val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    // Seeded realistic posts
    private val initialPosts = listOf(
        Post(
            id = "post_1",
            communityId = "commute",
            communityName = "Up-Down and Trains",
            title = "Vadodara -> Bharuch Morning Trains: Gujarat Queen delay status & Tips for 1st Year Up-Downers",
            content = "For all new juniors travelling from Vadodara Junction to Bharuch JN daily:\n\n1. Gujarat Queen (19034) departs Vadodara at 07:45 AM. It usually arrives on Platform 2 at Bharuch around 08:55 AM. Perfect for 10:15 AM college lectures.\n2. In case Gujarat Queen is delayed by 20+ mins, board the Surat MEMU (09108) from Platform 5 which has steady seats if you reach 10 mins early.\n3. Shared auto from Bharuch Railway Station to GEC campus costs ₹20-₹25 per seat.\n\nDrop any delay updates below for today!",
            authorId = "user_krups_gec",
            authorName = "krups_gec",
            isAnonymous = false,
            tags = listOf("Vadodara", "GujaratQueen", "TrainAdvice", "Up-Down"),
            upvotes = 48,
            downvotes = 1,
            userVote = 1,
            commentCount = 12,
            isSaved = true,
            isPinned = true,
            postType = PostType.COMMUTE_REPORT,
            createdAt = System.currentTimeMillis() - 1000L * 60 * 95
        ),
        Post(
            id = "post_2",
            communityId = "study",
            communityName = "Study Help & PYQs",
            title = "[GTU Sem 5] Best reference notes and last 3 years solved PYQs for Design and Analysis of Algorithms (DAA)?",
            content = "Mid-sem exams start in two weeks. Has anyone solved the dynamic programming and branch-and-bound questions from the Winter 2024 GTU paper? Looking for crisp step-by-step solutions or handwritten class notes.",
            authorId = "user_amit_ce",
            authorName = "amit_ce_23",
            isAnonymous = false,
            tags = listOf("Sem5", "Computer", "GTU", "DAA"),
            upvotes = 32,
            downvotes = 0,
            userVote = 0,
            commentCount = 7,
            isSaved = false,
            isPinned = false,
            postType = PostType.QUESTION,
            acceptedCommentId = "comment_daa_1",
            createdAt = System.currentTimeMillis() - 1000L * 60 * 240
        ),
        Post(
            id = "post_3",
            communityId = "food",
            communityName = "Bharuch Food & Canteens",
            title = "Where do you get the best and most hygienic student tiffin near college?",
            content = "Comparing Maa Annapurna Tiffin (near Zadeshwar Char Rasta) vs Royal Food Service. Looking for pure veg Gujarati & Kathiyawadi tiffin with roti and dal that doesn't feel overly oily every day. Monthly budget ₹2800 - ₹3400.",
            authorId = "user_priya_chem",
            authorName = "priya_chem",
            isAnonymous = false,
            tags = listOf("Tiffin", "Zadeshwar", "StudentFood", "Budget"),
            upvotes = 26,
            downvotes = 2,
            userVote = 0,
            commentCount = 9,
            isSaved = true,
            isPinned = false,
            postType = PostType.LOCAL_RECOMMENDATION,
            createdAt = System.currentTimeMillis() - 1000L * 60 * 360
        ),
        Post(
            id = "post_4",
            communityId = "confessions",
            communityName = "Anonymous Confessions",
            title = "The quiet corner on the 3rd floor library overlooking the Narmada river is pure therapeutic bliss",
            content = "Between continuous assignment deadlines, GTU viva stress, and running for the 5:15 PM train back to Surat, spending 45 minutes on that balcony bench during golden hour gave me so much peace today. Take care of your mental health guys, engineering gets heavy sometimes.",
            authorId = "anon_private_id_992",
            authorName = "Anonymous Student",
            isAnonymous = true,
            anonymousAlias = "Anonymous Student",
            tags = listOf("MentalHealth", "CampusLife", "Confession"),
            upvotes = 64,
            downvotes = 0,
            userVote = 1,
            commentCount = 14,
            isSaved = false,
            isPinned = false,
            postType = PostType.GENERAL,
            createdAt = System.currentTimeMillis() - 1000L * 60 * 500
        ),
        Post(
            id = "post_5",
            communityId = "commute",
            communityName = "Up-Down and Trains",
            title = "Poll: For Bharuch students travelling back to Vadodara / Surat, which evening train do you prefer?",
            content = "We are gathering student feedback to petition WR railway division for an extra halt or coach addition during peak 5 PM dismissal hours.",
            authorId = "user_krups_gec",
            authorName = "krups_gec",
            isAnonymous = false,
            tags = listOf("Poll", "CommuterPoll", "EveningTrain"),
            upvotes = 51,
            downvotes = 1,
            userVote = 0,
            commentCount = 18,
            isSaved = false,
            isPinned = false,
            postType = PostType.POLL,
            pollOptions = listOf(
                PollOption("opt_1", "19033 Gujarat Queen (5:10 PM)", 76),
                PollOption("opt_2", "Surat-Vadodara MEMU (4:45 PM)", 52),
                PollOption("opt_3", "Saurashtra Janata (6:15 PM)", 29),
                PollOption("opt_4", "GSRTC Express Bus (Station Depo)", 14)
            ),
            userSelectedPollOption = "opt_1",
            createdAt = System.currentTimeMillis() - 1000L * 60 * 620
        ),
        Post(
            id = "post_6",
            communityId = "career",
            communityName = "Placements & Internships",
            title = "Campus Placement Drive: L&T and Adani Enterprises visiting next month for 7th & 8th Sem",
            content = "Official notification from the T&P Cell: Eligibility criteria is 6.5+ CGPA without active backlogs. Branches eligible: Computer, Mechanical, Electrical, Chemical. Resume reviews will happen in the seminar hall on Saturday.",
            authorId = "user_tpo_rep",
            authorName = "tpo_student_cell",
            isAnonymous = false,
            tags = listOf("Placement", "Adani", "LnT", "Sem7"),
            upvotes = 89,
            downvotes = 0,
            userVote = 1,
            commentCount = 23,
            isSaved = true,
            isPinned = true,
            postType = PostType.ANNOUNCEMENT,
            createdAt = System.currentTimeMillis() - 1000L * 60 * 800
        )
    )

    private val _posts = MutableStateFlow(initialPosts)
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Comments mapping
    private val initialComments = mapOf(
        "post_1" to listOf(
            Comment(
                id = "comm_1_1",
                postId = "post_1",
                authorId = "user_rahul",
                authorName = "rahul_patel",
                content = "Can confirm! Today Gujarat Queen reached Bharuch at 08:52 AM, right on time. Auto stand at station had plenty of shared autos for GEC.",
                upvotes = 14,
                downvotes = 0,
                createdAt = System.currentTimeMillis() - 1000L * 60 * 80
            ),
            Comment(
                id = "comm_1_2",
                postId = "post_1",
                parentId = "comm_1_1",
                authorId = "user_krups_gec",
                authorName = "krups_gec",
                content = "Awesome update Rahul, thanks! Added this to the commute hub live status.",
                upvotes = 5,
                downvotes = 0,
                createdAt = System.currentTimeMillis() - 1000L * 60 * 70
            ),
            Comment(
                id = "comm_1_3",
                postId = "post_1",
                authorId = "anon_surat_9",
                authorName = "Anonymous Student",
                isAnonymous = true,
                content = "Does anyone travel from Surat side on the 08:15 AM intercity? How crowded is the general coach on Mondays?",
                upvotes = 7,
                downvotes = 0,
                createdAt = System.currentTimeMillis() - 1000L * 60 * 50
            )
        ),
        "post_2" to listOf(
            Comment(
                id = "comment_daa_1",
                postId = "post_2",
                authorId = "user_krups_gec",
                authorName = "krups_gec",
                content = "Here is the verified breakdown: Focus on Matrix Chain Multiplication, Dijkstra's algorithm, and 0/1 Knapsack Branch & Bound. They represent 28 marks consistently across the last 3 GTU winter papers. I've uploaded the PDF notes in our Study drive folder!",
                upvotes = 22,
                downvotes = 0,
                isAcceptedAnswer = true,
                createdAt = System.currentTimeMillis() - 1000L * 60 * 180
            ),
            Comment(
                id = "comm_2_2",
                postId = "post_2",
                parentId = "comment_daa_1",
                authorId = "user_amit_ce",
                authorName = "amit_ce_23",
                content = "Thank you so much! Marked as accepted answer, this saves so much time.",
                upvotes = 6,
                downvotes = 0,
                createdAt = System.currentTimeMillis() - 1000L * 60 * 160
            )
        )
    )

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(initialComments)
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    // Commute Reports
    private val initialCommuteReports = listOf(
        CommuteReport(
            id = "rep_1",
            routeTitle = "Vadodara JN to Bharuch JN",
            origin = "Vadodara",
            destination = "Bharuch",
            transportName = "Gujarat Queen Express (19034)",
            scheduledTime = "07:45 AM",
            delayMinutes = 4,
            crowdLevel = CrowdLevel.MODERATE,
            travelExperience = "Right on track. Moderate crowd in middle coaches, plenty of sitting space if boarded from Vadodara Platform 2.",
            status = ReportStatus.CONFIRMED,
            reportedBy = "krups_gec",
            reportedAt = System.currentTimeMillis() - 1000L * 60 * 45
        ),
        CommuteReport(
            id = "rep_2",
            routeTitle = "Surat to Bharuch JN",
            origin = "Surat",
            destination = "Bharuch",
            transportName = "Surat - Vadodara MEMU (09108)",
            scheduledTime = "08:10 AM",
            delayMinutes = 12,
            crowdLevel = CrowdLevel.HEAVY,
            travelExperience = "Heavy morning office rush from Kosamba & Ankleshwar. Recommended to board front 3 coaches.",
            status = ReportStatus.CONFIRMED,
            reportedBy = "surat_daily_updown",
            reportedAt = System.currentTimeMillis() - 1000L * 60 * 75
        ),
        CommuteReport(
            id = "rep_3",
            routeTitle = "Ankleshwar to GEC Bharuch Campus",
            origin = "Ankleshwar",
            destination = "Bharuch",
            transportName = "Golden Bridge Auto Shuttle",
            scheduledTime = "09:00 AM",
            delayMinutes = 0,
            crowdLevel = CrowdLevel.SEATS_AVAILABLE,
            travelExperience = "Smooth flow over the new Narmada bridge. ₹30 shared fare to college gate.",
            status = ReportStatus.CONFIRMED,
            reportedBy = "ankleshwar_student",
            reportedAt = System.currentTimeMillis() - 1000L * 60 * 110
        )
    )

    private val _commuteReports = MutableStateFlow(initialCommuteReports)
    val commuteReports: StateFlow<List<CommuteReport>> = _commuteReports.asStateFlow()

    // Bharuch Local Guide Listings
    private val initialListings = listOf(
        LocalListing(
            id = "loc_1",
            name = "Maa Annapurna Student Tiffin",
            category = ListingCategory.TIFFIN,
            neighborhood = "Near Zadeshwar Char Rasta",
            approximateLocation = "Opp. GEC Main Road, Bharuch",
            priceRange = "₹70 / meal (₹2,900 / month)",
            amenities = listOf("Unlimited Rotis", "Daily Dal-Bhaat", "Sunday Sweet", "Clean Kitchen"),
            contactInfo = "+91 98250 XXXXX (Ask for Kaka)",
            rating = 4.7f,
            reviewCount = 42,
            isVerified = true,
            submittedBy = "Computer Dept 3rd Year",
            description = "Top rated tiffin among GEC hostelers. Hot homemade food delivered directly to hostel gate or Zadeshwar PGs before 12:30 PM."
        ),
        LocalListing(
            id = "loc_2",
            name = "GEC Campus Canteen & Chai Corner",
            category = ListingCategory.FOOD,
            neighborhood = "Inside GEC Campus",
            approximateLocation = "Behind Mechanical Workshop",
            priceRange = "₹20 - ₹80",
            amenities = listOf("Poha & Chai", "Samosa", "Veg Sandwich", "Seating Area"),
            contactInfo = "Counter 1 inside campus",
            rating = 4.2f,
            reviewCount = 89,
            isVerified = true,
            submittedBy = "Student Council",
            description = "The classic college hangout. Famous for morning lemon tea and fresh batata pauva during recess."
        ),
        LocalListing(
            id = "loc_3",
            name = "Narmada Green Boys PG & Flats",
            category = ListingCategory.PG_ROOM,
            neighborhood = "Zadeshwar Road",
            approximateLocation = "1.5 km from GEC Bharuch",
            priceRange = "₹3,500 - ₹4,500 / month",
            amenities = listOf("High-Speed WiFi", "Water Cooler", "Beds & Study Tables", "No Brokerage"),
            contactInfo = "+91 94280 XXXXX",
            rating = 4.5f,
            reviewCount = 28,
            isVerified = true,
            submittedBy = "Rahul Patel",
            description = "Spacious 3BHK converted PG exclusively for GEC Bharuch engineering students. 5 min bike ride to college."
        ),
        LocalListing(
            id = "loc_4",
            name = "Bharuch Town Study Hall & Library",
            category = ListingCategory.STUDY,
            neighborhood = "Station Road / Kasak",
            approximateLocation = "Near Railway Station, Bharuch",
            priceRange = "₹300 / month registration",
            amenities = listOf("Silent AC Hall", "Charging Points", "GTU Reference Section", "Open till 9 PM"),
            contactInfo = "Municipal Corporation Library Desk",
            rating = 4.6f,
            reviewCount = 35,
            isVerified = true,
            submittedBy = "Library Committee",
            description = "Great backup study environment especially during exam season for commuters waiting for evening trains."
        ),
        LocalListing(
            id = "loc_5",
            name = "Royal Engineering Stationery & Print Hub",
            category = ListingCategory.ESSENTIALS,
            neighborhood = "College Gate Market",
            approximateLocation = "Shop 4, Campus Arcade",
            priceRange = "₹1 per Xerox, ₹15 Binding",
            amenities = listOf("GTU Exam Submission Binding", "Engineering Drawing Sheets", "Stationery"),
            contactInfo = "Walk-in shop opposite Gate 1",
            rating = 4.8f,
            reviewCount = 64,
            isVerified = true,
            submittedBy = "Mechanical Dept",
            description = "Go-to spot for lab manual spiral binding, black book project prints, and A1 drafting sheets."
        )
    )

    private val _localListings = MutableStateFlow(initialListings)
    val localListings: StateFlow<List<LocalListing>> = _localListings.asStateFlow()

    // Safety Reports Queue for Admin/Moderator
    private val initialReports = listOf(
        ReportItem(
            id = "rep_flag_1",
            targetType = ReportTargetType.POST,
            targetId = "post_sample_flagged",
            targetSummary = "Unsolicited paid assignment completion service posted in Study Help",
            reason = ReportReason.SPAM,
            details = "User is advertising paid GTU assignment writing service violating academic integrity.",
            reportedBy = "student_vigil",
            reportedAt = System.currentTimeMillis() - 1000L * 60 * 180,
            status = ModerationStatus.PENDING
        )
    )

    private val _reports = MutableStateFlow(initialReports)
    val reports: StateFlow<List<ReportItem>> = _reports.asStateFlow()

    // Pending student verifications for Admin/Moderator
    private val initialPendingVerifications = listOf(
        User(
            id = "user_pending_1",
            username = "dhruv_me24",
            displayName = "Dhruv Shah",
            email = "dhruv.me24@gecbharuch.ac.in",
            college = "Government Engineering College, Bharuch",
            department = "Mechanical Engineering",
            semester = 4,
            commuteCity = "Surat",
            verificationStatus = VerificationStatus.PENDING,
            role = UserRole.STUDENT,
            karma = 0,
            bio = "Mechanical 4th Sem | Surat daily up-down",
            avatarSeed = "dhruv"
        ),
        User(
            id = "user_pending_2",
            username = "ananya_ec23",
            displayName = "Ananya Desai",
            email = "ananya.ec@student.gecb.in",
            college = "Government Engineering College, Bharuch",
            department = "Electronics & Communication",
            semester = 2,
            commuteCity = "Bharuch Local",
            verificationStatus = VerificationStatus.PENDING,
            role = UserRole.STUDENT,
            karma = 0,
            bio = "EC 1st year fresher eager to join Study Help group",
            avatarSeed = "ananya"
        )
    )

    private val _pendingVerifications = MutableStateFlow(initialPendingVerifications)
    val pendingVerifications: StateFlow<List<User>> = _pendingVerifications.asStateFlow()

    // Notifications
    private val initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "Answer Accepted!",
            message = "amit_ce_23 marked your reply as the accepted answer on 'DAA Reference Notes & PYQs'. (+15 Karma)",
            type = NotificationType.UPVOTE,
            timestamp = System.currentTimeMillis() - 1000L * 60 * 160,
            isRead = false,
            targetPostId = "post_2"
        ),
        AppNotification(
            id = "notif_2",
            title = "New Reply on Commute Advice",
            message = "rahul_patel replied to your post on Vadodara -> Bharuch Morning Trains.",
            type = NotificationType.REPLY,
            timestamp = System.currentTimeMillis() - 1000L * 60 * 70,
            isRead = false,
            targetPostId = "post_1"
        ),
        AppNotification(
            id = "notif_3",
            title = "Official Announcement",
            message = "T&P Cell posted: Adani & L&T placement drive registration is now live.",
            type = NotificationType.ANNOUNCEMENT,
            timestamp = System.currentTimeMillis() - 1000L * 60 * 800,
            isRead = true,
            targetPostId = "post_6"
        )
    )

    private val _notifications = MutableStateFlow(initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // --- ACTIONS ---

    fun setTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun votePost(postId: String, direction: Int) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val currentVote = post.userVote
                    val newVote = if (currentVote == direction) 0 else direction

                    var newUpvotes = post.upvotes
                    var newDownvotes = post.downvotes

                    // Remove old vote
                    if (currentVote == 1) newUpvotes--
                    if (currentVote == -1) newDownvotes--

                    // Apply new vote
                    if (newVote == 1) newUpvotes++
                    if (newVote == -1) newDownvotes++

                    post.copy(
                        userVote = newVote,
                        upvotes = maxOf(0, newUpvotes),
                        downvotes = maxOf(0, newDownvotes)
                    )
                } else {
                    post
                }
            }
        }
    }

    fun voteComment(postId: String, commentId: String, direction: Int) {
        _comments.update { map ->
            val list = map[postId] ?: emptyList()
            val updated = list.map { comment ->
                if (comment.id == commentId) {
                    val currentVote = comment.userVote
                    val newVote = if (currentVote == direction) 0 else direction

                    var newUpvotes = comment.upvotes
                    var newDownvotes = comment.downvotes

                    if (currentVote == 1) newUpvotes--
                    if (currentVote == -1) newDownvotes--

                    if (newVote == 1) newUpvotes++
                    if (newVote == -1) newDownvotes++

                    comment.copy(
                        userVote = newVote,
                        upvotes = maxOf(0, newUpvotes),
                        downvotes = maxOf(0, newDownvotes)
                    )
                } else {
                    comment
                }
            }
            map + (postId to updated)
        }
    }

    fun toggleSavePost(postId: String) {
        _posts.update { list ->
            list.map { if (it.id == postId) it.copy(isSaved = !it.isSaved) else it }
        }
    }

    fun votePoll(postId: String, optionId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId && post.postType == PostType.POLL) {
                    val prevOption = post.userSelectedPollOption
                    val updatedOptions = post.pollOptions.map { opt ->
                        when {
                            opt.id == optionId && prevOption != optionId -> opt.copy(voteCount = opt.voteCount + 1)
                            opt.id == prevOption && prevOption != optionId -> opt.copy(voteCount = maxOf(0, opt.voteCount - 1))
                            else -> opt
                        }
                    }
                    post.copy(
                        pollOptions = updatedOptions,
                        userSelectedPollOption = optionId
                    )
                } else {
                    post
                }
            }
        }
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
        val user = _currentUser.value
        val community = _communities.value.find { it.id == communityId }
        val communityName = community?.name ?: "General Campus"

        val newPost = Post(
            id = "post_${UUID.randomUUID().toString().take(8)}",
            communityId = communityId,
            communityName = communityName,
            title = title.trim(),
            content = content.trim(),
            authorId = if (isAnonymous) "anon_author_private_${UUID.randomUUID().toString().take(6)}" else user.id,
            authorName = if (isAnonymous) "Anonymous Student" else user.username,
            isAnonymous = isAnonymous,
            anonymousAlias = if (isAnonymous) "Anonymous Student" else "",
            tags = tags,
            upvotes = 1,
            downvotes = 0,
            userVote = 1,
            commentCount = 0,
            postType = postType,
            pollOptions = pollOptions.mapIndexed { idx, text ->
                PollOption("opt_${idx + 1}", text.trim(), 0)
            },
            createdAt = System.currentTimeMillis()
        )

        _posts.update { listOf(newPost) + it }
        _currentUser.update { it.copy(karma = it.karma + 5) }
        return newPost
    }

    fun addComment(postId: String, parentId: String?, content: String, isAnonymous: Boolean): Comment {
        val user = _currentUser.value
        val newComment = Comment(
            id = "comm_${UUID.randomUUID().toString().take(8)}",
            postId = postId,
            parentId = parentId,
            authorId = if (isAnonymous) "anon_user_private" else user.id,
            authorName = if (isAnonymous) "Anonymous Student" else user.username,
            isAnonymous = isAnonymous,
            content = content.trim(),
            upvotes = 1,
            downvotes = 0,
            userVote = 1,
            createdAt = System.currentTimeMillis()
        )

        _comments.update { map ->
            val list = map[postId] ?: emptyList()
            map + (postId to (list + newComment))
        }

        _posts.update { list ->
            list.map { if (it.id == postId) it.copy(commentCount = it.commentCount + 1) else it }
        }

        _currentUser.update { it.copy(karma = it.karma + 2) }
        return newComment
    }

    fun markAcceptedAnswer(postId: String, commentId: String) {
        _comments.update { map ->
            val list = map[postId] ?: emptyList()
            val updated = list.map {
                it.copy(isAcceptedAnswer = (it.id == commentId))
            }
            map + (postId to updated)
        }

        _posts.update { list ->
            list.map { if (it.id == postId) it.copy(acceptedCommentId = commentId) else it }
        }
    }

    fun toggleJoinCommunity(communityId: String) {
        _communities.update { list ->
            list.map { community ->
                if (community.id == communityId) {
                    val willJoin = !community.isJoined
                    community.copy(
                        isJoined = willJoin,
                        memberCount = if (willJoin) community.memberCount + 1 else maxOf(0, community.memberCount - 1)
                    )
                } else {
                    community
                }
            }
        }

        val community = _communities.value.find { it.id == communityId }
        if (community != null) {
            _currentUser.update { user ->
                val joined = user.joinedCommunities.toMutableList()
                if (community.isJoined) {
                    if (!joined.contains(communityId)) joined.add(communityId)
                } else {
                    joined.remove(communityId)
                }
                user.copy(joinedCommunities = joined)
            }
        }
    }

    fun createCommunity(
        name: String,
        slug: String,
        description: String,
        category: CommunityCategory,
        rules: List<String>
    ): Community {
        val user = _currentUser.value
        val newCommunity = Community(
            id = slug.lowercase().replace(" ", "-"),
            name = name.trim(),
            slug = slug.lowercase().replace(" ", "-"),
            description = description.trim(),
            category = category,
            memberCount = 1,
            iconEmoji = when (category) {
                CommunityCategory.GENERAL -> "🏛️"
                CommunityCategory.COMMUTE -> "🚆"
                CommunityCategory.ACADEMICS -> "📚"
                CommunityCategory.HOUSING -> "🏠"
                CommunityCategory.CAMPUS_LIFE -> "✨"
                CommunityCategory.CAREER -> "💼"
                CommunityCategory.CONFESSIONS -> "🎭"
            },
            rules = rules.filter { it.isNotBlank() },
            moderators = listOf(user.username),
            isOfficial = false,
            isJoined = true
        )

        _communities.update { list -> list + newCommunity }
        return newCommunity
    }

    fun addCommuteReport(
        origin: String,
        destination: String,
        transportName: String,
        scheduledTime: String,
        delayMinutes: Int,
        crowdLevel: CrowdLevel,
        travelExperience: String
    ): CommuteReport {
        val user = _currentUser.value
        val report = CommuteReport(
            id = "rep_${UUID.randomUUID().toString().take(8)}",
            routeTitle = "$origin to $destination",
            origin = origin,
            destination = destination,
            transportName = transportName.trim(),
            scheduledTime = scheduledTime.trim(),
            delayMinutes = delayMinutes,
            crowdLevel = crowdLevel,
            travelExperience = travelExperience.trim(),
            status = ReportStatus.CONFIRMED,
            reportedBy = user.username,
            reportedAt = System.currentTimeMillis()
        )

        _commuteReports.update { listOf(report) + it }
        return report
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
    ): LocalListing {
        val user = _currentUser.value
        val listing = LocalListing(
            id = "loc_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            category = category,
            neighborhood = neighborhood.trim(),
            approximateLocation = approximateLocation.trim(),
            priceRange = priceRange.trim(),
            amenities = amenities.filter { it.isNotBlank() },
            contactInfo = contactInfo.trim(),
            rating = 5.0f,
            reviewCount = 1,
            isVerified = false,
            submittedBy = user.username,
            description = description.trim()
        )

        _localListings.update { listOf(listing) + it }
        return listing
    }

    fun submitReport(
        targetType: ReportTargetType,
        targetId: String,
        summary: String,
        reason: ReportReason,
        details: String
    ) {
        val user = _currentUser.value
        val report = ReportItem(
            id = "rep_flag_${UUID.randomUUID().toString().take(8)}",
            targetType = targetType,
            targetId = targetId,
            targetSummary = summary,
            reason = reason,
            details = details,
            reportedBy = user.username,
            reportedAt = System.currentTimeMillis(),
            status = ModerationStatus.PENDING
        )
        _reports.update { listOf(report) + it }
    }

    fun resolveReport(reportId: String, newStatus: ModerationStatus) {
        _reports.update { list ->
            list.map { if (it.id == reportId) it.copy(status = newStatus) else it }
        }
    }

    fun reviewVerification(userId: String, approve: Boolean) {
        _pendingVerifications.update { list -> list.filter { it.id != userId } }
        if (approve && _currentUser.value.id == userId) {
            _currentUser.update { it.copy(verificationStatus = VerificationStatus.VERIFIED) }
        }
    }

    fun updateUserProfile(
        displayName: String,
        department: String,
        semester: Int,
        commuteCity: String,
        bio: String
    ) {
        _currentUser.update {
            it.copy(
                displayName = displayName.trim(),
                department = department.trim(),
                semester = semester,
                commuteCity = commuteCity.trim(),
                bio = bio.trim()
            )
        }
    }

    fun markNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }
}
