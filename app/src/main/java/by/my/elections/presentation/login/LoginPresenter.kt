package by.my.elections.presentation.login

import android.content.Intent
import by.my.elections.data.datasource.firebase.exceptions.NotAuthorizedException
import by.my.elections.data.datasource.firebase.model.*
import by.my.elections.data.datasource.schedule.SchedulerProvider
import by.my.elections.domain.usecases.*
import by.my.elections.presentation.base.BasePresenter
import by.my.elections.presentation.navigation.NavigationAction
import by.my.elections.presentation.navigation.Navigator
import io.reactivex.*
import timber.log.Timber

class LoginPresenter(
    private val buildAuthIntentUseCase: BuildAuthIntentUseCase,
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val navigator: Navigator,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<LoginPresenter.View>() {
    override fun onViewWillShow(view: View) {
        super.onViewWillShow(view)
        disposeOnViewWillHide(view.onClickSignIn()
            .switchMapSingle { buildAuthIntentUseCase.execute() }
            .observeOn(schedulerProvider.ui())
            .subscribe({
                view.signIn(it)
            }, {
                Timber.d(it)
            })
        )

        disposeOnViewWillHide(
            view.onAuthResult()
                .switchMapSingle { signInWithCredentialUseCase.execute(it.authIntent()) }
                .switchMapSingle {
                    when (it) {
                        is AuthStatus.Authorized -> updateUserUseCase.execute().andThen(Single.just(true))
                        is AuthStatus.NotAuthorized -> Single.error(NotAuthorizedException())
                    }
                }
                .retry()
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    navigator.setScreen(NavigationAction.OpenMain)
                }, {
                    Timber.d(it)
                })
        )
    }

    interface View {
        fun signIn(intent: AuthIntent)
        fun onClickSignIn(): Observable<Unit>
        fun onAuthResult(): Observable<Intent>
    }
}

