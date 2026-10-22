package com.example.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.SampleMarketplaceData
import com.example.ui.components.LiquidFeatureGradientCard
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.LiquidPillTextField
import com.example.ui.components.LiquidPrimaryButton
import com.example.ui.components.MainGlassPanel
import com.example.ui.theme.AccentBluePurple
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextSecondary
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RegistrationDraft(
  val fullName: String = "",
  val companyName: String = "",
  val phone: String = "",
  val city: String = "",
  val accountType: String = "Buyer / Customer",
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

fun attemptAutoSignIn(
  context: Context,
  credentialManager: CredentialManager,
  onAuthSuccess: () -> Unit,
  onUnauthenticated: () -> Unit,
  scope: CoroutineScope,
) {
  if (Firebase.auth.currentUser != null) {
    onAuthSuccess()
    return
  }
  val activityContext = context.findActivity() ?: context
  val clientId = try {
    context.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    onUnauthenticated()
    return
  }

  val googleIdOption = GetGoogleIdOption.Builder()
    .setFilterByAuthorizedAccounts(true)
    .setServerClientId(clientId)
    .setAutoSelectEnabled(true)
    .build()

  val request = GetCredentialRequest.Builder()
    .addCredentialOption(googleIdOption)
    .build()

  scope.launch {
    try {
      val result = credentialManager.getCredential(activityContext, request)
      val credential = result.credential
      if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        Firebase.auth.signInWithCredential(authCredential).await()
        onAuthSuccess()
      } else {
        onUnauthenticated()
      }
    } catch (e: Exception) {
      onUnauthenticated()
    }
  }
}

fun onGoogleSignInClicked(
  context: Context,
  credentialManager: CredentialManager,
  onAuthSuccess: () -> Unit,
  onAuthError: (String) -> Unit,
  scope: CoroutineScope,
  onAuthCancelled: () -> Unit = {},
) {
  val clientId = try {
    context.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    onAuthError("Google Sign-In configuration missing: default_web_client_id not found")
    return
  }

  val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
  val request = GetCredentialRequest.Builder()
    .addCredentialOption(signInOption)
    .build()

  scope.launch {
    try {
      val activityContext = context.findActivity() ?: context
      val result = credentialManager.getCredential(activityContext, request)
      val credential = result.credential
      if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        Firebase.auth.signInWithCredential(authCredential).await()
        onAuthSuccess()
      } else {
        onAuthError("Unexpected credential type returned.")
      }
    } catch (e: GetCredentialCancellationException) {
      Log.w("Auth", "Google Sign-In cancelled or dismissed: ${e.message}", e)
      onAuthCancelled()
    } catch (e: Exception) {
      Log.e("Auth", "Google Sign-In failed", e)
      onAuthError(e.localizedMessage ?: "Sign in failed. Please try again.")
    }
  }
}

