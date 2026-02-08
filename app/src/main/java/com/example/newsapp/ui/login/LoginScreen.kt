package com.example.newsapp.ui.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.newsapp.ui.navigation.Screen
import com.example.newsapp.viewmodel.LoginViewModel
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data class representing a floating icon with its properties.
 * Each floating icon has a position, size, icon, and animation parameters.
 */
data class FloatingIcon(
    val icon: ImageVector,
    val initialX: Float,
    val initialY: Float,
    val size: Dp,
    val animationDuration: Int,
    val floatRange: Float,
    val rotationRange: Float,
    val alpha: Float
)

/**
 * Premium Login Screen with floating animated icons and modern design.
 * Features:
 * - Gradient background
 * - Floating animated news-related icons
 * - Glass-morphism style login card
 * - Smooth animations
 *
 * @param navController The navigation controller for navigating to other screens.
 * @param viewModel The ViewModel associated with this screen, provided by Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // Collect state from ViewModel
    val email by viewModel.email
    val password by viewModel.password
    val loginError by viewModel.loginError

    // Define floating icons with their properties
    // Using icons available in the standard Material Icons library
    val floatingIcons = remember {
        listOf(
            FloatingIcon(Icons.Outlined.MailOutline, 0.1f, 0.15f, 32.dp, 3000, 20f, 15f, 0.3f),
            FloatingIcon(Icons.Outlined.Info, 0.85f, 0.1f, 28.dp, 3500, 25f, -20f, 0.25f),
            FloatingIcon(Icons.Outlined.Home, 0.15f, 0.35f, 24.dp, 4000, 18f, 10f, 0.2f),
            FloatingIcon(Icons.Outlined.Star, 0.9f, 0.3f, 26.dp, 3200, 22f, -15f, 0.28f),
            FloatingIcon(Icons.Outlined.ThumbUp, 0.05f, 0.55f, 30.dp, 3800, 15f, 12f, 0.22f),
            FloatingIcon(Icons.Outlined.Settings, 0.92f, 0.5f, 34.dp, 2800, 28f, -18f, 0.3f),
            FloatingIcon(Icons.Outlined.Search, 0.12f, 0.75f, 22.dp, 4200, 20f, 8f, 0.18f),
            FloatingIcon(Icons.Outlined.Notifications, 0.88f, 0.72f, 28.dp, 3600, 24f, -12f, 0.25f),
            FloatingIcon(Icons.Outlined.Star, 0.25f, 0.88f, 26.dp, 3400, 16f, 20f, 0.2f),
            FloatingIcon(Icons.Outlined.Favorite, 0.75f, 0.85f, 24.dp, 4000, 22f, -10f, 0.22f),
            FloatingIcon(Icons.Outlined.Share, 0.5f, 0.08f, 20.dp, 3900, 18f, 14f, 0.15f),
            FloatingIcon(Icons.Outlined.Home, 0.4f, 0.92f, 30.dp, 3100, 26f, -16f, 0.28f),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),  // Dark navy
                        Color(0xFF16213e),  // Deep blue
                        Color(0xFF0f3460),  // Medium blue
                        Color(0xFF1a1a2e)   // Dark navy
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        // Floating Icons Layer
        floatingIcons.forEach { floatingIcon ->
            FloatingIconAnimation(floatingIcon)
        }

        // Decorative gradient circles in background
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-50).dp)
                .blur(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366f1).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 100.dp)
                .blur(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFec4899).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon with glow effect
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .blur(30.dp)
                        .background(
                            color = Color(0xFF6366f1).copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
                // Icon container
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366f1),
                                    Color(0xFF8b5cf6)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "App Logo",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title
            Text(
                text = "NewsApp",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Stay informed, Stay ahead",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login Card with glass morphism effect
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Text(
                        text = "Sign in to continue",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = { Text("Email", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = "Email Icon",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366f1),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF6366f1)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = { Text("Password", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = "Password Icon",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366f1),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF6366f1)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login Button with gradient
                    Button(
                        onClick = {
                            viewModel.onLoginClick {
                                navController.navigate(Screen.HomeScreen.route) {
                                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF6366f1),
                                            Color(0xFF8b5cf6),
                                            Color(0xFFa855f7)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Sign In",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Login",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Error Dialog
    loginError?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(
                    "Login Failed",
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(error) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366f1)
                    )
                ) {
                    Text("OK")
                }
            },
            containerColor = Color(0xFF1a1a2e),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }
}

/**
 * Composable that animates a single floating icon.
 * The icon floats up and down, rotates slightly, and scales.
 */
@Composable
fun FloatingIconAnimation(floatingIcon: FloatingIcon) {
    // Infinite transition for continuous animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    // Vertical floating animation
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = floatingIcon.floatRange,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = floatingIcon.animationDuration,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    // Rotation animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = -floatingIcon.rotationRange,
        targetValue = floatingIcon.rotationRange,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (floatingIcon.animationDuration * 1.5).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Scale pulsing animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (floatingIcon.animationDuration * 0.8).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = floatingIcon.icon,
            contentDescription = null,
            modifier = Modifier
                .offset(
                    x = (maxWidth * floatingIcon.initialX),
                    y = (maxHeight * floatingIcon.initialY) + offsetY.dp
                )
                .size(floatingIcon.size)
                .rotate(rotation)
                .scale(scale)
                .alpha(floatingIcon.alpha),
            tint = Color.White
        )
    }
}
