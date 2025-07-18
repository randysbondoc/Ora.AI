package tech.rb.ora

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * Span that applies a concrete Typeface (not just a family name string).
 */
class FontTypefaceSpan(
    private val newType: Typeface
) : MetricAffectingSpan() {

    override fun updateMeasureState(tp: TextPaint) = apply(tp)
    override fun updateDrawState(tp: TextPaint) = apply(tp)

    private fun apply(paint: TextPaint) {
        val old = paint.typeface
        val oldStyle = old?.style ?: 0
        val want = newType.style

        // Synthesize bold/italic if the new typeface lacks the old style bits.
        val fake = oldStyle and want.inv()
        if (fake and Typeface.BOLD != 0) paint.isFakeBoldText = true
        if (fake and Typeface.ITALIC != 0) paint.textSkewX = -0.25f

        paint.typeface = newType
    }
}