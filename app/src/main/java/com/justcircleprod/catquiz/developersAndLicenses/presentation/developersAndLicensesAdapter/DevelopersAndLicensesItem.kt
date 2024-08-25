package com.justcircleprod.catquiz.developersAndLicenses.presentation.developersAndLicensesAdapter

import androidx.annotation.DimenRes
import androidx.annotation.StringRes

sealed class DevelopersAndLicensesItem {
    data class Spacer(@DimenRes val heightDimenResId: Int) : DevelopersAndLicensesItem()
    data class SectionTitle(@StringRes val stringResId: Int) : DevelopersAndLicensesItem()
    data class Text(@StringRes val stringResId: Int) : DevelopersAndLicensesItem()
    object Line : DevelopersAndLicensesItem()
    object DevelopersImage : DevelopersAndLicensesItem()
    data class LicenseName(val licenseName: String, val licenseLink: String) :
        DevelopersAndLicensesItem()

    data class ProjectInfo(val projectNameVersion: String, val projectInfo: String) :
        DevelopersAndLicensesItem()
}