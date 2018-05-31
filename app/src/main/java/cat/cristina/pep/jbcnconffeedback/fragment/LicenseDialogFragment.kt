package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import cat.cristina.pep.jbcnconffeedback.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LicenseDialogFragment.LicenseDialogFragmentListener] interface
 * to handle interaction events.
 * Use the [LicenseDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LicenseDialogFragment : DialogFragment() {

    private val TAG = LicenseDialogFragment::class.java.name
    private var param1: String? = null
    private var param2: String? = null
    private var listener: LicenseDialogFragmentListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog")
        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val view = inflater.inflate(R.layout.fragment_license_dialog, null)

        return builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                    onButtonPressed("")
                }).create()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(msg: String) {
        listener?.onLicenseDialogFragmentInteraction(msg)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LicenseDialogFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement LicenseDialogFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface LicenseDialogFragmentListener {
        // TODO: Update argument type and name
        fun onLicenseDialogFragmentInteraction(msg: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LicenseDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LicenseDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}