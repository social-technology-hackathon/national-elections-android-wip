package by.my.elections.presentation.splash

import by.my.elections.data.datasource.firebase.exceptions.NotAuthorizedException
import by.my.elections.data.datasource.firebase.model.AuthStatus
import by.my.elections.data.datasource.schedule.SchedulerProvider
import by.my.elections.domain.usecases.AuthUseCase
import by.my.elections.domain.usecases.UpdateUserUseCase
import by.my.elections.presentation.base.BasePresenter
import by.my.elections.presentation.navigation.NavigationAction
import by.my.elections.presentation.navigation.Navigator
import io.reactivex.Completable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SplashPresenter(
    private val authUseCase: AuthUseCase,
    private val navigator: Navigator,
    private val updateUserUseCase: UpdateUserUseCase,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<SplashPresenter.View>() {

    override fun onViewWillShow(view: View) {
        super.onViewWillShow(view)
        disposeOnViewWillHide(
            authUseCase.execute()
                .firstOrError()
                .flatMapCompletable {
                    when (it) {
                        is AuthStatus.Authorized -> updateUserUseCase.execute()
                        is AuthStatus.NotAuthorized -> Completable.error(NotAuthorizedException())
                    }
                }
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(schedulerProvider.ui())
                .subscribe({
                    navigator.setScreen(NavigationAction.OpenMain)
                }, {
                    Timber.e(it)
                    navigator.setScreen(NavigationAction.OpenLogin)
                })
        )
    }

    interface View {

    }
}