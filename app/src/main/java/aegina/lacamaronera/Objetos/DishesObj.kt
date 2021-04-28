package aegina.lacamaronera.Objetos

import java.io.Serializable

data class DishesObj(
    var idPlatillo:Int,
    var nombre:String,
    var precio:Double,
    var ingredientes:ArrayList<IngredientDishObj>,
    var idFamilia:Int,
    var descripcion:String
):Serializable