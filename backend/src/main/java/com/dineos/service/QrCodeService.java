package com.dineos.service;

public interface QrCodeService {

    byte[] generatePng(String content, int width, int height);
}
