package aegina.lacamaronera.Objetos

import java.io.Serializable

data class User(
    val idEmpleado:Int,
    val user:String,
    val nombre:String,
    val admin:Boolean
):Serializable