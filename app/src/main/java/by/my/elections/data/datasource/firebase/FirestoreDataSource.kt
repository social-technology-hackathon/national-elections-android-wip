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
import timber.log.Timber
import java.security.Key
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.*
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


    fun upload(array: ByteArray, metaInformation: Map<String, String>): Completable {
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

        return RxFirebaseStorage.putBytes(imageRef, array, metadata)
            .flatMapCompletable {
                Completable.fromCallable {
                    try {
                        val secret = secret()
                        val hash = encrypt(array, secret)
                        Timber.d("Ok, hash size: ${hash.size}. Secret: ${Arrays.toString(secret.encoded)}")
                    } catch (ex: Throwable) {
                        Timber.e(ex, "Failed")
                    }
                }
            }
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