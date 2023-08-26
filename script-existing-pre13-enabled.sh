adb shell pm grant com.simple.grantpushnotificationpermission android.permission.POST_NOTIFICATIONS
adb shell pm set-permission-flags com.simple.grantpushnotificationpermission android.permission.POST_NOTIFICATIONS user-set
adb shell pm clear-permission-flags com.simple.grantpushnotificationpermission android.permission.POST_NOTIFICATIONS user-fixed