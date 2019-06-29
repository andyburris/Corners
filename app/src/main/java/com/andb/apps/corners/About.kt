package com.andb.apps.corners

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutFragment
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense

class About : MaterialAboutFragment() {

    private val aboutCard by lazy {
        MaterialAboutCard.Builder()
            .addItem(MaterialAboutTitleItem.Builder().text(R.string.app_name).icon(R.drawable.app_icon).build())
            .addItem(ConvenienceBuilder.createVersionActionItem(context, context?.getDrawable(R.drawable.ic_info_outline_black_24dp), resources.getText(R.string.about_version), true))
            .addItem(MaterialAboutActionItem.Builder().icon(R.drawable.ic_language_black_24dp).text(R.string.about_translations_title).subText(getTranslators()).setOnClickAction { openInBrowser("https://catalyststudios.oneskyapp.com/collaboration/project?id=154972") }.build())
            .build()
    }

    private val licenseCard by lazy { MaterialAboutCard.Builder()
        .title(R.string.about_licences)
        .addItem(createLicenseItem("Material Dialogs", "2019", "Aidan Follestad", OpenSourceLicense.APACHE_2, "https://github.com/afollestad/material-dialogs"))
        .addItem(createLicenseItem("android-gif-drawable", "2013", "Karol Wrótniak, Droids on Roids LLC", OpenSourceLicense.MIT, "https://github.com/koral--/android-gif-drawable"))
        .addItem(createLicenseItem("material-about-library", "2016", "Daniel Stone", OpenSourceLicense.APACHE_2, "https://github.com/daniel-stoneuk/material-about-library"))
        .build()
    }

    override fun getMaterialAboutList(ctxt: Context?): MaterialAboutList {
        return MaterialAboutList.Builder()
            .addCard(aboutCard)
            .addCard(licenseCard)
            .build()
    }

    private fun createLicenseItem(libraryTitle: CharSequence, year: String, name: String, license: OpenSourceLicense, link: String) : MaterialAboutActionItem{
        return MaterialAboutActionItem.Builder().icon(context?.getDrawable(R.drawable.ic_book_black_24dp)).setIconGravity(MaterialAboutActionItem.GRAVITY_TOP)
            .text(libraryTitle).subText(String.format(requireContext().getString(license.resourceId), year, name))
            .setOnClickAction { openInBrowser(link) }
            .build()

    }

    private fun getTranslators() : String{
        return resources.getString(R.string.about_translations_desc) + " " + "Pavel Hristov"
    }

    private fun openInBrowser(link: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }
}

