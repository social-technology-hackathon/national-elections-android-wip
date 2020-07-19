package by.my.elections.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import by.my.elections.R
import by.my.elections.data.datasource.firebase.model.AuthIntent
import by.my.elections.databinding.FragmentLoginBinding
import by.my.elections.presentation.base.BaseFragment
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject

class LoginFragment : BaseFragment<LoginPresenter.View, LoginPresenter>(), LoginPresenter.View {

    override val presenter: LoginPresenter by inject()
    override val abstractView: LoginPresenter.View
        get() = this
    lateinit var binding: FragmentLoginBinding

    private val authResultSubject = PublishSubject.create<Intent>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        return binding.root
    }

    override fun signIn(intent: AuthIntent) {
        startActivityForResult(intent.getIntent(), RC_SIGN_IN)
    }

    override fun onClickSignIn(): Observable<Unit> {
        return binding.btnGoogleSigIn.clicks()
    }

    override fun onAuthResult(): Observable<Intent> {
        return authResultSubject
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && data != null) {
            authResultSubject.onNext(data)
        }
    }


    companion object {
        const val RC_SIGN_IN = 2802
    }
}


