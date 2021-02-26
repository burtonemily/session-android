package org.thoughtcrime.securesms.loki.activities

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_private_chat.*
import kotlinx.android.synthetic.main.fragment_recovery_phrase.*
import network.loki.messenger.R
import org.session.libsignal.service.loki.crypto.MnemonicCodec
import org.session.libsignal.utilities.Hex
import org.thoughtcrime.securesms.BaseActionBarActivity
import org.thoughtcrime.securesms.loki.fragments.ScanQRCodeWrapperFragment
import org.thoughtcrime.securesms.loki.fragments.ScanQRCodeWrapperFragmentDelegate
import org.thoughtcrime.securesms.loki.utilities.MnemonicUtilities
import org.thoughtcrime.securesms.loki.utilities.setUpActionBarSessionLogo

class LinkDeviceActivity : BaseActionBarActivity(), ScanQRCodeWrapperFragmentDelegate {
    private val adapter = LinkDeviceActivityAdapter(this)

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpActionBarSessionLogo()
        setContentView(R.layout.activity_link_device)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
    // endregion

    // region Interaction
    override fun handleQRCodeScanned(mnemonic: String) {
        continueWithMnemonic(mnemonic)
    }

    fun continueWithMnemonic(mnemonic: String) {
        val loadFileContents: (String) -> String = { fileName ->
            MnemonicUtilities.loadFileContents(this, fileName)
        }
        try {
            val hexEncodedSeed = MnemonicCodec(loadFileContents).decode(mnemonic)
            val seed = Hex.fromStringCondensed(hexEncodedSeed)
            continueWithSeed(seed)
        } catch (error: Exception) {
            val message = if (error is MnemonicCodec.DecodingError) {
                error.description
            } else {
                "An error occurred."
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun continueWithSeed(seed: ByteArray) {
        // TODO: Implement
    }
    // endregion
}

// region Adapter
private class LinkDeviceActivityAdapter(private val activity: LinkDeviceActivity) : FragmentPagerAdapter(activity.supportFragmentManager) {
    val recoveryPhraseFragment = RecoveryPhraseFragment()

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(index: Int): Fragment {
        return when (index) {
            0 -> recoveryPhraseFragment
            1 -> {
                val result = ScanQRCodeWrapperFragment()
                result.delegate = activity
                result.message = "Navigate to Settings → Recovery Phrase on your other device to show your QR code."
                result
            }
            else -> throw IllegalStateException()
        }
    }

    override fun getPageTitle(index: Int): CharSequence? {
        return when (index) {
            0 -> "Recovery Phrase"
            1 -> "Scan QR Code"
            else -> throw IllegalStateException()
        }
    }
}
// endregion

// region Recovery Phrase Fragment
class RecoveryPhraseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_recovery_phrase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mnemonicEditText.imeOptions = EditorInfo.IME_ACTION_DONE or 16777216 // Always use incognito keyboard
        mnemonicEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        mnemonicEditText.setOnEditorActionListener { v, actionID, _ ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                handleContinueButtonTapped()
                true
            } else {
                false
            }
        }
        continueButton.setOnClickListener { handleContinueButtonTapped() }
    }

    private fun handleContinueButtonTapped() {
        val mnemonic = mnemonicEditText.text?.trim().toString()
        (requireActivity() as LinkDeviceActivity).continueWithMnemonic(mnemonic)
    }
}
// endregion
