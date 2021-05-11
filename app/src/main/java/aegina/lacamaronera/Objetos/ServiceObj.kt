package aegina.lacamaronera.Objetos

import java.io.Serializable

data class ServiceObj(
    var idServicio : Int,
    var nombre: String,
    var descripcion: String
):Serializable