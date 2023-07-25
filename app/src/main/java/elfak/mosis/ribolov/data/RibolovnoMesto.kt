package elfak.mosis.ribolov.data

import java.time.DateTimeException
import java.util.*

data class RibolovnoMesto(
    var id: String= "",
    var naziv: String="",
    var oglasavac: String="",
    var vrstaRibe: String="",
    var pristupacnost: Boolean,
    var uredjenost: Boolean,
    var cistocaDna: Boolean,
    var platforma: Boolean,
    var sator: Boolean,
    var latitude: Double=0.0,
    var longitude: Double=0.0,
    var datumPostavljanja: Date
){
    constructor():this("","","","",false,false,false,false,false,0.0,0.0,Date()
    )
    public fun getOglasavaca(): String
    {
        return oglasavac
    }
}
