package by.my.elections.presentation.base

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<T> {
    private val attachedDisposables = CompositeDisposable()
    private val visibleDisposables = CompositeDisposable()
    private var view: ViewHolder<T> = ViewHolder.UnAttachedView

    private fun isViewAttached(): Boolean {
        return view is ViewHolder.AttachedView
    }

    @CallSuper
    open fun onViewAttached(view: T) {
        check(!isViewAttached()) { "View $view is already attached. Cannot attach $view" }
        this.view = ViewHolder.AttachedView(view)
    }

    /**
     * On view will show. Called when your view is about to be seen on the screen.
     */
    @CallSuper
    open fun onViewWillShow(view: T) {
    }

    /**
     * On view will hide. Called when your view is about to hide from the screen.
     */
    @CallSuper
    open fun onViewWillHide() {
        visibleDisposables.clear()
    }

    /**
     * On view detached. Intended as a cleanup process that should be called when the view will no
     * longer be in use.
     */
    @CallSuper
    open fun onViewDetached() {
        check(isViewAttached()) { "View is already detached" }
        view = ViewHolder.UnAttachedView
        attachedDisposables.clear()
    }

    private sealed class ViewHolder<out VH> {
        data class AttachedView<V>(val view: V) : ViewHolder<V>()
        object UnAttachedView : ViewHolder<Nothing>()
    }

    protected fun disposeOnViewDetached(disposable: Disposable) {
        attachedDisposables.add(disposable)
    }

    protected fun disposeOnViewWillHide(disposable: Disposable) {
        visibleDisposables.add(disposable)
    }
}