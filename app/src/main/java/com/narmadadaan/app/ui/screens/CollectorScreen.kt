package com.narmadadaan.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.narmadadaan.app.R
import com.narmadadaan.app.model.Donation
import com.narmadadaan.app.model.User
import com.narmadadaan.app.ui.theme.*
import kotlinx.coroutines.launch

private val PRESET_PURPOSES = listOf(
    "श्री मां नर्मदा जन्मोत्सव चुनरी यात्रा",
    "श्री राम जन्मोत्सव शोभायात्रा",
    "श्री हनुमान जन्मोत्सव शोभायात्रा",
    "धर्म रक्षा निधि संग्रहण",
    "अन्य"
)

private val PRESET_AMOUNTS = listOf(101, 251, 501, 1100, 2100, 5100, 11000)
private const val UPI_ID = "7440771076-3@ibl"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectorScreen(
    currentUser: User?,
    onSaveDonation: suspend (name: String, mobile: String, amount: Double, purpose: String, method: String, utr: String?) -> Result<Donation>,
    onDonationSuccess: (Donation) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var donorName by remember { mutableStateOf("") }
    var donorMobile by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf(PRESET_PURPOSES[0]) }
    var otherPurpose by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("cash") } // "cash" or "upi"
    var transactionId by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var purposeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Collector Header Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BhagwaContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BhagwaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "दान संग्रहण पैनल (Collector Panel)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BhagwaDark
                    )
                    Text(
                        text = "संग्रहकर्ता: ${currentUser?.name ?: "दीपक पटेल"} • ${currentUser?.role ?: "collector"}",
                        fontSize = 12.sp,
                        color = OnBhagwaContainer
                    )
                }
            }
        }

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "नया दान दर्ज करें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Donor Name
                OutlinedTextField(
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = { Text("दानदाता का नाम *") },
                    placeholder = { Text("उदा. रमेश कुमार शर्मा") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Donor Mobile
                OutlinedTextField(
                    value = donorMobile,
                    onValueChange = { if (it.length <= 10) donorMobile = it.filter { char -> char.isDigit() } },
                    label = { Text("मोबाइल नंबर (10 अंक) *") },
                    placeholder = { Text("9876543210") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_mobile_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Amount Selection Chips
                Text(
                    text = "दान राशि चुनें (₹) *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(PRESET_AMOUNTS) { amt ->
                        val isSelected = amountText == amt.toString()
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) BhagwaPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { amountText = amt.toString() }
                        ) {
                            Text(
                                text = "₹$amt",
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Custom Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                    label = { Text("दान राशि (₹) *") },
                    leadingIcon = {
                        Text(
                            "₹",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BhagwaPrimary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Purpose Dropdown
                ExposedDropdownMenuBox(
                    expanded = purposeDropdownExpanded,
                    onExpandedChange = { purposeDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedPurpose,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("चंदे का उद्देश्य *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = purposeDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("purpose_dropdown"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = purposeDropdownExpanded,
                        onDismissRequest = { purposeDropdownExpanded = false }
                    ) {
                        PRESET_PURPOSES.forEach { purpose ->
                            DropdownMenuItem(
                                text = { Text(purpose, fontSize = 13.sp) },
                                onClick = {
                                    selectedPurpose = purpose
                                    purposeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedPurpose == "अन्य") {
                    OutlinedTextField(
                        value = otherPurpose,
                        onValueChange = { otherPurpose = it },
                        label = { Text("कृपया उद्देश्य लिखें *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Payment Method Selector
                Text(
                    text = "भुगतान माध्यम *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cash Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (paymentMethod == "cash") 2.dp else 1.dp,
                                color = if (paymentMethod == "cash") BhagwaPrimary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { paymentMethod = "cash" },
                        color = if (paymentMethod == "cash") BhagwaContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Payments,
                                contentDescription = null,
                                tint = if (paymentMethod == "cash") BhagwaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "नकद (Cash)",
                                fontWeight = if (paymentMethod == "cash") FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (paymentMethod == "cash") BhagwaDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // UPI Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (paymentMethod == "upi") 2.dp else 1.dp,
                                color = if (paymentMethod == "upi") NarmadaBlue else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { paymentMethod = "upi" },
                        color = if (paymentMethod == "upi") NarmadaBlueLight.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = null,
                                tint = if (paymentMethod == "upi") NarmadaBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UPI (ऑनलाइन)",
                                fontWeight = if (paymentMethod == "upi") FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (paymentMethod == "upi") NarmadaBlueDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // If UPI selected, show QR Code & UPI ID & UTR field
                if (paymentMethod == "upi") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = NarmadaBlueLight.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NarmadaBlue.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PhonePe / GPay / Paytm से स्कैन करें",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = NarmadaBlueDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Official PhonePe QR code
                            Image(
                                painter = painterResource(id = R.drawable.upi_qr),
                                contentDescription = "UPI QR Code",
                                modifier = Modifier
                                    .size(170.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .padding(6.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "UPI ID: $UPI_ID",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NarmadaBlueDark
                                )
                                IconButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(UPI_ID)) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "कॉपी करें",
                                        modifier = Modifier.size(14.dp),
                                        tint = NarmadaBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = transactionId,
                                onValueChange = { transactionId = it },
                                label = { Text("UPI UTR / ट्रांजेक्शन ID *") },
                                placeholder = { Text("उदा. 428192837192") },
                                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("upi_utr_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                // Error Message
                errorMessage?.let { err ->
                    Text(
                        text = "⚠️ $err",
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Submit Button
                Button(
                    onClick = {
                        // Validation
                        if (donorName.trim().length < 2) {
                            errorMessage = "कृपया दानदाता का सही नाम लिखें"
                            return@Button
                        }
                        if (donorMobile.trim().length != 10) {
                            errorMessage = "मोबाइल नंबर 10 अंक का होना चाहिए"
                            return@Button
                        }
                        val amt = amountText.toDoubleOrNull()
                        if (amt == null || amt <= 0) {
                            errorMessage = "कृपया मान्य दान राशि डालें"
                            return@Button
                        }
                        val finalPurpose = if (selectedPurpose == "अन्य") {
                            if (otherPurpose.isBlank()) {
                                errorMessage = "कृपया दान का उद्देश्य लिखें"
                                return@Button
                            }
                            otherPurpose.trim()
                        } else {
                            selectedPurpose
                        }
                        if (paymentMethod == "upi" && transactionId.trim().isBlank()) {
                            errorMessage = "UPI भुगतान के लिए UTR / Transaction ID आवश्यक है"
                            return@Button
                        }

                        errorMessage = null
                        isSubmitting = true

                        coroutineScope.launch {
                            val result = onSaveDonation(
                                donorName.trim(),
                                donorMobile.trim(),
                                amt,
                                finalPurpose,
                                paymentMethod,
                                if (paymentMethod == "upi") transactionId.trim() else null
                            )
                            isSubmitting = false
                            result.onSuccess { donation ->
                                // Reset form
                                donorName = ""
                                donorMobile = ""
                                amountText = ""
                                transactionId = ""
                                otherPurpose = ""
                                onDonationSuccess(donation)
                            }.onFailure { err ->
                                errorMessage = "दान दर्ज करने में समस्या: ${err.message}"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_donation_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BhagwaPrimary,
                        contentColor = Color.White
                    ),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("डेटाबेस में सहेजा जा रहा है...")
                    } else {
                        Icon(Icons.Default.Receipt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "दान रसीद जारी करें",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
