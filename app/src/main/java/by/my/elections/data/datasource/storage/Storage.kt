package by.my.elections.data.datasource.storage

import io.reactivex.Observable
import java.lang.reflect.Type

interface Storage {
    fun <T : Any> put(key: String, obj: T)
    operator fun <T> get(key: String, classOfT: Class<T>): T?
    operator fun <T> get(key: String, typeOfT: Type): T?
    fun onKeyChanged(key: String): Observable<String>
    fun remove(key: String)
    fun clear()
}