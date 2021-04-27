package aegina.lacamaronera.Objetos

import java.io.Serializable

data class ResponseObj(
    var status:Int,
    var mensaje:String,
    var dato:String
): Serializable