package aegina.lacamaronera.Objetos

import java.io.Serializable
import java.util.*

data class ExpenseServiceObj(
    var idGasto: Int,
    var idServicio: Int,
    var nombre:String,
    var descripcion:String,
    var fecha:Date,
    var gasto: Double
):Serializable