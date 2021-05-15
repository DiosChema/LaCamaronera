package aegina.lacamaronera.Objetos

import java.io.Serializable
import java.util.*

data class UploadDishesObj(
    var ventas: ArrayList<SaleObj>,
    var token: String
): Serializable