package com.andb.apps.credits

import android.graphics.drawable.Drawable

open class CreditsBuilder {
    var appIcon: Drawable? = null
    var appIconRes: Drawable? = null
    var appName: String? = null
    var playLink: String? = null
    val libraries = ArrayList<Library>()
}

class Library(val name: String, val link: String, val license: License)

