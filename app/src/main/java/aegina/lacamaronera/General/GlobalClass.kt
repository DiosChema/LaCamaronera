package aegina.lacamaronera.General

import aegina.lacamaronera.Objetos.UpdateWindow
import aegina.lacamaronera.Objetos.User
import android.app.Application

class GlobalClass(var user: User? = null, var updateWindow: UpdateWindow? = UpdateWindow(false, false)) : Application() {

}