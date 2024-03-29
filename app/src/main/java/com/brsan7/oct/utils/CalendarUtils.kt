package com.brsan7.oct.utils

import android.app.Activity
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.style.LineBackgroundSpan
import androidx.core.content.ContextCompat
import com.brsan7.oct.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CalendarUtils {

    class MultipleEventDecorator(private val tipo: String, dates: Collection<CalendarDay?>?) :
            DayViewDecorator {

        private val dates: HashSet<CalendarDay?> = HashSet(dates)
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DrawTipoSpan(tipo))
        }
    }

    class DrawTipoSpan(tipo: String) : LineBackgroundSpan {

        private var tipoDraw = tipo

        override fun drawBackground(
                canvas: Canvas,
                paint: Paint,
                left: Int,
                right: Int,
                top: Int,
                baseline: Int,
                bottom: Int,
                text: CharSequence,
                start: Int,
                end: Int,
                lnum: Int
        ) {
            val centroHorizontal = (right / 2F)+(right % 2F)
            val centroVertical = (bottom / 2F)+(bottom % 2F)
            val rect: RectF
            when (tipoDraw) {
                "Feriado" -> {
                    rect = RectF(
                            centroHorizontal - bottom * 0.6F,
                            centroVertical - bottom * 0.6F,
                            centroHorizontal + bottom * 0.6F,
                            centroVertical + bottom * 0.6F)
                    paint.color = Color.parseColor("#FA0303")
                    canvas.drawRoundRect(rect, 0f, 0f, paint)
                }
                "Compromisso" -> {
                    rect = RectF(
                            centroHorizontal - bottom * 0.5F,
                            centroVertical - bottom * 0.5F,
                            centroHorizontal + bottom * 0.5F,
                            centroVertical + bottom * 0.5F)
                    paint.color = Color.parseColor("#FDE301")
                    canvas.drawRoundRect(rect,0f, 0f, paint
                    )
                }
                "Lembrete" -> {
                    rect = RectF(
                            centroHorizontal - bottom * 0.4F,
                            centroVertical - bottom * 0.4F,
                            centroHorizontal + bottom * 0.4F,
                            centroVertical + bottom * 0.4F)
                    paint.color = Color.parseColor("#02FA0C")
                    canvas.drawRoundRect(rect,0f, 0f, paint)

                }
                else -> {
                    if (tipoDraw != "Hoje"){
                        rect = RectF(
                                centroHorizontal - bottom * 0.35F,
                                centroVertical - bottom * 0.35F,
                                centroHorizontal + bottom * 0.35F,
                                centroVertical + bottom * 0.35F)
                        paint.color = Color.parseColor("#FA0303")
                        canvas.drawRoundRect(rect, 0f, 0f, paint)
                    }
                }
            }
            when (tipoDraw) {
                "Hoje" -> {
                    paint.color = Color.parseColor("#FF018786")
                    canvas.drawText("", 0f, 0f, paint)
                }
                else -> {
                    if (tipoDraw.toDoubleOrNull() != null){
                        paint.color = Color.parseColor("#FDE301")
                        canvas.drawText("", 0f, 0f, paint)
                    }
                    else {
                        paint.color = Color.parseColor("#FF000000")
                        canvas.drawText("", 0f, 0f, paint)
                    }
                }
            }
        }
    }
    class DayDecorator(context: Activity?, dia: CalendarDay, tipo: String) : DayViewDecorator {

        private val drawable: Drawable?
        private val tipoDraw = tipo
        private var diaSelecionado = dia
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == diaSelecionado
        }

        override fun decorate(view: DayViewFacade) {
            if (tipoDraw.toDoubleOrNull() != null){
                view.setSelectionDrawable(drawable!!)
            }
            view.addSpan(DrawTipoSpan(tipoDraw))
        }
        init {
            if (tipoDraw.toDoubleOrNull() != null) {
                val img: Int = when (dia.month) {
                    3 -> {
                        if (tipoDraw.toDouble() < 0) { R.drawable.ic_baseline_eco_24 } //Equinócio de Outono - Hemisfério Sul
                        else{ R.drawable.ic_baseline_local_florist_24 } //Equinócio de Primavera - Hemisfério Norte
                    }
                    6 -> {
                        if (tipoDraw.toDouble() < 0) { R.drawable.ic_baseline_ac_unit_24 } //Solstício de Inverno - Hemisfério Sul
                        else{ R.drawable.ic_baseline_sol } //Solstício de Verão - Hemisfério Norte
                    }
                    9 -> {
                        if (tipoDraw.toDouble() < 0) { R.drawable.ic_baseline_local_florist_24 } //Equinócio de Primavera - Hemisfério Sul
                        else{ R.drawable.ic_baseline_eco_24 } //Equinócio de Outono - Hemisfério Norte
                    }
                    12 -> {
                        if (tipoDraw.toDouble() < 0) { R.drawable.ic_baseline_sol } //Solstício de Verão - Hemisfério Sul
                        else{ R.drawable.ic_baseline_ac_unit_24 } //Solstício de Inverno - Hemisfério Norte
                    }
                    else -> {
                        R.drawable.ic_launcher_background
                    }
                }
                drawable = ContextCompat.getDrawable(context!!, img)
            }
            else{ drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_launcher_background) }
        }
    }

}