package com.example.locationbasedservices2

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.locationbasedservices2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager


    var locationBR = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            mt("Location is Updated!")
            log("Location is updated!")
           var locationBR = intent!!.getParcelableExtra<Location>(LocationManager.KEY_LOCATION_CHANGED)
            binding.txtInfo.setText("${locationBR!!.longitude},${locationBR!!.latitude}")
        }
    }

    var proximityBroadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(intent!!.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING,false)){
                mt("You have entered the area or premises")
            }
            mt("You are leaving the area/state")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        for(locationProviderName in locationManager.allProviders){
            binding.txtInfo.setText(locationProviderName + "\n")
            var locationProvider : LocationProvider? = locationManager.getProvider(locationProviderName)
            log("${locationProvider!!.name}")
            log("${locationProvider!!.requiresCell()}")
            log("${locationProvider!!.requiresNetwork()}")
            log("${locationProvider!!.powerRequirement}")
            log("${locationProvider!!.requiresSatellite()}")
            log("${locationProvider!!.accuracy}")
            log("${locationProvider!!.hasMonetaryCost()}")
            log("${locationProvider!!.supportsAltitude()}")


            var location : Location? = locationManager.getLastKnownLocation(locationProviderName)
            if(location != null){
                    log("${location.latitude},${location.longitude}")
            }
            log("-----------------------------")
        }

        var criteria = Criteria()
        criteria.isAltitudeRequired = true
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.isCostAllowed = true
        criteria.accuracy = Criteria.ACCURACY_FINE


        var bestProvider = locationManager.getBestProvider(criteria,false)
        binding.txtInfo.append("Best Provider is ${bestProvider}")

        registerReceiver(
            locationBR,
            IntentFilter("in.bitcode.A")
        )

        locationManager.requestSingleUpdate(
            bestProvider!!,
            PendingIntent.getBroadcast(
                MainActivity@this,
                1,
                Intent("in.bitcode.A"),
                0
            )
        )

        registerReceiver(
            proximityBroadcastReceiver,
            IntentFilter("in.bitcode.HOME")
        )

        var proximityPendingIntent = PendingIntent.getBroadcast(
            MainActivity@this,
            1,
            Intent("in.bitcode.HOME"),
            0
        )

        locationManager.addProximityAlert(
            18.52,
            73.7,
            500F,
                -1,
            proximityPendingIntent
        )
    }

    private fun mt(text : String){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show()
    }

    private fun log(text: String){
        Log.e("tag",text)
    }
}