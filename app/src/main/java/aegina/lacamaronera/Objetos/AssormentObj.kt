package aegina.lacamaronera.Objetos

import java.io.Serializable

data class AssormentObj(
    var idGasto:Int,
    var ingredientes:List<AssormentIngredientObj>,
    var totalGasto:Double,
    var fecha: String
):Serializable