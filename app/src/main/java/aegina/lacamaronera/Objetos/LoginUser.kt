package aegina.lacamaronera.Objetos

import java.io.Serializable

data class LoginUser(
    var status:Int,
    var mensaje:String,
    var usuario:User
): Serializable