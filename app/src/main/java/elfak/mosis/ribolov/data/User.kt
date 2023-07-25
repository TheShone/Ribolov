package elfak.mosis.ribolov.data

import java.util.*

data class User(
    val ime:String,
    val prezime:String,
    val korisnickoime:String,
    val sifra:String,
    val brojtelefona:String,
    var imageURL:String,
    var rang:Int=0,
    var points:Int=0
) {
    constructor() : this("","","","","","",0,0)
}
