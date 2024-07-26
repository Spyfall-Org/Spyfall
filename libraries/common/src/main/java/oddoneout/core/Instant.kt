package oddoneout.core

import java.time.Instant
import java.time.temporal.ChronoUnit

fun Instant.daysAgo(now: Instant): Long = ChronoUnit.DAYS.between(this, now)
