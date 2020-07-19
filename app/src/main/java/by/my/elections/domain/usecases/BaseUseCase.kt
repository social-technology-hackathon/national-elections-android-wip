package by.my.elections.domain.usecases

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface BaseSingleUseCase<T : Any> {
    fun execute(): Single<T>
}

interface BaseSingleUseCaseWithParam<in P : Any, T : Any> {
    fun execute(param: P): Single<T>
}

interface BaseCompletableUseCase {
    fun execute(): Completable
}

interface BaseCompletableUseCaseWithParam<in P : Any> {
    fun execute(param: P): Completable
}

interface BaseObservableUseCase<T : Any> {
    fun execute(): Observable<T>
}

interface BaseObservableUseCaseWithParam<in P : Any, T : Any> {
    fun execute(param: P): Observable<T>
}