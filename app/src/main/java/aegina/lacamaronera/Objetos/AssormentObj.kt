package aegina.lacamaronera.Objetos

import java.io.Serializable
import java.util.*

data class AssormentObj(
    var idIngrediente:Int,
    var ingredientes:List<AssormentIngredientObj>,
    var precio:Double,
    var fecha: String
):Serializable