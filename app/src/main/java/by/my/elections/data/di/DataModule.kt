package by.my.elections.data.di

import by.my.elections.core.ModuleProvider
import by.my.elections.data.datasource.firebase.AuthDataSource
import by.my.elections.data.datasource.firebase.FirestoreDataSource
import by.my.elections.data.datasource.firebase.model.AuthTokenMapper
import by.my.elections.data.datasource.firebase.model.UserMapper
import by.my.elections.data.datasource.location.LocationService
import by.my.elections.data.datasource.schedule.ApplicationSchedulerProvider
import by.my.elections.data.datasource.schedule.SchedulerProvider
import by.my.elections.data.datasource.storage.SecureStorageDataSource
import by.my.elections.data.repository.*
import by.my.elections.domain.repository.*
import by.my.elections.utils.android.AndroidResourceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object DataModule : ModuleProvider {
    private const val URL_BASE = "https://us-central1-associationwords.cloudfunctions.net/v1/"

    private fun loggerInterceptor(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor()
        logger.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logger
    }

    private fun provideClient(): OkHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(loggerInterceptor())
        .build()

    private fun provideMoshi() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    private fun provideRetrofitInstance(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()


    private val scheduleModule = module {
        single<SchedulerProvider> { ApplicationSchedulerProvider() }
    }

    private val networkModule = module {
        single { loggerInterceptor() }
        single { provideClient() }
        single { provideMoshi() }
        single { provideRetrofitInstance(okHttpClient = get(), moshi = get()) }
    }

    private val mapperModule = module {
        single { UserMapper() }
        single { AuthTokenMapper() }
    }

    private val authModule = module {
        single { FirebaseAuth.getInstance() }
        single {
            AuthDataSource(
                context = get(),
                firebaseAuth = get(),
                userMapper = get(),
                authTokenMapper = get(),
                schedulerProvider = get()
            )
        }
    }

    private val firestoreModule = module {
        single {
            FirebaseFirestore.getInstance().apply {
                firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
            }
        }
        single { FirebaseStorage.getInstance() }
        single {
            FirestoreDataSource(
                firebaseFirestore = get(),
                firebaseStorage = get(),
                schedulerProvider = get()
            )
        }
    }


    private val androidModule = module {
        single { LocationService(androidContext()) }
        single { AndroidResourceManager(androidContext()) }
    }

    private val storageModule = module {
        single { SecureStorageDataSource(context = get(), moshi = get()) }
    }

    private val repositoryModule = module {
        single<UserRepository> {
            UserRepositoryImpl(
                firestoreDataSource = get(),
                authDataSource = get()
            )
        }
        single<AuthRepository> {
            AuthRepositoryImpl(
                secureStorageDataSource = get(),
                authDataSource = get()
            )
        }
        single<LocationRepository> {
            LocationRepositoryImpl(locationService = get())
        }

        single<FirebaseRepository> {
            FirebaseRepositoryImpl(
                firestoreDataSource = get(),
                locationService = get(),
                authDataSource = get()
            )
        }
    }

    override fun modules(): List<Module> {
        return listOf(
            scheduleModule,
            networkModule,
            mapperModule,
            authModule,
            firestoreModule,
            androidModule,
            storageModule,
            repositoryModule
        )
    }

}
