package elfak.mosis.ribolov.data

import java.util.*

data class Recenzija(
var id: String="",
var recezent: String="",
var ribMesto: String="",
var idRibMesto: String="",
var ocena: Int=0,
var komentar: String=""
){
    constructor():this("","","","",0,""
    )
}
