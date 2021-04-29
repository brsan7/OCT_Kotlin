package com.brsan7.oct

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.dialogs.ConsultaSolarDetailDialog
import com.brsan7.oct.model.LocalVO
import com.brsan7.oct.utils.CalendarUtils
import com.brsan7.oct.utils.SharedPreferencesUtils
import com.brsan7.oct.viewmodels.CalSolActivityViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class CalendarioSolarActivity : DrawerMenuActivity() {

    private lateinit var calSolActivityViewModel: CalSolActivityViewModel
    private lateinit var spnCalSolarActLocal: Spinner
    private lateinit var mcvCalSolarAct: MaterialCalendarView
    private lateinit var localSelecionado: LocalVO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_solar)

        setupDrawerMenu(getString(R.string.menu2_2_ConsSol))
        setupComponentes()
        setupCalSolActivityViewModel()
        setupCalendario()
    }

    override fun onDestroy() {
        super.onDestroy()
        calSolActivityViewModel.reStartActivity = true
    }

    private fun setupComponentes() {

        mcvCalSolarAct = findViewById(R.id.mcvCalSolarAct)
        spnCalSolarActLocal = findViewById(R.id.spnCalSolarActLocal)
    }
    private fun setupCalSolActivityViewModel() {
        calSolActivityViewModel = ViewModelProvider(this).get(CalSolActivityViewModel::class.java)

        localSelecionado = SharedPreferencesUtils().getShareLocalDefault()
        calSolActivityViewModel.getAllLocais(localSelecionado)

        calSolActivityViewModel.vmSpnCalSolarActLocal.observe(this, { itensSpinner->
            setupSpinner(itensSpinner)
        })
        calSolActivityViewModel.vmCalSolarActDadosLocal.observe(this, { itemLocalVo->
            localSelecionado = itemLocalVo
            calSolActivityViewModel.getAllEstacoesAno()
        })
        calSolActivityViewModel.vmMcvCalSolarAct.observe(this, { datasEstacoesDoAno->
            mcvCalSolarAct.removeDecorators()
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[0],localSelecionado.latitude)
            )
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[1],localSelecionado.latitude)
            )
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[2],localSelecionado.latitude)
            )
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[3],localSelecionado.latitude)
            )
        })
    }

    private fun setupSpinner(itensSpinner: Array<String>){
        val adapterSpnCalSolarActLocal = ArrayAdapter(this, R.layout.item_spinner, itensSpinner)
        adapterSpnCalSolarActLocal.setDropDownViewResource(R.layout.item_spinner)
        spnCalSolarActLocal.adapter = adapterSpnCalSolarActLocal
        spnCalSolarActLocal.setSelection(calSolActivityViewModel.getIdSpinner(localSelecionado.id))
        spnCalSolarActLocal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {

                calSolActivityViewModel.getDadosLocalSelecionado(position)
                calSolActivityViewModel.reStartActivity=false
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupCalendario() {

        mcvCalSolarAct.selectedDate = CalendarDay.today()

        mcvCalSolarAct.setOnDateChangedListener { widget, date, selected ->

            val fragment = ConsultaSolarDetailDialog.newInstance(
                    id = localSelecionado.id,
                    diaJuliano = convCalendarDay(date)[Calendar.DAY_OF_YEAR],
                    ano = convCalendarDay(date)[Calendar.YEAR]
            )
            fragment.show(supportFragmentManager, "dialog")
        }
    }

    private fun convCalendarDay(dataSelecionada: CalendarDay):Calendar{
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,dataSelecionada.day)
            set(Calendar.MONTH,dataSelecionada.month-1)
            set(Calendar.YEAR,dataSelecionada.year)
        }
    }
}