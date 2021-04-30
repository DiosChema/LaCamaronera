package aegina.lacamaronera.Objetos

import java.io.Serializable
import java.util.*

data class AssormentListObj(
    var idIngrediente:Int,
    var ingredientes:List<AssormentIngredientObj>,
    var totalGasto:Double,
    var fecha: Date
):Serializable