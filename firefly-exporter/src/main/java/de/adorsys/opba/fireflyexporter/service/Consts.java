package de.adorsys.opba.fireflyexporter.service;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Consts {

    // Format is: Mon Sep 17 03:00:00 EEST 2018
    public static final DateTimeFormatter FIREFLY_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
}
