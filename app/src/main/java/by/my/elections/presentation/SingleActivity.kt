package by.my.elections.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import by.my.elections.R
import by.my.elections.databinding.ActivitySingleBinding
import by.my.elections.presentation.navigation.NavigationAction
import org.koin.android.ext.android.inject

class SingleActivity : AppCompatActivity(), SinglePresenter.View {

    //DataBinding
    lateinit var binding: ActivitySingleBinding
    lateinit var navController: NavController

    private val presenter: SinglePresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_single)
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()
    }

    override fun navigateTo(screen: NavigationAction) {
        when (screen) {
            is NavigationAction.OpenSplash -> navController.navigate(R.id.nav_splash_screen)
            is NavigationAction.OpenLogin -> navController.navigate(R.id.nav_login)
            is NavigationAction.OpenMain -> navController.navigate(R.id.nav_main)
        }
    }

    override fun onBackPressed() {
        when (navController.currentDestination!!.id) {
            R.id.nav_splash_screen -> finish()
            R.id.nav_login -> finish()
            else -> super.onBackPressed()
        }
    }
}





