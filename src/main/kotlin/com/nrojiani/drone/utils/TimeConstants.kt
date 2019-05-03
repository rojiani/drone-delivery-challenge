@file:JvmName("TimeConstants")

package com.nrojiani.drone.utils

import java.time.Clock
import java.time.ZoneId
import java.time.ZoneOffset

/** Use UTC clock as default. */
@JvmField
val DEFAULT_CLOCK = Clock.systemUTC()

@JvmField
val UTC_ZONE_ID = ZoneId.of("Z")

@JvmField
val EST_ZONE_ID = ZoneId.of("EST", ZoneId.SHORT_IDS)

@JvmField
val DEFAULT_ZONE_OFFSET = ZoneOffset.UTC

@JvmField
val EST_ZONE_OFFSET = ZoneOffset.of("-05:00")