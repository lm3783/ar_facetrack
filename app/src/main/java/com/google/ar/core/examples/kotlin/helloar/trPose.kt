package com.google.ar.core.examples.kotlin.helloar

import com.google.ar.core.Pose
import kotlin.math.*

class trPose(pose: Pose) {
    var x: Double = pose.tx().toDouble()
    var y: Double = pose.ty().toDouble()
    var z: Double = pose.tz().toDouble()

    var pitch: Double = 0.0
    var yaw: Double = 0.0
    var roll: Double = 0.0

    init {
        this.getRotation(pose.rotationQuaternion)
    }

    fun toDouble(): DoubleArray {
        return doubleArrayOf(
            x * -100,           // meter to centimeter, reverse x axis
            y * 100,            // meter to centimeter
            z * 100,            // meter to centimeter
            yaw / PI * 180,     // rad to deg
            pitch / PI * 180,   // rad to deg
            roll / PI * 180     // rad to deg
        )
    }

    private fun getRotation(rotation: FloatArray) {
        val w: Double = rotation[3].toDouble()
        val x: Double = rotation[2].toDouble()    // google ar z axis -> opentrack x axis
        val y: Double = rotation[0].toDouble()    // google ar x axis -> opentrack y axis
        val z: Double = rotation[1].toDouble()    // google ar y axis -> opentrack z axis

        // roll (x-axis rotation)       switch axis
        val sinr_cosp = 2 * (w * x + y * z)
        val cosr_cosp = 1 - 2 * (x * x + y * y)
        roll = atan2(sinr_cosp, cosr_cosp)

        // pitch (y-axis rotation)
        val sinp = 2 * (w * y - z * x)
        if (abs(sinp) >= 1) {
            pitch = 1.57075.withSign(sinp) // use 90 degrees if out of range
        } else {
            pitch = asin(sinp)
        }

        // yaw (z-axis rotation)
        val siny_cosp = 2 * (w * z + x * y)
        val cosy_cosp = 1 - 2 * (y * y + z * z)
        yaw = atan2(siny_cosp, cosy_cosp)
    }
}