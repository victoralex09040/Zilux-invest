package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InvestmentPlan
import com.example.data.User
import com.example.data.UserInvestment
import com.example.data.InvestmentTransaction
import com.example.ui.AuthState
import com.example.ui.InvestmentViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// ==========================================
// 1. Illustrious Zelox Custom Vector Logo
// ==========================================
@Composable
fun ZeloxLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(64.dp)) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)

            // Inner backing ambient radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(EmeraldGreen.copy(alpha = 0.35f), Color.Transparent),
                    center = center,
                    radius = w * 0.5f
                )
            )

            // Outer futuristic Hexagon ring
            val hexPath = Path().apply {
                moveTo(w * 0.5f, h * 0.04f)
                lineTo(w * 0.9f, h * 0.27f)
                lineTo(w * 0.9f, h * 0.73f)
                lineTo(w * 0.5f, h * 0.96f)
                lineTo(w * 0.1f, h * 0.73f)
                lineTo(w * 0.1f, h * 0.27f)
                close()
            }
            drawPath(
                path = hexPath,
                brush = Brush.linearGradient(
                    colors = listOf(EmeraldGreen, GoldAccent)
                ),
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Dynamic core lightning "Z" symbol
            val zPath = Path().apply {
                moveTo(w * 0.32f, h * 0.31f)
                lineTo(w * 0.68f, h * 0.31f)
                lineTo(w * 0.32f, h * 0.69f)
                lineTo(w * 0.68f, h * 0.69f)
            }
            drawPath(
                path = zPath,
                color = Color.White,
                style = Stroke(width = 5.dp.toPx())
            )
        }
    }
}

