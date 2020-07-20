package by.my.elections.presentation.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import by.my.elections.R
import by.my.elections.databinding.FragmentMainBinding
import by.my.elections.presentation.base.BaseFragment
import com.jakewharton.rxbinding3.view.clicks
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import org.opencv.core.CvType
import org.opencv.core.Mat
import timber.log.Timber
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainFragment : BaseFragment<MainPresenter.View, MainPresenter>(), MainPresenter.View {
    override val presenter: MainPresenter by inject()
    override val abstractView: MainPresenter.View
        get() = this

    lateinit var binding: FragmentMainBinding

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    private val onTakeImageSubject = PublishSubject.create<Mat>()

    private lateinit var cameraExecutor: ExecutorService

    private val rxPermission by lazy {
        RxPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()

            //
            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                preview?.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())
            } catch (exc: Exception) {
                Timber.e(exc, "Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(this.requireContext()))

        binding.captureButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), object :
            ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeExperimentalUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                image.let {
                    onTakeImageSubject.onNext(it.toMat())
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Timber.e("On error occurred ${exception.message}")
                Toast.makeText(
                    requireContext(),
                    "Photo capture failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        // We can only change the foreground Drawable using API level 23+ API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.root.apply {
                postDelayed({
                    foreground = ColorDrawable(Color.WHITE)
                    postDelayed({ foreground = null }, ANIMATION_FAST_MILLIS)
                }, ANIMATION_SLOW_MILLIS)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        return binding.root
    }


    override fun requestPermission(): Observable<Permission> {
        return rxPermission.requestEach(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA
        )
    }

    override fun hasPermission(): Single<Boolean> {
        return Single.just(
            rxPermission.isGranted(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) &&
                    rxPermission.isGranted(
                        android.Manifest.permission.CAMERA
                    )
        )
    }

    override fun onGalleryClick(): Observable<Unit> {
        return binding.viewButton.clicks()
    }

    override fun onHelpClick(): Observable<Unit> {
        return binding.helpButton.clicks()
    }

    override fun onImageTaken(): Observable<Mat> = onTakeImageSubject


    fun ImageProxy.toByteArray(): ByteArray {
        val planeProxy: ImageProxy.PlaneProxy = this.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }

    private fun ImageProxy.toMat(): Mat {
        val graySourceMatrix = Mat(height, width, CvType.CV_8UC1)
        val yBuffer = planes[0].buffer
        val ySize = yBuffer.remaining()
        val yPlane = ByteArray(ySize)
        yBuffer[yPlane, 0, ySize]
        graySourceMatrix.put(0, 0, yPlane)
        return graySourceMatrix
    }


    companion object {
        /** Milliseconds used for UI animations */
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L
    }
}


