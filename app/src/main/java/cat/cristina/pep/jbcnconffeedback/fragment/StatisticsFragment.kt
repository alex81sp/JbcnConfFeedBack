package cat.cristina.pep.jbcnconffeedback.fragment

//import com.github.mikephil.charting.components.Description
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.activity.MainActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.stream.Collectors

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val TAG = StatisticsFragment::class.java.name

/**
 */
class StatisticsFragment : Fragment(), OnChartGestureListener {

    private val TAG = StatisticsFragment::class.java.name

    private var param1: String? = null
    private var param2: String? = null
    private var listenerStatistics: OnStatisticsFragmentListener? = null
    private var dataFromFirestore: Map<Long?, List<QueryDocumentSnapshot>>? = null
    private lateinit var dialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadScoring()
    }

    /* This method downloads the Scoring collection made up of documents(id_talk, score, date) */
    private fun downloadScoring(): Unit {

        if(!(context as MainActivity).isDeviceConnectedToWifiOrData().first) {
            Toast.makeText(context, R.string.sorry_no_graphic_available, Toast.LENGTH_LONG).show()
            return
        }

        dialog = ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setMessage(resources.getString(R.string.loading))
        dialog.isIndeterminate = true
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        FirebaseFirestore.getInstance()
                .collection("Scoring")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        dataFromFirestore = it.result.groupBy {
                            it.getLong("id_talk")
                        }
                        setupGraphTop10Talks()
                    } else {
                        dialog.dismiss()
                        Toast.makeText(context, R.string.sorry_no_graphic_available, Toast.LENGTH_LONG).show()
                        //Log.d(TAG, "*** Error *** ${it.exception?.message}")
                    }
                }
    }

    private fun setupGraph() {
        dialog.setMessage(resources.getString(R.string.processing));
        val labels = ArrayList<String>()
        val entries = ArrayList<BarEntry>()
        var i = 0.0F

        dataFromFirestore
                ?.asSequence()
                ?.sortedBy {
                    it.key
                }
                ?.forEach {
                    labels.add("Talk #${it.key}")
                    val avg: Double? = dataFromFirestore?.get(it.key)
                            ?.asSequence()
                            ?.map { doc ->
                                doc.get("score") as Long
                            }?.average()
                    entries.add(BarEntry(i++, avg!!.toFloat()))
                }

        val barDataSet: BarDataSet = BarDataSet(entries, "Score")

        with(barDataSet) {
            colors = ColorTemplate.COLORFUL_COLORS.asList()
            barBorderColor = Color.BLACK
        }

        val barData: BarData = BarData(barDataSet)

        with(barChart) {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.setDrawLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelCount = labels.size
            fitScreen()
            description.isEnabled = false
            setDrawBarShadow(true)
            setDrawValueAboveBar(true)
            //setFitBars(true)
            setBorderColor(Color.BLACK)
            setTouchEnabled(true)
            onChartGestureListener = this@StatisticsFragment
            animateY(1_000)
            legend.isEnabled = false
            legend.textColor = Color.GRAY
            legend.textSize = 15F

            notifyDataSetChanged()
            invalidate()
        }

        dialog.dismiss()

    }

    private fun setupGraphTop10Talks() {
        dialog.setMessage(resources.getString(R.string.processing));
        val labels = ArrayList<String>()
        val entries = ArrayList<BarEntry>()
        var i = 0.0F

        dataFromFirestore
                ?.asSequence()
                ?.sortedBy {
                    it.key
                }
                ?.forEach {
                    labels.add("Talk jhgkg kgkhg hkghg #${it.key}")
                    //labels.add("#${i}")
                    val avg: Double? = dataFromFirestore?.get(it.key)
                            ?.asSequence()
                            ?.map { doc ->
                                doc.get("score") as Long
                            }?.average()
                    entries.add(BarEntry(i++, avg!!.toFloat()))
                }

        val bestTalks = getBestTenTalks(entries)

        val barDataSet: BarDataSet = BarDataSet(bestTalks, "Score")

        with(barDataSet) {
            colors = ColorTemplate.COLORFUL_COLORS.asList()
            barBorderColor = Color.BLACK
        }

        val barData: BarData = BarData(barDataSet)

        with(barChart) {
            data = barData
            //xAxis.valueFormatter = IndexAxisValueFormatter(labels) as IAxisValueFormatter?
            xAxis.textSize = 24.0F
            xAxis.setDrawLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            xAxis.xOffset = 300.0F
            xAxis.yOffset = 100.0F

            //xAxis.labelCount = labels.size
            fitScreen()
            description.isEnabled = true
            //setDrawBarShadow(true)
            //setDrawValueAboveBar(true)
            //setFitBars(true)
            setBorderColor(Color.BLACK)
            setTouchEnabled(true)
            // onChartGestureListener = this@StatisticsFragment
            animateY(1_000)
            legend.isEnabled = false
            legend.textColor = Color.GRAY
            legend.textSize = 15F

            notifyDataSetChanged()
            invalidate()
        }

        dialog.dismiss()

    }

    private fun getBestTenTalks(entries: ArrayList<BarEntry>): MutableList<BarEntry>? {
        val ordered: MutableList<BarEntry> = entries
                .stream()
                .sorted { o1, o2 -> if(o1.y < o2.y) 1 else if (o1.y == o2.y) 0 else -1 }
                .collect(Collectors.toList())
        val limit: MutableList<BarEntry>? = ordered.stream().limit(10).collect(Collectors.toList())
        var i = 0.0F
        for (entry in limit!!.iterator()) {
            val id = entry.x
            // buscar titol
            entry.x = i++

        }
        return limit
    }

    fun onButtonPressed(msg: String) {
        listenerStatistics?.onStatisticsFragment(msg)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnStatisticsFragmentListener) {
            listenerStatistics = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnStatisticsFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerStatistics = null
    }

    /**
     */
    interface OnStatisticsFragmentListener {
        fun onStatisticsFragment(msg: String)
    }

    companion object {
        /**
         */
        @JvmStatic
        fun newInstance(param1: String = "param1", param2: String = "param2") =
                StatisticsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     *
     * @param me
     * @param lastPerformedGesture
     */
    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
    }

    /**
     * Callbacks then a fling gesture is made on the chart.
     *
     * @param me1
     * @param me2
     * @param velocityX
     * @param velocityY
     */
    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
    }

    /**
     * Callbacks when the chart is single-tapped.
     *
     * @param me
     */
    override fun onChartSingleTapped(me: MotionEvent?) {
        Log.d(TAG, "singledTap ${me.toString()}")

    }

    /**
     * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
     *
     * @param me
     * @param lastPerformedGesture
     */
    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
    }

    /**
     * Callbacks when the chart is scaled / zoomed via pinch zoom gesture.
     *
     * @param me
     * @param scaleX scalefactor on the x-axis
     * @param scaleY scalefactor on the y-axis
     */
    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
    }

    /**
     * Callbacks when the chart is longpressed.
     *
     * @param me
     */
    override fun onChartLongPressed(me: MotionEvent?) {
    }

    /**
     * Callbacks when the chart is double-tapped.
     *
     * @param me
     */
    override fun onChartDoubleTapped(me: MotionEvent?) {
    }

    /**
     * Callbacks when the chart is moved / translated via drag gesture.
     *
     * @param me
     * @param dX translation distance on the x-axis
     * @param dY translation distance on the y-axis
     */
    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
    }
}
