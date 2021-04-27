package aegina.lacamaronera.Activities.General

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.checkSelfPermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class Photo
{

    var image_uri: Uri? = null

    fun abrirCamara(contextTmp: Context, activityTmp: Activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(contextTmp, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(contextTmp, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            )
            {
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissions(activityTmp, permission,
                    PERMISSION_CODE
                )
            }
            else
            {
                openCamera(activityTmp)
            }
        }
        else
        {
            openCamera(activityTmp)
        }
    }

    private fun openCamera(activityTmp: Activity) {
        try
        {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            image_uri = activityTmp.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            //camera intent
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            activityTmp.startActivityForResult(cameraIntent,
                IMAGE_CAPTURE_CODE
            )
        }
        catch (e: java.lang.Exception){}

    }

    fun resizeBitmap(bitmap: Bitmap) : Bitmap
    {
        val bitmapCuadrado =
            if(bitmap.height > bitmap.width)
            {
                Bitmap.createBitmap(bitmap,0,((bitmap.height - bitmap.width)/2),bitmap.width,bitmap.width)
            }
            else
            {
                Bitmap.createBitmap(bitmap,((bitmap.width - bitmap.height)/2),0,bitmap.height,bitmap.height)
            }

        return Bitmap.createScaledBitmap(bitmapCuadrado, 250, 250, true)
    }

    fun abrirGaleria(contextTmp: Context, activityTmp: Activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(contextTmp, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED)
            {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                //show popup to request runtime permission
                requestPermissions(activityTmp, permissions,
                    PERMISSION_CODE
                )
            }
            else{
                //permission already granted
                pickImageFromGallery(activityTmp)
            }
        }
        else
        {
            //system OS is < Marshmallow
            pickImageFromGallery(activityTmp)
        }
    }

    private fun pickImageFromGallery(activityTmp: Activity) {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(activityTmp, intent,
            IMAGE_PICK_CODE, null)
    }

    fun bitmapToFile(bitmap:Bitmap, applicationContext: Context): File {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpeg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return file
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
        private val IMAGE_CAPTURE_CODE = 1001;
    }

}