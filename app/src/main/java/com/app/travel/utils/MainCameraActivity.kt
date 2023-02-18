package com.app.travel.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.app.travel.R
import com.app.travel.databinding.ActivityMainCameraBinding
import com.github.chrisbanes.photoview.PhotoView
import com.namangarg.androiddocumentscannerandfilter.DocumentFilter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

typealias LumaListener = (luma: Double) -> Unit

class MainCameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainCameraBinding
    private lateinit var viewFinder: PreviewView
    private lateinit var outputDirectory: File
    private lateinit var container: ConstraintLayout
    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageView: PhotoView? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null
    private lateinit var cameraExecutor: ExecutorService
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var photoFile: File? = null
    private var output: OutputStream? = null
    private var imageVisible: Boolean? = false
    private var bitmap: Bitmap? = null

    private lateinit var type: String

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }

    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set


        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(imageProxy: ImageProxy) {
            if (listeners.isEmpty()) {
                imageProxy.close()

                return
            }


            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0

            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases

            lastAnalyzedTimestamp = frameTimestamps.first

            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val buffer = imageProxy.planes[0].buffer

            // Extract image data from callback object
            val data = buffer.toByteArray()

            // Convert the data into an array of pixel values ranging 0-255
            val pixels = data.map { it.toInt() and 0xFF }

            // Compute average luminance for the image
            val luma = pixels.average()

            // Call all listeners with new value
            listeners.forEach { it(luma) }

            imageProxy.close()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        container = findViewById(R.id.camera_ui_container)
        viewFinder = findViewById(R.id.cameraShow)
        imageView = findViewById(R.id.imageShow)

        viewFinder.post {
            displayId = viewFinder.display.displayId
            cameraUi()
            setUpCamera()
        }
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.change.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_cameraswitch_24,
                null
            )
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        cameraUi()
        updateCameraSwitchButton()
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }
            updateCameraSwitchButton()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun toBitmap(file: File): Bitmap? {
        val filePath = file.path
        return BitmapFactory.decodeFile(filePath)
    }

    private fun bitmapToFile(bitmap: Bitmap) {
        output = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        output?.flush()
        output?.close()
    }

    private fun cameraUi() {
        val documentFilter = DocumentFilter()
        outputDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        binding.switchFilter.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                documentFilter.getMagicFilter(bitmap) { bitmap_filter ->
                    imageView!!.setImageBitmap(bitmap_filter)
                    bitmapToFile(bitmap_filter)
                }
            } else {
                imageView!!.setImageBitmap(bitmap)
                bitmapToFile(bitmap!!)
            }
        }

        binding.captureButton.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            binding.captureButton.isEnabled = false
            binding.close.isEnabled = false
            binding.change.isEnabled = false
            imageCapture?.let { imageCapture ->
                photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!)
//                    .setMetadata(metadata)
                    .build()

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(image: ImageCapture.OutputFileResults) {
                            binding.switchFilter.visibility = View.VISIBLE
                            binding.tvFilter.visibility = View.VISIBLE
                            binding.change.visibility = View.VISIBLE
                            bitmap = viewFinder.bitmap
                            imageView!!.setImageBitmap(bitmap)
                            imageVisible = true
                            imageView!!.visibility = View.VISIBLE
                            viewFinder.visibility = View.GONE
                            binding.loading.visibility = View.GONE
                            binding.captureButton.visibility = View.INVISIBLE
                            binding.footer.visibility = View.GONE
                            binding.close.isEnabled = true
                            binding.change.isEnabled = true
                            binding.change.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_cek_list_true,
                                    null
                                )
                            )
                        }
                    })
            }
        }

        binding.close.setSafeOnClickListener {
            if (imageVisible == false) {
                finish()
            } else {
                binding.switchFilter.visibility = View.GONE
                binding.tvFilter.visibility = View.GONE
                imageView!!.visibility = View.GONE
                viewFinder.visibility = View.VISIBLE
                binding.captureButton.visibility = View.VISIBLE
                binding.footer.visibility = View.VISIBLE
                binding.change.visibility = View.VISIBLE
                binding.change.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_cameraswitch_24,
                        null
                    )
                )
                binding.captureButton.isEnabled = true
                imageVisible = false

            }
        }

        binding.change.let {
            it.isEnabled = false
            it.setSafeOnClickListener {
                if (imageVisible == false) {
                    lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                        CameraSelector.LENS_FACING_BACK
                    } else {
                        CameraSelector.LENS_FACING_FRONT
                    }
                    bindCameraUseCases()
                } else {
                    val savedUri = Uri.fromFile(photoFile)
                    val intent = Intent()
                    intent.putExtra("data", savedUri)

                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

            }

        }

        binding.cameraFlash.setSafeOnClickListener {
            when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> {
                    flashMode = ImageCapture.FLASH_MODE_ON
                    binding.cameraFlash.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_flash_on_24,
                            null
                        )
                    )
                }
                ImageCapture.FLASH_MODE_ON -> {
                    flashMode = ImageCapture.FLASH_MODE_AUTO
                    binding.cameraFlash.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_flash_auto_24,
                            null
                        )
                    )
                }
                ImageCapture.FLASH_MODE_AUTO -> {
                    flashMode = ImageCapture.FLASH_MODE_OFF
                    binding.cameraFlash.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_flash_off_24,
                            null
                        )
                    )
                }

            }
            bindCameraUseCases()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        @Suppress("DEPRECATION")
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = viewFinder.display.rotation
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build().also { it.setSurfaceProvider(viewFinder.surfaceProvider) }
        touchEvent()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .setFlashMode(flashMode)
            .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(rotation)
            .build()
            .also { imageAnalyzer ->
                imageAnalyzer.setAnalyzer(cameraExecutor, LuminosityAnalyzer { listener ->
                    Log.d(TAG, "Average luminosity: $listener")
                })
            }

        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )
            cameraControl = camera?.cameraControl
            cameraInfo = camera?.cameraInfo
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchEvent() {

        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scale = cameraInfo?.zoomState?.value?.zoomRatio ?: 0F
                val ratio = scale * detector.scaleFactor
                cameraControl?.setZoomRatio(ratio)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(this, listener)
        viewFinder.afterMeasured {
            viewFinder.setOnTouchListener { _, event ->
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            viewFinder.width.toFloat(), viewFinder.height.toFloat()
                        )
                        val autoFocusPoint = factory.createPoint(event.x, event.y)
                        try {
                            cameraControl?.startFocusAndMetering(
                                FocusMeteringAction.Builder(
                                    autoFocusPoint,
                                    FocusMeteringAction.FLAG_AF
                                ).apply {
                                    //focus only when the user tap the preview
                                    disableAutoCancel()
                                }.build()

                            )
                        } catch (e: CameraInfoUnavailableException) {
                            Log.d("ERROR", "cannot access camera", e)
                        }
                        true
                    }
                    else -> {
                        scaleGestureDetector.onTouchEvent(event)
                        return@setOnTouchListener true
                    }
                }
            }
        }
    }

    private inline fun View.afterMeasured(crossinline block: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun updateCameraSwitchButton() {
        try {
            binding.change.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            binding.change.isEnabled = false
        }
    }

    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }


    private fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}