/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.examples.kotlin.helloar

import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper
import java.io.IOException

/** Contains UI elements for Hello AR. */
class HelloArView(val activity: HelloArActivity) : DefaultLifecycleObserver {
  val root = View.inflate(activity, R.layout.activity_main, null)
  val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview)
  val session
    get() = activity.arCoreSessionHelper.session
  val snackbarHelper = SnackbarHelper()

  val showvideo: SwitchCompat = root.findViewById(R.id.video_switch)
  val showdebug: SwitchCompat = root.findViewById(R.id.debug_switch)
  val sendudp: SwitchCompat = root.findViewById(R.id.udp_switch)
  val ip: EditText = root.findViewById(R.id.ip_input)
  val port: EditText = root.findViewById(R.id.port_input)
  val trackstate: TextView = root.findViewById(R.id.textTracking)
  var tracked: Boolean = false
  override fun onCreate(owner: LifecycleOwner) {
    super.onCreate(owner)

    // restore settings
    ip.setText(activity.udpip)
    port.setText(activity.udpport.toString())

    // connect button
    val rollButton: Button = root.findViewById(R.id.button)
    rollButton.setOnClickListener {
      updateip()
      activity.saveperf()
    }

    // connection switch
    sendudp.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        try {
          updateip()
          activity.udpconn = UDPmanager(activity.udpip, activity.udpport)
          ip.isEnabled = false
          port.isEnabled = false
        } catch (e: IOException) {
          Log.e(HelloArRenderer.TAG, "Failed to open port", e)
          snackbarHelper.showError(activity, "Failed to open port: $e")
          buttonView.isChecked = false
        }
      } else {
        activity.udpconn?.close()
        ip.isEnabled = true
        port.isEnabled = true
      }
    }
  }

  private fun updateip() {
    val ip = ip.text.toString()
    val port = Integer.parseInt(port.text.toString())
    activity.udpip = ip
    activity.udpport = port
  }

  fun setTrackState(newstate: Boolean) {
    if (newstate != tracked) {
      tracked = newstate
      activity.runOnUiThread {
        if (tracked) {
          trackstate.setTextColor(activity.resources.getColor(R.color.green, null))
        } else {
          trackstate.setTextColor(activity.resources.getColor(R.color.red, null))
        }
      }
    }
  }
  override fun onResume(owner: LifecycleOwner) {
    surfaceView.onResume()
  }

  override fun onPause(owner: LifecycleOwner) {
    surfaceView.onPause()
  }

  fun showDebug(pose: trPose) {
    activity.runOnUiThread {
      val valuex: TextView = activity.view.root.findViewById(R.id.value_x)
      val valuey: TextView = activity.view.root.findViewById(R.id.value_y)
      val valuez: TextView = activity.view.root.findViewById(R.id.value_z)
      val valueroll: TextView = activity.view.root.findViewById(R.id.value_roll)
      val valuepitch: TextView = activity.view.root.findViewById(R.id.value_pitch)
      val valueyaw: TextView = activity.view.root.findViewById(R.id.value_yaw)
      val dataarray: DoubleArray = pose.toDouble()
      valuex.text = "%.1f".format(dataarray[0])
      valuey.text = "%.1f".format(dataarray[1])
      valuez.text = "%.1f".format(dataarray[2])
      valueroll.text = "%.1f".format(dataarray[5])
      valuepitch.text = "%.1f".format(dataarray[4])
      valueyaw.text = "%.1f".format(dataarray[3])
    }
  }
}
