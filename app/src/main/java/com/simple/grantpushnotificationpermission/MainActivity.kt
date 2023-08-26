package com.simple.grantpushnotificationpermission

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.simple.grantpushnotificationpermission.ui.theme.GrantPushNotificationPermissionTheme

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var permissionStateCheck = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                permissionStateCheck.value = it
                val state = if (it) "Granted" else "Not Granted"
                Toast
                    .makeText(this, "Push Notification Permission is $state", Toast.LENGTH_SHORT)
                    .show()
            }
        createNotificationChannel()
        setContent {
            GrantPushNotificationPermissionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        permissionStateCheck.value =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.default_notification_channel_id)
            val name = getString(R.string.default_notification_channel_name)
            val descriptionText = getString(R.string.default_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Composable
    fun Greeting(modifier: Modifier = Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Permission ${if (permissionStateCheck.value) "Granted" else "Denied"}",
                modifier = modifier
            )

            Button(onClick = { openPermissionSettingPage() }) {
                Text("Open Permission Setting Page")
            }

            Button(onClick = { requestForPermission() }) {
                Text("Request for Permission")
            }

            Button(onClick = { postPushNotification() }) {
                Text("Post Notification")
            }

            Button(onClick = { shouldShowRequestPermissionRationale() }) {
                Text("Should show Rationale")
            }
        }
    }

    private fun shouldShowRequestPermissionRationale() {
        val shouldShowRational = shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
        Toast.makeText(this, "Should ${ if (!shouldShowRational) "NOT " else "" }show rational",
            Toast.LENGTH_SHORT).show()

    }

    private fun openPermissionSettingPage() {
        val intent = Intent().apply {
            action = "android.settings.APP_NOTIFICATION_SETTINGS"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //for Android 5-7
            putExtra("app_package", packageName)
            putExtra("app_uid", applicationInfo.uid)
            // for Android 8 and above
            putExtra("android.provider.extra.APP_PACKAGE", packageName)
        }
        startActivity(intent)
    }

    private fun requestForPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun postPushNotification() {
        val id = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Push Title")
            .setContentText("Push Content Out Loudly")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationBuilder.build()

        notificationManager.notify(0, notificationBuilder.build())
    }
}

