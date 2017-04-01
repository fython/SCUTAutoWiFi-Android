package moe.feng.scut.autowifi

import android.net.Uri
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.view.MenuItem
import com.orhanobut.hawk.Hawk
import android.support.customtabs.CustomTabsIntent
import android.content.pm.PackageManager
import android.preference.SwitchPreference
import org.jetbrains.anko.email


class SettingsActivity : PreferenceActivity(), Preference.OnPreferenceChangeListener {

	lateinit var prefUsername : EditTextPreference
	lateinit var prefPassword : EditTextPreference
	lateinit var prefVersion : Preference
	lateinit var prefGithub : Preference
	lateinit var prefTelegram : Preference
	lateinit var prefEmail : Preference
	lateinit var prefAutoLogin : SwitchPreference

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(R.xml.pref_settings)

		actionBar.setDisplayHomeAsUpEnabled(true)

		prefUsername = findPreference("username") as EditTextPreference
		prefPassword = findPreference("password") as EditTextPreference
		prefVersion = findPreference("version")
		prefGithub = findPreference("github")
		prefTelegram = findPreference("telegram")
		prefEmail = findPreference("email")
		prefAutoLogin = findPreference("auto_login") as SwitchPreference

		prefUsername.text = Hawk.get("username", "")
		prefPassword.text = Hawk.get("password", "")
		if (!prefUsername.text.isNullOrEmpty()) prefUsername.summary = prefUsername.text
		if (!prefPassword.text.isNullOrEmpty()) prefPassword.summary = getString(R.string.pref_account_user_pwd_saved)
		prefAutoLogin.isChecked = Hawk.get("auto_login", true)

		var versionName: String? = null
		var versionCode = 0
		try {
			versionName = packageManager.getPackageInfo(packageName, 0).versionName
			versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}
		prefVersion.summary = "$versionName ($versionCode)"

		prefUsername.onPreferenceChangeListener = this
		prefPassword.onPreferenceChangeListener = this
		prefAutoLogin.onPreferenceChangeListener = this
		prefGithub.setOnPreferenceClickListener { openWebsite(getString(R.string.pref_about_github_url)); true }
		prefTelegram.setOnPreferenceClickListener { openWebsite(getString(R.string.pref_about_author_telegram_url)); true }
		prefEmail.setOnPreferenceClickListener { email(getString(R.string.pref_about_author_email_url), "", ""); true }
	}

	override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
		when (preference) {
			prefUsername -> {
				Hawk.put("username", newValue as String)
				preference.summary =
						if (newValue.isNullOrEmpty()) getString(R.string.pref_account_user_name_hint) else newValue
			}
			prefPassword -> {
				Hawk.put("password", newValue as String)
				preference.summary =
						if (newValue.isNullOrEmpty())
							getString(R.string.pref_account_user_pwd_hint)
						else getString(R.string.pref_account_user_pwd_saved)
			}
			prefAutoLogin -> Hawk.put("auto_login", (newValue as Boolean))
		}
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (item?.itemId == android.R.id.home) {
			onBackPressed()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	private fun openWebsite(url : String) {
		val builder = CustomTabsIntent.Builder()
		builder.setToolbarColor(resources.getColor(R.color.teal_500))
		builder.build().launchUrl(this, Uri.parse(url))
	}

}