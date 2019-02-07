package mhashim6.android.putback.data

import android.os.Environment
import mhashim6.android.putback.data.NotionsRealm.realmFile
import java.io.File


private const val TAG = "StorageRepository"

val isStorageWritable: Boolean
    get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

val isStorageReadable: Boolean
    get() = Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)

val storageFile: File by lazy {
    val file = File(Environment.getExternalStorageDirectory(), "Put-Back")
    file.apply { mkdirs() }
}

val backupFile by lazy { File(storageFile.absolutePath, "backup") }

fun backup(): Boolean {
    if (isStorageWritable.not() || realmFile.exists().not()) return false
    realmFile.copyTo(backupFile, overwrite = true)
    return true
}

fun restore(): Boolean {
    if (isStorageReadable.not() || backupFile.exists().not()) return false
    backupFile.copyTo(realmFile, overwrite = true)
    return true
}