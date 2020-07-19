package by.my.elections.presentation.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

abstract class BaseFragment<V, P : BasePresenter<V>> : Fragment() {
    abstract val presenter: P
    abstract val abstractView: V

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached(abstractView)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onViewDetached()
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        presenter.onViewWillShow(abstractView)
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        presenter.onViewWillHide()
    }
}