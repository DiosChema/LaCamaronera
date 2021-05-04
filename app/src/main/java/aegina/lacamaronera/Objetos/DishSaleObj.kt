package aegina.lacamaronera.Objetos

import java.io.Serializable

data class DishSaleObj(
    var idPlatillo:Int,
    var cantidad:Double,
    var precio:Double,
    var nombre:String,
    var ingredientes:ArrayList<IngredientDishObj>
): Serializable