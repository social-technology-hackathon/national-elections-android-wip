package by.my.elections.presentation

import by.my.elections.data.datasource.schedule.SchedulerProvider
import by.my.elections.presentation.base.BasePresenter
import by.my.elections.presentation.navigation.NavigationAction
import by.my.elections.presentation.navigation.Navigator

class SinglePresenter(
    private val navigator: Navigator,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<SinglePresenter.View>() {

    override fun onViewWillShow(view: View) {
        super.onViewWillShow(view)
        disposeOnViewWillHide(
            navigator.onScreenChanged()
                .observeOn(schedulerProvider.ui())
                .subscribe {
                    view.navigateTo(it)
                }
        )
    }

    interface View {
        fun navigateTo(screen: NavigationAction)
    }
}