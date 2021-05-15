package aegina.lacamaronera.Objetos

import aegina.lacamaronera.R
import android.app.Activity
import android.content.Context
import android.widget.Toast

class Errores {
    fun procesarError(context: Context, activity: Activity)
    {
        activity.runOnUiThread()
        {
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_LONG).show()
        }
    }

    fun procesarErrorMensaje(context: Context, activity: Activity, responseObj: ResponseObj)
    {
        activity.runOnUiThread()
        {
            Toast.makeText(context, responseObj.mensaje, Toast.LENGTH_LONG).show()
        }
    }
}