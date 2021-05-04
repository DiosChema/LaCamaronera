package aegina.lacamaronera.Objetos

import java.io.Serializable

data class AssormentIngredientObj(
    var idIngrediente:Int,
    var precio:Double,
    var cantidad:Double,
    var nombre:String,
    var descripcion:String
):Serializable