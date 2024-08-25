package com.justcircleprod.catquiz.developersAndLicenses.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.databinding.ActivityDevelopersAndLicensesBinding
import com.justcircleprod.catquiz.developersAndLicenses.presentation.developersAndLicensesAdapter.DevelopersAndLicenseItemAdapter
import com.justcircleprod.catquiz.developersAndLicenses.presentation.developersAndLicensesAdapter.DevelopersAndLicensesItem
import com.justcircleprod.catquiz.settings.presentation.SettingsActivity

class DevelopersAndLicensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDevelopersAndLicensesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDevelopersAndLicensesBinding.inflate(layoutInflater)

        onBackPressedDispatcher.addCallback { startSettingsActivity() }
        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.developersAndLicensesRecyclerView.adapter =
            DevelopersAndLicenseItemAdapter(createItemListForDevelopersAndLicensesRecyclerView())

        setContentView(binding.root)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createItemListForDevelopersAndLicensesRecyclerView(): List<DevelopersAndLicensesItem> {
        val items = mutableListOf(
            /*DevelopersAndLicensesItem.SectionTitle(R.string.copyright_disclaimer),
            DevelopersAndLicensesItem.Spacer(R.dimen.developers_and_licenses_section_title_copyright_margin_bottom),
            DevelopersAndLicensesItem.Text(R.string.copyright_disclaimer_text),
            DevelopersAndLicensesItem.Spacer(R.dimen.developers_and_licenses_line_under_copyright_margin_top),
            DevelopersAndLicensesItem.Line,*/
            DevelopersAndLicensesItem.SectionTitle(R.string.developers),
            DevelopersAndLicensesItem.Spacer(R.dimen.developers_and_licenses_section_title_developers_margin_bottom),
            DevelopersAndLicensesItem.DevelopersImage,
            DevelopersAndLicensesItem.Spacer(R.dimen.developers_and_licenses_line_margin_top),
            DevelopersAndLicensesItem.Line,
            DevelopersAndLicensesItem.SectionTitle(R.string.licenses),
            DevelopersAndLicensesItem.Spacer(R.dimen.developers_and_licenses_section_title_licenses_margin_bottom)
        )

        Licenses.forEachIndexed { index, license ->
            if (index != 0) {
                items.add(
                    DevelopersAndLicensesItem.Spacer(
                        R.dimen.developers_and_licenses_license_name_margin_top
                    )
                )
            }

            items.add(
                DevelopersAndLicensesItem.LicenseName(
                    license["licenseName"] as String,
                    license["licenseLink"] as String
                )
            )

            val projects = license["projects"] as List<Map<String, String>>

            projects.forEachIndexed { i, project ->
                items.add(
                    DevelopersAndLicensesItem.ProjectInfo(
                        project["projectNameVersion"]!!,
                        project["projectInfo"]!!
                    )
                )

                if (i != projects.size - 1) {
                    items.add(
                        DevelopersAndLicensesItem.Spacer(
                            R.dimen.developers_and_licenses_developers_project_info_margin_between
                        )
                    )
                }
            }
        }

        return items
    }

    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }
}