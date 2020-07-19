package by.my.elections.presentation.navigation

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class Navigator {
    //LiveData --> user
    private val screenSubject = PublishSubject.create<NavigationAction>()


    fun setScreen(screen: NavigationAction) {
        screenSubject.onNext(screen)
    }

    fun onScreenChanged(): Observable<NavigationAction> = screenSubject.hide()
}