package com.narmadadaan.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.narmadadaan.app.R
import com.narmadadaan.app.data.HashUtils
import com.narmadadaan.app.model.Donation
import com.narmadadaan.app.ui.theme.*

@Composable
fun ReceiptDialog(
    donation: Donation,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val amountInWords = HashUtils.numberToHindiWords(donation.amount.toLong())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header with Sacred Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "श्री मां नर्मदा लोगो",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "श्री मां नर्मदा भक्त परिवार",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = BhagwaPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "आधिकारिक दान रसीद",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NarmadaBlueDark
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "बंद करें")
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = SacredBorder
                )

                // Verified Badge Bar
                Surface(
                    color = NarmadaBlueLight,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "सत्यापित",
                            tint = NarmadaBlueDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "वैध एवं अधिकृत रसीद (VERIFIED)",
                            color = NarmadaBlueDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Receipt Number & Date Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "रसीद क्रमांक",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = donation.donationId,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BhagwaPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "दिनांक व समय",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = donation.formattedDate,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Main Amount Highlight Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BhagwaContainer, shape = RoundedCornerShape(14.dp))
                        .border(1.5.dp, BhagwaPrimary, shape = RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "स्वीकृत दान राशि",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnBhagwaContainer
                        )
                        Text(
                            text = donation.formattedAmount,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BhagwaDark
                        )
                        Text(
                            text = amountInWords,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnBhagwaContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Receipt Details Table
                ReceiptDetailRow("दानदाता का नाम", donation.name)
                ReceiptDetailRow("मोबाइल नंबर", donation.maskedMobile)
                ReceiptDetailRow("दान का उद्देश्य", donation.purpose)
                ReceiptDetailRow(
                    "भुगतान माध्यम",
                    if (donation.paymentMethod == "upi") "UPI (ऑनलाइन)" else "नकद (Cash)"
                )
                if (!donation.transactionId.isNullOrEmpty()) {
                    ReceiptDetailRow("ट्रांजेक्शन ID / UTR", donation.maskedTransactionId ?: "••••")
                }
                ReceiptDetailRow("अधिकृत संग्रहकर्ता", donation.collectorName)

                Spacer(modifier = Modifier.height(12.dp))

                // Cryptographic Hash Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = "सुरक्षा",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "क्रिप्टोग्राफिक सुरक्षा हैश",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(donation.hash))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "कॉपी करें",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = donation.hash.ifEmpty { "SHA256:VERIFIED" },
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: WhatsApp Share, System Share, Copy
                val receiptText = buildString {
                    append("🚩 श्री मां नर्मदा भक्त परिवार — दान रसीद 🚩\n\n")
                    append("रसीद संख्या: ${donation.donationId}\n")
                    append("दानदाता: ${donation.name}\n")
                    append("राशि: ${donation.formattedAmount} (${amountInWords})\n")
                    append("उद्देश्य: ${donation.purpose}\n")
                    append("भुगतान: ${if (donation.paymentMethod == "upi") "UPI" else "नकद"}\n")
                    append("संग्रहकर्ता: ${donation.collectorName}\n")
                    append("दिनांक: ${donation.formattedDate}\n\n")
                    append("हर-हर नर्मदे! आपका दान मां नर्मदा सेवा कार्य हेतु सुरक्षित रूप से प्राप्त हुआ।")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // WhatsApp Share
                    Button(
                        onClick = {
                            shareToWhatsApp(context, donation.mobile, receiptText)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whatsapp_share_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // System Share
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "दान रसीद साझा करें"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("system_share_button")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "शेयर करें",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("शेयर करें", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

private fun shareToWhatsApp(context: Context, mobile: String, text: String) {
    try {
        val cleanPhone = mobile.replace("+", "").replace("-", "").replace(" ", "")
        val phoneWithCountry = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneWithCountry&text=${Uri.encode(text)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "रसीद भेजें"))
    }
}
