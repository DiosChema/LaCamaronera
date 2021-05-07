package aegina.lacamaronera.Objetos

import java.io.Serializable

data class SaleObj(
    var idVenta:Int,
    var platillos:List<DishSaleObj>,
    var totalVenta:Double,
    var fecha: String,
    var idEmpleado: Int
):Serializable