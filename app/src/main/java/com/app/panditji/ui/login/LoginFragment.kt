package com.app.panditji.ui.login
import LoginRequest
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.panditji.MainActivity
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentLoginBinding
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private val apiVm by viewModel<apiVm>()
    private var progresbar: Dialog? = null

//    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1001
    private var isPasswordVisible = false

    private val prefs by inject<PrefsHelper>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseApp.initializeApp(requireActivity())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

//        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//        auth = FirebaseAuth.getInstance()

    }

    private fun initViews() {
        alreadyHaveAccount()
        onClick()
        /*binding.loginBtn.setOnClickListener {
//        validation()
          findNavController().navigate(R.id.otpFragment)

        }*/
        binding.loginPage.setOnClickListener {
//        validation()
          findNavController().navigate(R.id.registerFragment)

        }

        binding.fogotPass.setOnClickListener {
            findNavController().navigate(R.id.forgetFragment)
           /* googleSignInClient.signOut().addOnCompleteListener {
                signIn() // Always show account picker
            }*/
        }


        AppUtils.setupHideKeyboardOnTouch(binding.root,requireActivity())

       /* // Close keyboard when touching anywhere outside of EditText
        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            false
        }*/
        binding.togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.passInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_off) // 👁️ eye-open icon
            } else {
                binding.passInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.togglePasswordVisibility.setImageResource(R.drawable.eye_on) // 🙈 eye-off icon
            }
            binding.passInput.setSelection(binding.passInput.text!!.length) // keep cursor at end
        }
    }

    /*private fun validation() {
        val etPhone = binding.etPhone.text.toString()

        if(etPhone.isEmpty()){
            requireActivity().toast("Please enter phone number")
        }else{
            val model = RegistrationModel(
                device_type = "A",
                device_token = "device_token",
                social_type = "P",
                phone = etPhone
            )
            loginUser(model)
        }
    }*/
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }




    fun onClick(){
        /*binding.loginPage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }*/
        binding.logiWithOtp.setOnClickListener {
            findNavController().navigate(R.id.enterNumberFragment)
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.phonenumberInput.text.toString().trim()
            val password = binding.passInput.text.toString().trim()

            // Validate email
            if (email.isEmpty()) {
//                binding.phonenumberInput.error = "Please enter your email"
                Toast.makeText(requireActivity(),
                    getString(R.string.please_enter_your_email), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isValidEmail(email)) {
//                binding.phonenumberInput.error = "Invalid email format"
                Toast.makeText(requireActivity(),
                    getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate password
            if (password.isEmpty()) {
//                binding.passInput.error = "Please enter your password"
                Toast.makeText(requireActivity(),
                    getString(R.string.please_enter_your_password), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            } else if (!isValidPassword(password)) {
//                binding.passInput.error = "Password must be at least 6 characters"

                Toast.makeText(requireActivity(),
                    getString(R.string.password_must_be_at_least_6_characters), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }


         /*   val intent = Intent(requireActivity(),MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()            // Proceed with login
         */
            /*
            apiVm.loginUser(request)
*/
            val request = LoginRequest(email, password, deviceToken = prefs.fcmToken, deviceType = "android")
            loginUser(request)

        }

    }





    private fun alreadyHaveAccount(){
        val fullText = getString(R.string.don_t_have_an_account_sign_up)
        val spannableString = SpannableString(fullText)
        val signUpClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().popBackStack()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireActivity(),R.color.colorPrimary) // your link color
                ds.isUnderlineText = true
            }
        }

        val signUpStart = fullText.indexOf(getString(R.string.sign_up))
        val signUpEnd = signUpStart + getString(R.string.sign_up).length

        spannableString.setSpan(signUpClick, signUpStart, signUpEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
       binding.alreadyHaveAccountId.text = spannableString
        binding.alreadyHaveAccountId.movementMethod = LinkMovementMethod.getInstance()
        binding.alreadyHaveAccountId.highlightColor = Color.TRANSPARENT
    }

    private fun loginUser(model: LoginRequest?) {
        progresbar = AppUtils.progressDialog(requireActivity())
        apiVm.loginUser(model)
            .observe(requireActivity()
            ) { it ->
                println("Pankaj:$it")
                when (it) {
                    is Resource.Success -> {
                        val data = it.data?.payload
                        progresbar?.dismiss()

                        if (it.data?.code == 200) {
                            if (it.data.payload.isVendor){
                                prefs.firstName = data?.customer?.firstName ?: ""
                                prefs.lastName = data?.customer?.lastName ?: ""
                                prefs.authToken = data?.accessToken .toString()
                                prefs.userId = data?.customer?.id.toString()
                                prefs.email = data?.customer?.email.toString()
                                prefs.phone = data?.customer?.phone.toString()
                                prefs.profileImage = data?.customer?.cover?.url.toString()
                                prefs.isLoggedIn = true
                                prefs.profileImage = data?.customer?.image.toString()
//                            prefs.profileImage = data?.customer?.profile_img.toString()
                                Log.i("TAG", ": "+prefs.userId)
                                Log.i("TAG", "loginUser: "+prefs.authToken)

                                Toast.makeText(requireActivity(), it.data.message, Toast.LENGTH_SHORT).show()

                                startActivity(Intent(requireActivity(), MainActivity::class.java))
                                requireActivity().finish()
                                /*if (model?.social_type == "G") {

                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    val bundle = Bundle()
                                    bundle.putString("phoneNumber", model?.phone)
                                    bundle.putString("fromScreen", "login")
                                    bundle.putString("otp", otp)
                                    findNavController().navigate(R.id.otpFragment, bundle)
                                }*/
                            }else{
                                Toast.makeText(requireActivity(),
                                    getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
                            }
                        }
                        else if (it.data?.message == "Account not active.") {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.your_account_is_not_active_redirecting_to_registration),
                                Toast.LENGTH_LONG
                            ).show()

                           /* if (model?.social_type == "G") {
                                val bundle = Bundle().apply {
                                    putString("name", model.name)
                                    putString("email", model.email)
                                    putString("social_token", model.social_token)
                                    putString("social_type", model.social_type)
                                }
//                                findNavController().navigate(R.id.completeProfileFragment, bundle)
                            }*/
                        }
                        else {
                            Toast.makeText(requireActivity(), it.data?.message, Toast.LENGTH_LONG).show()
                        }

                    }
                    is Resource.Error ->{
                         progresbar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {

                            }
                            else -> {
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.errorCode}", )
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.errorMessage}", )
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.statusCode}", )
                                it.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                                    requireActivity().toast(errorMessage)
                                }
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
    }

    fun extractOtp(message: String): String? {
        val otpRegex = Regex("\\d{4,6}")  // 4 ya 6 digit OTP match karega
        val match = otpRegex.find(message)
        return match?.value
    }


//    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
             //   firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                progresbar?.dismiss()
                Log.w("GoogleSignIn", "Google sign in failed", e)
                Toast.makeText(requireActivity(), "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

   /* private fun firebaseAuthWithGoogle(idToken: String) {
        progresbar = AppUtils.progressDialog(requireActivity())
        progresbar?.show()

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                progresbar?.dismiss()

                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d("GoogleSignIn", "User: $user")

                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val firebaseIdToken = tokenTask.result?.token

                            if (firebaseIdToken != null) {
                                val model = RegistrationModel(
                                    name = user.displayName ?: "",
                                    email = user.email ?: "",
                                    device_type = "A",
                                    device_token = "device_token", // Replace with real device token
                                    social_type = "G",
                                    social_token = firebaseIdToken
                                )
                                loginUser(model)
                            } else {
                                Toast.makeText(requireContext(), "Failed to get ID token", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("Firebase", "Token error: ${tokenTask.exception}")
                            Toast.makeText(requireContext(), "Failed to get token", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Log.e("FirebaseAuth", "Sign-in failed", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Google Sign-In Failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
*/


}