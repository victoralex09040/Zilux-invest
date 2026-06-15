package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    // Pattern Lock challenge states
    var showPatternChallenge by remember { mutableStateOf(false) }
    var currentChallengeRound by remember { mutableStateOf(1) } // 1, 2, 3
    var challengeSequence by remember { mutableStateOf(listOf<Int>()) } 
    var userTappedSequence by remember { mutableStateOf(listOf<Int>()) }
    var challengeFeedbackText by remember { mutableStateOf("Tap sequence of the generated code.") }
    var challengeIsError by remember { mutableStateOf(false) }

    val authState by viewModel.signUpState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignUpSuccess()
            viewModel.clearAuthStates()
        }
    }

    // Pattern Challenge Secure overlay dialog
    if (showPatternChallenge) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showPatternChallenge = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                border = BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "9-DOT SECURITY ROUTING",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Solve sequence path to authorize ledger deployment.",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Round Counter Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldAccent.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ROUND STATE: $currentChallengeRound OF 3",
                            color = GoldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Challenge sequence display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        border = BorderStroke(0.5.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "TAP CHALLENGE PATTERN",
                                color = DarkGreyText,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                challengeSequence.forEachIndexed { idx, value ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen.copy(alpha = 0.15f))
                                            .border(1.dp, EmeraldGreen, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = value.toString(),
                                            color = EmeraldGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (idx < challengeSequence.lastIndex) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = DarkGreyText,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // User current input display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "YOUR INPUT GRID PATH",
                            color = DarkGreyText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(26.dp)
                        ) {
                            if (userTappedSequence.isEmpty()) {
                                Text("[ Waiting for input... ]", color = DarkGreyText, fontSize = 10.sp)
                            } else {
                                userTappedSequence.forEachIndexed { idx, value ->
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(GoldAccent.copy(alpha = 0.12f))
                                            .border(1.dp, GoldAccent, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = value.toString(),
                                            color = GoldAccent,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (idx < userTappedSequence.lastIndex) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = DarkGreyText,
                                            modifier = Modifier.size(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Feedback Message text display
                    Text(
                        text = challengeFeedbackText,
                        color = if (challengeIsError) LossRed else EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )

                    // Interactive 9-Dot Visual Pattern Buttons
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (row in 0 until 3) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                for (col in 0 until 3) {
                                    val dotValue = row * 3 + col + 1
                                    val isDotSelectedInUserPath = userTappedSequence.contains(dotValue)
                                    
                                    val ringColor = when {
                                        isDotSelectedInUserPath -> GoldAccent
                                        else -> BorderColor
                                    }
                                    val interiorBg = when {
                                        isDotSelectedInUserPath -> GoldAccent.copy(alpha = 0.2f)
                                        else -> Color.Transparent
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(interiorBg)
                                            .border(1.2.dp, ringColor, CircleShape)
                                            .clickable {
                                                if (userTappedSequence.size < 5) {
                                                    // Append clicked dot
                                                    val newSeq = userTappedSequence + dotValue
                                                    userTappedSequence = newSeq
                                                    challengeIsError = false
                                                    challengeFeedbackText = "Registering node $dotValue. Keep entering sequence..."

                                                    if (newSeq.size == 5) {
                                                        // Evaluate complete match
                                                        if (newSeq == challengeSequence) {
                                                            if (currentChallengeRound < 3) {
                                                                val oldRound = currentChallengeRound
                                                                currentChallengeRound++
                                                                challengeSequence = (1..9).shuffled().take(5)
                                                                userTappedSequence = emptyList()
                                                                challengeFeedbackText = "PASS ROUND $oldRound! Proceeding to Round $currentChallengeRound"
                                                                challengeIsError = false
                                                            } else {
                                                                // Passed all 3 rounds successfully!
                                                                showPatternChallenge = false
                                                                viewModel.signUp(
                                                                    username = username,
                                                                    passwordRaw = password,
                                                                    confirmPasswordRaw = confirmPassword,
                                                                    fullName = fullName,
                                                                    email = email,
                                                                    agreedToTerms = agreedToTerms,
                                                                    referralCodeUsed = referralCode.ifBlank { null }
                                                                )
                                                            }
                                                        } else {
                                                            // Invalid entry path
                                                            challengeFeedbackText = "Path mismatched. Restart sequence of Round $currentChallengeRound!"
                                                            challengeIsError = true
                                                            userTappedSequence = emptyList()
                                                        }
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dotValue.toString(),
                                            color = if (isDotSelectedInUserPath) GoldAccent else Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom helper actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                userTappedSequence = emptyList()
                                challengeFeedbackText = "Sequence path cleared. Please input from the start."
                                challengeIsError = false
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BorderColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("RESET CURRENT", color = Color.White, fontSize = 9.sp)
                        }

                        Button(
                            onClick = { showPatternChallenge = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, LossRed.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("ABORT SIGNUP", color = LossRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
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
                            val validationError = when {
                                fullName.isBlank() -> "Please enter your Full Identification Name."
                                email.isBlank() || !email.contains("@") -> "Please enter a valid Email Address Protocol."
                                username.isBlank() -> "Please enter a System Link Username."
                                password.isBlank() -> "Please enter a Password Vault Token."
                                password != confirmPassword -> "Passwords do not match. Please re-type your secure password."
                                !agreedToTerms -> "You must agree to the Zelox terms of service to deploy a new node."
                                else -> null
                            }

                            if (validationError != null) {
                                viewModel.setSignUpError(validationError)
                            } else {
                                // Trigger the Pattern Challenge Security Protocol
                                currentChallengeRound = 1
                                challengeSequence = (1..9).shuffled().take(5)
                                userTappedSequence = emptyList()
                                challengeFeedbackText = "Input the challenge sequence displayed above."
                                challengeIsError = false
                                showPatternChallenge = true
                            }
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
            "portfolio" -> "ACTIVE PORTFOLIO ASSETS HOLDINGS"
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
                "deposit" -> CashOperationsSubTab(user = activeUser, viewModel = viewModel, forceDepositMode = true, onNavigateToTab = { activeActionDetail = it })
                "withdrawal" -> CashOperationsSubTab(user = activeUser, viewModel = viewModel, forceDepositMode = false, onNavigateToTab = { activeActionDetail = it })
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
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "AVAILABLE WALLET BALANCE",
                                color = DarkGreyText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${String.format(Locale.getDefault(), "%,.2f", activeUser?.cashBalance ?: 0.0)}",
                                color = EmeraldGreen,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("networth_display")
                            )
                        }
                    }
                }

                // Interactive Daily Arrival Bonus Card
                item {
                    DailyArrivalBonusCard(user = activeUser, viewModel = viewModel)
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

@Composable
fun DailyArrivalBonusCard(
    user: com.example.data.User?,
    viewModel: InvestmentViewModel
) {
    if (user == null) return

    val currentTime = System.currentTimeMillis()
    val lastClaim = user.lastArrivalClaimTime
    val streakDay = user.arrivalStreak

    // Fetch dynamic bonus settings
    val bonusList = viewModel.getDailyDropBonusList()
    val isSystemEnabled = viewModel.isDailyDropEnabled()

    // Calculate initial status
    var timeRemainingText by remember(lastClaim, isSystemEnabled) { mutableStateOf("") }
    var buttonEnabled by remember(lastClaim, isSystemEnabled) { mutableStateOf(false) }
    var streakStatusLabel by remember(lastClaim, isSystemEnabled) { mutableStateOf("") }
    var alertLabel by remember(lastClaim, isSystemEnabled) { mutableStateOf("") }

    LaunchedEffect(lastClaim, isSystemEnabled) {
        if (!isSystemEnabled) {
            timeRemainingText = "System Offline (Maintenance)"
            buttonEnabled = false
            streakStatusLabel = "Daily Drops Disabled"
            alertLabel = "Locked by Administrator"
        } else {
            while (true) {
                val now = System.currentTimeMillis()
                if (lastClaim == 0L) {
                    timeRemainingText = "Ready to claim instantly!"
                    buttonEnabled = true
                    streakStatusLabel = "Streak: Day 1 (Pending Check-in)"
                    alertLabel = "Claim Day 1 bonus: $${String.format("%.2f", bonusList.getOrElse(0) { 1.00 })}"
                } else {
                    val msDiff = now - lastClaim
                    val hoursDiff = msDiff / (1000.0 * 3600.0)

                    if (hoursDiff < 24.0) {
                        val msLeft = (24.0 * 3600.0 * 1000.0) - msDiff
                        val hrs = (msLeft / (3600.0 * 1000.0)).toInt()
                        val mins = ((msLeft % (3600.0 * 1000.0)) / (60.0 * 1000.0)).toInt()
                        val secs = (((msLeft % (1000.0 * 60.0 * 60.0)) % (60.0 * 1000.0)) / 1000.0).toInt()
                        timeRemainingText = String.format("Next claim unlocks in: %02d:%02d:%02d", hrs, mins, secs)
                        buttonEnabled = false
                        streakStatusLabel = "Streak: Day $streakDay (Claimed)"
                        alertLabel = "Already claimed for today"
                    } else if (hoursDiff >= 24.0 && hoursDiff < 48.0) {
                        val nextStreak = (streakDay % 7) + 1
                        val award = bonusList.getOrElse(nextStreak - 1) { 1.00 }
                        timeRemainingText = "Ready to claim Day $nextStreak!"
                        buttonEnabled = true
                        streakStatusLabel = "Streak: Day $streakDay -> Day $nextStreak"
                        alertLabel = "Claim Day $nextStreak bonus: $${String.format("%.2f", award)}"
                    } else {
                        timeRemainingText = "Window missed! Start over."
                        buttonEnabled = true
                        streakStatusLabel = "Streak expired (missed check-in)"
                        alertLabel = "Claim Day 1 bonus: $${String.format("%.2f", bonusList.getOrElse(0) { 1.00 })}"
                    }
                }
                delay(1000)
            }
        }
    }

    val brush = Brush.linearGradient(
        colors = listOf(GoldAccent.copy(alpha = 0.4f), Color(0xFFE5A93B).copy(alpha = 0.1f))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_arrival_bonus_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
        border = BorderStroke(1.2.dp, brush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Daily Arrival",
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DAILY ARRIVAL BONUS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = streakStatusLabel,
                            color = GoldAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Small badge for status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (buttonEnabled) EmeraldGreen.copy(alpha = 0.2f) else DarkGreyText.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (buttonEnabled) "AVAIL" else "LOCKED",
                        color = if (buttonEnabled) EmeraldGreen else DarkGreyText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calendar strip for visualization of 7 days
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (day in 1..7) {
                    val isCurrentOrPast = if (lastClaim == 0L) false else {
                        val hours = (System.currentTimeMillis() - lastClaim) / (1000.0 * 3600.0)
                        if (hours >= 48.0) false // reset
                        else {
                            if (day <= streakDay) true
                            else false
                        }
                    }
                    val isNextToClaim = if (lastClaim == 0L) day == 1 else {
                        val hours = (System.currentTimeMillis() - lastClaim) / (1000.0 * 3600.0)
                        if (hours >= 48.0) day == 1
                        else if (hours >= 24.0) day == (streakDay % 7) + 1
                        else false
                    }

                    val circleBg = when {
                        isCurrentOrPast -> EmeraldGreen
                        isNextToClaim -> GoldAccent
                        else -> DeepObsidian
                    }
                    val textColor = when {
                        isCurrentOrPast || isNextToClaim -> DeepObsidian
                        else -> SilverGray
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(circleBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "D$day",
                                color = textColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val bonusVal = bonusList.getOrElse(day - 1) { 1.00 }
                        val bonusText = if (bonusVal % 1.0 == 0.0) "$${bonusVal.toInt()}" else "$$bonusVal"
                        Text(
                            text = bonusText,
                            color = if (isCurrentOrPast) EmeraldGreen else if (isNextToClaim) GoldAccent else DarkGreyText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeRemainingText,
                    color = if (buttonEnabled) GoldAccent else SilverGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )

                Button(
                    onClick = { viewModel.claimArrivalBonus() },
                    enabled = buttonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        disabledContainerColor = DeepObsidian,
                        contentColor = DeepObsidian,
                        disabledContentColor = DarkGreyText
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("claim_arrival_bonus_button")
                ) {
                    Text(
                        text = if (buttonEnabled) "CLAIM" else "CLAIMED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
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

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.15f))
                                    .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "ADMIN DISTRIBUTED",
                                    color = EmeraldGreen,
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
    forceDepositMode: Boolean = true,
    onNavigateToTab: (String) -> Unit = {}
) {
    val isDepositMode = forceDepositMode
    var entryAmount by remember { mutableStateOf("") }

    // Nested states for Deposit Steps
    var depositStep by remember { mutableStateOf("SELECT_METHOD") } // "SELECT_METHOD", "TIMER_GENERATING_DETAILS", "PAYMENT_DETAILS", "TIMER_CONFIRMATION"
    var selectedMethod by remember { mutableStateOf("BANK") } // "BANK" or "CRYPTO"

    var senderFullName by remember(user) { mutableStateOf(user?.fullName ?: "") }
    var senderAccountNumber by remember { mutableStateOf("") }
    var senderBankName by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }
    var timerVal by remember { mutableStateOf(7) }
    var animationPercentage by remember { mutableStateOf(1) }

    // LaunchedEffect to manage the 7-second timers
    LaunchedEffect(depositStep) {
        if (depositStep == "TIMER_GENERATING_DETAILS") {
            animationPercentage = 1
            val totalTicks = 100
            val delayMs = 70L // 70ms * 100 = 7000ms (7s total duration)
            for (i in 1..totalTicks) {
                delay(delayMs)
                animationPercentage = i
            }
            depositStep = "PAYMENT_DETAILS"
        } else if (depositStep == "TIMER_CONFIRMATION") {
            timerVal = 7
            while (timerVal > 0) {
                delay(1000)
                timerVal--
            }
            // Once timer ends, reset state to beginning
            depositStep = "SELECT_METHOD"
            selectedMethod = "BANK"
            entryAmount = ""
            senderFullName = user?.fullName ?: ""
            senderAccountNumber = ""
            senderBankName = ""
            transactionId = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Welcoming portal header based on current modes
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, if (isDepositMode) EmeraldGreen.copy(alpha = 0.3f) else ElectricBlue.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDepositMode) EmeraldGreen.copy(alpha = 0.12f) else ElectricBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDepositMode) Icons.Default.Send else Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = if (isDepositMode) EmeraldGreen else ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isDepositMode) "CAPITAL DEPOSIT GATEWAY" else "CHECKOUT SECURE DISPATCH",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isDepositMode) "Submit a secure capital deposit inflow to credit your active balance ledger." else "Transfer ledger balance directly back to your bound bank or crypto address.",
                        color = DarkGreyText,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (isDepositMode) {
            // --- DEPOSIT FLOW STEPS ---
            when (depositStep) {
                "SELECT_METHOD" -> {
                    Text(
                        text = "CHOOSE FUNDING TYPE & AMOUNT",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mark your preferred deposit method, input desired amount, and proceed to connection phase.",
                        color = DarkGreyText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // METHOD SELECTION (They should mark anyone they want)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Option 1: Local Bank
                        val bankSelected = selectedMethod == "BANK"
                        Card(
                            onClick = { selectedMethod = "BANK" },
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = if (bankSelected) DarkSlateCard else DeepObsidian
                            ),
                            border = BorderStroke(1.5.dp, if (bankSelected) EmeraldGreen else BorderColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null,
                                        tint = if (bankSelected) EmeraldGreen else DarkGreyText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    
                                    // Custom beautifully-marked circular radio check
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (bankSelected) EmeraldGreen else Color.Transparent)
                                            .border(2.dp, if (bankSelected) EmeraldGreen else BorderColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (bankSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(DeepObsidian)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Local Bank",
                                    color = if (bankSelected) Color.White else SilverGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Transfer ₦ Naira",
                                    color = if (bankSelected) EmeraldGreen else DarkGreyText,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Option 2: Cryptocurrency
                        val cryptoSelected = selectedMethod == "CRYPTO"
                        Card(
                            onClick = { selectedMethod = "CRYPTO" },
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = if (cryptoSelected) DarkSlateCard else DeepObsidian
                            ),
                            border = BorderStroke(1.5.dp, if (cryptoSelected) EmeraldGreen else BorderColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (cryptoSelected) ElectricBlue else DarkGreyText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    
                                    // Custom beautifully-marked circular radio check
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (cryptoSelected) EmeraldGreen else Color.Transparent)
                                            .border(2.dp, if (cryptoSelected) EmeraldGreen else BorderColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (cryptoSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(DeepObsidian)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Crypto USDT",
                                    color = if (cryptoSelected) Color.White else SilverGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "USDT (TRC20)",
                                    color = if (cryptoSelected) ElectricBlue else DarkGreyText,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ENTER SEC_VALUE AMOUNT ($)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Amount box for them to fill in
                    OutlinedTextField(
                        value = entryAmount,
                        onValueChange = { entryAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Capital Deposit Amount ($)", fontSize = 11.sp) },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Conversion Rate Box Beneath
                    val nairaRate = viewModel.getAdminNairaRate()
                    val enteredAmt = entryAmount.toDoubleOrNull() ?: 0.0
                    val totalNaira = enteredAmt * nairaRate

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (selectedMethod == "BANK") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Base Conversion Rate:", color = SilverGray, fontSize = 11.sp)
                                    Text(text = "1 USD = ₦${String.format("%,.2f", nairaRate)} NGN", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                androidx.compose.material3.HorizontalDivider(color = BorderColor.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Naira Amount to Transfer:", color = SilverGray, fontSize = 11.sp)
                                    Text(
                                        text = "₦${String.format("%,.2f", totalNaira)}",
                                        color = EmeraldGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Crypto Index Value:", color = SilverGray, fontSize = 11.sp)
                                    Text(text = "1 USD = 1.00 USDT Token", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                androidx.compose.material3.HorizontalDivider(color = BorderColor.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Tokens to Transfer:", color = SilverGray, fontSize = 11.sp)
                                    Text(
                                        text = "${String.format("%.2f", enteredAmt)} USDT",
                                        color = ElectricBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        enabled = enteredAmt > 0.0
                    ) {
                        Text(
                            text = "CONFIRM & SECURE GATEWAY LINK",
                            color = DeepObsidian,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                "TIMER_GENERATING_DETAILS" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(95.dp)) {
                                CircularProgressIndicator(
                                    progress = { animationPercentage / 100f },
                                    color = EmeraldGreen,
                                    strokeWidth = 5.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = "$animationPercentage%",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "SECURING CRYPTO/BANK PROTOCOL",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "Pulling up target active gateway coordinates... Handshake secure. Generating destination credentials directly from settlement admin.",
                                color = SilverGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
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
                        text = "DISPATCH TRANSLATION PACKET",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Transfer the precise required funds directly to the target system recovery coordinates provided below.",
                        color = DarkGreyText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destination Parameters Card
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ADMIN DESTINATION COORDINATES", color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldAccent.copy(alpha = 0.12f))
                                        .clickable {
                                            val targetNum = if (selectedMethod == "BANK") adminAccountNumber else "TYZ34sfdg91gHskf891sPqWzLkj91Mpx"
                                            clipboardManager.setText(androidx.compose.ui.text.buildAnnotatedString { append(targetNum) })
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("COPY TARGET", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (selectedMethod == "BANK") {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Bank Name:", color = SilverGray, fontSize = 11.sp)
                                    Text(adminBankName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Account Name:", color = SilverGray, fontSize = 11.sp)
                                    Text(adminAccountName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Account Number:", color = SilverGray, fontSize = 11.sp)
                                    Text(adminAccountNumber, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                androidx.compose.material3.HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Conversion Rate Selected:", color = SilverGray, fontSize = 11.sp)
                                    Text("1 USD = ₦${String.format("%,.0f", nairaRate)} NGN", color = DarkGreyText, fontSize = 10.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Transfer Amount:", color = SilverGray, fontSize = 11.sp)
                                    Text("₦${String.format("%,.2f", totalNaira)} NGN", color = EmeraldGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                                }
                            } else {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("USDT Token Protocol:", color = SilverGray, fontSize = 11.sp)
                                    Text("TRC20 Blockchain Network", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Deposit Address Token:", color = SilverGray, fontSize = 11.sp)
                                    Text("TYZ34sfdg91gHskf891sPqWz...1Mpx", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                androidx.compose.material3.HorizontalDivider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Amount to Send:", color = SilverGray, fontSize = 11.sp)
                                    Text("${String.format("%.2f", enteredAmt)} USDT", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "SUBMIT SENDER VERIFICATION STACK",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Form Fields for manual deposit audit verification
                    // Field 1: Sender Name
                    OutlinedTextField(
                        value = senderFullName,
                        onValueChange = { senderFullName = it },
                        label = { Text(if (selectedMethod == "BANK") "Your Full Account Holder Name" else "Your Source Wallet Account / Exchange Name", fontSize = 11.sp) },
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

                    // Field 2: Sender Account Number / Sender Wallet Address
                    OutlinedTextField(
                        value = senderAccountNumber,
                        onValueChange = { senderAccountNumber = it },
                        label = { Text(if (selectedMethod == "BANK") "Your Account Number From Transfer" else "Your Outflow TRC20 Sender Wallet Address", fontSize = 11.sp) },
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

                    // Field 3: Sender Bank / Network choice
                    OutlinedTextField(
                        value = senderBankName,
                        onValueChange = { senderBankName = it },
                        label = { Text(if (selectedMethod == "BANK") "Sender Outflow Bank Name" else "USDT Dispatch Network (e.g. TRC20)", fontSize = 11.sp) },
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

                    // Field 4: Reference ID
                    OutlinedTextField(
                        value = transactionId,
                        onValueChange = { transactionId = it },
                        label = { Text("Transfer Transaction ID / Reference (Optional)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        enabled = isFormFilled
                    ) {
                        Text("DISPATCH PAYMENT FOR AUDITING", color = DeepObsidian, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                text = "DEPOSIT DISPATCHED TO AUDIT QUEUE",
                                color = EmeraldGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "Your deposit verification payload loaded successfully. Funds are registered in internal pending audit. Balance is dispatched within 1 to 24 hours upon confirming actual bank or crypto inflow.",
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
            var withdrawMethod by remember { mutableStateOf("BANK") } // "BANK" or "CRYPTO"
            var inlineCryptoAddress by remember { mutableStateOf("") }

            Text(
                text = "WITHDRAWAL FUNDS TERMINAL",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Request a secure, rapid withdrawal of your ledger balance. Payout configurations must be set active.",
                color = DarkGreyText,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Option selection: BANK vs CRYPTO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // LOCAL BANK card Selector
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { withdrawMethod = "BANK" },
                    colors = CardDefaults.cardColors(
                        containerColor = if (withdrawMethod == "BANK") DeepObsidian else DarkSlateCard
                    ),
                    border = BorderStroke(
                        width = if (withdrawMethod == "BANK") 2.dp else 1.dp,
                        color = if (withdrawMethod == "BANK") EmeraldGreen else BorderColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = null,
                                tint = if (withdrawMethod == "BANK") EmeraldGreen else SilverGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "LOCAL BANK",
                                color = if (withdrawMethod == "BANK") EmeraldGreen else Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text("Direct NGN Wire Transfer", color = DarkGreyText, fontSize = 9.sp)
                    }
                }

                // CRYPTO card Selector
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { withdrawMethod = "CRYPTO" },
                    colors = CardDefaults.cardColors(
                        containerColor = if (withdrawMethod == "CRYPTO") DeepObsidian else DarkSlateCard
                    ),
                    border = BorderStroke(
                        width = if (withdrawMethod == "CRYPTO") 2.dp else 1.dp,
                        color = if (withdrawMethod == "CRYPTO") ElectricBlue else BorderColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (withdrawMethod == "CRYPTO") ElectricBlue else SilverGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "USDT (TRC20)",
                                color = if (withdrawMethod == "CRYPTO") ElectricBlue else Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text("Rapid Crypto Blockchain", color = DarkGreyText, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (withdrawMethod == "BANK") {
                // Check if user has a bank account linked
                val isBankLinked = !user?.boundBankAccount.isNullOrBlank()

                if (!isBankLinked) {
                    // Redirect Warning Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                        border = BorderStroke(1.dp, LossRed.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = LossRed,
                                modifier = Modifier.size(34.dp)
                            )
                            Text(
                                text = "LOCAL BANK DETAILS NOT BINDED (REQUIRED)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "To retrieve payments directly to local bank accounts, you must first specify your bank outlet and account Swift IBAN coordinate. Click below to go to Settings automatically.",
                                color = SilverGray,
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = { onNavigateToTab("settings") },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("REDIRECT TO SETTINGS TAB", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Bank details set! Render Form & The beautiful conversion green box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("TARGET RECEIVED ACCOUNT SETTLED", color = DarkGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("Bank Outlet: ${user?.boundBankName}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Account IBAN/Num: ${user?.boundBankAccount}", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = entryAmount,
                        onValueChange = { entryAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Checkout Cash Target Amount ($)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text("$", color = EmeraldGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = EmeraldGreen,
                            unfocusedLabelColor = DarkGreyText,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Green Box for Local Bank withdrawal rate conversions
                    val withdrawalNairaRate = viewModel.getAdminWithdrawalNairaRate()
                    val usdAmount = entryAmount.toDoubleOrNull() ?: 0.0
                    val totalNairaEquivalent = usdAmount * withdrawalNairaRate

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.08f)),
                        border = BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "REAL-TIME SETTLEMENT CALCULATION",
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Withdrawal Transfer Rate:", color = SilverGray, fontSize = 11.sp)
                                Text("1 USD = ₦${String.format("%,.0f", withdrawalNairaRate)} NGN", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            HorizontalDivider(color = EmeraldGreen.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Naira Settlement Balance:", color = SilverGray, fontSize = 11.sp)
                                Text("₦${String.format("%,.2f", totalNairaEquivalent)} NGN", color = EmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val amt = entryAmount.toDoubleOrNull() ?: 0.0
                            if (amt <= 0.0) return@Button
                            viewModel.withdraw(amt, isCrypto = false)
                            entryAmount = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        enabled = usdAmount > 0.0
                    ) {
                        Text(
                            text = "DISPATCH BANK WITHDRAW PROTOCOL",
                            color = DeepObsidian,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Cryptocurrency (USDT TRC20) Withdrawal Flow
                val isCryptoLinked = !user?.boundCryptoAddress.isNullOrBlank()

                if (!isCryptoLinked) {
                    // Prompt user inline to enter address and bind right away!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                Text("NO TRC20 WALLET ADAPTER BINDS DETECTED", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "Specify your target USDT (TRC-20 Network) wallet address inline below to instantly bind it and open direct blockchain transit channels:",
                                color = DarkGreyText,
                                fontSize = 10.sp,
                                lineHeight = 13.sp
                            )

                            OutlinedTextField(
                                value = inlineCryptoAddress,
                                onValueChange = { inlineCryptoAddress = it.trim() },
                                label = { Text("Enter TRC20 Wallet Address ($)", fontSize = 10.sp) },
                                placeholder = { Text("TXXXXXXXXXXXX...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = BorderColor,
                                    unfocusedTextColor = Color.White,
                                    focusedTextColor = Color.White
                                )
                            )

                            Button(
                                onClick = {
                                    if (inlineCryptoAddress.isNotBlank()) {
                                        viewModel.bindCrypto(inlineCryptoAddress)
                                        inlineCryptoAddress = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                enabled = inlineCryptoAddress.length >= 24
                            ) {
                                Text("BIND WALLET ADDRESS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Crypto details set! Render amount input & 1:1 conversion green box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("TARGET REGISTERED BLOCKCHAIN WALLET", color = DarkGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("Protocol: USDT ERC20/TRC20 Adapter", color = Color.White, fontSize = 10.sp)
                            Text("Address: ${user?.boundCryptoAddress}", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = entryAmount,
                        onValueChange = { entryAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Checkout Amount to Withdraw ($)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text("$", color = ElectricBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = DarkGreyText,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Green conversion box but for Crypto 1:1
                    val usdAmount = entryAmount.toDoubleOrNull() ?: 0.0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.08f)),
                        border = BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "BLOCKCHAIN STABLE PARITY RATE",
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Token Conversion Ratio:", color = SilverGray, fontSize = 11.sp)
                                Text("1.00 USD = 1.00 USDT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            HorizontalDivider(color = EmeraldGreen.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Estimated USDT Disbursed:", color = SilverGray, fontSize = 11.sp)
                                Text("${String.format("%.2f", usdAmount)} USDT", color = EmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val amt = entryAmount.toDoubleOrNull() ?: 0.0
                            if (amt <= 0.0) return@Button
                            viewModel.withdraw(amt, isCrypto = true)
                            entryAmount = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = usdAmount > 0.0
                    ) {
                        Text(
                            text = "DISPATCH CRYPTO BLOCKCHAIN WIRE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
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
    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var ageVal by remember { mutableStateOf(user?.age?.toString() ?: "0") }
    var bankName by remember { mutableStateOf(user?.boundBankName ?: "") }
    var accountNum by remember { mutableStateOf(user?.boundBankAccount ?: "") }
    var cryptoAddress by remember { mutableStateOf(user?.boundCryptoAddress ?: "") }
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmNewPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcome Header info with stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(
                        text = "ACCOUNT PREFERENCES & CORE SECURITY",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Customize your profile name, change secure pins, link bank transfer gateways, and configure cloud synchronization.",
                        color = DarkGreyText,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }
            }
        }

        // Section 1: Edit Profile Preferences (Name and Age)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                    Text(
                        text = "PROFILE PREFERENCES",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Configure your official display identification name and registered profile age to satisfy network KYC verification.",
                    color = DarkGreyText,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Display Full Name", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = ageVal,
                    onValueChange = { ageVal = it.filter { c -> c.isDigit() } },
                    label = { Text("Profile Age", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.updateFullName(fullName) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "SAVE NAME", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.updateAge(ageVal.toIntOrNull() ?: 0) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "SAVE AGE", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 2: Local Bank Account Binding Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.AccountBox, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                    Text(
                        text = "LOCAL BANK SETTLE ADDRESS",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Bound account required directly to request and process local fiat bank withdrawals.",
                    color = DarkGreyText,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
                
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Outlet Name", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = accountNum,
                    onValueChange = { accountNum = it.filter { c -> c.isDigit() || c.isLetter() } },
                    label = { Text("Account / Card Swift IBAN", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Button(
                    onClick = { viewModel.bindBank(bankName, accountNum) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "SAVE & BIND BANK SETTLEMENTS", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 3: Blockchain Wallet Binding Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                    Text(
                        text = "BLOCKCHAIN USDT WALLET (TRC20)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Enables rapid, borderless payouts in standard stable crypto currency. Ensure network remains TRC-20.",
                    color = DarkGreyText,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )

                OutlinedTextField(
                    value = cryptoAddress,
                    onValueChange = { cryptoAddress = it.trim() },
                    label = { Text("Crypto Receives Wallet (USDT-TRC20)", fontSize = 11.sp) },
                    placeholder = { Text("TXXXXXXXXXXXX...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = ElectricBlue,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                Button(
                    onClick = { viewModel.bindCrypto(cryptoAddress) },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "SAVE & BIND TRC20 ENDPOINT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 4: Alter Access Passphrase Card (with confirmation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text(
                        text = "ALTER ACCOUNT ACCESS PASS",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Enter your current secure code, then input your new security pin and confirm it to apply the rewrite.",
                    color = DarkGreyText,
                    fontSize = 11.sp,
                    lineHeight = 13.sp
                )

                OutlinedTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it },
                    label = { Text("Current Pin / Code Key", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Pin / Code Key", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = confirmNewPass,
                    onValueChange = { confirmNewPass = it },
                    label = { Text("Confirm New Pin / Code Key", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )

                val passwordsMatch = newPass == confirmNewPass
                if (newPass.isNotEmpty() && confirmNewPass.isNotEmpty()) {
                    Text(
                        text = if (passwordsMatch) "✓ New pin confirms fit and match." else "✗ New pins do not match.",
                        color = if (passwordsMatch) EmeraldGreen else LossRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    enabled = passwordsMatch && oldPass.isNotEmpty() && newPass.isNotEmpty(),
                    onClick = {
                        viewModel.changePassword(oldPass, newPass)
                        oldPass = ""
                        newPass = ""
                        confirmNewPass = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        disabledContainerColor = BorderColor.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "COMMIT CREDENTIAL REWRITE", color = DeepObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 6: Control Panel Exit Channels Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, LossRed.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.resetDemoAccount() },
                    colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "RESET PORTFOLIO", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "CLOSE CHANNEL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
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

                // Determine layout & theme color based on transaction status
                val isPending = tx.status == "PENDING"
                val isRejected = tx.status == "REJECTED"

                val statusLabel = when (tx.status) {
                    "PENDING" -> "PENDING"
                    "REJECTED" -> "REJECTED"
                    else -> "COMPLETED"
                }

                val statusColor = when (tx.status) {
                    "PENDING" -> GoldAccent
                    "REJECTED" -> LossRed
                    else -> EmeraldGreen
                }

                val amountColor = if (isPending) {
                    GoldAccent
                } else if (isRejected) {
                    LossRed
                } else {
                    if (isPositive) EmeraldGreen else LossRed
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, if (isPending) GoldAccent.copy(alpha = 0.4f) else if (isRejected) LossRed.copy(alpha = 0.4f) else BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = tx.planName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Action: ${tx.type}",
                                    color = DarkGreyText,
                                    fontSize = 11.sp
                                )
                                // If there are custom deposit details, add a small info badge for method
                                if (tx.depositMethod != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White.copy(alpha = 0.08f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(text = tx.depositMethod, color = SilverGray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            val fStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                            Text(text = fStr, color = DarkGreyText, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$prefix$${String.format("%.2f", tx.totalAmount)}",
                                color = amountColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Draw status badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusColor.copy(alpha = 0.12f))
                                    .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = statusLabel,
                                    color = statusColor,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
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
// 9. SECURE ADMINISTRATIVE PANEL GATED GATEWAY
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
    val tabs = listOf("DEPOSITS", "WITHDRAWALS", "CLIENT DIRECTORY", "YIELD CREATOR", "ROI DEPLOYMENT", "SETTINGS", "DAILY DROP")

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
                    0 -> AdminDepositsTab(viewModel = viewModel)
                    1 -> AdminWithdrawalsTab(viewModel = viewModel)
                    2 -> AdminClientsDirectoryTab(users = users)
                    3 -> AdminYieldCreatorTab(plans = plans, viewModel = viewModel)
                    4 -> AdminRoiDeploymentTab(viewModel = viewModel)
                    5 -> AdminSettingsTabReplicated(viewModel = viewModel)
                    6 -> AdminDailyDropTab(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AdminDepositsTab(
    viewModel: InvestmentViewModel
) {
    val allTransactions by viewModel.allGlobalTransactions.collectAsState()
    
    // Filter to only DEPOSITS
    val deposits = remember(allTransactions) { allTransactions.filter { it.type == "DEPOSIT" } }
    val pendingDeposits = remember(deposits) { deposits.filter { it.status == "PENDING" } }
    val processedDeposits = remember(deposits) {
        deposits.filter { it.status == "APPROVED" || it.status == "REJECTED" }
            .sortedByDescending { it.timestamp }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "PENDING DEPOSIT REQUESTS (${pendingDeposits.size})",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (pendingDeposits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.3f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No pending deposit requests in the queue.", color = DarkGreyText, fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(pendingDeposits) { tx ->
                val labelColor = EmeraldGreen
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

                        // If there are detailed sender fields, display them in an Auditing Panel inside the Card!
                        if (tx.senderName != null || tx.senderAccountNumber != null || tx.senderBankName != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "CLIENT FUNDING AUDIT PAYLOAD",
                                        color = GoldAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    androidx.compose.material3.HorizontalDivider(color = BorderColor.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 2.dp))

                                    val isBank = tx.depositMethod == "BANK"

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = if (isBank) "Holder Account Name:" else "Exchange/Wallet Name:", color = SilverGray, fontSize = 10.sp)
                                        Text(text = tx.senderName ?: "N/A", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = if (isBank) "Sender Account Number:" else "TRC20 Wallet Address:", color = SilverGray, fontSize = 10.sp)
                                        Text(text = tx.senderAccountNumber ?: "N/A", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = if (isBank) "Outflowing Bank Name:" else "Blockchain Network:", color = SilverGray, fontSize = 10.sp)
                                        Text(text = tx.senderBankName ?: "N/A", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    if (tx.paymentTransactionId != null) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = "Transaction Hash/ID:", color = SilverGray, fontSize = 10.sp)
                                            Text(text = tx.paymentTransactionId, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                    if (tx.localCurrencyAmount != null && tx.localCurrencyAmount > 0.0) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = if (isBank) "Required Naira Flow:" else "Required Crypto Flow:", color = SilverGray, fontSize = 10.sp)
                                            val curSign = if (isBank) "₦" else "$"
                                            val curCode = if (isBank) "NGN" else "USDT"
                                            Text(
                                                text = "$curSign${String.format("%,.2f", tx.localCurrencyAmount)} $curCode",
                                                color = if (isBank) EmeraldGreen else ElectricBlue,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }

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

        // Historic Processed Ledger
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "HISTORIC DEPOSIT ARCHIVES (${processedDeposits.size})",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        if (processedDeposits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No historic deposit audits in system archives.", color = DarkGreyText, fontSize = 10.sp)
                    }
                }
            }
        } else {
            items(processedDeposits) { tx ->
                val isApproved = tx.status == "APPROVED"
                val statusColor = if (isApproved) EmeraldGreen else LossRed

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = tx.type + " [${tx.status}]",
                                    color = statusColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "$${String.format("%.2f", tx.totalAmount)}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Client User: @${tx.username}", color = SilverGray, fontSize = 10.sp)
                            val dateStr = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(Date(tx.timestamp))
                            Text("Logged: $dateStr", color = DarkGreyText, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }

                        Text("Destination Info: ${tx.planName}", color = DarkGreyText, fontSize = 10.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminWithdrawalsTab(
    viewModel: InvestmentViewModel
) {
    val allTransactions by viewModel.allGlobalTransactions.collectAsState()
    
    // Filter to only WITHDRAWALS
    val withdrawals = remember(allTransactions) { allTransactions.filter { it.type == "WITHDRAWAL" } }
    val pendingWithdrawals = remember(withdrawals) { withdrawals.filter { it.status == "PENDING" } }
    val processedWithdrawals = remember(withdrawals) {
        withdrawals.filter { it.status == "APPROVED" || it.status == "REJECTED" }
            .sortedByDescending { it.timestamp }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                "PENDING WITHDRAWAL REQUESTS (${pendingWithdrawals.size})",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (pendingWithdrawals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.3f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No pending withdrawal requests in the queue.", color = DarkGreyText, fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(pendingWithdrawals) { tx ->
                val labelColor = ElectricBlue
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

                        Text(text = "Payout Channel: ${tx.planName}", color = SilverGray, fontSize = 11.sp)
                        val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                        Text(text = "Requested: $dateString", color = DarkGreyText, fontSize = 9.sp)

                        // Display detailed destination/holder payload
                        if (tx.senderName != null || tx.senderAccountNumber != null || tx.senderBankName != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "CLIENT PAYOUT SETTLEMENT DETAILS",
                                        color = GoldAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    androidx.compose.material3.HorizontalDivider(color = BorderColor.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 2.dp))

                                    val isBank = tx.depositMethod == "BANK" || !tx.planName.contains("USDT", ignoreCase = true)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = if (isBank) "Holder Account Name:" else "Exchange/Wallet Name:", color = SilverGray, fontSize = 10.sp)
                                        Text(text = tx.senderName ?: "N/A", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = if (isBank) "Receiving Account Number:" else "TRC20 Wallet Address:", color = SilverGray, fontSize = 10.sp)
                                        Text(text = tx.senderAccountNumber ?: "N/A", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = if (isBank) "Receiving Bank Name:" else "Blockchain Network:", color = SilverGray, fontSize = 10.sp)
                                        Text(text = tx.senderBankName ?: "N/A", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    if (tx.localCurrencyAmount != null && tx.localCurrencyAmount > 0.0) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = "Paying Out Flow Value:", color = SilverGray, fontSize = 10.sp)
                                            val curSign = if (isBank) "₦" else "$"
                                            val curCode = if (isBank) "NGN" else "USDT"
                                            Text(
                                                text = "$curSign${String.format("%,.2f", tx.localCurrencyAmount)} $curCode",
                                                color = if (isBank) EmeraldGreen else ElectricBlue,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }

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

        // Historic Processed Ledger
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "HISTORIC WITHDRAWAL ARCHIVES (${processedWithdrawals.size})",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        if (processedWithdrawals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No historic withdrawal audits in system archives.", color = DarkGreyText, fontSize = 10.sp)
                    }
                }
            }
        } else {
            items(processedWithdrawals) { tx ->
                val isApproved = tx.status == "APPROVED"
                val statusColor = if (isApproved) EmeraldGreen else LossRed

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = tx.type + " [${tx.status}]",
                                    color = statusColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "$${String.format("%.2f", tx.totalAmount)}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Client User: @${tx.username}", color = SilverGray, fontSize = 10.sp)
                            val dateStr = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(Date(tx.timestamp))
                            Text("Logged: $dateStr", color = DarkGreyText, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }

                        Text("Destination Info: ${tx.planName}", color = DarkGreyText, fontSize = 10.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminRoiDeploymentTab(
    viewModel: InvestmentViewModel
) {
    val holdings by viewModel.allGlobalHoldings.collectAsState()
    val distributionHistory by viewModel.distributionHistory.collectAsState()

    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // Math calculations
    val activePlansCount = holdings.count { it.daysElapsed < it.durationDays }
    val totalDailyPotential = holdings.filter { it.daysElapsed < it.durationDays }.sumOf { it.amount * (it.dailyPercentage / 100.0) }
    val remainingToPayToday = holdings.filter { it.daysElapsed < it.durationDays && it.lastDistributedDate != todayDate }.sumOf { it.amount * (it.dailyPercentage / 100.0) }
    val pendingDeploymentCount = holdings.count { it.daysElapsed < it.durationDays && it.lastDistributedDate != todayDate }

    // Auto states
    var autoEnabled by remember { mutableStateOf(viewModel.isAutoRoiEnabled()) }
    var autoTime by remember { mutableStateOf(viewModel.getAutoRoiTime()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Core Control Hub Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ROI DISPATCH ACTION CENTER",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Pending Today payouts", color = DarkGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "$pendingDeploymentCount contracts",
                                color = if (pendingDeploymentCount > 0) GoldAccent else Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Est. Remaining Payout", color = DarkGreyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "$${String.format("%.2f", remainingToPayToday)}",
                                color = if (remainingToPayToday > 0.0) EmeraldGreen else Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Stats Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Active Contracts", color = DarkGreyText, fontSize = 9.sp)
                            Text("$activePlansCount contracts", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Full Daily Potential Total", color = DarkGreyText, fontSize = 9.sp)
                            Text("$${String.format("%.2f", totalDailyPotential)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { viewModel.distributeUserRoiManually() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = DeepObsidian)
                            Text("DEPLOY MANUAL ROI NOW", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                        }
                    }
                }
            }
        }

        // Auto ROI Scheduler Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AUTOMATED ROI DISPATCH ENGINE (BACKGROUND WORKER)",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "When active, the automated loop evaluates all running contracts every 15 seconds. If the scheduled time matches, ROI payouts execute completely automatically once per calendar day.",
                        color = DarkGreyText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Autonomous Deployer Status",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        androidx.compose.material3.Switch(
                            checked = autoEnabled,
                            onCheckedChange = {
                                autoEnabled = it
                                viewModel.saveAutoRoiSettings(it, autoTime)
                            },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = EmeraldGreen,
                                checkedTrackColor = EmeraldGreen.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Scheduled Dispatch Time (24h)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedTextField(
                            value = autoTime,
                            onValueChange = {
                                autoTime = it
                                viewModel.saveAutoRoiSettings(autoEnabled, it)
                            },
                            placeholder = { Text("12:00") },
                            modifier = Modifier.width(100.dp).height(50.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = BorderColor,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Active Investments Table Card Header
        item {
            Text(
                "ACTIVE INVESTMENT CONTRACTS STATUS",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        if (holdings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No active user contracts found in database.", color = DarkGreyText, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(holdings) { hold ->
                val isExpired = hold.daysElapsed >= hold.durationDays
                val dailyPayout = hold.amount * (hold.dailyPercentage / 100.0)
                val isDispatchedToday = hold.lastDistributedDate == todayDate

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (isExpired) DeepObsidian else DarkSlateCard),
                    border = BorderStroke(1.dp, if (isExpired) BorderColor.copy(alpha = 0.3f) else BorderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Client: ${hold.username.uppercase()}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (isExpired) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BorderColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("FINISHED/MATURED", color = DarkGreyText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (isDispatchedToday) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("DISPATCHED TODAY", color = EmeraldGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("DISPATCH PENDING", color = GoldAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text(
                            "${hold.planName} (${hold.durationDays} Days Tenure)",
                            color = DarkGreyText,
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Principal: $${String.format("%,.2f", hold.amount)}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Daily ROI: +$${String.format("%.2f", dailyPayout)} (${hold.dailyPercentage}%)",
                                color = EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Progress representation
                        val progressNormalized = if (hold.durationDays > 0) hold.daysElapsed.toFloat() / hold.durationDays.toFloat() else 1f
                        val progressClamped = progressNormalized.coerceIn(0f, 1f)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Contract Progress",
                                color = DarkGreyText,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "Day ${hold.daysElapsed} / ${hold.durationDays} (${String.format("%.0f", progressClamped * 100)}%)",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = progressClamped,
                            color = if (isExpired) BorderColor else EmeraldGreen,
                            trackColor = BorderColor.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape)
                        )
                    }
                }
            }
        }

        // Terminal Log Terminal Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "DISPATCH ENGINE LOG TERMINAL",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 200.dp)
                            .background(Color.Black)
                            .border(1.dp, BorderColor)
                            .padding(8.dp)
                    ) {
                        if (distributionHistory.isEmpty()) {
                            Text(
                                "No dispatch events logged. Execute manual deploy or enable auto dispatch.",
                                color = Color.Green,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            LazyColumn(reverseLayout = true) {
                                items(distributionHistory) { log ->
                                    Text(
                                        text = log,
                                        color = Color.Green,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(bottom = 2.dp)
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

@Composable
fun AdminPlatformSettingsTab(
    viewModel: InvestmentViewModel
) {
    var depRateStr by remember { mutableStateOf(viewModel.getAdminNairaRate().toInt().toString()) }
    var wthRateStr by remember { mutableStateOf(viewModel.getAdminWithdrawalNairaRate().toInt().toString()) }
    var bankName by remember { mutableStateOf(viewModel.getAdminBankName()) }
    var actNumber by remember { mutableStateOf(viewModel.getAdminBankAccountNumber()) }
    var actName by remember { mutableStateOf(viewModel.getAdminBankAccountName()) }

    var settingsSupabaseUrl by remember { mutableStateOf(viewModel.getSupabaseUrl()) }
    var settingsSupabaseKey by remember { mutableStateOf(viewModel.getSupabaseKey()) }
    val isSupabaseActiveSet = viewModel.getSupabaseUrl().isNotBlank()

    var feedbackMsg by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                border = BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "CONVERSION EXCHANGE FEES & RATE CONFIG",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "Set exchange rates for client bank operations. Depositing naira is modeled strictly in reference to these parameters.",
                        color = DarkGreyText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = depRateStr,
                            onValueChange = { depRateStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Deposit Rate (₦/$)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = wthRateStr,
                            onValueChange = { wthRateStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Withdraw Rate (₦/$)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "SYSTEM DESTINATION WIRE PARAMETERS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "Incoming wire transfers will specify these credentials to users. Make sure account information is valid.",
                        color = DarkGreyText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Gateway Bank Outlet Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = actName,
                        onValueChange = { actName = it },
                        label = { Text("Admin Account Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = actNumber,
                        onValueChange = { actNumber = it },
                        label = { Text("Admin Account Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "DATABASE SYNC & SUPABASE ADAPTER",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "Database synchronization backs up user records (including balances, active plans, transactions) to Supabase Cloud on every transaction or deposit.",
                        color = DarkGreyText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    OutlinedTextField(
                        value = settingsSupabaseUrl,
                        onValueChange = { settingsSupabaseUrl = it.trim() },
                        label = { Text("Supabase Endpoint API URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = settingsSupabaseKey,
                        onValueChange = { settingsSupabaseKey = it.trim() },
                        label = { Text("Supabase Public Anon Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )

                    if (isSupabaseActiveSet) {
                        Button(
                            onClick = {
                                viewModel.disconnectSupabase()
                                settingsSupabaseUrl = ""
                                settingsSupabaseKey = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("WIPE & DISCONNECT CLOUD DATABASESYNC", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (feedbackMsg.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha=0.4f))
                ) {
                    Text(
                        text = feedbackMsg,
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    val dep = depRateStr.toDoubleOrNull() ?: 1500.0
                    val wth = wthRateStr.toDoubleOrNull() ?: 1500.0
                    viewModel.saveAdminPlatformSettings(
                        depositRate = dep,
                        withdrawalRate = wth,
                        bankName = bankName,
                        accountNumber = actNumber,
                        accountName = actName
                    )
                    if (settingsSupabaseUrl.isNotBlank() && settingsSupabaseKey.isNotBlank()) {
                        viewModel.saveSupabaseCredentials(settingsSupabaseUrl, settingsSupabaseKey)
                    }
                    feedbackMsg = "System financial metrics successfully committed!"
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("COMMIT SYSTEM UPDATES", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
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

// ==========================================
// 8a. User Active & Historic Investment Plans Maturity Screen
// ==========================================
@Composable
fun MyPlansScreen(
    viewModel: InvestmentViewModel
) {
    val holdings by viewModel.userHoldings.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepObsidian,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = "MY SYSTEM PLAN CONTRACTS",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSlateCard)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DeepObsidian)
                .padding(16.dp)
        ) {
            Text(
                text = "ACTIVE CONTRACTS & MATURITY LOGS (${holdings.size})",
                color = GoldAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (holdings.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = DarkGreyText,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No quant contracts found in your active directory.",
                        color = DarkGreyText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Deploy high-yield dynamic structures in the Market page.",
                        color = DarkGreyText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(holdings) { hold ->
                        val isExpired = hold.daysElapsed >= hold.durationDays
                        val progress = if (hold.durationDays > 0) hold.daysElapsed.toFloat() / hold.durationDays.toFloat() else 1f
                        val progressClamped = progress.coerceIn(0f, 1f)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
                            border = BorderStroke(1.dp, if (isExpired) BorderColor else EmeraldGreen.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = hold.planName,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Principal deployed: $${String.format("%.2f", hold.amount)}",
                                            color = DarkGreyText,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isExpired) BorderColor.copy(alpha = 0.2f) else EmeraldGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = if (isExpired) "MATURED / REDEEMED" else "ACTIVE RUNNING",
                                            color = if (isExpired) DarkGreyText else EmeraldGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Contract Term Maturation", color = DarkGreyText, fontSize = 10.sp)
                                        Text(
                                            text = "Term: Day ${hold.daysElapsed} of ${hold.durationDays}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = progressClamped,
                                        color = if (isExpired) BorderColor else EmeraldGreen,
                                        trackColor = BorderColor.copy(alpha = 0.38f),
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Collected Yield dividends", color = DarkGreyText, fontSize = 10.sp)
                                        Text(
                                            text = "$${String.format("%.2f", hold.totalClaimed)}",
                                            color = GoldAccent,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    if (!isExpired) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(EmeraldGreen.copy(alpha = 0.15f))
                                                .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "AUTO ADMIN MANAGED",
                                                color = EmeraldGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.White.copy(alpha = 0.1f))
                                                .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "MATURED / FINISHED",
                                                color = DarkGreyText,
                                                fontSize = 11.sp,
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
        }
    }
}

@Composable
fun AdminSettingsTabReplicated(
    viewModel: InvestmentViewModel
) {
    var ticketPriceStr by remember { mutableStateOf(viewModel.getSpinTicketPrice().toString()) }
    
    var depositRateStr by remember { mutableStateOf(viewModel.getAdminNairaRate().toString()) }
    var withdrawRateStr by remember { mutableStateOf(viewModel.getAdminWithdrawalNairaRate().toString()) }
    
    var roiAutoEnabled by remember { mutableStateOf(viewModel.isAutoRoiEnabled()) }
    var roiAutoTimeStr by remember { mutableStateOf(viewModel.getAutoRoiTime()) }
    
    var maxWithdrawalsStr by remember { mutableStateOf(viewModel.getMaxWithdrawalsPerDay().toString()) }
    
    var refLevel1Str by remember { mutableStateOf(viewModel.getRefLevel1Pct().toString()) }
    var refLevel2Str by remember { mutableStateOf(viewModel.getRefLevel2Pct().toString()) }
    
    var adminAccessCodeStr by remember { mutableStateOf(viewModel.getAdminAccessCode()) }
    
    var legacyRefRebateStr by remember { mutableStateOf(viewModel.getLegacyRefRebate().toString()) }
    
    var siteNameStr by remember { mutableStateOf(viewModel.getSiteName()) }
    var launchDateTimeStr by remember { mutableStateOf(viewModel.getLaunchDateTime()) }
    var pColorStr by remember { mutableStateOf(viewModel.getColorPrimary()) }
    var sColorStr by remember { mutableStateOf(viewModel.getColorSecondary()) }
    
    var globalWithdrawalLocked by remember { mutableStateOf(viewModel.isGlobalWithdrawalLocked()) }
    
    var bankNameStr by remember { mutableStateOf(viewModel.getAdminBankDetailBank()) }
    var bankAccountNameStr by remember { mutableStateOf(viewModel.getAdminBankDetailName()) }
    var bankAccountNumberStr by remember { mutableStateOf(viewModel.getAdminBankDetailNum()) }
    
    var cryptoAddrStr by remember { mutableStateOf(viewModel.getAdminCryptoAddress()) }
    var cryptoNetStr by remember { mutableStateOf(viewModel.getAdminCryptoNetwork()) }
    
    var popupMsgStr by remember { mutableStateOf(viewModel.getAdminPopupMessage()) }
    
    var depositRulesBankStr by remember { mutableStateOf(viewModel.getDepositRulesBank()) }
    var depositRulesCryptoStr by remember { mutableStateOf(viewModel.getDepositRulesCrypto()) }
    var withdrawalRulesBankStr by remember { mutableStateOf(viewModel.getWithdrawalRulesBank()) }
    var withdrawalRulesCryptoStr by remember { mutableStateOf(viewModel.getWithdrawalRulesCrypto()) }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SLOT CREDIT SYSTEM CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = GoldAccent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Slot Credit System", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("MANAGE USER PURCHASE COST FOR SPIN TICKETS", color = DarkGreyText, fontSize = 9.sp)
                    }
                }
                
                OutlinedTextField(
                    value = ticketPriceStr,
                    onValueChange = { ticketPriceStr = it },
                    label = { Text("SPIN TICKET PRICE (USD)", fontSize = 10.sp, color = DarkGreyText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )
                
                Button(
                    onClick = {
                        val price = ticketPriceStr.toDoubleOrNull() ?: 20.0
                        viewModel.saveSpinTicketPrice(price)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5A93B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SET PRICE", color = DeepObsidian, fontWeight = FontWeight.Bold)
                }
            }
        }

        // CONVERSION RATE SETTINGS CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = GoldAccent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Conversion Rate Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("MANAGE GLOBAL EXCHANGE SPREADS", color = DarkGreyText, fontSize = 9.sp)
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E88E5).copy(alpha = 0.1f))
                        .border(1.dp, Color(0xFF1E88E5).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        "You can set different rates for deposits and withdrawals to manage exchange spreads and fees effectively.",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
                
                OutlinedTextField(
                    value = depositRateStr,
                    onValueChange = { depositRateStr = it },
                    label = { Text("DEPOSIT RATE (USD TO NAIRA)", fontSize = 10.sp, color = DarkGreyText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )
                
                OutlinedTextField(
                    value = withdrawRateStr,
                    onValueChange = { withdrawRateStr = it },
                    label = { Text("WITHDRAWAL RATE (USD TO NAIRA)", fontSize = 10.sp, color = DarkGreyText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )
                
                // Live preview calculated based on $100 USD
                val depRate = depositRateStr.toDoubleOrNull() ?: 1492.52
                val wthRate = withdrawRateStr.toDoubleOrNull() ?: 1420.24
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("📊 LIVE PREVIEW (\$100 USD)", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Deposit cost:", color = Color.White, fontSize = 12.sp)
                            Text("₦${String.format("%,.2f", depRate * 100)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Withdrawal payout:", color = Color.White, fontSize = 12.sp)
                            Text("₦${String.format("%,.2f", wthRate * 100)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Divider(color = BorderColor)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SPREAD VALUE:", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("₦${String.format("%,.2f", (depRate - wthRate) * 100)}", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
                
                Button(
                    onClick = {
                        viewModel.saveAdminPlatformSettings(
                            depRate,
                            wthRate,
                            bankNameStr,
                            bankAccountNumberStr,
                            bankAccountNameStr
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5A93B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("UPDATE CONVERSION RATES", color = DeepObsidian, fontWeight = FontWeight.Bold)
                }
            }
        }

        // AUTOMATED ROI DISTRIBUTION CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = GoldAccent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Automated ROI Distribution", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("CONFIGURE SCHEDULED PROFIT PAYOUTS", color = DarkGreyText, fontSize = 9.sp)
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Automation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Automatically distribute ROI to all active investments daily", color = DarkGreyText, fontSize = 9.sp)
                    }
                    Switch(
                        checked = roiAutoEnabled,
                        onCheckedChange = { roiAutoEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen, checkedTrackColor = EmeraldGreen.copy(alpha=0.4f))
                    )
                }
                
                OutlinedTextField(
                    value = roiAutoTimeStr,
                    onValueChange = { roiAutoTimeStr = it },
                    label = { Text("DISTRIBUTION TIME (24H)", fontSize = 10.sp, color = DarkGreyText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )
                
                Button(
                    onClick = {
                        viewModel.saveAutoRoiSettings(roiAutoEnabled, roiAutoTimeStr)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4500)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("UPDATE", color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("STATUS BEACON", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("NEXT SCHEDULED RUN", color = GoldAccent, fontSize = 8.sp)
                        Text("11/10/2025, 12:00:00 AM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("MANUAL OVERRIDE", color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("Instant ROI Distribution", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Trigger a complete payout cycle immediately for all active plans.", color = DarkGreyText, fontSize = 10.sp)
                        
                        Button(
                            onClick = { viewModel.distributeUserRoiManually() },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeepObsidian, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DISTRIBUTE NOW", color = DeepObsidian, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // DAILY WITHDRAWAL LIMIT CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = GoldAccent)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Daily Withdrawal Limit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("GLOBAL PAYOUT VELOCITY CONTROL", color = DarkGreyText, fontSize = 9.sp)
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("💡 HOW IT WORKS", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("• Users can make a maximum number of withdrawal requests per day", color = Color.LightGray, fontSize = 9.sp)
                        Text("• The limit resets automatically at 12:00 AM (midnight) daily", color = Color.LightGray, fontSize = 9.sp)
                        Text("• This helps prevent abuse and manages withdrawal processing workload", color = Color.LightGray, fontSize = 9.sp)
                        Text("• Users see how many withdrawals they have remaining when they request", color = Color.LightGray, fontSize = 9.sp)
                    }
                }
                
                OutlinedTextField(
                    value = maxWithdrawalsStr,
                    onValueChange = { maxWithdrawalsStr = it.filter { c -> c.isDigit() } },
                    label = { Text("MAXIMUM WITHDRAWALS PER DAY", fontSize = 10.sp, color = DarkGreyText) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).background(Color(0xFFFFD700).copy(alpha=0.15f)).padding(8.dp), contentAlignment = Alignment.Center) {
                        Text("$maxWithdrawalsStr withdrawal", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Box(modifier = Modifier.weight(1f).background(Color(0xFFFFD700).copy(alpha=0.15f)).padding(8.dp), contentAlignment = Alignment.Center) {
                        Text("$maxWithdrawalsStr withdrawal", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                Text("EACH USER CAN REQUEST THIS MANY WITHDRAWALS PER DAY", color = DarkGreyText, fontSize = 8.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { maxWithdrawalsStr = "1" },
                        colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("RESET", color = Color.White, fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            val limit = maxWithdrawalsStr.toIntOrNull() ?: 1
                            viewModel.saveMaxWithdrawalsPerDay(limit)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4500)),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text("SAVE SETTINGS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // MULTI-LEVEL REFERRAL SYSTEM CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E88E5).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = ElectricBlue)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Multi-Level Referral System", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("CONFIGURE REWARD PERCENTAGES FOR CONVERSIONS", color = DarkGreyText, fontSize = 9.sp)
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("👥 HOW REFERRALS WORK", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("• LEVEL 1 (Direct): Users who sign up using your referral code. You earn a percentage of their approval deposits.", color = Color.LightGray, fontSize = 9.sp)
                        Text("• LEVEL 2 (Indirect): Users who sign up using your Level 1 referrals' codes. You earn a smaller percentage of their approval deposits.", color = Color.LightGray, fontSize = 9.sp)
                    }
                }
                
                OutlinedTextField(
                    value = refLevel1Str,
                    onValueChange = { refLevel1Str = it },
                    label = { Text("LEVEL 1 (DIRECT) PERCENTAGE (%)", fontSize = 10.sp, color = DarkGreyText) },
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text("%") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = BorderColor
                    )
                )
                
                OutlinedTextField(
                    value = refLevel2Str,
                    onValueChange = { refLevel2Str = it },
                    label = { Text("LEVEL 2 (INDIRECT) PERCENTAGE (%)", fontSize = 10.sp, color = DarkGreyText) },
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text("%") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = BorderColor
                    )
                )
                
                // Live preview of rewards
                val l1 = refLevel1Str.toDoubleOrNull() ?: 15.0
                val l2 = refLevel2Str.toDoubleOrNull() ?: 3.0
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💲 LIVE EARNINGS PREVIEW", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        listOf(100.0, 500.0, 1000.0).forEach { depositValue ->
                            Text("On \$${depositValue.toInt()} deposit:", color = Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Level 1 direct payout:", color = Color.Gray, fontSize = 11.sp)
                                Text("\$${String.format("%.2f", depositValue * (l1/100))}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Level 2 indirect payout:", color = Color.Gray, fontSize = 11.sp)
                                Text("\$${String.format("%.2f", depositValue * (l2/100))}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            if (depositValue != 1000.0) Divider(color = BorderColor.copy(alpha=0.5f))
                        }
                    }
                }
                
                Button(
                    onClick = {
                        viewModel.saveReferralRates(l1, l2)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE GLOBAL REFERRAL CONFIG", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ADMIN PASSCODE & LEGACY REBATE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Admin Access Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = adminAccessCodeStr,
                        onValueChange = { adminAccessCodeStr = it },
                        label = { Text("AUTHORIZATION PASSCODE", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = LossRed,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                    Button(
                        onClick = { viewModel.saveAdminAccessCode(adminAccessCodeStr) },
                        colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CHANGE CODE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(6.dp))
                
                Text("Referral Rebate Settings (Legacy)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = legacyRefRebateStr,
                    onValueChange = { legacyRefRebateStr = it },
                    label = { Text("REFERRAL REBATE (%)", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text("%") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = BorderColor
                    )
                )
                Text("Legacy setting. Please use the Multi-Level Manager for better control.", color = DarkGreyText, fontSize = 9.sp)
            }
        }

        // SITE CUSTOMIZATION CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Site Customization", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                OutlinedTextField(
                    value = siteNameStr,
                    onValueChange = { siteNameStr = it },
                    label = { Text("SITE NAME", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                OutlinedTextField(
                    value = launchDateTimeStr,
                    onValueChange = { launchDateTimeStr = it },
                    label = { Text("LAUNCH DATE & TIME (COUNTDOWN)", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pColorStr,
                        onValueChange = { pColorStr = it },
                        label = { Text("PRIMARY COLOR", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = sColorStr,
                        onValueChange = { sColorStr = it },
                        label = { Text("SECONDARY COLOR", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = LossRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Global Withdrawal Lock", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Switch(
                        checked = globalWithdrawalLocked,
                        onCheckedChange = { globalWithdrawalLocked = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = LossRed, checkedTrackColor = LossRed.copy(alpha=0.4f))
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = globalWithdrawalLocked,
                        onCheckedChange = { globalWithdrawalLocked = it },
                        colors = CheckboxDefaults.colors(checkedColor = LossRed)
                    )
                    Text("Lock withdrawals globally for all users", color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }

        // BANK AND CRYPTO DETAILS CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Bank Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                OutlinedTextField(
                    value = bankNameStr,
                    onValueChange = { bankNameStr = it },
                    label = { Text("KUDA MFB / WIRE OUTLET", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                OutlinedTextField(
                    value = bankAccountNameStr,
                    onValueChange = { bankAccountNameStr = it },
                    label = { Text("ACCOUNT FULL NAME", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                OutlinedTextField(
                    value = bankAccountNumberStr,
                    onValueChange = { bankAccountNumberStr = it },
                    label = { Text("ACCOUNT NUMBER", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(4.dp))
                
                Text("Crypto Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = cryptoAddrStr,
                    onValueChange = { cryptoAddrStr = it },
                    label = { Text("WALLET ADDRESS", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                OutlinedTextField(
                    value = cryptoNetStr,
                    onValueChange = { cryptoNetStr = it },
                    label = { Text("NETWORK (e.g., BEP20)", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
        }

        // RULES TEXT CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Website Popup Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = popupMsgStr,
                    onValueChange = { popupMsgStr = it },
                    placeholder = { Text("Announcement shown to clients...", color = Color.Gray, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                Text("Deposit Rules (Local Bank)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = depositRulesBankStr,
                    onValueChange = { depositRulesBankStr = it },
                    placeholder = { Text("Enter local bank deposit guidelines...", color = Color.Gray, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                Text("Deposit Rules (Cryptocurrency)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = depositRulesCryptoStr,
                    onValueChange = { depositRulesCryptoStr = it },
                    placeholder = { Text("Enter cryptocurrency deposit boundaries...", color = Color.Gray, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                Text("Withdrawal Rules (Local Bank)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = withdrawalRulesBankStr,
                    onValueChange = { withdrawalRulesBankStr = it },
                    placeholder = { Text("Enter local bank withdrawal boundaries...", color = Color.Gray, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                
                Text("Withdrawal Rules (Cryptocurrency)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = withdrawalRulesCryptoStr,
                    onValueChange = { withdrawalRulesCryptoStr = it },
                    placeholder = { Text("Enter cryptocurrency withdrawal boundaries...", color = Color.Gray, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
        }

        // GREEN SAVE ALL GLOBAL SETTINGS BUTTON
        Button(
            onClick = {
                val ticketPrice = ticketPriceStr.toDoubleOrNull() ?: 20.0
                val depRate = depositRateStr.toDoubleOrNull() ?: 1492.52
                val wthRate = withdrawRateStr.toDoubleOrNull() ?: 1420.24
                val maxWth = maxWithdrawalsStr.toIntOrNull() ?: 1
                val rL1 = refLevel1Str.toDoubleOrNull() ?: 15.0
                val rL2 = refLevel2Str.toDoubleOrNull() ?: 3.0
                val legacyRebate = legacyRefRebateStr.toDoubleOrNull() ?: 2.0
                
                viewModel.saveAllGlobalSettings(
                    ticketPrice,
                    depRate,
                    wthRate,
                    maxWth,
                    rL1,
                    rL2,
                    legacyRebate,
                    siteNameStr,
                    launchDateTimeStr,
                    pColorStr,
                    sColorStr,
                    globalWithdrawalLocked,
                    bankNameStr,
                    bankAccountNameStr,
                    bankAccountNumberStr,
                    cryptoAddrStr,
                    cryptoNetStr,
                    popupMsgStr,
                    depositRulesBankStr,
                    depositRulesCryptoStr,
                    withdrawalRulesBankStr,
                    withdrawalRulesCryptoStr
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = null, tint = DeepObsidian)
            Spacer(modifier = Modifier.width(8.dp))
            Text("SAVE ALL GLOBAL SETTINGS", color = DeepObsidian, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun AdminDailyDropTab(
    viewModel: InvestmentViewModel
) {
    var isEnabled by remember { mutableStateOf(viewModel.isDailyDropEnabled()) }
    val bonusList = viewModel.getDailyDropBonusList()
    
    var day1Str by remember { mutableStateOf(bonusList.getOrElse(0) { 1.00 }.toString()) }
    var day2Str by remember { mutableStateOf(bonusList.getOrElse(1) { 2.00 }.toString()) }
    var day3Str by remember { mutableStateOf(bonusList.getOrElse(2) { 3.00 }.toString()) }
    var day4Str by remember { mutableStateOf(bonusList.getOrElse(3) { 4.50 }.toString()) }
    var day5Str by remember { mutableStateOf(bonusList.getOrElse(4) { 6.00 }.toString()) }
    var day6Str by remember { mutableStateOf(bonusList.getOrElse(5) { 8.00 }.toString()) }
    var day7Str by remember { mutableStateOf(bonusList.getOrElse(6) { 12.00 }.toString()) }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.5.dp, GoldAccent.copy(alpha=0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Daily Drop System Toggle", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("MAINTENANCE FLAG GATE", color = DarkGreyText, fontSize = 9.sp)
                        }
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { 
                            isEnabled = it
                            viewModel.setDailyDropEnabled(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = GoldAccent.copy(alpha=0.4f))
                    )
                }
                Text(
                    text = "Configure if system users can claim daily consecutive sign-in drop tokens. When disabled (off), users will see a system maintenance alert on their dashboard.",
                    color = DarkGreyText,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSlateCard),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "CONSECUTIVE REWARD VALUES (DAY 1 TO 7)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    "Assign how much cash balance users can obtain daily. The day streak increments consecutively on 24-48 hour intervals.",
                    color = DarkGreyText,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
                
                listOf(
                    Pair("DAY 1 DROP VALUE (\$)", day1Str to { s: String -> day1Str = s }),
                    Pair("DAY 2 DROP VALUE (\$)", day2Str to { s: String -> day2Str = s }),
                    Pair("DAY 3 DROP VALUE (\$)", day3Str to { s: String -> day3Str = s }),
                    Pair("DAY 4 DROP VALUE (\$)", day4Str to { s: String -> day4Str = s }),
                    Pair("DAY 5 DROP VALUE (\$)", day5Str to { s: String -> day5Str = s }),
                    Pair("DAY 6 DROP VALUE (\$)", day6Str to { s: String -> day6Str = s }),
                    Pair("DAY 7 DROP VALUE (\$)", day7Str to { s: String -> day7Str = s })
                ).forEachIndexed { index, pair ->
                    OutlinedTextField(
                        value = pair.second.first,
                        onValueChange = pair.second.second,
                        label = { Text("Day ${index + 1} Reward Value (USD)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("\$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                }
                
                Button(
                    onClick = {
                        val d1 = day1Str.toFloatOrNull() ?: 1.00f
                        val d2 = day2Str.toFloatOrNull() ?: 2.00f
                        val d3 = day3Str.toFloatOrNull() ?: 3.00f
                        val d4 = day4Str.toFloatOrNull() ?: 4.50f
                        val d5 = day5Str.toFloatOrNull() ?: 6.00f
                        val d6 = day6Str.toFloatOrNull() ?: 8.00f
                        val d7 = day7Str.toFloatOrNull() ?: 12.00f
                        
                        viewModel.saveDailyDropBonuses(listOf(d1, d2, d3, d4, d5, d6, d7))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("SAVE ALL DROP BONUS VALUES", color = DeepObsidian, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
