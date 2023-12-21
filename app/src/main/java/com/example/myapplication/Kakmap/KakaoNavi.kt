package com.example.myapplication.Kakmap



data class Orijin(
    var Orijin: Any,
    var x: Double,
    var y: Double
)

data class destination(
    var destination: Any,
    var x: Double,
    var y: Double
)

data class waypoints(
    var waypoints: Array<Any?>,
    var x: Double,
    var y: Double
)

data class NavigationRequest(
    val origin: Any,
    val destination: Any,
    val waypoints: Array<Any?>,
    val priority: String = "DISTANCE",
    val avoid: Array<String> = (arrayOf("roadevent")),
    val alternatives: Boolean = false,
    val road_details: Boolean = true,
    val car_type: Int = 1,
    val car_fuel: String = "GASOLINE",
    val car_hipass: Boolean = true,
    val summary: Boolean = true
)

