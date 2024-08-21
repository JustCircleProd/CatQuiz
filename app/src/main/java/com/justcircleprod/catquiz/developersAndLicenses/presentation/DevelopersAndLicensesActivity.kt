package com.justcircleprod.catquiz.developersAndLicenses.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.databinding.ActivityDevelopersAndLicensesBinding
import com.justcircleprod.catquiz.developersAndLicenses.presentation.components.MyRippleTheme
import com.justcircleprod.catquiz.settings.presentation.SettingsActivity

class DevelopersAndLicensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDevelopersAndLicensesBinding

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDevelopersAndLicensesBinding.inflate(layoutInflater)

        onBackPressedDispatcher.addCallback { startSettingsActivity() }
        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                CompositionLocalProvider(
                    LocalRippleTheme provides MyRippleTheme(color = colorResource(id = R.color.accent_text_color))
                ) {
                    Surface(
                        color = colorResource(id = R.color.card_background)
                    ) {
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.inner_padding)))
                            }

                            /*item {
                                SectionTitle(text = stringResource(id = R.string.copyright_disclaimer))
                            }
                            item {
                                Text(
                                    text = stringResource(id = R.string.copyright_disclaimer_text),
                                    color = colorResource(id = R.color.gray_text_color),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            item {
                                Line()
                            }*/

                            item {
                                SectionTitle(text = stringResource(id = R.string.developers))
                            }
                            item {
                                DevelopersImage()
                            }
                            item {
                                Line()
                            }

                            item {
                                SectionTitle(text = stringResource(id = R.string.licenses))
                            }

                            Licenses.forEachIndexed { index, license ->
                                item {
                                    if (index != 0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    LicenseName(
                                        licenseName = license["licenseName"] as String,
                                        licenseLink = license["licenseLink"] as String
                                    )
                                }

                                for (packageInfo in (license["projects"] as List<Map<String, String>>)) {
                                    item {
                                        ProjectInfoElement(
                                            packageName = packageInfo["projectNameVersion"]!!,
                                            licenseInfo = packageInfo["projectInfo"]!!
                                        )
                                    }
                                }
                            }

                            item {
                                LicenseName(
                                    licenseName = "Zvukipro.com",
                                    licenseLink = "https://zvukipro.com/"
                                )
                            }

                            item {
                                Text(
                                    text = stringResource(R.string.level_opening_sound),
                                    fontSize = 16.sp,
                                    color = colorResource(id = R.color.gray_text_color),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp)
                                        .padding(bottom = 5.dp)
                                )
                            }

                            item {
                                Spacer(Modifier.height(dimensionResource(id = R.dimen.inner_padding)))
                            }
                        }
                    }
                }
            }
        }

        setContentView(binding.root)
    }

    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Composable
    private fun SectionTitle(text: String) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.gray_text_color),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))
    }

    @Composable
    fun DevelopersImage() {
        Image(
            painter = painterResource(id = R.drawable.developers_image),
            contentDescription = stringResource(id = R.string.developers_content_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(15.dp))
        )
    }

    @Composable
    fun Line() {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.line_top_margin)))

        Divider(
            color = colorResource(id = R.color.line_color),
            thickness = dimensionResource(id = R.dimen.line_height),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.line_bottom_margin)))
    }

    @Composable
    private fun LicenseName(licenseName: String, licenseLink: String) {
        val uriHandler = LocalUriHandler.current

        Text(
            text = licenseName,
            fontSize = 17.sp,
            color = colorResource(id = R.color.accent_text_color),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        color = colorResource(id = R.color.accent_text_color),
                        bounded = true
                    ),
                    onClick = {
                        uriHandler.openUri(licenseLink)
                    }
                )
        )

        Spacer(modifier = Modifier.height(6.dp))
    }

    @Composable
    private fun ProjectInfoElement(
        packageName: String,
        licenseInfo: String,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .padding(bottom = 5.dp)
        ) {
            Text(
                text = packageName,
                fontSize = 16.sp,
                color = colorResource(id = R.color.gray_text_color),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = licenseInfo,
                color = colorResource(id = R.color.gray_text_color),
                fontSize = 15.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}