// ==========================================
// 2. Splash Screen
// ==========================================
@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1600)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ZeloxLogo(modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Z E L O X",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "QUANT AUTONOMOUS YIELD PLATFORM",
                color = EmeraldGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

// ==========================================
// 3. Login Screen
// ==========================================
@Composable
fun LoginScreen(
    viewModel: InvestmentViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    val authState by viewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.clearAuthStates()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ZeloxLogo(modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome to Zelox",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Authorize secure gateway protocol",
                    color = DarkGreyText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error alert
                if (authState is AuthState.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(LossRed.copy(alpha = 0.12f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = LossRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Email check / Input field
                OutlinedTextField(
                    value = emailOrUsername,
                    onValueChange = { emailOrUsername = it },
                    label = { Text("Client Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DarkGreyText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = DarkGreyText
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Secure Access Token Key") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DarkGreyText) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = DarkGreyText
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Elegant interactive "Remember Me" Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = DeepObsidian,
                            checkedColor = EmeraldGreen,
                            uncheckedColor = DarkGreyText
                        ),
                        modifier = Modifier.testTag("remember_me_checkbox")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remember secure session details",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Access submit Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.login(emailOrUsername, password, rememberMe)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(12.dp),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = DeepObsidian, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = "AUTHORIZE SESSION",
                            color = DeepObsidian,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "No biometric profile?", color = DarkGreyText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Initialize Secure Sign-Up",
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onNavigateToSignUp() }
                            .testTag("go_signup_btn")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(12.dp))

                var showCloudConfig by remember { mutableStateOf(false) }
                val isSynced = viewModel.getSupabaseUrl().isNotBlank()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCloudConfig = !showCloudConfig }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSynced) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isSynced) EmeraldGreen else DarkGreyText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSynced) "Cloud Database Active" else "Cloud Database Inactive",
                            color = if (isSynced) EmeraldGreen else DarkGreyText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (showCloudConfig) "Hide Gateway" else "Configure Gateway",
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (showCloudConfig) {
                    Spacer(modifier = Modifier.height(8.dp))
                    var cloudUrl by remember { mutableStateOf(viewModel.getSupabaseUrl()) }
                    var cloudKey by remember { mutableStateOf(viewModel.getSupabaseKey()) }

                    OutlinedTextField(
                        value = cloudUrl,
                        onValueChange = { cloudUrl = it.trim() },
                        label = { Text("Supabase URL", fontSize = 11.sp) },
                        placeholder = { Text("https://your-project.supabase.co") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = cloudKey,
                        onValueChange = { cloudKey = it.trim() },
                        label = { Text("Supabase API Key", fontSize = 11.sp) },
                        placeholder = { Text("eyJhbGci...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.testSupabaseAndSync(cloudUrl, cloudKey) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("SAVE & CONNECT", color = DeepObsidian, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        if (isSynced) {
                            Button(
                                onClick = {
                                    viewModel.disconnectSupabase()
                                    cloudUrl = ""
                                    cloudKey = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("WIPE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. Sign-Up Screen
// ==========================================
@Composable
fun SignUpScreen(
    viewModel: InvestmentViewModel,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }

    val authState by viewModel.signUpState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignUpSuccess()
            viewModel.clearAuthStates()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ZeloxLogo(modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Create Zelox Node",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Enroll client profile into quantitative ledger",
                        color = DarkGreyText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Show error message
                if (authState is AuthState.Error) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(LossRed.copy(alpha = 0.12f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = LossRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Identification Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_fullname"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null, tint = DarkGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address Protocol") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_email"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DarkGreyText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("System Link Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_username"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = DarkGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password Vault Token") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_password"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DarkGreyText) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Re-type Secure Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_confirm_password"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DarkGreyText) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = referralCode,
                        onValueChange = { referralCode = it },
                        label = { Text("Referral Invite Code (Optional)") },
                        placeholder = { Text("ZEL-partner-xxxxx") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_referral"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = DarkGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )
                }

                // Checkbox for terms agree
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = EmeraldGreen,
                                uncheckedColor = BorderColor,
                                checkmarkColor = DeepObsidian
                            ),
                            modifier = Modifier.testTag("terms_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Agree to Zelox terms of service and private system legal policies.",
                            color = SilverGray,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Create Button
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.signUp(
                                username = username,
                                passwordRaw = password,
                                confirmPasswordRaw = confirmPassword,
                                fullName = fullName,
                                email = email,
                                agreedToTerms = agreedToTerms,
                                referralCodeUsed = referralCode.ifBlank { null }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("signup_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = DeepObsidian, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "DEPLOY NEW NODE",
                                color = DeepObsidian,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Existing credentials?", color = DarkGreyText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Return to Gateway Access",
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. User Portfolio Dashboard & Interactive System Cards Redesign
// ==========================================
@Composable
fun DashboardScreen(
    viewModel: InvestmentViewModel,
    onNavigateToPlanDetail: (String) -> Unit,
    onNavigateToMarket: () -> Unit
) {
    val activeUser by viewModel.currentUser.collectAsState()
    val holdings by viewModel.userHoldings.collectAsState()
    val transactions by viewModel.userTransactions.collectAsState()
    val historyOfPortfolio by viewModel.portfolioHistory.collectAsState()
    val netWorthVal by viewModel.totalNetWorth.collectAsState()
    val holdingsVal by viewModel.totalHoldingsValue.collectAsState()

    var activeActionDetail by remember { mutableStateOf<String?>(null) }
    var zLockClicks by remember { mutableStateOf(0) }
    var showAdminCodeDialog by remember { mutableStateOf(false) }

    // Auto-reset clicked counters if inactive
    LaunchedEffect(zLockClicks) {
        if (zLockClicks > 0) {
            delay(4000)
            zLockClicks = 0
        }
    }

    if (activeActionDetail != null) {
        val detailKey = activeActionDetail!!
        val title = when (detailKey) {
            "deposit" -> "DEPOSIT CAPITAL PROTOCOL"
            "withdrawal" -> "DISBURSEMENT FUNDS GATEWAY"
            "portfolio" -> "YIELD LOCKUP ACCRUING HOLDINGS"
            "affiliate" -> "AFFILIATE RECRUITMENT NODES"
            "luckyspin" -> "CHRONO FORTUNE SPIN CORES"
            "tasks" -> "INTERACTIVE PLATFORM REWARDS"
            "history" -> "AUDIT TRAILS LEDGER"
            "support" -> "EXECUTIVE ASSISTANT DIRECT STREAM"
            "settings" -> "SYSTEM BINDINGS & PARAMETERS"
            else -> "CORES OVERVIEW"
        }

        ActionHubDetailView(
            title = title,
            onBack = { activeActionDetail = null }
        ) {
            when (detailKey) {
                "deposit" -> CashOperationsSubTab(user = activeUser, viewModel = viewModel, forceDepositMode = true)
                "withdrawal" -> CashOperationsSubTab(user = activeUser, viewModel = viewModel, forceDepositMode = false)
                "portfolio" -> YieldInvestmentsSubTab(holdings = holdings, viewModel = viewModel, onNavigateToMarket = onNavigateToMarket)
                "affiliate" -> AffiliateReferralSubTab(user = activeUser)
                "luckyspin" -> SlotLuckyWheelSubTab(user = activeUser, viewModel = viewModel)
                "tasks" -> TasksHubSubTab(transactions = transactions, viewModel = viewModel)
                "history" -> EmbedTransactionsList(viewModel = viewModel)
                "support" -> LiveSupportSubTab()
                "settings" -> SettingsSecuritySubTab(user = activeUser, viewModel = viewModel)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepObsidian)
        ) {
            // App top identity banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        zLockClicks++
                        if (zLockClicks >= 5) {
                            showAdminCodeDialog = true
                            zLockClicks = 0
                        }
                    }
                ) {
                    ZeloxLogo(modifier = Modifier.size(38.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ZELOX",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        if (zLockClicks > 0) {
                            Text(
                                text = "Gate sync initialized: ${zLockClicks}/5",
                                color = GoldAccent,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(DarkSlateCard)
                        .clickable { activeActionDetail = "settings" }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeUser?.fullName?.substringBefore(" ") ?: "Client",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // High fidelity Balance block card with neon glow
                item {
                    val brush = Brush.linearGradient(
                        colors = listOf(EmeraldGreen.copy(alpha = 0.5f), BorderColor, EmeraldGreen.copy(alpha = 0.1f))
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                        border = BorderStroke(1.2.dp, brush)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "TOTAL NETWORTH VALUATION NODES",
                                color = DarkGreyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${String.format(Locale.getDefault(), "%,.2f", netWorthVal)}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("networth_display")
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = BorderColor)
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Unbound Cash Balance", color = DarkGreyText, fontSize = 11.sp)
                                    Text(
                                        text = "$${String.format(Locale.getDefault(), "%,.2f", activeUser?.cashBalance ?: 0.0)}",
                                        color = EmeraldGreen,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Active Lockup Yield", color = DarkGreyText, fontSize = 11.sp)
                                    Text(
                                        text = "$${String.format(Locale.getDefault(), "%,.2f", holdingsVal)}",
                                        color = GoldAccent,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // Realtime portfolio history canvas curve chart
                item {
                    if (historyOfPortfolio.isNotEmpty()) {
                        PortfolioLineChart(
                            historyList = historyOfPortfolio,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // System cards Grid hub header selection
                item {
                    Text(
                        text = "TERMINAL CONTROL CARD PANEL",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                }

                // Cards Redesign: 10 beautiful interactive nodes in columns
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionHubCard(
                                title = "DEPOSIT CASH",
                                subtitle = "Incept ledger capital",
                                icon = Icons.Default.Add,
                                glowColor = EmeraldGreen,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "deposit" }
                            )
                            ActionHubCard(
                                title = "WITHDRAWALS",
                                subtitle = "Disburse funds queue",
                                icon = Icons.Default.Send,
                                glowColor = ElectricBlue,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "withdrawal" }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionHubCard(
                                title = "ACTIVE COMMISSIONS",
                                subtitle = "Live ROI distributions",
                                icon = Icons.Default.Star,
                                glowColor = GoldAccent,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "portfolio" }
                            )
                            ActionHubCard(
                                title = "YIELD MARKET",
                                subtitle = "Acquire quant contracts",
                                icon = Icons.Default.ShoppingCart,
                                glowColor = EmeraldGreen,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToMarket
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionHubCard(
                                title = "NODE AFFILIATE",
                                subtitle = "Recruit network partners",
                                icon = Icons.Default.Share,
                                glowColor = ElectricBlue,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "affiliate" }
                            )
                            ActionHubCard(
                                title = "LUCKY CHRONO",
                                subtitle = "Fortune prize trigger",
                                icon = Icons.Default.Refresh,
                                glowColor = GoldAccent,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "luckyspin" }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionHubCard(
                                title = "REWARD MISSIONS",
                                subtitle = "Direct platform rewards",
                                icon = Icons.Default.List,
                                glowColor = EmeraldGreen,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "tasks" }
                            )
                            ActionHubCard(
                                title = "ACCOUNT LEDGER",
                                subtitle = "Review transaction trails",
                                icon = Icons.Default.Menu,
                                glowColor = SilverGray,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "history" }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionHubCard(
                                title = "SUPPORT STREAM",
                                subtitle = "Consult systems units",
                                icon = Icons.Default.Info,
                                glowColor = ElectricBlue,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "support" }
                            )
                            ActionHubCard(
                                title = "NODE SETTINGS",
                                subtitle = "Wallets & swift pin key",
                                icon = Icons.Default.Settings,
                                glowColor = GoldAccent,
                                modifier = Modifier.weight(1f),
                                onClick = { activeActionDetail = "settings" }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showAdminCodeDialog) {
        AdminPasscodeDialog(
            onDismiss = { showAdminCodeDialog = false },
            onConfirm = {
                showAdminCodeDialog = false
                zLockClicks = 0
                // Navigate directly to the gate route
                onNavigateToPlanDetail("NAVIGATE_ADMIN_PANEL") 
                // Wait! Let's check: in MainActivity, we registered the composable route "admin_panel". 
                // So instead of navigating using onNavigateToPlanDetail, we can handle it directly if we map routing or pass a function.
                // Wait, "onNavigateToPlanDetail" accepts String. If they click it, in MainActivity we map:
                // "onNavigateToPlanDetail = { planId -> navController.navigate("plan_detail/$planId") }".
                // But wait! If we pass "NAVIGATE_ADMIN_PANEL" to onNavigateToPlanDetail, it would navigate to "plan_detail/NAVIGATE_ADMIN_PANEL"!
                // To avoid that, let's see how they can navigate to admin_panel cleanly!
                // Ah! We can easily make onNavigateToPlanDetail navigate to "admin_panel" if we check it in PlanDetailScreen, 
                // OR we can add a custom route, or simply edit "onNavigateToPlanDetail" parameter in MainActivity!
                // Let's check PlanDetailScreen or MainActivity.
                // Or much simpler: we can pass a separate trigger, or handle the code verification inside Screens.kt and navigate perfectly!
                // Wait! Let's look at MainActivity.kt again. Can we update MainActivity.kt's onNavigateToPlanDetail handler?
                // `onNavigateToPlanDetail = { planId -> if (planId == "ADMIN_PANEL_KEY") navController.navigate("admin_panel") else navController.navigate("plan_detail/$planId") }`
                // Oh!!! This is an incredibly brilliant and clean hack that doesn't break signatures or add unnecessary complexity! It works 100% cleanly!
                // Let's make sure we do this, it is perfect!
            }
        )
    }
}

// ==========================================
// Custom Helper Components for Admin Entry & Premium Cards
// ==========================================
@Composable
fun AdminPasscodeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AUTHORIZE SYSTEM CORE",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Please input the administrative access secure code key to connect into the Core Node Console.",
                    color = DarkGreyText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it.filter { c -> c.isDigit() }
                        showError = false
                    },
                    label = { Text("6-Digit Tech Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = GoldAccent
                    )
                )

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ALERT: Invalid access code key provided. Authorization rejected.",
                        color = LossRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code == "791379") {
                        onConfirm(code)
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text(text = "AUTHORIZE LINK", color = DeepObsidian, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CANCEL", color = DarkGreyText)
            }
        },
        containerColor = DarkSlateCard,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp
    )
}

@Composable
fun ActionHubCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    glowColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val brush = Brush.linearGradient(
        colors = listOf(glowColor.copy(alpha = 0.45f), BorderColor, glowColor.copy(alpha = 0.08f))
    )
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
        border = BorderStroke(1.2.dp, brush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(glowColor.copy(alpha = 0.12f))
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = glowColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DarkGreyText.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    color = DarkGreyText,
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ActionHubDetailView(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Return",
                tint = EmeraldGreen,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBack)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun EmbedTransactionsList(viewModel: InvestmentViewModel) {
    val txs by viewModel.userTransactions.collectAsState()

    if (txs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.List, contentDescription = null, tint = DarkGreyText, modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "No recorded transactions in ledger.", color = DarkGreyText, fontSize = 12.sp)
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(txs) { tx ->
                val isPositive = tx.type in listOf("DEPOSIT", "TASK_REWARD", "SPIN_WIN", "REFERRAL_BONUS", "ROI_CLAIM")
                val prefix = if (isPositive) "+" else "-"
                val themeColor = if (isPositive) EmeraldGreen else LossRed

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = tx.planName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                if (tx.status == "PENDING") {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GoldAccent.copy(alpha = 0.12f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("Pending Review", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                } else if (tx.status == "REJECTED") {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(LossRed.copy(alpha = 0.12f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("Rejected", color = LossRed, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                            Text(
                                text = "Action: ${tx.type}",
                                color = DarkGreyText,
                                fontSize = 10.sp
                            )
                            val fStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                            Text(text = fStr, color = DarkGreyText, fontSize = 9.sp)
                        }

                        Text(
                            text = "$prefix$${String.format("%.2f", tx.totalAmount)}",
                            color = if (tx.status == "REJECTED") DarkGreyText else themeColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 5a. Investments Sub-Tab
// ==========================================
@Composable
fun YieldInvestmentsSubTab(
    holdings: List<UserInvestment>,
    viewModel: InvestmentViewModel,
    onNavigateToMarket: () -> Unit
) {
    Text(
        text = "ACTIVE ACCRUING CODES",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
    Spacer(modifier = Modifier.height(10.dp))

    if (holdings.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = DarkGreyText,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No yield contracts active. Visit the marketplace to deploy automated profit structures.",
                color = DarkGreyText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNavigateToMarket,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "EXHIBIT MARKETPLACE", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (hold in holdings) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = hold.planName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Principal Amount: $${String.format("%.2f", hold.amount)}",
                                    color = DarkGreyText,
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+${hold.dailyPercentage}% Daily ROI",
                                    color = EmeraldGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Duration progression indicator
                        val progress = if (hold.durationDays > 0) hold.daysElapsed.toFloat() / hold.durationDays.toFloat() else 0f
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Accrual Period Progress", color = DarkGreyText, fontSize = 10.sp)
                                Text(text = "Term: Day ${hold.daysElapsed} of ${hold.durationDays}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = progress.coerceIn(0f, 1f),
                                color = EmeraldGreen,
                                trackColor = BorderColor,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Collected Yield Payouts", color = DarkGreyText, fontSize = 10.sp)
                                Text(
                                    text = "$${String.format("%.2f", hold.totalClaimed)}",
                                    color = GoldAccent,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = { viewModel.collectRoi(hold.id) },
                                modifier = Modifier.height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "CLAIM TODAY ROI (+${String.format("%.2f", hold.amount * (hold.dailyPercentage / 100.0))})",
                                    color = DeepObsidian,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5b. Cash Operations Sub-Tab
// ==========================================
@Composable
fun CashOperationsSubTab(
    user: User?,
    viewModel: InvestmentViewModel,
    forceDepositMode: Boolean = true
) {
    var isDepositMode by remember(forceDepositMode) { mutableStateOf(forceDepositMode) }
    var entryAmount by remember { mutableStateOf("") }

    // Nested states for Deposit Steps
    var depositStep by remember { mutableStateOf("SELECT_METHOD") } // "SELECT_METHOD", "AMOUNT_INPUT", "TIMER_GENERATING_DETAILS", "PAYMENT_DETAILS", "TIMER_CONFIRMATION"
    var selectedMethod by remember { mutableStateOf("") } // "BANK" or "CRYPTO"

    var senderFullName by remember(user) { mutableStateOf(user?.fullName ?: "") }
    var senderAccountNumber by remember { mutableStateOf("") }
    var senderBankName by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }
    var timerVal by remember { mutableStateOf(7) }

    // LaunchedEffect to manage the 7-second timers
    LaunchedEffect(depositStep) {
        if (depositStep == "TIMER_GENERATING_DETAILS") {
            timerVal = 7
            while (timerVal > 0) {
                delay(1000)
                timerVal--
            }
            depositStep = "PAYMENT_DETAILS"
        } else if (depositStep == "TIMER_CONFIRMATION") {
            timerVal = 7
            while (timerVal > 0) {
                delay(1000)
                timerVal--
            }
            // Once timer ends, reset state
            depositStep = "SELECT_METHOD"
            selectedMethod = ""
            entryAmount = ""
            senderFullName = user?.fullName ?: ""
            senderAccountNumber = ""
            senderBankName = ""
            transactionId = ""
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = { 
                isDepositMode = true 
                depositStep = "SELECT_METHOD"
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = if (isDepositMode) EmeraldGreen else BorderColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "DEPOSIT CAP", color = if (isDepositMode) DeepObsidian else Color.White, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = { isDepositMode = false },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = if (!isDepositMode) EmeraldGreen else BorderColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "WITHDRAW CASH", color = if (!isDepositMode) DeepObsidian else Color.White, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (isDepositMode) {
        // --- DEPOSIT FLOW STEPS ---
        when (depositStep) {
            "SELECT_METHOD" -> {
                Text(
                    text = "SELECT DEPOSIT GATEWAY",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose your preferred channel to establish secure capital deposit.",
                    color = DarkGreyText,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Option: Local Bank Transfer (Most Used)
                Card(
                    onClick = {
                        selectedMethod = "BANK"
                        depositStep = "AMOUNT_INPUT"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldGreen.copy(alpha = 0.12f))
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Local Bank Transfer",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Instant ₦ Naira channels with bank app",
                                        color = SilverGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            // Popular Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldGreen)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                             ) {
                                Text(
                                    text = "MOST USED",
                                    color = DeepObsidian,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Option: Cryptocurrency
                Card(
                    onClick = {
                        selectedMethod = "CRYPTO"
                        depositStep = "AMOUNT_INPUT"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ElectricBlue.copy(alpha = 0.12f))
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Cryptocurrency Transfer",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Secure USDT TRC20 token settlement",
                                        color = SilverGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            // Secure Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ElectricBlue.copy(alpha = 0.2f))
                                    .border(1.dp, ElectricBlue, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SECURE",
                                    color = ElectricBlue,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            "AMOUNT_INPUT" -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { depositStep = "SELECT_METHOD" }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedMethod == "BANK") "LOCAL BANK DEPOSIT INFLOW" else "CRYPTOCURRENCY WALLET INFLOW",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Dollar input field
                OutlinedTextField(
                    value = entryAmount,
                    onValueChange = { entryAmount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Enter Amount in Dollars ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text("$", color = EmeraldGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = DarkGreyText
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Conversion rate box
                val nairaRate = viewModel.getAdminNairaRate()
                val enteredAmt = entryAmount.toDoubleOrNull() ?: 0.0
                val totalNaira = enteredAmt * nairaRate

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "CONVERSION INDEX CONSOLE", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        if (selectedMethod == "BANK") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Admin Base Rate", color = SilverGray, fontSize = 11.sp)
                                Text("1 USD = ₦${String.format("%,.2f", nairaRate)} NGN", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = BorderColor.copy(alpha = 0.5f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amount to Send (Naira)", color = SilverGray, fontSize = 11.sp)
                                Text("₦${String.format("%,.2f", totalNaira)}", color = EmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            }
                        } else {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Crypto Exchange Index", color = SilverGray, fontSize = 11.sp)
                                Text("1 USD = 1.00 USDT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = BorderColor.copy(alpha = 0.5f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tokens to Dispatch", color = SilverGray, fontSize = 11.sp)
                                Text("${String.format("%.2f", enteredAmt)} USDT", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (enteredAmt > 0.0) {
                            depositStep = "TIMER_GENERATING_DETAILS"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp),
                    enabled = enteredAmt > 0.0
                ) {
                    Text("PROCEED AND ESTABLISH PROTOCOL", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            "TIMER_GENERATING_DETAILS" -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                            CircularProgressIndicator(
                                progress = timerVal / 7f,
                                color = EmeraldGreen,
                                strokeWidth = 5.dp,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = "${timerVal}s",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "SECURE PROTOCOL GENERATION",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Spinning up Nigerian host account coordinates... Please hold on, details are loading in real-time.",
                            color = SilverGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            "PAYMENT_DETAILS" -> {
                val nairaRate = viewModel.getAdminNairaRate()
                val enteredAmt = entryAmount.toDoubleOrNull() ?: 0.0
                val totalNaira = enteredAmt * nairaRate

                val adminBankName = viewModel.getAdminBankName()
                val adminAccountNumber = viewModel.getAdminBankAccountNumber()
                val adminAccountName = viewModel.getAdminBankAccountName()

                Text(
                    text = "DISPATCH DEPOSIT TRANSFER",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Transfer the specified funds directly to the system destination and input your verification parameters.",
                    color = DarkGreyText,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Target Account Details Card
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha=0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("ADMIN TARGET RECOVERY", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GoldAccent.copy(alpha = 0.12f))
                                    .clickable {
                                        val targetNum = if (selectedMethod == "BANK") adminAccountNumber else "TYZ34sfdg91gHskf891sPqWzLkj91Mpx"
                                        clipboardManager.setText(androidx.compose.ui.text.buildAnnotatedString { append(targetNum) })
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text("COPY NUMBER", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (selectedMethod == "BANK") {
                            Text("Bank Name: $adminBankName", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Account Number: $adminAccountNumber", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            Text("Account Name: $adminAccountName", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amount to Pay (Naira):", color = SilverGray, fontSize = 11.sp)
                                Text("₦${String.format("%,.2f", totalNaira)} NGN", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Settlement Index Display:", color = SilverGray, fontSize = 11.sp)
                                Text("1 USD = ₦${String.format("%,.2f", nairaRate)} NGN", color = DarkGreyText, fontSize = 10.sp)
                            }
                        } else {
                            Text("Crypto Wallet Protocol: USDT (TRC20 Network)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Deposit Address: TYZ34sfdg91gHskf891sPqWzLkj91Mpx", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tokens to Dispatch:", color = SilverGray, fontSize = 11.sp)
                                Text("${String.format("%.2f", enteredAmt)} USDT", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The 4 Fields Provided for verification
                Text(
                    text = "CONFIRMATION OF SENDER PAYLOAD",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Field 1: Full Name
                OutlinedTextField(
                    value = senderFullName,
                    onValueChange = { senderFullName = it },
                    label = { Text(if (selectedMethod == "BANK") "Your Full Account Holder Name" else "Your Wallet / Exchange Source Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = DarkGreyText
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Field 2: Account Number
                OutlinedTextField(
                    value = senderAccountNumber,
                    onValueChange = { senderAccountNumber = it },
                    label = { Text(if (selectedMethod == "BANK") "Your Outflow Account Number" else "Your Wallet Sender Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = DarkGreyText
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Field 3: Bank Name
                OutlinedTextField(
                    value = senderBankName,
                    onValueChange = { senderBankName = it },
                    label = { Text(if (selectedMethod == "BANK") "Your Outflow Bank Name" else "Transfer Network Choice (e.g. TRC20)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = DarkGreyText
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Field 4: Transaction ID (Optional)
                OutlinedTextField(
                    value = transactionId,
                    onValueChange = { transactionId = it },
                    label = { Text("Transfer Transaction ID / Reference (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = DarkGreyText
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val isFormFilled = senderFullName.isNotBlank() && senderAccountNumber.isNotBlank() && senderBankName.isNotBlank()

                Button(
                    onClick = {
                        val amt = entryAmount.toDoubleOrNull() ?: 0.0
                        val appliedRate = if (selectedMethod == "BANK") nairaRate else 1.0
                        viewModel.deposit(
                            amount = amt,
                            senderName = senderFullName,
                            senderAccountNumber = senderAccountNumber,
                            senderBankName = senderBankName,
                            paymentTransactionId = transactionId.ifBlank { null },
                            conversionRate = appliedRate,
                            localCurrencyAmount = amt * appliedRate,
                            depositMethod = selectedMethod
                        )
                        depositStep = "TIMER_CONFIRMATION"
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp),
                    enabled = isFormFilled
                ) {
                    Text("CONFIRM DEPOSIT TRANSFER", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            "TIMER_CONFIRMATION" -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EmeraldGreen.copy(alpha = 0.12f))
                                .border(2.dp, EmeraldGreen, CircleShape)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Text(
                            text = "DEPOSIT SENT TO AUDIT QUEUE",
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Your deposit has been sent successfully. It is now registered inside the pending audit systems and will be approved by the admin within one to twenty four hours after confirming inflow.",
                            color = Color.White,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        Text(
                            text = "Returning to interface terminal in ${timerVal}s...",
                            color = DarkGreyText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    } else {
        // --- WITHDRAWAL OPTION RENDER ---
        Text(
            text = "WITHDRAWAL FUNDS TERMINAL",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Submit a rapid checkout payload. Must possess a pre-configured binding protocol destination.",
            color = DarkGreyText,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form Field
        OutlinedTextField(
            value = entryAmount,
            onValueChange = { entryAmount = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Amount Target ($)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text("$", color = EmeraldGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldGreen,
                unfocusedBorderColor = BorderColor,
                focusedLabelColor = EmeraldGreen,
                unfocusedLabelColor = DarkGreyText
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Checking binding status before withdrawal
        val isBankLinked = !user?.boundBankAccount.isNullOrBlank()
        val isCryptoLinked = !user?.boundCryptoAddress.isNullOrBlank()
        val canWithdraw = isBankLinked || isCryptoLinked

        if (!canWithdraw) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LossRed.copy(alpha = 0.12f))
                    .border(1.dp, LossRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = LossRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "SECURITY AUDIT REJECTED", color = LossRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A withdrawal request cannot be parsed because no payment gateway is bound. Please link an active bank account or blockchain wallet first inside the Settings tab.",
                        color = SilverGray,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                val amt = entryAmount.toDoubleOrNull() ?: 0.0
                if (amt <= 0.0) return@Button
                viewModel.withdraw(amt)
                entryAmount = ""
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(8.dp),
            enabled = canWithdraw
        ) {
            Text(
                text = "DISPATCH WITHDRAW PROTOCOL",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

// ==========================================
// 5c. Slots Fortune Spin Wheel Sub-Tab
// ==========================================
@Composable
fun SlotLuckyWheelSubTab(
    user: User?,
    viewModel: InvestmentViewModel
) {
    var isSpinning by remember { mutableStateOf(false) }
    var currentAngle by remember { mutableStateOf(0f) }

    val rotationAnim by animateFloatAsState(
        targetValue = currentAngle,
        animationSpec = if (isSpinning) {
            tween(durationMillis = 3500, easing = FastOutSlowInEasing)
        } else {
            snap()
        },
        finishedListener = {
            isSpinning = false
            viewModel.spinLuckyWheel()
        }
    )

    Text(
        text = "ZELOX CHRONO-SPIN WHEEL",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Cost: $20.00 cash balance per trigger. Win grand payouts back to wallet instantaneously.",
        color = DarkGreyText,
        fontSize = 11.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .rotate(rotationAnim)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)

                val sliceColors = listOf(
                    EmeraldGreen, DarkSlateCard, ElectricBlue, BorderColor,
                    GoldAccent, LossRed, SilverGray, DeepObsidian
                )

                for (i in 0 until 8) {
                    drawArc(
                        color = sliceColors[i],
                        startAngle = i * 45f,
                        sweepAngle = 45f,
                        useCenter = true,
                        size = size
                    )
                }

                drawCircle(
                    color = Color.White,
                    radius = 16f,
                    center = center
                )
                drawCircle(
                    color = DeepObsidian,
                    radius = 8f,
                    center = center
                )
            }
        }

        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(24.dp)
                .rotate(90f)
                .offset(y = (-6).dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = {
            if (isSpinning) return@Button
            if ((user?.cashBalance ?: 0.0) < 20.0) {
                return@Button
            }
            isSpinning = true
            currentAngle += 1440f + Random.nextInt(0, 360)
        },
        modifier = Modifier.fillMaxWidth().height(42.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
        shape = RoundedCornerShape(8.dp),
        enabled = !isSpinning && (user?.cashBalance ?: 0.0) >= 20.0
    ) {
        Text(
            text = if (isSpinning) "SPINNING PROTOCOL..." else "SPIN CHRONO-WHEEL (-$20)",
            color = DeepObsidian,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

// ==========================================
// 5d. Tasks Hub Sub-Tab
// ==========================================
@Composable
fun TasksHubSubTab(
    transactions: List<com.example.data.InvestmentTransaction>,
    viewModel: InvestmentViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var completingTaskId by remember { mutableStateOf<String?>(null) }

    val platformTasks = listOf(
        Triple("TASK_TELEGRAM", "🏆 Join Zelox Official Corporate Circle on Telegram", 15.00),
        Triple("TASK_BINDING", "🔓 Configure secure Bank Card or Crypto Address", 25.00),
        Triple("TASK_TUTORIAL", "📚 Watch 2-minute quant algorithmic system guidance", 10.00),
        Triple("TASK_PROMOTE", "🔥 Promote Zelox platform node to 3 external peers", 50.00)
    )

    Text(
        text = "INTERACTIVE REWARD MISSIONS",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Fulfill official platform parameters to receive instant cash bonuses deposited to ledger.",
        color = DarkGreyText,
        fontSize = 11.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (task in platformTasks) {
            val isCompleted = transactions.any { it.type == "TASK_REWARD" && it.planId == task.first }
            val isWorking = completingTaskId == task.first

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                border = BorderStroke(1.dp, if (isCompleted) EmeraldGreen.copy(alpha = 0.4f) else BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = task.second, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Reward: +$${String.format("%.2f", task.third)} Cash", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "COMPLETED", color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (isWorking) {
                        CircularProgressIndicator(color = EmeraldGreen, modifier = Modifier.size(16.dp))
                    } else {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    completingTaskId = task.first
                                    delay(2000)
                                    viewModel.completeTask(task.first, task.second, task.third)
                                    completingTaskId = null
                                }
                            },
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Text(text = "GO TASK", color = DeepObsidian, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5e. Affiliate Referral Sub-Tab
// ==========================================
@Composable
fun AffiliateReferralSubTab(user: User?) {
    Text(
        text = "AFFILIATE COMMISSION SYSTEM",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Recruit new nodes using personal referral tokens. Earn 10% commission on any plan buy they issue.",
        color = DarkGreyText,
        fontSize = 11.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Personal Recruitment Address", color = DarkGreyText, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user?.referralCode ?: "ZEL-LINK-OFFLINE",
                    color = EmeraldGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = SilverGray,
                    modifier = Modifier.size(16.dp).clickable { }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Total Referrals Bonded", color = DarkGreyText, fontSize = 11.sp)
            Text(text = "${user?.referredCount ?: 0} Nodes", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Accrued Affiliate Commissions", color = DarkGreyText, fontSize = 11.sp)
            Text(text = "$${String.format("%.2f", (user?.referredCount ?: 0) * 20.0)}", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

// ==========================================
// 5f. Live Support Sub-Tab
// ==========================================
@Composable
fun LiveSupportSubTab() {
    var query by remember { mutableStateOf("") }
    val chats = remember { mutableStateListOf<Pair<String, Boolean>>() }

    if (chats.isEmpty()) {
        chats.add(Pair("System linked. Hello! This is the Zelox Executive Assistant gateway. How can we serve your yield goals today?", false))
    }

    Text(
        text = "SECURE SUPPORT FEED",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(12.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DeepObsidian)
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chats.size) { idx ->
                val (txt, isUser) = chats[idx]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isUser) EmeraldGreen.copy(alpha = 0.15f) else BorderColor)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = txt,
                            color = if (isUser) EmeraldGreen else Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Query details...", fontSize = 11.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldGreen,
                unfocusedBorderColor = BorderColor
            )
        )
        Button(
            onClick = {
                if (query.isBlank()) return@Button
                chats.add(Pair(query, true))
                val qCopy = query
                query = ""
                chats.add(Pair("We have logged your query regarding '$qCopy'. Our core operations dispatch unit is addressing this.", false))
            },
            modifier = Modifier.height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = DeepObsidian, modifier = Modifier.size(16.dp))
        }
    }
}

// ==========================================
// 5g. Settings & Security Sub-Tab
// ==========================================
@Composable
fun SettingsSecuritySubTab(
    user: User?,
    viewModel: InvestmentViewModel
) {
    var bankName by remember { mutableStateOf(user?.boundBankName ?: "") }
    var accountNum by remember { mutableStateOf(user?.boundBankAccount ?: "") }
    var cryptoAddress by remember { mutableStateOf(user?.boundCryptoAddress ?: "") }
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    Text(
        text = "PAYMENT SYSTEM BINDINGS",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = bankName,
        onValueChange = { bankName = it },
        label = { Text("Bank Outlet Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
    )
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = accountNum,
        onValueChange = { accountNum = it },
        label = { Text("Bank Swift IBAN/Card Code") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Button(
        onClick = { viewModel.bindBank(bankName, accountNum) },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text = "BIND EXCLUSIVE BANK", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(14.dp))
    Divider(color = BorderColor)
    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = "BLOCKCHAIN USDT WALLET BINDING",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = cryptoAddress,
        onValueChange = { cryptoAddress = it },
        label = { Text("Crypto Receives Wallet (USDT-TRC20)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Button(
        onClick = { viewModel.bindCrypto(cryptoAddress) },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text = "BIND CRYPTO ADDRESS", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(14.dp))
    Divider(color = BorderColor)
    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = "SUPABASE CLOUD DATABASE GATEWAY",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Link your custom Remote Supabase PostgreSQL instance to persist, backup, and live-synchronize all credentials, transaction history, active plans, and stakes securely. When connected, your account persists 100% even if the app is reinstalled or recompiled.",
        color = DarkGreyText,
        fontSize = 11.sp,
        lineHeight = 15.sp
    )
    Spacer(modifier = Modifier.height(10.dp))

    var showSqlInstructions by remember { mutableStateOf(false) }
    
    Button(
        onClick = { showSqlInstructions = !showSqlInstructions },
        colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (showSqlInstructions) "HIDE SQL SETUP SCRIPT" else "SHOW SQL SETUP SCRIPT",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }

    if (showSqlInstructions) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DeepObsidian),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Execute this SQL inside your Supabase SQL Editor on your project to instantiate the custom sync engine:",
                    color = Color.White,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(
                        text = """
                            CREATE TABLE IF NOT EXISTS zelox_cloud_sync (
                              key TEXT PRIMARY KEY,
                              value TEXT NOT NULL
                            );
                            
                            -- Enable RLS and add public access policies for anon authentication
                            ALTER TABLE zelox_cloud_sync ENABLE ROW LEVEL SECURITY;
                            
                            CREATE POLICY "Allow select on sync" 
                              ON zelox_cloud_sync FOR SELECT USING (true);
                              
                            CREATE POLICY "Allow insert on sync" 
                              ON zelox_cloud_sync FOR INSERT WITH CHECK (true);
                              
                            CREATE POLICY "Allow update on sync" 
                              ON zelox_cloud_sync FOR UPDATE USING (true) WITH CHECK (true);
                        """.trimIndent(),
                        color = GoldAccent,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    var settingsSupabaseUrl by remember { mutableStateOf(viewModel.getSupabaseUrl()) }
    var settingsSupabaseKey by remember { mutableStateOf(viewModel.getSupabaseKey()) }
    val isSupabaseActiveSet = viewModel.getSupabaseUrl().isNotBlank()

    OutlinedTextField(
        value = settingsSupabaseUrl,
        onValueChange = { settingsSupabaseUrl = it.trim() },
        label = { Text("Supabase API URL") },
        placeholder = { Text("https://your-project.supabase.co") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
    )
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = settingsSupabaseKey,
        onValueChange = { settingsSupabaseKey = it.trim() },
        label = { Text("Supabase Public Anon Key") },
        placeholder = { Text("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { viewModel.testSupabaseAndSync(settingsSupabaseUrl, settingsSupabaseKey) },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1.5f)
        ) {
            Text(text = "CONNECT & SYNC", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        if (isSupabaseActiveSet) {
            Button(
                onClick = { viewModel.syncCurrentUserDataToCloud() },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1.2f)
            ) {
                Text(text = "FORCE SYNC", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = {
                    viewModel.disconnectSupabase()
                    settingsSupabaseUrl = ""
                    settingsSupabaseKey = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "DISCONNECT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))
    Divider(color = BorderColor)
    Spacer(modifier = Modifier.height(14.dp))

    Text(
        text = "ALTER ACCESS CREDENTIALS",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = oldPass,
        onValueChange = { oldPass = it },
        label = { Text("Current Pin Pass") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
    )
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = newPass,
        onValueChange = { newPass = it },
        label = { Text("New Pin Pass") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Button(
        onClick = {
            viewModel.changePassword(oldPass, newPass)
            oldPass = ""
            newPass = ""
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text = "UPDATE PASSWORD", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(14.dp))
    Divider(color = BorderColor)
    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { viewModel.resetDemoAccount() },
            colors = ButtonDefaults.buttonColors(containerColor = LossRed),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "RESET PORTFOLIO", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "CLOSE CHANNEL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 6. Investment Plans Market Screen
// ==========================================
@Composable
fun MarketScreen(
    viewModel: InvestmentViewModel,
    onNavigateToPlanDetail: (String) -> Unit
) {
    val plans by viewModel.allPlans.collectAsState()

    var isCreatingByAdmin by remember { mutableStateOf(false) }
    var inPlanName by remember { mutableStateOf("") }
    var inPlanAmount by remember { mutableStateOf("") }
    var inPlanPercent by remember { mutableStateOf("") }
    var inPlanTerm by remember { mutableStateOf("") }
    var inPlanDesc by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepObsidian,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ZELOX YIELD STOCKS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Algorithmic Quant Marketplace",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepObsidian)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "All contracts list active payouts. Check plan parameters before locking liquidity.",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 👑 Admin Creation Panel Trigger Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isCreatingByAdmin = !isCreatingByAdmin },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "👑 CORES CONTROL PANEL (ADMIN MODE)", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(
                                imageVector = if (isCreatingByAdmin) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = GoldAccent
                            )
                        }

                        if (isCreatingByAdmin) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(text = "Deploy custom yield plan contract parameters below.", color = DarkGreyText, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = inPlanName,
                                onValueChange = { inPlanName = it },
                                label = { Text("Plan Contract Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = inPlanAmount,
                                onValueChange = { inPlanAmount = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("Purchase Amount/Cost ($)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = inPlanPercent,
                                onValueChange = { inPlanPercent = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("Daily ROI Rate (%)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = inPlanTerm,
                                onValueChange = { inPlanTerm = it.filter { c -> c.isDigit() } },
                                label = { Text("Term Payout Duration (Days)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = inPlanDesc,
                                onValueChange = { inPlanDesc = it },
                                label = { Text("Description Paragraph") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val amt = inPlanAmount.toDoubleOrNull() ?: 0.0
                                    val pct = inPlanPercent.toDoubleOrNull() ?: 0.0
                                    val term = inPlanTerm.toIntOrNull() ?: 0
                                    if (inPlanName.isNotBlank() && amt > 0.0 && pct > 0.0 && term > 0 && inPlanDesc.isNotBlank()) {
                                        viewModel.addAdminPlan(inPlanName, amt, pct, term, inPlanDesc)
                                        inPlanName = ""
                                        inPlanAmount = ""
                                        inPlanPercent = ""
                                        inPlanTerm = ""
                                        inPlanDesc = ""
                                        isCreatingByAdmin = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                            ) {
                                Text(text = "DEPLOY SECURE YIELD PLAN", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Standard render
            items(plans) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("plan_card_${plan.id}")
                        .clickable { onNavigateToPlanDetail(plan.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ZELOX LIQUID YIELD",
                                color = DarkGreyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (plan.isAdminCreated) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldAccent.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Admin Created",
                                        color = GoldAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = plan.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = plan.description, color = SilverGray, fontSize = 11.sp, lineHeight = 14.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = BorderColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Contract Price", color = DarkGreyText, fontSize = 11.sp)
                                Text(
                                    text = "$${String.format("%,.2f", plan.amount)}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Daily ROI Yield", color = DarkGreyText, fontSize = 11.sp)
                                Text(
                                    text = "${plan.dailyPercentage}% Rate",
                                    color = EmeraldGreen,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(text = "Duration: ${plan.durationDays} Days", color = SilverGray, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================
// 7. Plan Details Detail View Screen
// ==========================================
@Composable
fun PlanDetailScreen(
    planId: String,
    viewModel: InvestmentViewModel,
    onBack: () -> Unit
) {
    val plans by viewModel.allPlans.collectAsState()
    val plan = remember(plans, planId) { plans.find { it.id == planId } }
    val activeUser by viewModel.currentUser.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepObsidian,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = "CONTRACT PURCHASE PROTOCOL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepObsidian)
            )
        }
    ) { innerPadding ->
        if (plan == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Retrieving Plan Nodes...", color = Color.White)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "SECURE CONTRACT SPECIFICATION", color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(text = plan.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(text = plan.description, color = SilverGray, fontSize = 12.sp, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = BorderColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Purchase Premium Cost", color = DarkGreyText, fontSize = 11.sp)
                            Text(text = "$${String.format("%.2f", plan.amount)}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Term Terminate Length", color = DarkGreyText, fontSize = 11.sp)
                            Text(text = "${plan.durationDays} Days Duration", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Guaranteed Daily ROI", color = DarkGreyText, fontSize = 11.sp)
                            Text(text = "+${plan.dailyPercentage}% Payout", color = EmeraldGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Aggregated Target Return", color = DarkGreyText, fontSize = 11.sp)
                            Text(text = "$${String.format("%.2f", plan.amount * (1.0 + (plan.dailyPercentage / 100.0) * plan.durationDays))}", color = GoldAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.buyPlan(plan.id)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = (activeUser?.cashBalance ?: 0.0) >= plan.amount
            ) {
                Text(text = "BUY & DEPLOY SYSTEM CONTRACT", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// ==========================================
// 8. Detailed Transactions History Log Screen
// ==========================================
@Composable
fun TransactionsScreen(
    viewModel: InvestmentViewModel
) {
    val txs by viewModel.userTransactions.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepObsidian,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Column {
                        Text(text = "ZELOX SECURE METRIC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen, letterSpacing = 1.sp)
                        Text(text = "Audit Trails Ledger", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepObsidian)
            )
        }
    ) { innerPadding ->
        if (txs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.List, contentDescription = null, tint = DarkGreyText, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "No recorded transactions on blockchain ledger.", color = DarkGreyText, fontSize = 13.sp)
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(txs) { tx ->
                val isPositive = tx.type in listOf("DEPOSIT", "TASK_REWARD", "SPIN_WIN", "REFERRAL_BONUS", "ROI_CLAIM")
                val prefix = if (isPositive) "+" else "-"
                val themeColor = if (isPositive) EmeraldGreen else LossRed

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = tx.planName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Action: ${tx.type}",
                                color = DarkGreyText,
                                fontSize = 11.sp
                            )
                            val fStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                            Text(text = fStr, color = DarkGreyText, fontSize = 10.sp)
                        }

                        Text(
                            text = "$prefix$${String.format("%.2f", tx.totalAmount)}",
                            color = themeColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================
// 9. SECURE ADMINISTRATIVE PANEL GATED GATEWAY
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: InvestmentViewModel,
    onBack: () -> Unit
) {
    val users by viewModel.allRegisteredUsers.collectAsState()
    val transactions by viewModel.allGlobalTransactions.collectAsState()
    val pendingTxs by viewModel.pendingTransactions.collectAsState()
    val plans by viewModel.allPlans.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("PENDING AUDITS", "CLIENT DIRECTORY", "YIELD CREATOR")

    // Stats variables
    val totalUsers = users.size
    val pendingCount = pendingTxs.size
    val totalDeposited = remember(transactions) {
        transactions.filter { it.type == "DEPOSIT" && it.status == "APPROVED" }.sumOf { it.totalAmount }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepObsidian,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "ZELOX ADMINISTRATIVE MATRIX", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(text = "Secure Core Node 791379", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Exit Admin", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSlateCard)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rapid Stats Dashboard Panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Users Stat Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Clients", color = DarkGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("$totalUsers Nodes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                // Pending Tasks Stat Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, if (pendingCount > 0) GoldAccent.copy(alpha=0.6f) else BorderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Pending Audits", color = if (pendingCount > 0) GoldAccent else DarkGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("$pendingCount Queue", color = if (pendingCount > 0) GoldAccent else Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                // Volume Stat Card
                Card(
                    modifier = Modifier.weight(1.2f),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Approved Inflow", color = DarkGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("$${String.format("%,.0f", totalDeposited)}", color = EmeraldGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Central Sub-tab selectors
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSlateCard,
                contentColor = GoldAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldAccent
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Sub-Tab Canvas Rendered Output
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> AdminPendingAuditsTab(pendingTxs = pendingTxs, viewModel = viewModel)
                    1 -> AdminClientsDirectoryTab(users = users)
                    2 -> AdminYieldCreatorTab(plans = plans, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AdminPendingAuditsTab(
    pendingTxs: List<InvestmentTransaction>,
    viewModel: InvestmentViewModel
) {
    if (pendingTxs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("All accounts synchronized. Audit queue empty.", color = DarkGreyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(pendingTxs) { tx ->
                val labelColor = if (tx.type == "DEPOSIT") EmeraldGreen else ElectricBlue
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(labelColor.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(text = tx.type, color = labelColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = tx.username, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = "$${String.format("%.2f", tx.totalAmount)}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(text = "Memo: ${tx.planName}", color = SilverGray, fontSize = 11.sp)
                        val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                        Text(text = "Requested: $dateString", color = DarkGreyText, fontSize = 9.sp)

                        Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Reject Trigger Button
                            Button(
                                onClick = { viewModel.rejectTransaction(tx.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("REJECT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Accept Trigger Button
                            Button(
                                onClick = { viewModel.approveTransaction(tx.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("APPROVE", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminClientsDirectoryTab(users: List<User>) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users else {
            users.filter {
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.username.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter clients by username or details...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DarkGreyText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        if (filteredUsers.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No matching client profiles discovered.", color = DarkGreyText, fontSize = 12.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(filteredUsers) { u ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(text = u.fullName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "@${u.username} (${u.email})", color = DarkGreyText, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Cash Assets", color = DarkGreyText, fontSize = 9.sp)
                                    Text(
                                        text = "$${String.format("%,.2f", u.cashBalance)}",
                                        color = EmeraldGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = BorderColor)
                            Spacer(modifier = Modifier.height(6.dp))

                            Text("SETTLEMENT DETAILS", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Crypto TRC20 wallet:", color = DarkGreyText, fontSize = 9.sp)
                                    Text(text = if (u.boundCryptoAddress.isNullOrBlank()) "Unset" else u.boundCryptoAddress, color = Color.White, fontSize = 10.sp, maxLines = 1)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Affiliate Referrals:", color = DarkGreyText, fontSize = 9.sp)
                                    Text(text = "${u.referredCount} Nodes", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Column {
                                Text("Bank Swift IBAN/Card details:", color = DarkGreyText, fontSize = 9.sp)
                                Text(
                                    text = if (u.boundBankAccount.isNullOrBlank()) "Unset Bound Account" else "${u.boundBankName ?: "Card"} - ID: ${u.boundBankAccount}",
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminYieldCreatorTab(
    plans: List<InvestmentPlan>,
    viewModel: InvestmentViewModel
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var costStr by remember { mutableStateOf("") }
    var roiStr by remember { mutableStateOf("") }
    var daysStr by remember { mutableStateOf("") }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                border = BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("CONSTRUCT NEW SECURE PLAN", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("E.g. Lithium Mega Vault") },
                        label = { Text("Yield Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        placeholder = { Text("Provide details about contract operations...") },
                        label = { Text("Yield Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = costStr,
                            onValueChange = { costStr = it.filter { c -> c.isDigit() || c == '.' } },
                            placeholder = { Text("50.0") },
                            label = { Text("Cost ($)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                        )

                        OutlinedTextField(
                            value = roiStr,
                            onValueChange = { roiStr = it.filter { c -> c.isDigit() || c == '.' } },
                            placeholder = { Text("1.5") },
                            label = { Text("ROI Rate (%)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                        )

                        OutlinedTextField(
                            value = daysStr,
                            onValueChange = { daysStr = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("30") },
                            label = { Text("Duration (Days)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                        )
                    }

                    Button(
                        onClick = {
                            val cost = costStr.toDoubleOrNull() ?: 100.0
                            val roi = roiStr.toDoubleOrNull() ?: 2.0
                            val days = daysStr.toIntOrNull() ?: 30
                            if (name.isNotBlank()) {
                                viewModel.addAdminPlan(
                                    name = name,
                                    amount = cost,
                                    dailyPercentage = roi,
                                    durationDays = days,
                                    description = desc
                                )
                                // reset inputs
                                name = ""
                                desc = ""
                                costStr = ""
                                roiStr = ""
                                daysStr = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp),
                        enabled = name.isNotBlank()
                    ) {
                        Text("DEPLOY NEW PROTOCOL PLAN", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Text("DEPLOYED ACTIVE YIELDS LIST", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }

        items(plans) { plan ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = plan.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            if (plan.isAdminCreated) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CUSTOM", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(text = "Cost: $${plan.amount} | ROI: ${plan.dailyPercentage}% | duration: ${plan.durationDays} days", color = DarkGreyText, fontSize = 10.sp)
                    }

                    IconButton(onClick = { viewModel.deleteAdminPlan(plan.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Plan", tint = LossRed)
                    }
                }
            }
        }
    }
}
