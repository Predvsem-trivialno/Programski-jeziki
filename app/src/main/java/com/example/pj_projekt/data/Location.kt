package com.example.pj_projekt.data

class Location(private var index: Int, private var postcode:String, private var city: String, private var address: String, private var numOfBoxes:Int, private var coordLat: Double, private var coordLong: Double){

    private var isSelected = false

    override fun toString(): String {
        return "$address, $postcode $city"
    }

    fun setIndex(i: Int){
        index = i
    }

    fun setPostcode(p: String){
        postcode = p
    }

    fun setCity(c: String){
        city = c
    }

    fun setAddress(a: String){
        address = a
    }

    fun setNumOfBoxes(num: Int){
        numOfBoxes = num
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

    fun getPostcode(): String {
        return postcode
    }

    fun getCity(): String {
        return city
    }

    fun getAddress(): String {
        return address
    }

    fun getNumOfBoxes(): Int {
        return numOfBoxes
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