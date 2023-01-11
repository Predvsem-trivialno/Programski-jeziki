package com.example.pj_projekt.data

class Location(private var index: Int, private var name:String, private var coordLat: Double, private var coordLong: Double){

    private var isSelected = false

    override fun toString(): String {
        return "$name: $coordLat, $coordLong"
    }

    fun setIndex(i: Int){
        index = i
    }

    fun setName(s: String){
        name = s
    }

    fun setCoordLat(lat: Double){
        coordLat = lat
    }

    fun setCoordLong(long: Double){
        coordLong = long
    }

    fun getIndex(): Int {
        return index
    }

    fun getName(): String {
        return name
    }

    fun getCoordLat(): Double {
        return coordLat
    }

    fun getCoordLong(): Double {
        return coordLong
    }

    fun select(bool: Boolean) {
        isSelected = bool
    }

    fun isSelected(): Boolean {
        return isSelected
    }
}