package com.narmadadaan.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.narmadadaan.app.R
import com.narmadadaan.app.model.AnalyticsSummary
import com.narmadadaan.app.model.Donation
import com.narmadadaan.app.ui.theme.*

@Composable
fun DashboardScreen(
    donations: List<Donation>,
    summary: AnalyticsSummary,
    onNavigateToCollect: () -> Unit,
    onNavigateToVerify: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDonationForReceipt by remember { mutableStateOf<Donation?>(null) }

    val filteredDonations = remember(donations, searchQuery) {
        if (searchQuery.isBlank()) donations
        else {
            val q = searchQuery.trim().lowercase()
            donations.filter {
                it.name.lowercase().contains(q) ||
                it.mobile.contains(q) ||
                it.donationId.lowercase().contains(q) ||
                it.purpose.lowercase().contains(q)
            }
        }
    }

    selectedDonationForReceipt?.let { donation ->
        ReceiptDialog(
            donation = donation,
            onDismiss = { selectedDonationForReceipt = null }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Divine Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(BhagwaContainer.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "मां नर्मदा",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "श्री मां नर्मदा भक्त परिवार",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = BhagwaPrimary
                            )
                            Text(
                                text = "आधिकारिक दान प्रबंधन एवं रसीद प्रणाली",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "लाइव क्लाउड डेटाबेस सक्रिय",
                                    fontSize = 11.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Summary Statistics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Raised Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("total_raised_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BhagwaContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "कुल एकत्रित राशि",
                                fontSize = 12.sp,
                                color = OnBhagwaContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = BhagwaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${summary.totalAmount.toLong()}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BhagwaDark
                        )
                        Text(
                            text = "+₹${summary.todayAmount.toLong()} आज",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SuccessGreen
                        )
                    }
                }

                // Total Donors Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("total_donors_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NarmadaBlueLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "कुल दानदाता",
                                fontSize = 12.sp,
                                color = NarmadaBlueDark,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = null,
                                tint = NarmadaBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${summary.totalDonors}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NarmadaBlueDark
                        )
                        Text(
                            text = "+${summary.todayDonors} आज जुड़े",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NarmadaBlue
                        )
                    }
                }
            }
        }

        // Payment Method Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "नकद दान (Cash)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹${summary.cashAmount.toLong()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BhagwaPrimary)
                        Text(text = "${summary.cashCount} दान", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ऑनलाइन दान (UPI)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹${summary.upiAmount.toLong()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NarmadaBlue)
                        Text(text = "${summary.upiCount} दान", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Search Bar & Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("दानदाता या मोबाइल खोजें...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "खोजें", modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "साफ़ करें", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_donations_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Verify Button
                IconButton(
                    onClick = onNavigateToVerify,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NarmadaBlueLight)
                        .testTag("quick_verify_button")
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = "सत्यापन",
                        tint = NarmadaBlueDark
                    )
                }
            }
        }

        // Recent Donations Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "हाल के दान (${filteredDonations.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onNavigateToCollect) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("नया दान दर्ज करें", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (filteredDonations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = BhagwaPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "कोई दान रिकॉर्ड नहीं मिला" else "अभी कोई दान दर्ज नहीं हुआ है",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "नया दान जोड़ने के लिए 'दान दर्ज करें' पर टैप करें",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredDonations, key = { it.donationId }) { donation ->
                DonationItemCard(
                    donation = donation,
                    onViewReceipt = { selectedDonationForReceipt = donation }
                )
            }
        }
    }
}

@Composable
fun DonationItemCard(
    donation: Donation,
    onViewReceipt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewReceipt() }
            .testTag("donation_card_${donation.donationId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sacred Bhagwa icon box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BhagwaContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "₹",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BhagwaDark
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = donation.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        color = if (donation.paymentMethod == "upi") NarmadaBlueLight else SuccessGreenLight,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (donation.paymentMethod == "upi") "UPI" else "नकद",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (donation.paymentMethod == "upi") NarmadaBlueDark else SuccessGreen,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = donation.purpose,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${donation.donationId} • ${donation.formattedDate}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = donation.formattedAmount,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = BhagwaPrimary
                )
                Text(
                    text = "रसीद देखें",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NarmadaBlue
                )
            }
        }
    }
}
