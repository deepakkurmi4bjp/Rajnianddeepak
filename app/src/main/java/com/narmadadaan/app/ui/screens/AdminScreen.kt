package com.narmadadaan.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.narmadadaan.app.model.AnalyticsSummary
import com.narmadadaan.app.model.AuditLog
import com.narmadadaan.app.model.Donation
import com.narmadadaan.app.model.User
import com.narmadadaan.app.ui.theme.*

@Composable
fun AdminScreen(
    summary: AnalyticsSummary,
    donations: List<Donation>,
    auditLogs: List<AuditLog>,
    currentUser: User?,
    onSwitchUser: (User) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("विश्लेषण (Analytics)", "संग्रहकर्ता (Collectors)", "ऑडिट लॉग (Audit)")

    // Group donations by collector
    val collectorStats = remember(donations) {
        donations.groupBy { it.collectorName.ifEmpty { "अज्ञात" } }
            .map { (name, list) ->
                Triple(name, list.sumOf { it.amount }, list.size)
            }
            .sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NarmadaBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "प्रशासन एवं निगरानी पैनल",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "सक्रिय व्यवस्थापक: ${currentUser?.name ?: "रमेश शर्मा (Super Admin)"}",
                            fontSize = 12.sp,
                            color = NarmadaBlueDark
                        )
                    }
                }
            }
        }

        // Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BhagwaPrimary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Analytics View
                item {
                    Text(
                        text = "वित्तीय सारांश (Financial Overview)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BhagwaContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("कुल प्राप्त दान", fontSize = 12.sp, color = OnBhagwaContainer)
                            Text(
                                "₹${summary.totalAmount.toLong()}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BhagwaDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = BhagwaPrimary.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("आज का संग्रह", fontSize = 11.sp, color = OnBhagwaContainer)
                                    Text("₹${summary.todayAmount.toLong()}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("आज के दानदाता", fontSize = 11.sp, color = OnBhagwaContainer)
                                    Text("${summary.todayDonors}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "भुगतान विधियों का विवरण",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            val total = if (summary.totalAmount > 0) summary.totalAmount else 1.0
                            val upiPercent = (summary.upiAmount / total * 100).toInt()
                            val cashPercent = 100 - upiPercent

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ऑनलाइन UPI ($upiPercent%)", fontSize = 12.sp)
                                Text("₹${summary.upiAmount.toLong()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NarmadaBlue)
                            }
                            LinearProgressIndicator(
                                progress = { (summary.upiAmount / total).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = NarmadaBlue,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("नकद Cash ($cashPercent%)", fontSize = 12.sp)
                                Text("₹${summary.cashAmount.toLong()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BhagwaPrimary)
                            }
                            LinearProgressIndicator(
                                progress = { (summary.cashAmount / total).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = BhagwaPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            1 -> {
                // Collectors Rankings
                item {
                    Text(
                        text = "संग्रहकर्ता प्रदर्शन (Collector Rankings)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                if (collectorStats.isEmpty()) {
                    item {
                        Text("कोई संग्रहकर्ता डेटा उपलब्ध नहीं है", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    items(collectorStats) { (name, total, count) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(BhagwaContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = BhagwaPrimary, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "$count दान रसीदें जारी", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text(
                                    text = "₹${total.toLong()}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = BhagwaPrimary
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Audit Logs Timeline
                item {
                    Text(
                        text = "सुरक्षा व ऑडिट ट्रेल (Audit Trail - ${auditLogs.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                if (auditLogs.isEmpty()) {
                    item {
                        Text("अभी कोई ऑडिट लॉग दर्ज नहीं हुआ है", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    items(auditLogs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .padding(top = 4.dp)
                                        .clip(CircleShape)
                                        .background(if (log.action.contains("CREATE")) SuccessGreen else NarmadaBlue)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = log.action,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = log.formattedDate,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = log.details,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Text(
                                        text = "कर्ता: ${log.userName}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
