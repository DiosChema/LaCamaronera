package aegina.lacamaronera.General

import aegina.lacamaronera.R
import android.content.Context
class Moths
{
    fun getMoths(mes: Int, context: Context) : String
    {
        var textoMes = ""

        when (mes) {
            1 -> textoMes = context.getString(R.string.moth_enero)
            2 -> textoMes = context.getString(R.string.moth_febrero)
            3 -> textoMes = context.getString(R.string.moth_marzo)
            4 -> textoMes = context.getString(R.string.moth_abril)
            5 -> textoMes = context.getString(R.string.moth_mayo)
            6 -> textoMes = context.getString(R.string.moth_junio)
            7 -> textoMes = context.getString(R.string.moth_julio)
            8 -> textoMes = context.getString(R.string.moth_agosto)
            9 -> textoMes = context.getString(R.string.moth_septiempre)
            10 -> textoMes = context.getString(R.string.moth_octubre)
            11 -> textoMes = context.getString(R.string.moth_noviembre)
            12 -> textoMes = context.getString(R.string.moth_diciembre)
        }

        return textoMes
    }
}