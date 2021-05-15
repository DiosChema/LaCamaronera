package aegina.lacamaronera.Objetos

import java.io.Serializable

data class User(
    val idUsuario:Int,
    val usuario:String,
    val nombre:String,
    val admin:Boolean,
    val token:String,
    val url:String,
    var online:Boolean = true
):Serializable