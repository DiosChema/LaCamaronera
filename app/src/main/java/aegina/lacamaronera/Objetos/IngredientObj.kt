package aegina.lacamaronera.Objetos

import java.io.Serializable

data class IngredientObj(
    var idIngrediente:Int,
    var nombre:String,
    var costo:Double,
    var descripcion:String,
    var existencia:Double,
    var unidad:String,
    var usoPlatillo:ArrayList<Int>
):Serializable