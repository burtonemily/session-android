package org.session.libsession.messaging

import org.session.libsession.database.MessageDataProvider
import org.session.libsignal.libsignal.loki.SessionResetProtocol
import org.session.libsignal.libsignal.state.*
import org.session.libsignal.metadata.certificate.CertificateValidator
import org.session.libsignal.service.loki.protocol.closedgroups.SharedSenderKeysDatabaseProtocol

class Configuration(
        val storage: StorageProtocol,
        val signalStorage: SignalProtocolStore,
        val sskDatabase: SharedSenderKeysDatabaseProtocol,
        val messageDataProvider: MessageDataProvider,
        val sessionResetImp: SessionResetProtocol,
        val certificateValidator: CertificateValidator)
{
    companion object {
        lateinit var shared: Configuration

        fun configure(storage: StorageProtocol,
                      signalStorage: SignalProtocolStore,
                      sskDatabase: SharedSenderKeysDatabaseProtocol,
                      messageDataProvider: MessageDataProvider,
                      sessionResetImp: SessionResetProtocol,
                      certificateValidator: CertificateValidator
        ) {
            if (Companion::shared.isInitialized) { return }
            shared = Configuration(storage, signalStorage, sskDatabase, messageDataProvider, sessionResetImp, certificateValidator)
        }
    }
}