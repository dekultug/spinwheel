package com.amvfunny.dev.wheelist.presentaition

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: " + token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.tag(TAG).d("From: " + remoteMessage.from)

        if (remoteMessage.data.size > 0) {
            Timber.tag(TAG).d("Message data payload: " + remoteMessage.data)
        }

        if (remoteMessage.notification != null) {
            Timber.tag(TAG).d("Message Notification Body: " + remoteMessage.notification!!.body)
        }
    }

    companion object {
        private const val TAG = "SERVICE"
    }
}
