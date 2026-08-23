package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.entity.QrCode;
import com.omardev.event_ticketing.entity.Ticket;

public interface QrCodeService {

    QrCode generateForTicket(Ticket ticket);

}