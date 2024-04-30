import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmationDialog(
    private val title: String,
    private val message: String,
    private val positiveButtonText: String = "Yes",
    private val negativeButtonText: String = "No",
    private val onConfirm: () -> Unit,
    private val onCancel: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, which ->
                onConfirm()
            }
            .setNegativeButton(negativeButtonText) { dialog, which ->
                onCancel()
            }
            .create()
    }

    companion object {
        fun newInstance(
            title: String,
            message: String,
            positiveButtonText: String = "Yes",
            negativeButtonText: String = "No",
            onConfirm: () -> Unit,
            onCancel: () -> Unit
        ): ConfirmationDialog {
            return ConfirmationDialog(title, message, positiveButtonText, negativeButtonText, onConfirm, onCancel)
        }
    }
}
