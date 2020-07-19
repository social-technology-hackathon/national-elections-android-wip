package by.my.elections.presentation.splash

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import by.my.elections.R
import by.my.elections.databinding.FragmentSplashBinding
import by.my.elections.presentation.base.BaseFragment
import org.koin.android.ext.android.inject

class SplashFragment : BaseFragment<SplashPresenter.View, SplashPresenter>(), SplashPresenter.View {
    override val presenter: SplashPresenter by inject()
    override val abstractView: SplashPresenter.View
        get() = this


    lateinit var binding: FragmentSplashBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_splash,
            container,
            false
        )

        return binding.root
    }
}
