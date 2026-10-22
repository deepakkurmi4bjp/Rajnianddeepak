package com.narmadadaan.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.narmadadaan.app.R
import com.narmadadaan.app.data.VerificationResult
import com.narmadadaan.app.model.Donation
import com.narmadadaan.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VerifyScreen(
    recentDonations: List<Donation>,
    onVerify: suspend (String) -> VerificationResult
) {
    val coroutineScope = rememberCoroutineScope()
    var receiptQuery by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<VerificationResult?>(null) }
    var showReceiptModal by remember { mutableStateOf<Donation?>(null) }

    showReceiptModal?.let {
        ReceiptDialog(donation = it, onDismiss = { showReceiptModal = null })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Divine Logo & Header
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "श्री मां नर्मदा लोगो",
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "श्री मां नर्मदा भक्त परिवार",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = BhagwaPrimary
            )
            Text(
                text = "आधिकारिक दान रसीद सत्यापन पोर्टल",
                fontSize = 13.sp,
                color = NarmadaBlueDark,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Search Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "रसीद क्रमांक दर्ज करें",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = receiptQuery,
                    onValueChange = { receiptQuery = it },
                    placeholder = { Text("उदा. ND-2026-0001") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = NarmadaBlue
                        )
                    },
                    trailingIcon = {
                        if (receiptQuery.isNotEmpty()) {
                            IconButton(onClick = { receiptQuery = ""; result = null }) {
                                Icon(Icons.Default.Clear, contentDescription = "हटाएं")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verify_receipt_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Quick sample chips
                if (recentDonations.isNotEmpty()) {
                    Column {
                        Text(
                            text = "हाल के रसीद क्रमांक से तुरंत जांचें:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recentDonations.take(4)) { d ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = NarmadaBlueLight,
                                    modifier = Modifier.clickable {
                                        receiptQuery = d.donationId
                                    }
                                ) {
                                    Text(
                                        text = d.donationId,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NarmadaBlueDark,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (receiptQuery.isNotBlank()) {
                            isVerifying = true
                            result = null
                            coroutineScope.launch {
                                val res = onVerify(receiptQuery.trim())
                                result = res
                                isVerifying = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("verify_action_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NarmadaBlueDark,
                        contentColor = Color.White
                    ),
                    enabled = !isVerifying && receiptQuery.isNotBlank()
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("सत्यापित किया जा रहा है...")
                    } else {
                        Icon(Icons.Default.Shield, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("रसीद सत्यापित करें (Verify)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Result Display
        AnimatedVisibility(
            visible = result != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            when (val r = result) {
                is VerificationResult.Valid -> {
                    // Sky Blue Valid Receipt Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verified_valid_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = NarmadaBlueLight),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NarmadaBlue)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "सत्यापित",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "✅ वैध रसीद (VERIFIED)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NarmadaBlueDark
                            )

                            Text(
                                text = "क्रिप्टोग्राफिक सुरक्षा हैश से सत्यापित आधिकारिक रसीद",
                                fontSize = 11.sp,
                                color = NarmadaBlue,
                                textAlign = TextAlign.Center
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = NarmadaBlue.copy(alpha = 0.3f)
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                VerifyRow("रसीद क्रमांक", r.donation.donationId)
                                VerifyRow("दानदाता का नाम", r.donation.name)
                                VerifyRow("दान राशि", r.donation.formattedAmount)
                                VerifyRow("उद्देश्य", r.donation.purpose)
                                VerifyRow(
                                    "भुगतान माध्यम",
                                    if (r.donation.paymentMethod == "upi") "UPI (${r.donation.maskedTransactionId ?: "ऑनलाइन"})" else "नकद (Cash)"
                                )
                                VerifyRow("संग्रहकर्ता", r.donation.collectorName)
                                VerifyRow("दिनांक", r.donation.formattedDate)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showReceiptModal = r.donation },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BhagwaPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("पूर्ण रसीद कार्ड देखें")
                            }
                        }
                    }
                }

                is VerificationResult.Tampered -> {
                    // Tampered warning
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verified_tampered_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ErrorRedLight),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ErrorRed)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⚠️ छेड़छाड़ की गई रसीद (TAMPERED)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = ErrorRed
                            )
                            Text(
                                text = "इस रसीद का डेटा मूल डेटाबेस से मेल नहीं खाता है। कृपया अधिकृत पदाधिकारियों से संपर्क करें।",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                is VerificationResult.NotFound -> {
                    // Not found warning
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verified_not_found_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ErrorRedLight),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ErrorRed)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.GppBad,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "❌ अमान्य रसीद (INVALID)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = ErrorRed
                            )
                            Text(
                                text = "यह रसीद हमारे अधिकृत क्लाउड डेटाबेस में दर्ज नहीं है। यह दस्तावेज़ अमान्य या अनधिकृत हो सकता है।",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                null -> {}
            }
        }
    }
}

@Composable
fun VerifyRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
