package uk.ac.tees.mad.stox.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.stox.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.stox.model.dataclass.firebase.UserData
import uk.ac.tees.mad.stox.model.dataclass.firebase.UserDetails
import uk.ac.tees.mad.stox.view.navigation.Dest
import uk.ac.tees.mad.stox.view.navigation.SubGraph
import uk.ac.tees.mad.stox.viewmodel.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileScreenViewModel = koinViewModel(),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val offlineMode by viewModel.offlineMode.collectAsStateWithLifecycle()
    val context = LocalContext.current
    context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    //val isDarkMode = LocalIsDarkMode.current
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val userDetailsResult by viewModel.userDetails.collectAsStateWithLifecycle()
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = "Profile",
                        maxLines = 1,
                        fontSize = 30.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }, navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = {
                        navController.navigate(Dest.HomeScreen) {
                            popUpTo(Dest.HomeScreen) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }, actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    AnimatedVisibility(offlineMode == true) {

                        Icon(
                            Icons.Outlined.CloudOff,
                            "Offline",
                            tint = MaterialTheme.colorScheme.error
                        )

                    }
                    AnimatedVisibility(offlineMode == false) {
                        Icon(Icons.Outlined.CloudDone, "Online", tint = Color.Green)
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }, scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
//            item {
//                AppSettingsSection(isDarkMode, sharedPreferences)
//            }
            item {
                PersonalDetailsSection(userDetailsResult, userData, viewModel)
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(8.dp), thickness = 2.dp)
                SignOutSection(navController, viewModel)
            }
        }
    }
}

@Composable
fun PersonalDetailsSection(
    userDetailsResult: AuthResult<UserDetails>,
    userData: UserData,
    viewModel: ProfileScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SectionHeader(
            icon = Icons.Default.Person, title = "Profile Details"
        )
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxSize(),
            border = BorderStroke(
                2.dp, brush = Brush.horizontalGradient(
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (userDetailsResult) {
                    is AuthResult.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is AuthResult.Success -> {
                        userData.userDetails?.let {
                            ProfileContent(it, viewModel)
                        }
                    }

                    is AuthResult.Error -> {
                        val error = (userDetailsResult as AuthResult.Error).exception
                        Text(
                            text = "Error: ${error.message}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(userDetails: UserDetails, viewModel: ProfileScreenViewModel) {
    var showNameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DetailRow(
            icon = Icons.Default.AccountCircle,
            iconDesc = "User ID",
            label = "User ID",
            value = userDetails.userId
        )
        HorizontalDivider(modifier = Modifier.padding(4.dp), thickness = 2.dp)
        DetailRow(
            icon = Icons.Filled.Email,
            iconDesc = "Email",
            label = "Email",
            value = userDetails.email ?: "Not available"
        )
        HorizontalDivider(modifier = Modifier.padding(4.dp), thickness = 2.dp)
        DetailRow(icon = Icons.Filled.Person,
            iconDesc = "Name",
            label = "Name",
            value = if (userDetails.displayName != null && userDetails.displayName.isNotEmpty()) userDetails.displayName else "Not available",
            endContent = {
                IconButton(onClick = {
                    showNameDialog = true
                    newName = userDetails.displayName ?: ""
                }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Name")
                }
            })

        // Name Update Dialog
        if (showNameDialog) {
            AlertDialog(onDismissRequest = { showNameDialog = false },
                title = { Text("Update Name") },
                text = {
                    OutlinedTextField(value = newName,
                        onValueChange = { newName = it },
                        label = { Text("New Name") })
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.updateDisplayName(newName)
                        showNameDialog = false
                    }) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    Button(onClick = { showNameDialog = false }) {
                        Text("Cancel")
                    }
                })
        }
    }
}

@Composable
fun SignOutSection(navController: NavHostController, viewModel: ProfileScreenViewModel) {
    Button(
        onClick = {
            viewModel.signOut()
            navController.navigate(SubGraph.AuthGraph) {
                popUpTo(SubGraph.HomeGraph) {
                    inclusive = true
                }
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "Sign Out", style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    iconDesc: String,
    label: String,
    value: String,
    endContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = icon, contentDescription = iconDesc)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "$label:", fontWeight = FontWeight.Bold)
            Text(text = value)
        }
        if (endContent != null) {
            endContent()
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}