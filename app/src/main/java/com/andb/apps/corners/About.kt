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
            .addItem(ConvenienceBuilder.createVersionActionItem(context, resources.getDrawable(R.drawable.ic_info_outline_black_24dp), resources.getText(R.string.about_version), true))
            .addItem(MaterialAboutActionItem.Builder().icon(R.drawable.ic_language_black_24dp).text(R.string.about_translations_title).subText(getTranslators()).setOnClickAction { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://catalyststudios.oneskyapp.com/collaboration/project?id=154972"))) }.build())
            .build()
    }

    private val licenseCard by lazy { MaterialAboutCard.Builder()
        .title(R.string.about_licences)
        .addItem(createLicenseItem("material-about-library", "2016", "Daniel Stone", OpenSourceLicense.APACHE_2))
        .addItem(createLicenseItem("android-gif-drawable", "2013", "Karol Wrótniak, Droids on Roids LLC", OpenSourceLicense.MIT))
        .addItem(createLicenseItem("ColorPicker", "2019", "Jared Rummler", OpenSourceLicense.APACHE_2))
        .build()
    }

    override fun getMaterialAboutList(ctxt: Context?): MaterialAboutList {
        return MaterialAboutList.Builder()
            .addCard(aboutCard)
            .addCard(licenseCard)
            .build()
    }

    private fun createLicenseItem(libraryTitle: CharSequence, year: String, name: String, license: OpenSourceLicense) : MaterialAboutActionItem{
        return MaterialAboutActionItem.Builder().icon(resources.getDrawable(R.drawable.ic_book_black_24dp)).setIconGravity(MaterialAboutActionItem.GRAVITY_TOP)
            .text(libraryTitle).subText(String.format(requireContext().getString(license.resourceId), year, name))
            .build()

    }

    private fun getTranslators() : String{
        return resources.getString(R.string.about_translations_desc) + " " + "Pavel Hristov"
    }
}

