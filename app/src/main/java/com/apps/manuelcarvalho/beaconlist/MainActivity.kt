package com.apps.manuelcarvalho.beaconlist

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.altbeacon.beacon.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), BeaconConsumer {

    private var beaconManager: BeaconManager? = null
    private var beaconRegion: Region? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 0)

        //beaconManager.getInstanceForApplication(this)
        beaconManager = BeaconManager.getInstanceForApplication(this)
        val beaconManager = beaconManager

        beaconManager?.beaconParsers?.add(
            BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT)
        )

        beaconManager?.beaconParsers?.add(
            BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT)
        )

        beaconManager?.beaconParsers?.add(
            BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT)
        )

        beaconManager?.beaconParsers?.add(
            BeaconParser().setBeaconLayout("m:2-3=0215, i:4-19, i:20-21, i:22-23, p:24-24, d:25-25")
        )

        beaconManager?.bind(this)

        fab.setOnClickListener { view ->
            startBeacon()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBeaconServiceConnect() {

        beaconManager?.setMonitorNotifier(object : MonitorNotifier {
            var text = ""
            override fun didEnterRegion(region: Region) {
                Log.d(TAG, "didEnterRegion: ")
                text = text + "\n didEnterRegion \n" + region.uniqueId + " " + region.id1 + " " + region.id2 + " " +
                        region.id3
                textView.text = text
                beep()

            }

            override fun didExitRegion(region: Region) {
                text = text + "\n didExitRegion: \n" + region.uniqueId + " " + region.id1 + " " + region.id2 + " " +
                        region.id3
                Log.d(TAG, "didExitRegion: ")
                textView.text = text
                beep()
            }

            override fun didDetermineStateForRegion(i: Int, region: Region) {
                Log.d(TAG, "didDetermineStateForRegion: ")
            }
        })

        beaconManager?.setRangeNotifier(RangeNotifier { collection, region -> Log.d(TAG, "didRangeBeaconsInRegion: ") })
    }

    private fun startBeacon() {
        var beaconRegion = beaconRegion
        try {
            beaconRegion = Region(
                "MyBeacons",
                Identifier.parse("FDA50693-A4E2-4FB1-AFCF-C6EB07647825"),
                Identifier.parse("5"),
                Identifier.parse("6")
            )
            beaconManager?.startMonitoringBeaconsInRegion(beaconRegion)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    private fun beep() {
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
    }

}




