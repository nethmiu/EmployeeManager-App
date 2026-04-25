package com.nethmi.employeemanager.ui.theme

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    var isLoginMode       by remember { mutableStateOf(true) }
    var username          by remember { mutableStateOf("") }
    var password          by remember { mutableStateOf("") }
    var confirmPassword   by remember { mutableStateOf("") }
    var passwordVisible   by remember { mutableStateOf(false) }
    var confirmVisible    by remember { mutableStateOf(false) }
    var errorMessage      by remember { mutableStateOf("") }
    var successMessage    by remember { mutableStateOf("") }

    // Scroll state එක නිර්මාණය කිරීම
    val scrollState = rememberScrollState()

    // ── Logic preserved exactly ───────────────────────────────────────────────
    fun isPasswordValid(pass: String): Boolean {
        val hasUpperCase   = pass.any { it.isUpperCase() }
        val hasSymbol      = pass.any { !it.isLetterOrDigit() }
        val isLongEnough   = pass.length >= 5
        return hasUpperCase && hasSymbol && isLongEnough
    }

    // ── Background gradient ───────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // scroll
                .padding(horizontal = 28.dp),
            verticalArrangement   = Arrangement.Top,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            // ඉහළින් මඳ ඉඩක් (Padding සඳහා)
            Spacer(Modifier.height(60.dp))

            // ── App icon + branding ───────────────────────────────────
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape    = CircleShape,
                    color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(100.dp)
                ) {}
                Surface(
                    shape    = CircleShape,
                    color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                    modifier = Modifier.size(82.dp)
                ) {}
                Surface(
                    shape    = CircleShape,
                    color    = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "TEAM MANAGER",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            // ── Title & subtitle — animated on mode switch ────────────
            AnimatedContent(
                targetState = isLoginMode,
                transitionSpec = {
                    (fadeIn() + slideInVertically { -20 }) togetherWith
                            (fadeOut() + slideOutVertically { 20 })
                },
                label = "title_anim"
            ) { loginMode ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = if (loginMode) "Welcome Back" else "Create Account",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text  = if (loginMode)
                            "Sign in to manage your team"
                        else
                            "Register a new admin account",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Card container ────────────────────────────────────────
            Surface(
                shape          = RoundedCornerShape(28.dp),
                color          = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 2.dp,
                modifier       = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Username field
                    OutlinedTextField(
                        value         = username,
                        onValueChange = { username = it },
                        label         = { Text("Username") },
                        leadingIcon   = {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine    = true,
                        shape         = RoundedCornerShape(16.dp),
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
                        )
                    )

                    // Password field
                    OutlinedTextField(
                        value                  = password,
                        onValueChange          = { password = it },
                        label                  = { Text("Password") },
                        leadingIcon            = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon           = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide" else "Show",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation   = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine             = true,
                        shape                  = RoundedCornerShape(16.dp),
                        modifier               = Modifier.fillMaxWidth(),
                        colors                 = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
                        )
                    )

                    // Confirm Password + rules (register mode only)
                    AnimatedVisibility(
                        visible = !isLoginMode,
                        enter   = fadeIn() + slideInVertically(),
                        exit    = fadeOut() + slideOutVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value                = confirmPassword,
                                onValueChange        = { confirmPassword = it },
                                label                = { Text("Confirm Password") },
                                leadingIcon          = {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon         = {
                                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                        Icon(
                                            if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                visualTransformation = if (confirmVisible)
                                    VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine           = true,
                                shape                = RoundedCornerShape(16.dp),
                                modifier             = Modifier.fillMaxWidth(),
                                colors               = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor    = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor  = MaterialTheme.colorScheme.outlineVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
                                )
                            )

                            // Password rules pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment   = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint     = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        "5+ characters · 1 Uppercase · 1 Symbol",
                                        style  = MaterialTheme.typography.labelSmall,
                                        color  = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // ── Error message ─────────────────────────────────
                    AnimatedVisibility(
                        visible = errorMessage.isNotEmpty(),
                        enter   = fadeIn() + slideInVertically(),
                        exit    = fadeOut() + slideOutVertically()
                    ) {
                        Surface(
                            shape    = RoundedCornerShape(12.dp),
                            color    = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment   = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint     = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    errorMessage,
                                    style      = MaterialTheme.typography.bodySmall,
                                    color      = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // ── Success message ───────────────────────────────
                    AnimatedVisibility(
                        visible = successMessage.isNotEmpty(),
                        enter   = fadeIn() + slideInVertically(),
                        exit    = fadeOut() + slideOutVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF34A853).copy(alpha = 0.13f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment   = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint     = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    successMessage,
                                    style      = MaterialTheme.typography.bodySmall,
                                    color      = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // ── Primary action button ─────────────────────────
                    Button(
                        onClick = {
                            errorMessage   = ""
                            successMessage = ""

                            if (username.isEmpty() || password.isEmpty()) {
                                errorMessage = "Please fill all fields"
                                return@Button
                            }

                            if (isLoginMode) {
                                val savedPass = sharedPref.getString("REG_PASSWORD_$username", null)
                                if ((username == "admin" && password == "1234") || savedPass == password) {
                                    sharedPref.edit().putString("CURRENT_USER", username).apply()
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Invalid username or password"
                                }
                            } else {
                                val isUserExists = sharedPref.contains("REG_PASSWORD_$username")
                                if (isUserExists) {
                                    errorMessage = "Username already taken! Try another."
                                } else if (password != confirmPassword) {
                                    errorMessage = "Passwords do not match!"
                                } else if (!isPasswordValid(password)) {
                                    errorMessage = "Password must be 5+ chars with 1 Uppercase & 1 Symbol"
                                } else {
                                    sharedPref.edit().putString("REG_PASSWORD_$username", password).apply()
                                    successMessage = "Registration Successful! Please Login."
                                    isLoginMode      = true
                                    username         = ""
                                    password         = ""
                                    confirmPassword  = ""
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape  = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            if (isLoginMode) Icons.Default.Person else Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            if (isLoginMode) "Sign In" else "Create Account",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight    = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Mode switch ───────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    if (isLoginMode) "New here?" else "Already registered?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = {
                        isLoginMode     = !isLoginMode
                        errorMessage    = ""
                        successMessage  = ""
                        username        = ""
                        password        = ""
                        confirmPassword = ""
                    }
                ) {
                    Text(
                        if (isLoginMode) "Register" else "Sign In",
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // priventing Keyboard hide
            Spacer(Modifier.height(60.dp))
        }
    }
}