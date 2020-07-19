package by.my.elections.data.datasource.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import java.lang.reflect.Type

class SecureStorageDataSource(context: Context, private val moshi: Moshi) : Storage {
    private val preference = EncryptedSharedPreferences.create(
        ".secret",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val observable = Observable.create<String> { emitter ->
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (!emitter.isDisposed) {
                emitter.onNext(key)
            }
        }
        preference.registerOnSharedPreferenceChangeListener(listener)
        emitter.setCancellable { preference.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override fun <T : Any> put(key: String, obj: T) {
        val json: String = obj.toJson(moshi = moshi) ?: return
        preference.edit(commit = true) {
            putString(key, json)
        }
    }


    override fun <T> get(key: String, classOfT: Class<T>): T? {
        val json = preference.getString(key, null) ?: return null
        return moshi.adapter(classOfT).fromJson(json)
    }

    override fun <T> get(key: String, typeOfT: Type): T? {
        val json = preference.getString(key, null) ?: return null
        return moshi.adapter<T>(typeOfT).fromJson(json)
    }

    override fun onKeyChanged(key: String): Observable<String> =
        observable.filter { keyChanged -> keyChanged == key }

    override fun remove(key: String) {
        preference.edit(commit = true) {
            remove(key)
        }
    }

    override fun clear() {
        preference.edit(commit = true) {
            clear()
        }
    }

    private fun <T : Any> T.toJson(moshi: Moshi): String? {
        val classOfT = this::class.java
        return moshi.adapter<T>(classOfT).toJson(this)
    }
}