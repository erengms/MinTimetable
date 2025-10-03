package com.islandparadise14.mintable.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.islandparadise14.mintable.R
import com.islandparadise14.mintable.model.ScheduleEntity
import com.islandparadise14.mintable.tableinterface.OnScheduleClickListener
import com.islandparadise14.mintable.tableinterface.OnScheduleLongClickListener
import com.islandparadise14.mintable.utils.dpToPx
import com.islandparadise14.mintable.utils.getTotalMinute
import kotlinx.android.synthetic.main.item_schedule.view.*

@SuppressLint("ViewConstructor")
class ScheduleView(
    context: Context,
    entity: ScheduleEntity,
    height: Int,
    width: Int,
    scheduleClickListener: OnScheduleClickListener?,
    scheduleLongClickListener: OnScheduleLongClickListener?,
    tableStartTime: Int,
    radiusStyle: Int
) : LinearLayout(context) {
    init {
        setting(
            context,
            entity,
            height,
            width,
            scheduleClickListener,
            scheduleLongClickListener,
            tableStartTime,
            radiusStyle
        )
    }

    @SuppressLint("RtlHardcoded")
    private fun setting(
        context: Context,
        entity: ScheduleEntity,
        height: Int,
        width: Int,
        scheduleClickListener: OnScheduleClickListener?,
        scheduleLongClickListener: OnScheduleLongClickListener?,
        tableStartTime: Int,
        radiusStyle: Int
    ) {

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_schedule, this, true)

        // Süre hesapla (dakika cinsinden)
        val duration = getTotalMinute(entity.endTime) - getTotalMinute(entity.startTime)

        // Yükseklik hesaplama
        var calculatedHeight = ((height * duration).toDouble() / 60).toInt()

        // 🔒 Minimum 2dp, ama cellHeight büyüdükçe orantılı şekilde artsın
        val minHeight = dpToPx(context, 2f).toInt()
        if (calculatedHeight < minHeight) {
            // 1 dakikanın yüksekliği
            val oneMinuteHeight = (height.toFloat() / 60f).toInt()
            calculatedHeight = maxOf(minHeight, oneMinuteHeight)
        }

        val layoutSetting = LayoutParams(width, calculatedHeight)

        // Başlangıç saatine göre üst margin
        layoutSetting.topMargin =
            (((height * getTotalMinute(entity.startTime)).toDouble() / 60) - (height * tableStartTime)).toInt()

        // Gün sütunu
        layoutSetting.leftMargin = width * entity.scheduleDay

        tableItem.layoutParams = layoutSetting

        // Click listener
        tableItem.setOnClickListener {
            scheduleClickListener?.scheduleClicked(entity)
            entity.mOnClickListener?.onClick(tableItem)
        }

        tableItem.setOnLongClickListener {
            scheduleLongClickListener?.scheduleLongClicked(entity)
            return@setOnLongClickListener true
        }

        // Text layout ayarları
        val layoutText = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        val cornerRadius = dpToPx(context, RADIUS.toFloat())
        val roundRadius = dpToPx(context, ROUND.toFloat())

        val border = GradientDrawable()
        border.setColor(Color.parseColor(entity.backgroundColor))
        border.shape = GradientDrawable.RECTANGLE

        when (radiusStyle) {
            NONE -> {}
            LEFT -> {
                layoutText.leftMargin = (width.toDouble() * 0.15).toInt()
                tableItem.gravity = Gravity.RIGHT
                name.layoutParams = layoutText
                name.gravity = Gravity.RIGHT
                room.gravity = Gravity.RIGHT

                border.cornerRadii = floatArrayOf(
                    cornerRadius, cornerRadius,
                    0f, 0f,
                    cornerRadius, cornerRadius,
                    0f, 0f
                )
            }
            RIGHT -> {
                layoutText.rightMargin = (width.toDouble() * 0.15).toInt()
                name.layoutParams = layoutText

                border.cornerRadii = floatArrayOf(
                    0f, 0f,
                    cornerRadius, cornerRadius,
                    0f, 0f,
                    cornerRadius, cornerRadius
                )
            }
            ALL -> {
                border.cornerRadius = roundRadius
            }
        }

        tableItem.background = border

        // Text renkleri ve içerikleri
        name.text = entity.scheduleName
        room.text = entity.roomInfo

        name.setTextColor(Color.parseColor(entity.textColor))
        room.setTextColor(Color.parseColor(entity.textColor))
    }

    companion object {
        const val NONE = 0
        const val LEFT = 1
        const val RIGHT = 2
        const val ALL = 3
        private const val RADIUS = 30
        private const val ROUND = 15
    }
}
