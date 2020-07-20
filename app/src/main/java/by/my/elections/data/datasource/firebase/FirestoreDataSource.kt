package by.my.elections.data.datasource.firebase

import by.my.elections.data.datasource.firebase.model.User
import by.my.elections.data.datasource.schedule.SchedulerProvider
import by.my.elections.data.model.DeviceLocation
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import durdinapps.rxfirebase2.RxFirebaseStorage
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import timber.log.Timber
import java.security.Key
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec


class FirestoreDataSource(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val schedulerProvider: SchedulerProvider
) {

    fun createOrUpdateUser(user: User): Completable {
        return RxFirestore.setDocument(
            firebaseFirestore.collection(COLLECTIONS_PATH_TO_USERS).document(user.uuid),
            user.apply { this.lastSignIn = Date() }
        )
            .subscribeOn(schedulerProvider.io())
    }

    private fun encrypt(array: ByteArray, secret: Key): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(iv)

        cipher.init(Cipher.ENCRYPT_MODE, secret, IvParameterSpec(iv))
        return cipher.doFinal(array)
    }

    private fun secret(): Key {
        val keyGen: KeyGenerator = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    private fun resize(img: Mat, size: Size) {
        val interpolation: Int =
            if (size.width > img.size().width && size.height > img.size().height) 2 // Imgproc.CV_INTER_CUBIC //enlarge image
            else if (size.width < img.size().width && size.height < img.size().height) 3 // Imgproc.CV_INTER_AREA //shrink image
            else 1 // Imgproc.CV_INTER_LINEAR //not entirely sure, so use safe option
        Imgproc.resize(img, img, size, 0.0, 0.0, interpolation)
    }


    private fun scale(img: Mat, factor: Double) {
        resize(img, Size(img.size().width * factor, img.size().height * factor))
    }

    fun upload(mat: Mat, metaInformation: Map<String, String>): Completable {

        Timber.d("Mat size: ${mat.size()}")
//        scale(mat, 0.5)
        Timber.d("Mat size: ${mat.size()}")


        val params = MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 40)
        val buff40 = MatOfByte()
        Imgcodecs.imencode(".jpg", mat, buff40, params)

        val storageRef = firebaseStorage.reference
        val folder = getFolder()
        val imageRef = storageRef.child("$folder/image.jpg")
        val hashRef = storageRef.child("$folder/encrypted.jpg.enc")

        // Create file metadata including the content type
        val metadata = storageMetadata {
            contentType = "image/jpg"
            metaInformation.forEach {
                setCustomMetadata(it.key, it.value)
            }
        }

        val array = buff40.toArray()
        Timber.d("array size: ${array.size}")


        return RxFirebaseStorage.putBytes(imageRef, array, metadata)
            //            .flatMap {
            //                val secret = secret()
            //                val hash = encrypt(array, secret)
            //                RxFirebaseStorage.putBytes(hashRef, hash, storageMetadata {
            //                    setCustomMetadata("encoded", Arrays.toString(secret.encoded))
            //                    setCustomMetadata("algorithm", secret.algorithm)
            //                    setCustomMetadata("format", secret.format)
            //                })
            //            }
            .ignoreElement()
            .subscribeOn(schedulerProvider.io())
    }

    private fun getFolder() =
        SimpleDateFormat(FOLDER_FORMAT, Locale.US).format(System.currentTimeMillis())

    companion object {
        private const val COLLECTIONS_PATH_TO_USERS = "users"
        private const val FOLDER_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private data class Location(
        @PropertyName("location")
        val location: GeoPoint = GeoPoint(0.0, 0.0),
        @PropertyName("lastUpdatedTime")
        val lastUpdatedTime: Date = Date()
    )

    private fun DeviceLocation.toLocation(): Location {
        return Location(location = GeoPoint(latitude, longitude), lastUpdatedTime = Date())
    }


}