package com.example.vanotificator.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTimeUtil {

    public static ZonedDateTime convertTimeZone(LocalDate localDate,
                                                LocalTime localTime,
                                                int timezone) {
        LocalDateTime utcDateTime = LocalDateTime.of(localDate, localTime);
        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneOffset.UTC);
        return utcZoned.withZoneSameInstant(ZoneOffset.ofTotalSeconds(timezone));
    }
}
