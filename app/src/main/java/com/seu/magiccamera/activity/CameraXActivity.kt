package com.seu.magiccamera.activity

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.view.TextureView
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.seu.magiccamera.R
import com.seu.magiccamera.adapter.FilterAdapter
import com.seu.magiccamera.view.onClick
import com.seu.magicfilter.filter.helper.MagicFilterType
import com.seu.magicfilter.utils.MagicParams
import com.seu.magicfilter.widget.MagicCameraView
import kotlinx.android.synthetic.main.activity_camerax.*
import kotlinx.android.synthetic.main.filter_layout.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CameraXActivity : AppCompatActivity() {

    private lateinit var mAdapter: FilterAdapter
    private var isRecording = false
    private val MODE_PIC = 1
    private val MODE_VIDEO = 2
    private val mode = MODE_PIC
    private lateinit var animator: ObjectAnimator

    private val types = arrayOf(
        MagicFilterType.NONE,
        MagicFilterType.FAIRYTALE,
        MagicFilterType.SUNRISE,
        MagicFilterType.SUNSET,
        MagicFilterType.WHITECAT,
        MagicFilterType.BLACKCAT,
        MagicFilterType.SKINWHITEN,
        MagicFilterType.HEALTHY,
        MagicFilterType.SWEETS,
        MagicFilterType.ROMANCE,
        MagicFilterType.SAKURA,
        MagicFilterType.WARM,
        MagicFilterType.ANTIQUE,
        MagicFilterType.NOSTALGIA,
        MagicFilterType.CALM,
        MagicFilterType.LATTE,
        MagicFilterType.TENDER,
        MagicFilterType.COOL,
        MagicFilterType.EMERALD,
        MagicFilterType.EVERGREEN,
        MagicFilterType.CRAYON,
        MagicFilterType.SKETCH,
        MagicFilterType.AMARO,
        MagicFilterType.BRANNAN,
        MagicFilterType.BROOKLYN,
        MagicFilterType.EARLYBIRD,
        MagicFilterType.FREUD,
        MagicFilterType.HEFE,
        MagicFilterType.HUDSON,
        MagicFilterType.INKWELL,
        MagicFilterType.KEVIN,
        MagicFilterType.LOMO,
        MagicFilterType.N1977,
        MagicFilterType.NASHVILLE,
        MagicFilterType.PIXAR,
        MagicFilterType.RISE,
        MagicFilterType.SIERRA,
        MagicFilterType.SUTRO,
        MagicFilterType.TOASTER2,
        MagicFilterType.VALENCIA,
        MagicFilterType.WALDEN,
        MagicFilterType.XPROII
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camerax)
        animator = ObjectAnimator.ofFloat(btn_camera_shutter, "rotation", 0f, 360f)
        animator.duration = 500
        animator.repeatCount = ValueAnimator.INFINITE
        val screenSize = Point()
        windowManager.defaultDisplay.getSize(screenSize)
        val params =
            glsurfaceview_camera.layoutParams as RelativeLayout.LayoutParams
        params.width = screenSize.x
        params.height = screenSize.x * 4 / 3
        glsurfaceview_camera.layoutParams = params

        filter_listView.layoutManager =
            LinearLayoutManager(this).apply { orientation = LinearLayoutManager.HORIZONTAL }
        mAdapter = FilterAdapter(this, types)
        filter_listView.adapter = mAdapter
        mAdapter.setOnFilterChangeListener {
            magicEngine.setFilter(it)
        }


        btn_camera_mode.onClick {
            switchMode()
        }

        btn_camera_shutter.onClick {
            if (PermissionChecker.checkSelfPermission(
                    this@CameraXActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PermissionChecker.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this@CameraXActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            } else {
                if (mode == MODE_PIC) takePhoto() else takeVideo()
            }
        }

        btn_camera_filter.onClick {
            showFilters()
        }


        btn_camera_switch.onClick {
            magicEngine.switchCamera()
        }

        btn_camera_beauty.setOnClickListener {
            AlertDialog.Builder(this@CameraXActivity)
                .setSingleChoiceItems(
                    arrayOf("关闭", "1", "2", "3", "4", "5"),
                    MagicParams.beautyLevel
                ) { dialog, which ->
                    magicEngine.setBeautyLevel(which)
                    dialog.dismiss()
                }
                .setNegativeButton("取消", null)
                .show()
        }


        btn_camera_closefilter.onClick {
            hideFilters()
        }


        val previewConfig = PreviewConfig.Builder().build()
        val imageCaptureConfig = ImageCaptureConfig.Builder().build()
        val imageCapture = ImageCapture(imageCaptureConfig)
        val preview = Preview(previewConfig)
        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imageCapture)

    }


    private fun showFilters() {
        ObjectAnimator.ofFloat(
            layout_filter,
            "translationY",
            layout_filter.height.toFloat(),
            0f
        ).apply {
            duration = 200
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    findViewById<View>(R.id.btn_camera_shutter).isClickable = false
                    layout_filter.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animator) = Unit
                override fun onAnimationEnd(animation: Animator) = Unit
                override fun onAnimationCancel(animation: Animator) = Unit
            })
            start()
        }
    }

    private fun hideFilters() {
        ObjectAnimator.ofFloat(
            layout_filter,
            "translationY",
            0f,
            layout_filter.height.toFloat()
        ).apply {
            duration = 200

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) = Unit

                override fun onAnimationRepeat(animation: Animator) = Unit
                override fun onAnimationEnd(animation: Animator) {
                    layout_filter.visibility = View.INVISIBLE
                    findViewById<View>(R.id.btn_camera_shutter).isClickable = true
                }

                override fun onAnimationCancel(animation: Animator) {
                    layout_filter.visibility = View.INVISIBLE
                    findViewById<View>(R.id.btn_camera_shutter).isClickable = true
                }
            })
            start()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.size != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mode == MODE_PIC) takePhoto() else takeVideo()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        CameraX.unbindAll()
    }

    private fun takeVideo() {
        if (isRecording) {
            animator.end()
            magicEngine.stopRecord()
        } else {
            animator.start()
            magicEngine.startRecord()
        }
        isRecording = !isRecording
    }

    private fun takePhoto() {
        magicEngine.savePicture(getOutputMediaFile(), null)
    }

    fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "MagicCamera"
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE)
                .format(Date())
        return File(
            mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".jpg"
        )
    }
}