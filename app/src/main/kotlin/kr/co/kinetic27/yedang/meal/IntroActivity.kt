package kr.co.kinetic27.yedang.meal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


/**
* Created by Kinetic on 2018-02-24.
*/

class IntroActivity : AppCompatActivity() {

    private var layouts: IntArray? = null
    private var viewPager: ViewPager? = null
    private var introManager: IntroManager? = null
    private var dotsLayout: LinearLayout? = null
    internal lateinit var btnNext: Button
    internal lateinit var btnSkip: Button

    private val item: Int
        get() = viewPager!!.currentItem + 1

    private var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == layouts!!.size - 1) {
                btnNext.text = "시작하기"
                btnSkip.visibility = View.GONE
            } else {
                btnNext.text = "다음"
                btnSkip.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        //풀 스크린
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        introManager = IntroManager(this)
        if (!introManager!!.check()) {
            introManager!!.setFirst(false)
            val intent = Intent(this, BapActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setContentView(R.layout.activity_intro)
        requestPermission()

        viewPager = findViewById(R.id.view_pager)
        dotsLayout = findViewById(R.id.layoutDots)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_skip)

        layouts = intArrayOf(R.layout.intro1, R.layout.intro2, R.layout.intro_shaky)

        addBottomDots(0)
        changeStatusBarColor()
        val viewPagerAdapter = ViewPagerAdapter()
        viewPager!!.adapter = viewPagerAdapter
        viewPager!!.addOnPageChangeListener(viewListener)

        btnSkip.setOnClickListener {
            introManager!!.setFirst(false)
            val intent = Intent(this@IntroActivity, BapActivity::class.java)
            startActivity(intent)
            finish()
        }


        btnNext.setOnClickListener {
            val current = item
            if (current < layouts!!.size) {
                viewPager!!.currentItem = current
            } else {
                introManager!!.setFirst(false)
                val intent = Intent(this@IntroActivity, BapActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun addBottomDots(position: Int) {

        val dots = arrayOfNulls<TextView>(layouts!!.size)

        dotsLayout!!.removeAllViews()

        for (i in dots.indices) {
            dots[i] = TextView(this)

            @Suppress("DEPRECATION")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                dots[i]!!.text = Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY)
            else
                dots[i]!!.text = Html.fromHtml("&#8226")

            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            dotsLayout!!.addView(dots[i])
        }

        if (dots.isNotEmpty()) {
            dots[position]!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        }
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }


    inner class ViewPagerAdapter : PagerAdapter() {

        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = layoutInflater!!.inflate(layouts!![position], container, false)
            container.addView(v)
            return v

        }

        override fun getCount(): Int {
            return layouts!!.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`

        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val v = `object` as View
            container.removeView(v)
        }
    }

    private fun requestPermission() {
        val isStoragePermitted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val isReadStatePermitted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        if (!isStoragePermitted || !isReadStatePermitted) {
            SweetAlertDialog(this).apply {
                titleText = "권한 허용"
                contentText = "급식 앱의 원활한 작동을 위해 권한이 필요합니다"
                setConfirmClickListener {
                    dismiss()
                    ActivityCompat.requestPermissions(this@IntroActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE), 9001)
                }
                setCancelable(false)
            }.show()

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermission()
    }
}
