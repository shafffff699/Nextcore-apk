package com.example.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
  CREATE("create"),
  UPDATE("update"),
  DELETE("delete"),
  LIST("list"),
  GET("get"),
  WRITE("write"),
}

fun handleFirestoreError(
  error: Throwable,
  operationType: OperationType,
  path: String?,
  auth: FirebaseAuth = FirebaseAuth.getInstance(),
): Nothing {
  val currentUser = auth.currentUser
  val providerInfoArray = JSONArray()
  currentUser?.providerData?.forEach { profile ->
    providerInfoArray.put(
      JSONObject().apply {
        put("providerId", profile.providerId)
        put("displayName", profile.displayName)
        put("email", profile.email)
        put("photoUrl", profile.photoUrl?.toString())
      }
    )
  }

  val authInfo = JSONObject().apply {
    put("userId", currentUser?.uid)
    put("email", currentUser?.email)
    put("emailVerified", currentUser?.isEmailVerified)
    put("isAnonymous", currentUser?.isAnonymous)
    put("tenantId", currentUser?.tenantId)
    put("providerInfo", providerInfoArray)
  }

  val errInfo = JSONObject().apply {
    put("error", error.message ?: error.toString())
    put("operationType", operationType.value)
    put("path", path)
    put("authInfo", authInfo)
  }

  val jsonString = errInfo.toString()
  Log.e("FirestoreError", "Firestore Error: $jsonString", error)
  throw RuntimeException(jsonString, error)
}
