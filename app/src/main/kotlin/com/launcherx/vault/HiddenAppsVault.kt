package com.launcherx.vault

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.launcherx.home.AppInfo
import com.launcherx.home.IconCell
import com.launcherx.ui.theme.LauncherColors
import com.launcherx.ui.theme.SFProFontFamily
import java.security.MessageDigest

@Composable
fun HiddenAppsVault(
    allApps: List<AppInfo>,
    hiddenApps: List<AppInfo>,
    iconBitmaps: Map<String, Bitmap>,
    isOpen: Boolean,
    onClose: () -> Unit,
    onLaunchApp: (String) -> Unit,
    onUnhideApp: (String) -> Unit,
    onHideApp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPinVerified by remember { mutableStateOf(false) }
    var isPinSetup by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isAddingApps by remember { mutableStateOf(false) }
    
    // Keyword recovery state
    var isRecoveringPin by remember { mutableStateOf(false) }
    var recoveryKeyword by remember { mutableStateOf("") }
    var recoveryError by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    
    // Change PIN flow states
    var isChangingPin by remember { mutableStateOf(false) }
    var changePinStep by remember { mutableIntStateOf(0) } // 0=verify old, 1=enter new, 2=confirm new
    var newPinCandidate by remember { mutableStateOf("") }
    var changePinError by remember { mutableStateOf(false) }
    var biometricVerifiedForChange by remember { mutableStateOf(false) }

    // Re-check PIN status every time the vault opens (no caching!)
    var hasPin by remember { mutableStateOf(false) }

    LaunchedEffect(isOpen) {
        if (!isOpen) {
            isPinVerified = false
            isPinSetup = false
            isError = false
            isAddingApps = false
            isChangingPin = false
            changePinStep = 0
            newPinCandidate = ""
            changePinError = false
            biometricVerifiedForChange = false
            isRecoveringPin = false
            recoveryKeyword = ""
            recoveryError = false
        } else {
            // Re-read PIN status from encrypted prefs every time vault opens
            hasPin = hasPinSet(context)
            if (hasPin) {
                // Try biometric first
                tryBiometricAuth(context) { success ->
                    if (success) {
                        isPinVerified = true
                    }
                }
            }
        }
    }

    if (isOpen) {
        BackHandler(enabled = true) {
            if (isAddingApps) {
                isAddingApps = false
            } else if (isChangingPin) {
                isChangingPin = false
                changePinStep = 0
                biometricVerifiedForChange = false
                changePinError = false
                newPinCandidate = ""
                recoveryKeyword = ""
                recoveryError = false
            } else if (isRecoveringPin) {
                isRecoveringPin = false
                recoveryKeyword = ""
                recoveryError = false
            } else {
                onClose()
            }
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        if (!isPinVerified) {
            if (!hasPin && !isPinSetup) {
                PinScreen(
                    title = "Set Passcode",
                    onPinEntered = { pin ->
                        savePinHash(context, pin)
                        isPinSetup = true
                        isPinVerified = true
                    },
                    onCancel = onClose,
                    isError = false
                )
            } else {
                if (isRecoveringPin) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xEE000000))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Recovery Keyword",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SFProFontFamily
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = recoveryKeyword,
                            onValueChange = { recoveryKeyword = it; recoveryError = false },
                            label = { Text("Enter secret keyword") },
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (recoveryError) {
                            Text(
                                "Incorrect keyword.",
                                color = LauncherColors.deleteRed,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Row {
                            TextButton(onClick = { isRecoveringPin = false; recoveryKeyword = "" }) {
                                Text("Cancel", color = Color.White.copy(0.7f))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    if (recoveryKeyword.trim().equals("Juicy pussy", ignoreCase = true)) {
                                        isRecoveringPin = false
                                        isPinVerified = true
                                        isChangingPin = true
                                        changePinStep = 1 // Skip old pin check, go straight to new pin
                                        recoveryKeyword = ""
                                    } else {
                                        recoveryError = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text("Verify", color = Color.Black)
                            }
                        }
                    }
                } else {
                    PinScreen(
                        title = "Enter Passcode",
                        onPinEntered = { pin ->
                            if (verifyPin(context, pin)) {
                                isPinVerified = true
                                isError = false
                            } else {
                                isError = true
                            }
                        },
                        onCancel = onClose,
                        onForgotPin = { isRecoveringPin = true },
                        isError = isError
                    )
                }
            }
        } else {
            // Check if changing PIN
            if (isChangingPin) {
                when (changePinStep) {
                    0 -> {
                        // Step 1: Verify keyword (replaces fingerprint)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xEE000000))
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Verify Identity",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SFProFontFamily
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            androidx.compose.material3.OutlinedTextField(
                                value = recoveryKeyword,
                                onValueChange = { recoveryKeyword = it; recoveryError = false },
                                label = { Text("Enter secret keyword") },
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(0.5f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (recoveryError) {
                                Text(
                                    "Incorrect keyword.",
                                    color = LauncherColors.deleteRed,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Row {
                                TextButton(onClick = { isChangingPin = false; recoveryKeyword = "" }) {
                                    Text("Cancel", color = Color.White.copy(0.7f))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Button(
                                    onClick = {
                                        if (recoveryKeyword.trim().equals("Juicy pussy", ignoreCase = true)) {
                                            changePinStep = 1 // Go to enter new PIN step
                                            recoveryKeyword = ""
                                        } else {
                                            recoveryError = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                ) {
                                    Text("Proceed", color = Color.Black)
                                }
                            }
                        }
                    }
                    1 -> {
                        // Step 3: Enter new PIN
                        PinScreen(
                            title = "Enter New PIN",
                            onPinEntered = { pin ->
                                newPinCandidate = pin
                                changePinStep = 2
                                changePinError = false
                            },
                            onCancel = { isChangingPin = false; changePinStep = 0; biometricVerifiedForChange = false; changePinError = false },
                            isError = false
                        )
                    }
                    2 -> {
                        // Step 4: Confirm new PIN
                        PinScreen(
                            title = "Confirm New PIN",
                            onPinEntered = { pin ->
                                if (pin == newPinCandidate) {
                                    savePinHash(context, pin)
                                    isChangingPin = false
                                    changePinStep = 0
                                    biometricVerifiedForChange = false
                                    changePinError = false
                                    newPinCandidate = ""
                                } else {
                                    changePinError = true
                                    changePinStep = 1
                                    newPinCandidate = ""
                                }
                            },
                            onCancel = { isChangingPin = false; changePinStep = 0; biometricVerifiedForChange = false; changePinError = false },
                            isError = changePinError
                        )
                    }
                }
            } else {
            // Vault content
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xEE000000))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount > 50 && !isAddingApps) onClose()
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(20.dp)
                ) {
                    if (isAddingApps) {
                        // App Selection Screen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Select App to Hide",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SFProFontFamily,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { isAddingApps = false }) {
                                Text("Done", color = Color(0xFF007AFF), fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val visibleApps = allApps.filter { app -> hiddenApps.none { it.packageName == app.packageName } }
                        
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = visibleApps, 
                                key = { it.packageName }
                            ) { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onHideApp(app.packageName) }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = iconBitmaps[app.packageName]
                                    if (icon != null) {
                                        Image(
                                            bitmap = icon.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    } else {
                                        Box(modifier = Modifier.size(40.dp).background(Color.Gray))
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = app.label,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontFamily = SFProFontFamily
                                    )
                                }
                            }
                        }
                    } else {
                        // Regular Vault Screen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hidden Apps",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SFProFontFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Swipe down to close",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 13.sp,
                                    fontFamily = SFProFontFamily
                                )
                            }
                            // 3-dot menu
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "More options",
                                        tint = Color.White
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier.background(Color(0xFF2C2C2E))
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Change PIN", color = Color.White) },
                                        onClick = {
                                            showMenu = false
                                            isChangingPin = true
                                            changePinStep = 0
                                            biometricVerifiedForChange = false
                                        }
                                    )
                                }
                            }
                            IconButton(onClick = { isAddingApps = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add Apps",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (hiddenApps.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hidden apps",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 16.sp,
                                    fontFamily = SFProFontFamily
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                items(
                                    items = hiddenApps,
                                    key = { it.packageName }
                                ) { app ->
                                    IconCell(
                                        app = app,
                                        iconBitmap = iconBitmaps[app.packageName],
                                        onLaunch = { onLaunchApp(app.packageName) },
                                        onRemove = { onUnhideApp(app.packageName) },
                                        onUninstall = { }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            } // end if isChangingPin else
        }
    }
}

private fun hasPinSet(context: Context): Boolean {
    return try {
        val prefs = getEncryptedPrefs(context)
        prefs.contains("vault_pin_hash")
    } catch (_: Exception) { false }
}

private fun savePinHash(context: Context, pin: String) {
    try {
        val prefs = getEncryptedPrefs(context)
        val hash = hashPin(pin)
        prefs.edit().putString("vault_pin_hash", hash).apply()
    } catch (_: Exception) { }
}

private fun verifyPin(context: Context, pin: String): Boolean {
    return try {
        val prefs = getEncryptedPrefs(context)
        val storedHash = prefs.getString("vault_pin_hash", "") ?: ""
        hashPin(pin) == storedHash
    } catch (_: Exception) { false }
}

private fun hashPin(pin: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(pin.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}

private fun getEncryptedPrefs(context: Context): android.content.SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    return EncryptedSharedPreferences.create(
        context,
        "launcherx_vault_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

private fun tryBiometricAuth(context: Context, onResult: (Boolean) -> Unit) {
    val biometricManager = BiometricManager.from(context)
    val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    
    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
        // Biometrics not available, fall back to PIN
        onResult(false)
        return
    }
    
    val activity = context as? FragmentActivity ?: run {
        onResult(false)
        return
    }
    
    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(true)
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onResult(false)
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Don't call onResult here — user can retry
            }
        }
    )
    
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Hidden Apps")
        .setSubtitle("Use your fingerprint to access the vault")
        .setNegativeButtonText("Use PIN")
        .build()
    
    biometricPrompt.authenticate(promptInfo)
}
