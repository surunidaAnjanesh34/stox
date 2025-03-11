package uk.ac.tees.mad.stox.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.stox.R
import uk.ac.tees.mad.stox.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.stox.view.navigation.CARD_TRANSITION_KEY
import uk.ac.tees.mad.stox.view.navigation.SubGraph
import uk.ac.tees.mad.stox.viewmodel.SignUpScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SignUpScreen(
    navController: NavHostController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewmodel: SignUpScreenViewModel = koinViewModel()
) {
    val email by viewmodel.email.collectAsStateWithLifecycle()
    val password by viewmodel.password.collectAsStateWithLifecycle()
    val isPasswordVisible by viewmodel.isPasswordVisible.collectAsStateWithLifecycle()
    val isSignUpMode by viewmodel.isSignUpMode.collectAsStateWithLifecycle()
    val signUpResult by viewmodel.signUpResult.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    // Infinite transition for the background gradient animation
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val targetOffset = with(LocalDensity.current) {
        1000.dp.toPx()
    }
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = targetOffset, animationSpec = infiniteRepeatable(
            tween(20000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "offset"
    )
    val brushColors = listOf(
        MaterialTheme.colorScheme.surfaceContainerHighest,
        MaterialTheme.colorScheme.surfaceContainerLowest
    )
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .drawWithCache {
                    val brushSize = 400f
                    val brush = Brush.linearGradient(
                        colors = brushColors,
                        start = Offset(offset, offset),
                        end = Offset(offset + brushSize, offset + brushSize),
                        tileMode = TileMode.Mirror
                    )
                    onDrawBehind {
                        drawRect(brush)
                    }
                }, contentAlignment = Alignment.Center
        ) {

            if (!isSignUpMode) {
                when (val result = signUpResult) {
                    is AuthResult.Loading -> {
                        AlertDialog(onDismissRequest = {
                            viewmodel.switchSignUpMode()
                        }, icon = {
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }, title = {
                            Text(
                                text = "Signing Up",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, text = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }, confirmButton = { })
                    }

                    is AuthResult.Success -> {
                        // Handle successful sign-up
                        AlertDialog(icon = {
                            Icon(
                                Icons.Default.CloudDone,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }, title = {
                            Text(
                                text = "Sign Up Successful",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "You have successfully signed up.",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }, confirmButton = {
                            TextButton(onClick = {
                                navController.navigate(SubGraph.AuthGraph) {
                                    popUpTo(SubGraph.AuthGraph) {
                                        inclusive = true
                                    }
                                }
                            }) {
                                Text(
                                    text = "Go to Sign In Screen", fontWeight = FontWeight.Bold
                                )
                            }
                        }, onDismissRequest = {
                            navController.navigate(SubGraph.AuthGraph) {
                                popUpTo(SubGraph.AuthGraph) {
                                    inclusive = true
                                }
                            }
                        })

                    }

                    is AuthResult.Error -> {
                        // Handle sign-up error
                        AlertDialog(icon = {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }, title = {
                            Text(
                                text = "Error",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }, text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(result.exception.message.toString())
                            }
                        }, confirmButton = {
                            TextButton(onClick = {
                                viewmodel.switchSignUpMode()
                            }) {
                                Text(text = "Retry?", fontWeight = FontWeight.Bold)
                            }
                        }, onDismissRequest = {
                            viewmodel.switchSignUpMode()
                        })
                    }
                }
            }

            // Elevated Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = CARD_TRANSITION_KEY),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    4.dp, brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerLowest,
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        startX = 0f,
                        endX = 1000f,
                        tileMode = TileMode.Mirror,
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // App Logo and Name
                    Image(
                        painter = painterResource(id = R.drawable.stox),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(80.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                    Text(
                        text = "Stox",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Create an account to get started.",
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Email TextField
                    OutlinedTextField(
                        value = email,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequesterEmail),
                        onValueChange = {
                            viewmodel.updateEmail(it)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        label = {
                            Text(
                                text = "Email", color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterPassword.requestFocus()
                        }),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    // Password TextField
                    OutlinedTextField(value = password,
                        onValueChange = {
                            viewmodel.updatePassword(it)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        label = {
                            Text(
                                text = "Password", color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = "Password",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequesterPassword),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                viewmodel.togglePasswordVisibility()
                            }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        })

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        enabled = email.isNotBlank() && password.isNotBlank(), onClick = {
                            viewmodel.signUp(email, password)
                            viewmodel.switchSignUpMode()
                        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.HowToReg,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Sign Up", style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Already have an account?
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account?",
                            textAlign = TextAlign.Center,
                        )
                        TextButton(onClick = {
                            navController.navigate(SubGraph.AuthGraph) {
                                popUpTo(SubGraph.AuthGraph) {
                                    inclusive = true
                                }
                            }
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sign In",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}