package aegina.lacamaronera.Objetos

import java.io.Serializable
import java.util.*

data class SaleDishObj(
    var idVenta:Int,
    var platillos:List<DishSaleObj>,
    var totalVenta:Double,
    var fecha: Date
):Serializable