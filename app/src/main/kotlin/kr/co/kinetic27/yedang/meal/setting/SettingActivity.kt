package kr.co.kinetic27.yedang.meal.setting

import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.*
import androidx.appcompat.app.AlertDialog
import kr.co.kinetic27.yedang.meal.R
import kr.co.kinetic27.yedang.meal.tools.BaseActivity
import kr.co.kinetic27.yedang.meal.update.UpdateAlarm

/**
* Created by Kinetic on 2018-02-27.
*/

class SettingsActivity : BaseActivity() {

    override var viewId: Int = R.layout.activity_settings
    override var toolbarId: Int? = R.id.mToolbar

    override fun onCreate() {
        showActionBar()
        enableToggle()
        setToolbarTitle("설정")
        fragmentManager.beginTransaction().replace(R.id.container, PrefsFragment()).commit()
    }

    class PrefsFragment : PreferenceFragment() {

        private val onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
            val getKey = preference.key

            when (getKey) {
                "openSource" -> {
                    val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
                    builder.setTitle(R.string.license_title)
                    builder.setMessage(R.string.license_msg)
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.show()
                }
                "ChangeLog" -> {
                    val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
                    builder.setTitle(R.string.changeLog_title)
                    builder.setMessage(R.string.changeLog_msg)
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.show()
                }
                "infoAutoUpdate" -> showNotification()
            }

            true
        }

        private val onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val stringValue = newValue.toString()

            if (preference is EditTextPreference) {
                preference.setSummary(stringValue)

            } else if (preference is ListPreference) {

                val index = preference.findIndexOfValue(stringValue)

                preference.setSummary(if (index >= 0) preference.entries[index] else null)

                val updateAlarm = UpdateAlarm(activity)
                updateAlarm.cancel()

                when (index) {
                    0 -> updateAlarm.autoUpdate()
                    1 -> updateAlarm.saturdayUpdate()
                    2 -> updateAlarm.sundayUpdate()
                }

            } else if (preference is CheckBoxPreference) {
                val mPref = kr.co.kinetic27.yedang.meal.tools.Preference(activity)

                if (mPref.getBoolean("firstOfAutoUpdate", true)) {
                    mPref.putBoolean("firstOfAutoUpdate", false)
                    showNotification()
                }

                if (!mPref.getBoolean("autoBapUpdate", false) && preference.isEnabled()) {
                    val updateLife = Integer.parseInt(mPref.getString("updateLife", "0")!!)

                    val updateAlarm = UpdateAlarm(activity)
                    when (updateLife) {
                        1 -> updateAlarm.autoUpdate()
                        0 -> updateAlarm.saturdayUpdate()
                        -1 -> updateAlarm.sundayUpdate()
                    }

                } else {
                    val updateAlarm = UpdateAlarm(activity)
                    updateAlarm.cancel()
                }
            }
            true
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_settings)

            setOnPreferenceClick(findPreference("infoAutoUpdate"))
            setOnPreferenceClick(findPreference("openSource"))
            setOnPreferenceClick(findPreference("ChangeLog"))
            setOnPreferenceChange(findPreference("autoBapUpdate"))
            setOnPreferenceChange(findPreference("updateLife"))

            try {
                val packageManager = activity.packageManager
                val info = packageManager.getPackageInfo(activity.packageName, PackageManager.GET_META_DATA)
                findPreference("appVersion").summary = info.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

        }

        private fun setOnPreferenceClick(mPreference: Preference) {
            mPreference.onPreferenceClickListener = onPreferenceClickListener
        }

        private fun setOnPreferenceChange(mPreference: Preference) {
            mPreference.onPreferenceChangeListener = onPreferenceChangeListener

            if (mPreference is ListPreference) {
                val index = mPreference.findIndexOfValue(mPreference.value)
                mPreference.setSummary(if (index >= 0) mPreference.entries[index] else null)
            } else if (mPreference is EditTextPreference) {
                var values: String? = mPreference.text
                if (values == null) values = ""
                onPreferenceChangeListener.onPreferenceChange(mPreference, values)
            }
        }

        private fun showNotification() {
            val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
            builder.setTitle(R.string.info_autoUpdate_title)
            builder.setMessage(R.string.info_autoUpdate_msg)
            builder.setPositiveButton(android.R.string.ok, null)
            builder.show()
        }
    }
}