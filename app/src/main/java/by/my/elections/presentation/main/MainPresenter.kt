package by.my.elections.presentation.main

import android.media.Image
import by.my.elections.data.datasource.schedule.SchedulerProvider
import by.my.elections.domain.usecases.*
import by.my.elections.presentation.base.BasePresenter
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.Observable
import io.reactivex.Single
import org.opencv.core.Mat
import timber.log.Timber

class MainPresenter(
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<MainPresenter.View>() {

    override fun onViewAttached(view: View) {
        super.onViewAttached(view)
        disposeOnViewDetached(
            view.requestPermission()
                .switchMapSingle {
                    if (it.granted) {
                        getLastKnownLocationUseCase.execute()
                    } else {
                        Single.error(Throwable("Permission isn't granted"))
                    }
                }
                .subscribeOn(schedulerProvider.ui())
                .subscribe({
                    view.startCamera()

                }, {
                    Timber.e(it)
                })
        )


    }

    override fun onViewWillShow(view: View) {
        super.onViewWillShow(view)


        disposeOnViewWillHide(
            view.onHelpClick()
                .observeOn(schedulerProvider.ui())
                .subscribe({

                }, {
                    Timber.e(it)
                })
        )

        disposeOnViewWillHide(
            view.onImageTaken()
                .flatMapCompletable { uploadFileUseCase.execute(it) }
                .observeOn(schedulerProvider.ui())
                .subscribe({

                }, {
                    Timber.e(it)
                })
        )


    }

    private fun View.permissionState(): Observable<Boolean> = hasPermission()
        .flatMapObservable {
            if (it) {
                Observable.just(true)
            } else {
                requestPermission().map { permission -> permission.granted }
            }
        }


    interface View {
        fun requestPermission(): Observable<Permission>
        fun onHelpClick(): Observable<Unit>
        fun onGalleryClick(): Observable<Unit>
        fun hasPermission(): Single<Boolean>
        fun startCamera()

        fun onImageTaken(): Observable<Mat>
    }
}