fun signOut(
  context: Context,
  credentialManager: CredentialManager,
  onSignOutComplete: () -> Unit,
  scope: CoroutineScope,
) {
  Firebase.auth.signOut()
  scope.launch {
    try {
      credentialManager.clearCredentialState(ClearCredentialStateRequest())
    } catch (e: Exception) {
      Log.e("Auth", "Failed to clear credential state", e)
    } finally {
      onSignOutComplete()
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
  onAuthSuccess: (RegistrationDraft) -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  var fullName by rememberSaveable { mutableStateOf("") }
  var companyName by rememberSaveable { mutableStateOf("") }
  var phone by rememberSaveable { mutableStateOf("") }
  var city by rememberSaveable { mutableStateOf("") }
  var selectedRole by rememberSaveable { mutableStateOf(SampleMarketplaceData.accountRoles.first()) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val triggerInteractiveSignIn = {
    if (!isLoading) {
      errorMessage = null
      isLoading = true
      onGoogleSignInClicked(
        context = context,
        credentialManager = credentialManager,
        onAuthSuccess = {
          isLoading = false
          onAuthSuccess(
            RegistrationDraft(
              fullName = fullName,
              companyName = companyName,
              phone = phone,
              city = city,
              accountType = selectedRole
            )
          )
        },
        onAuthError = { msg ->
          isLoading = false
          errorMessage = msg
        },
        scope = scope,
        onAuthCancelled = {
          isLoading = false
        }
      )
    }
  }

  LaunchedEffect(Unit) {
    withFrameNanos { }
    delay(1200L)
    if (!isLoading && Firebase.auth.currentUser == null) {
      attemptAutoSignIn(
        context = context,
        credentialManager = credentialManager,
        onAuthSuccess = {
          onAuthSuccess(
            RegistrationDraft(
              fullName = fullName,
              companyName = companyName,
              phone = phone,
              city = city,
              accountType = selectedRole
            )
          )
        },
        onUnauthenticated = {},
        scope = scope
      )
    }
  }

  LiquidGlassBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 560.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
      ) {
        LiquidFeatureGradientCard(
          title = "Construction Waste. Renewed Value.",
          subtitle = "Digital marketplace connecting demolition contractors, recycled aggregate buyers, and mobile crushing services.",
          badgeText = "C&D Marketplace",
          buttonText = if (isLoading) "Signing in..." else "Sign in with Google",
          onButtonClick = triggerInteractiveSignIn,
          testTag = "hero_google_sign_in_button"
        )

        MainGlassPanel(
          title = "NEXCORE Account Setup",
          subtitle = "Select your marketplace role & sign in securely with Google"
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
              text = "1. Choose Marketplace Role",
              style = MaterialTheme.typography.titleMedium,
              color = TextDark
            )

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              SampleMarketplaceData.accountRoles.forEach { role ->
                LiquidGlassChip(
                  text = role,
                  selected = selectedRole == role,
                  isSuccessChip = selectedRole == role,
                  onClick = { selectedRole = role },
                  testTag = "role_chip_${role.take(10).lowercase().replace(" ", "_")}"
                )
              }
            }

            if (errorMessage != null) {
              LiquidGlassCard(cornerRadius = 16.dp) {
                Text(
                  text = errorMessage ?: "",
                  style = MaterialTheme.typography.bodySmall,
                  color = DangerCoral
                )
              }
            }

            if (isLoading) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                CircularProgressIndicator(
                  color = AccentBluePurple,
                  modifier = Modifier.size(28.dp),
                  strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = "Authenticating with Google...",
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextDark
                )
              }
            } else {
              LiquidPrimaryButton(
                text = "Sign in with Google",
                icon = Icons.Default.Security,
                onClick = triggerInteractiveSignIn,
                modifier = Modifier.fillMaxWidth(),
                testTag = "google_sign_in_button"
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "2. Optional Organization & Location Details",
              style = MaterialTheme.typography.titleMedium,
              color = TextDark
            )
            Text(
              text = "You can pre-fill your contractor/company profile now or update it anytime in your dashboard.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )

            LiquidPillTextField(
              value = fullName,
              onValueChange = { fullName = it },
              label = "Full Name (or leave blank to use Google profile name)",
              placeholder = "e.g., Rajesh Sharma",
              trailingIcon = Icons.Default.Person,
              testTag = "auth_full_name_input"
            )

            LiquidPillTextField(
              value = companyName,
              onValueChange = { companyName = it },
              label = "Company / Contractor Name (Optional)",
              placeholder = "e.g., Sharma Civil & Demolition Pvt Ltd",
              trailingIcon = Icons.Default.Apartment,
              testTag = "auth_company_input"
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              LiquidPillTextField(
                value = city,
                onValueChange = { city = it },
                label = "City / Service Area",
                placeholder = "e.g., Bengaluru, KA",
                trailingIcon = Icons.Default.LocationCity,
                modifier = Modifier.weight(1f),
                testTag = "auth_city_input"
              )

              LiquidPillTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Contact Phone",
                placeholder = "+91 98765 43210",
                trailingIcon = Icons.Default.Phone,
                modifier = Modifier.weight(1f),
                testTag = "auth_phone_input"
              )
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Protected by Google Identity & Zero-Trust Firestore Security",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }
        }
      }
    }
  }
}
