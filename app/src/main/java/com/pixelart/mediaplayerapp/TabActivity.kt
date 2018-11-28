package com.pixelart.mediaplayerapp

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.view.ViewPager
import android.os.Bundle
import android.os.IBinder
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import com.pixelart.mediaplayerapp.adapters.TabsAdapter
import com.pixelart.mediaplayerapp.fragments.AlbumFragment
import com.pixelart.mediaplayerapp.fragments.ArtistFragment
import com.pixelart.mediaplayerapp.fragments.TracksFragment

import kotlinx.android.synthetic.main.activity_tab.*

class TabActivity : AppCompatActivity() {
    private val TAG = "TabActivity"

    var isPlaying: Boolean = false
    var isBound = false
    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var mediaPlayerService: MediaPlayerService
    lateinit var title: String
    lateinit var artist:String

    private val constraintSetOld = ConstraintSet()
    private val constraintSetNew = ConstraintSet()
    private var altLayout = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        setSupportActionBar(toolbar)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }
        else {
            tabs.setupWithViewPager(container, true)
            setupViewPager(container)
        }

        constraintSetOld.clone(main_content)
        constraintSetNew.clone(this, R.layout.activity_media_controller)

        val boundServiceIntent = Intent(this, MediaPlayerService::class.java)
        bindService(boundServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "musicAction")
                {
                    title = intent.getStringExtra("title")
                    artist = intent.getStringExtra("artist")
                    tvTitle.text = title
                    tvArtist.text = artist
                }
                isPlaying = intent!!.getBooleanExtra("isPlaying", false)
            }

        }
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))


    }

    private fun setupViewPager(viewPager: ViewPager){
        val adapter = TabsAdapter(supportFragmentManager)

        adapter.addFragments(TracksFragment.newInstance(), getString(R.string.tracks))
        adapter.addFragments(ArtistFragment.newInstance(), getString(R.string.artist))
        adapter.addFragments(AlbumFragment.newInstance(), getString(R.string.album))
        viewPager.adapter = adapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            tabs.setupWithViewPager(container, true)
            setupViewPager(container)
        }
        else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private val serviceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            Log.d(TAG, "Service Disconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myIBinder : MediaPlayerService.MyIBinder = service as MediaPlayerService.MyIBinder
            mediaPlayerService = myIBinder.getService()
            Toast.makeText(this@TabActivity, "Service Connected", Toast.LENGTH_SHORT).show()
            isBound = true

            mediaPlayerService.mediaControls(ibPlay, ibPause, ibPrevious, ibNext, tvTitle, tvArtist)
            mediaPlayerService.seekBar(seekBar, tvCurrentDuration, tvTotalDuration)


            Log.d(TAG, "Service Connected ${mediaPlayerService.isPlaying()}")
        }

    }

    fun swapView(view: View)
    {
        val changeBounds = ChangeBounds()
        changeBounds.interpolator = OvershootInterpolator()
        TransitionManager.beginDelayedTransition(main_content, changeBounds)

         if(altLayout) {
            constraintSetOld.applyTo(main_content)
             ibSwapView.setImageResource(R.drawable.ic_arrow_up)
        } else {
            constraintSetNew.applyTo(main_content)
             ibSwapView.setImageResource(R.drawable.ic_arrow_down)
        }
        altLayout = !altLayout
    }

    override fun onBackPressed() {
        val changeBounds = ChangeBounds()
        changeBounds.interpolator = OvershootInterpolator()
        TransitionManager.beginDelayedTransition(main_content, changeBounds)

        if(altLayout) {
            constraintSetOld.applyTo(main_content)
            ibSwapView.setImageResource(R.drawable.ic_arrow_up)
        } else {
            super.onBackPressed()
        }
        altLayout = !altLayout
    }

    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter()
        intentFilter.addAction("musicAction")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound)
            unbindService(serviceConnection)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }




}
