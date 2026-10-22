package com.narmadadaan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.narmadadaan.app.data.FirestoreRepository
import com.narmadadaan.app.model.Donation
import com.narmadadaan.app.model.User
import com.narmadadaan.app.ui.screens.*
import com.narmadadaan.app.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: FirestoreRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = FirestoreRepository(this)

        lifecycleScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        setContent {
            NarmadaDaanTheme {
                MainAppScreen(repository = repository)
            }
        }
    }
}

enum class Screen(val title: String) {
    DASHBOARD("डैशबोर्ड"),
    COLLECT("दान दर्ज करें"),
    VERIFY("रसीद सत्यापन"),
    ADMIN("व्यवस्थापक")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(repository: FirestoreRepository) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    var currentUser by remember { mutableStateOf<User?>(DEMO_ACCOUNTS[1]) } // Default to Deepak Patel (Collector)
    var showAuthDialog by remember { mutableStateOf(false) }
    var newlyCreatedReceipt by remember { mutableStateOf<Donation?>(null) }

    val donations by repository.getDonationsFlow().collectAsState(initial = emptyList())
    val auditLogs by repository.getAuditLogsFlow().collectAsState(initial = emptyList())
    val summary = remember(donations) { repository.calculateSummary(donations) }

    // Handle back button on secondary screens
    if (currentScreen != Screen.DASHBOARD) {
        BackHandler {
            currentScreen = Screen.DASHBOARD
        }
    }

    // Show popup receipt when a donation is created or inspected
    newlyCreatedReceipt?.let { donation ->
        ReceiptDialog(
            donation = donation,
            onDismiss = { newlyCreatedReceipt = null }
        )
    }

    if (showAuthDialog) {
        AuthDialog(
            currentUser = currentUser,
            onSelectUser = { currentUser = it },
            onSignOut = { currentUser = null },
            onDismiss = { showAuthDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "श्री मां नर्मदा लोगो",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                        Column {
                            Text(
                                text = "श्री मां नर्मदा भक्त परिवार",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BhagwaPrimary
                            )
                            Text(
                                text = currentScreen.title,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAuthDialog = true },
                        modifier = Modifier.testTag("auth_profile_button")
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "उपयोगकर्ता खाता",
                            tint = BhagwaPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.DASHBOARD,
                    onClick = { currentScreen = Screen.DASHBOARD },
                    icon = {
                        Icon(
                            if (currentScreen == Screen.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "डैशबोर्ड"
                        )
                    },
                    label = { Text("डैशबोर्ड", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BhagwaDark,
                        selectedTextColor = BhagwaDark,
                        indicatorColor = BhagwaContainer
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.COLLECT,
                    onClick = { currentScreen = Screen.COLLECT },
                    icon = {
                        Icon(
                            if (currentScreen == Screen.COLLECT) Icons.Filled.VolunteerActivism else Icons.Outlined.VolunteerActivism,
                            contentDescription = "दान दर्ज करें"
                        )
                    },
                    label = { Text("दान दर्ज", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BhagwaDark,
                        selectedTextColor = BhagwaDark,
                        indicatorColor = BhagwaContainer
                    ),
                    modifier = Modifier.testTag("nav_collect")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.VERIFY,
                    onClick = { currentScreen = Screen.VERIFY },
                    icon = {
                        Icon(
                            if (currentScreen == Screen.VERIFY) Icons.Filled.VerifiedUser else Icons.Outlined.VerifiedUser,
                            contentDescription = "सत्यापन"
                        )
                    },
                    label = { Text("सत्यापन", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NarmadaBlueDark,
                        selectedTextColor = NarmadaBlueDark,
                        indicatorColor = NarmadaBlueLight
                    ),
                    modifier = Modifier.testTag("nav_verify")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.ADMIN,
                    onClick = { currentScreen = Screen.ADMIN },
                    icon = {
                        Icon(
                            if (currentScreen == Screen.ADMIN) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "व्यवस्थापक"
                        )
                    },
                    label = { Text("व्यवस्थापक", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BhagwaDark,
                        selectedTextColor = BhagwaDark,
                        indicatorColor = BhagwaContainer
                    ),
                    modifier = Modifier.testTag("nav_admin")
                )
            }
        },
        floatingActionButton = {
            if (currentScreen == Screen.DASHBOARD) {
                ExtendedFloatingActionButton(
                    onClick = { currentScreen = Screen.COLLECT },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("दान दर्ज करें", fontWeight = FontWeight.Bold) },
                    containerColor = BhagwaPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_create_donation")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> DashboardScreen(
                    donations = donations,
                    summary = summary,
                    onNavigateToCollect = { currentScreen = Screen.COLLECT },
                    onNavigateToVerify = { currentScreen = Screen.VERIFY }
                )
                Screen.COLLECT -> CollectorScreen(
                    currentUser = currentUser,
                    onSaveDonation = { name, mobile, amount, purpose, method, utr ->
                        repository.addDonation(
                            name = name,
                            mobile = mobile,
                            amount = amount,
                            purpose = purpose,
                            paymentMethod = method,
                            transactionId = utr,
                            collectorId = currentUser?.uid ?: "collector_001",
                            collectorName = currentUser?.name ?: "दीपक पटेल"
                        )
                    },
                    onDonationSuccess = { donation ->
                        newlyCreatedReceipt = donation
                    }
                )
                Screen.VERIFY -> VerifyScreen(
                    recentDonations = donations,
                    onVerify = { query -> repository.verifyDonation(query) }
                )
                Screen.ADMIN -> AdminScreen(
                    summary = summary,
                    donations = donations,
                    auditLogs = auditLogs,
                    currentUser = currentUser,
                    onSwitchUser = { currentUser = it }
                )
            }
        }
    }
}
