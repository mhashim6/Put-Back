package mhashim.android.putback.data

import io.realm.RealmObject

open class Priority(
        var interval: Int = 7,
        var name: String = "custom",
        var isBuiltIn: Boolean = false,
        var color: String = ""  //TODO
) : RealmObject()


