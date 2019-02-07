package mhashim6.android.putback.data

import android.os.Environment
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.util.Log.wtf
import mhashim6.android.putback.data.NotionsRealm.realmFile
import java.io.File


private const val TAG = "StorageRepository"

val isStorageWritable: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

val isStorageReadable: Boolean
    get() = Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)

val storageFile: File by lazy {
    val file = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "Put-Back")
    if (!file.mkdirs()) wtf(TAG, "Backup directory not created")
    file
}

val backupFile by lazy { File(storageFile.absolutePath, "backup") }

fun backup() {
    if (isStorageWritable)
        realmFile.copyTo(backupFile, overwrite = true)
}

fun restore() {
    if (isStorageReadable)
        backupFile.copyTo(realmFile, overwrite = true)
}